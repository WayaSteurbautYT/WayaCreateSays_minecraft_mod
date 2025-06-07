package com.wayacreate.wayacreatesays.voice;

import com.wayacreate.wayacreatesays.WayaCreateSaysMod;
import edu.cmu.sphinx.api.Configuration;
import edu.cmu.sphinx.api.LiveSpeechRecognizer;
import edu.cmu.sphinx.api.SpeechResult;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.fabricmc.fabric.api.client.rendering.v1.HudRenderCallback;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.util.InputUtil;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.text.Text;
import net.minecraft.util.math.MathHelper;
import org.lwjgl.glfw.GLFW;

import java.util.LinkedList;
import java.util.Queue;

public class VoiceRecognition {
    private static LiveSpeechRecognizer recognizer;
    private static boolean isListening = false;
    private static KeyBinding listenKey;
    private static long lastCommandTime = 0;
    private static final Queue<String> commandQueue = new LinkedList<>();
    private static String lastHeard = "";
    private static int listeningAnimation = 0;
    private static long lastAnimationUpdate = 0;

    public static void init() {
        // Set up the key binding (default key: V)
        listenKey = KeyBindingHelper.registerKeyBinding(new KeyBinding(
            "key.wayacreatesays.listen",
            InputUtil.Type.KEYSYM,
            GLFW.GLFW_KEY_V,
            "category.wayacreatesays.main"
        ));

        // Set up voice recognition
        try {
            Configuration configuration = new Configuration();
            configuration.setAcousticModelPath("resource:/edu/cmu/sphinx/models/en-us/en-us");
            configuration.setDictionaryPath("resource:/edu/cmu/sphinx/models/en-us/cmudict-en-us.dict");
            configuration.setLanguageModelPath("resource:/edu/cmu/sphinx/models/en-us/en-us.lm.bin");
            
            // Enable more accurate but slower recognition
            configuration.setUseGrammar(false);
            configuration.setSampleRate(16000);
            
            recognizer = new LiveSpeechRecognizer(configuration);
            WayaCreateSaysMod.LOGGER.info("Voice recognition initialized successfully");
        } catch (Exception e) {
            WayaCreateSaysMod.LOGGER.error("Failed to initialize voice recognition", e);
            return;
        }

        // Register the key press event
        ClientTickEvents.END_CLIENT_TICK.register(client -> {
            long currentTime = System.currentTimeMillis();
            
            // Update listening animation
            if (isListening && currentTime - lastAnimationUpdate > 100) {
                listeningAnimation = (listeningAnimation + 1) % 4;
                lastAnimationUpdate = currentTime;
            }
            
            // Process key press
            if (listenKey.wasPressed()) {
                toggleListening();
            }
            
            // Process command queue (one command per tick)
            if (!commandQueue.isEmpty() && currentTime - lastCommandTime > 500) {
                String command = commandQueue.poll();
                if (command != null) {
                    CommandHandler.handleCommand(command);
                    lastCommandTime = currentTime;
                }
            }
            
            // Process voice input when listening
            if (isListening) {
                processVoiceCommands();
            }
        });
        
        // Register HUD renderer for listening indicator
        HudRenderCallback.EVENT.register((matrixStack, tickDelta) -> {
            if (isListening) {
                renderListeningIndicator(matrixStack);
            }
        });
    }
    
    private static void renderListeningIndicator(MatrixStack matrixStack) {
        MinecraftClient client = MinecraftClient.getInstance();
        if (client == null || client.player == null) return;
        
        TextRenderer textRenderer = client.textRenderer;
        int width = client.getWindow().getScaledWidth();
        int height = client.getWindow().getScaledHeight();
        
        // Draw background
        int bgX = width / 2 - 100;
        int bgY = height - 40;
        int bgWidth = 200;
        int bgHeight = 20;
        
        // Draw listening indicator
        String text = "Listening" + ". ".repeat(listeningAnimation + 1);
        int textWidth = textRenderer.getWidth(text);
        textRenderer.drawWithShadow(
            matrixStack,
            Text.of(text),
            (width - textWidth) / 2f,
            bgY + 6,
            0xFFFFFF
        );
        
        // Draw last heard command
        if (!lastHeard.isEmpty()) {
            String heardText = "Heard: " + lastHeard;
            int heardWidth = textRenderer.getWidth(heardText);
            textRenderer.drawWithShadow(
                matrixStack,
                Text.of(heardText),
                (width - heardWidth) / 2f,
                bgY - 15,
                0xAAAAAA
            );
        }
    }

    private static void toggleListening() {
        MinecraftClient client = MinecraftClient.getInstance();
        
        if (isListening) {
            recognizer.stopRecognition();
            isListening = false;
            lastHeard = "";
            WayaCreateSaysMod.LOGGER.info("Stopped listening for commands");
            if (client.player != null) {
                client.player.playSound(SoundEvents.BLOCK_NOTE_BLOCK_BASS, SoundCategory.MASTER, 0.5f, 0.5f);
            }
        } else {
            try {
                recognizer.startRecognition(true);
                isListening = true;
                listeningAnimation = 0;
                lastAnimationUpdate = System.currentTimeMillis();
                WayaCreateSaysMod.LOGGER.info("Listening for commands...");
                if (client.player != null) {
                    client.player.playSound(SoundEvents.BLOCK_NOTE_BLOCK_PLING, SoundCategory.MASTER, 0.5f, 1.5f);
                }
            } catch (Exception e) {
                WayaCreateSaysMod.LOGGER.error("Failed to start voice recognition", e);
                if (client.player != null) {
                    client.player.sendMessage(Text.of("§cFailed to start voice recognition: " + e.getMessage()), false);
                }
            }
        }
    }

    private static void processVoiceCommands() {
        try {
            SpeechResult result = recognizer.getResult();
            if (result != null) {
                String command = result.getHypothesis().toLowerCase().trim();
                float confidence = result.getResult().getBestFinalToken().getScore();
                
                // Only process commands with reasonable confidence
                if (confidence > 0.3f) {
                    WayaCreateSaysMod.LOGGER.info(String.format("Heard: %s (%.2f%% confidence)", command, confidence * 100));
                    lastHeard = command;
                    
                    // Process the command if it starts with a trigger phrase
                    if (command.startsWith("simon says ") || command.startsWith("waya create says ") || 
                        command.startsWith("simon says") || command.startsWith("waya create says")) {
                        
                        // Extract the actual command
                        String actualCommand = command
                            .replaceFirst("(?i)(simon says|waya create says)\\s*", "")
                            .trim();
                        
                        if (!actualCommand.isEmpty()) {
                            commandQueue.add(actualCommand);
                            // Play a sound to indicate command was understood
                            if (MinecraftClient.getInstance().player != null) {
                                MinecraftClient.getInstance().player.playSound(
                                    SoundEvents.BLOCK_NOTE_BLOCK_BELL, 
                                    SoundCategory.MASTER, 
                                    0.5f, 
                                    1.0f
                                );
                            }
                        }
                    }
                } else {
                    WayaCreateSaysMod.LOGGER.warn("Low confidence in voice recognition: " + command + " (" + (confidence * 100) + "%)");
                }
            }
        } catch (Exception e) {
            WayaCreateSaysMod.LOGGER.error("Error processing voice command", e);
        }
    }
}
