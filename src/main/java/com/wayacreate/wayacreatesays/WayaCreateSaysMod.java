package com.wayacreate.wayacreatesays;

import com.wayacreate.wayacreatesays.commands.AutoSpeedrunCommands;
import com.wayacreate.wayacreatesays.commands.WayaCreateCommand;
import com.wayacreate.wayacreatesays.game.WayaCreateGameMode;
import com.wayacreate.wayacreatesays.game.WayaCreateGameModeType;
import com.wayacreate.wayacreatesays.network.ExecuteFunctionPacket;
import com.wayacreate.wayacreatesays.network.StartAutoSpeedrunPacket;
import com.wayacreate.wayacreatesays.network.StopAutoSpeedrunPacket; // Added import
import com.wayacreate.wayacreatesays.network.AttackDragonPacket; // Added import
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.fabricmc.fabric.api.entity.event.living.LivingEvent;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerEntityEvents;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;
import net.fabricmc.fabric.api.event.player.PlayerBlockBreakEvents;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.Identifier;
import net.fabricmc.fabric.api.advancement.v1.AdvancementProgressChangeEvent;
import com.wayacreate.wayacreatesays.advancement.criterion.DragonPacifiedCriterion;
import com.wayacreate.wayacreatesays.commands.AutoSpeedrunCommands; // Added for helper methods
import net.minecraft.advancement.criterion.Criteria;
import net.fabricmc.fabric.api.event.player.UseEntityCallback;
import net.minecraft.entity.boss.dragon.EnderDragonEntity;
import net.minecraft.util.ActionResult;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items; // For minecraft:stick
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class WayaCreateSaysMod implements ModInitializer {
    public static final String MOD_ID = "wayacreatesays";
    public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);
    public static MinecraftServer server;
    
    // Game mode identifier
    public static final Identifier WAYACREATE_GAMEMODE_ID = new Identifier(MOD_ID, "wayacreate");
    public static DragonPacifiedCriterion DRAGON_PACIFIED;
    
    @Override
    public void onInitialize() {
        LOGGER.info("WayaCreateSays mod initialized!");
        
        // Register server start/stop events
        ServerLifecycleEvents.SERVER_STARTING.register(server -> {
            WayaCreateSaysMod.server = server;
            // Register our game mode
            WayaCreateGameModeType.register();
            LOGGER.info("WayaCreate game mode registered");
        });
        
        ServerLifecycleEvents.SERVER_STOPPED.register(server -> {
            WayaCreateSaysMod.server = null;
            LOGGER.info("WayaCreate server stopped");
        });
        
        // Register player join and respawn events
        ServerEntityEvents.ENTITY_LOAD.register((entity, world) -> {
            if (entity instanceof ServerPlayerEntity) {
                ServerPlayerEntity player = (ServerPlayerEntity) entity;
                
                // Apply game mode effects if needed
                if (player.interactionManager.getGameMode() == WayaCreateGameMode.INSTANCE) {
                    WayaCreateGameModeType.applyGameModeEffects(player);
                }
                
                // Initialize auto speedrun if needed
                AutoSpeedrunCommands.onPlayerJoin(player);
            }
        });
        
        // Register commands
        CommandRegistrationCallback.EVENT.register((dispatcher, registryAccess, environment) -> {
            WayaCreateCommand.register(dispatcher);
            LOGGER.info("WayaCreate commands registered");
        });
        
        // Register server-side packet receiver
        ServerPlayNetworking.registerGlobalReceiver(ExecuteFunctionPacket.PACKET_ID, (server, player, handler, buf, responseSender) -> {
            ExecuteFunctionPacket packet = new ExecuteFunctionPacket(buf);
            String functionPath = packet.getFunctionPath();
            server.execute(() -> {
                // Ensure the command is executed on the server thread
                server.getCommandManager().executeWithPrefix(player.getCommandSource().withSilent(), "function " + functionPath);
            });
        });

        // Register server-side receiver for StopAutoSpeedrunPacket
        ServerPlayNetworking.registerGlobalReceiver(StopAutoSpeedrunPacket.PACKET_ID, (server, player, handler, buf, responseSender) -> {
            server.execute(() -> {
                AutoSpeedrunCommands.stopAutoSpeedrun(player);
            });
        });

        // Register server-side receiver for AttackDragonPacket
        ServerPlayNetworking.registerGlobalReceiver(AttackDragonPacket.PACKET_ID, (server, player, handler, buf, responseSender) -> {
            server.execute(() -> {
                AutoSpeedrunCommands.attackDragon(player);
            });
        });

        // Register server-side receiver for StartAutoSpeedrunPacket
        ServerPlayNetworking.registerGlobalReceiver(StartAutoSpeedrunPacket.PACKET_ID, (server, player, handler, buf, responseSender) -> {
            server.execute(() -> {
                // Ensure this is run on the server thread
                AutoSpeedrunCommands.handleAutoSpeedrunCommand(player);
            });
        });

        // Register Advancement Event Handler
        AdvancementProgressChangeEvent.EVENT.register((playerAdvancementTracker, serverPlayerEntity, advancement, criterionName) -> {
            AutoSpeedrunCommands.onAdvancementProgress(playerAdvancementTracker, serverPlayerEntity, advancement, criterionName);
        });

        DRAGON_PACIFIED = Criteria.register(new DragonPacifiedCriterion());
        LOGGER.info("Registered custom advancement criteria.");

        UseEntityCallback.EVENT.register((player, world, hand, entity, hitResult) -> {
            if (!world.isClient() && player instanceof ServerPlayerEntity && entity instanceof EnderDragonEntity) { // Ensure server-side
                ServerPlayerEntity serverPlayer = (ServerPlayerEntity) player;
                ItemStack stackInHand = player.getStackInHand(hand);

                // Check if auto speedrun is active and at stage 5, and player is holding the placeholder item (stick)
                if (AutoSpeedrunCommands.getIsAutoSpeedrunActive() &&
                    AutoSpeedrunCommands.getCurrentStage() == 5 &&
                    stackInHand.isOf(Items.STICK)) {

                    // Trigger the custom advancement
                    DRAGON_PACIFIED.trigger(serverPlayer, stackInHand);

                    // Optionally, provide feedback or consume item (example)
                    // serverPlayer.sendMessage(Text.of("You used the item on the Dragon!"), false);
                    // stackInHand.decrement(1);
                    // world.playSound(null, player.getBlockPos(), SoundEvents.ENTITY_PLAYER_LEVELUP, SoundCategory.PLAYERS, 0.5f, 1.0f);

                    return ActionResult.SUCCESS; // Indicate success and prevent further processing
                }
            }
            return ActionResult.PASS; // Pass to allow normal interaction if conditions not met
        });

        LOGGER.info("WayaCreate Mode, Mob Army, and Auto Speedrun systems ready!");
    }
}
