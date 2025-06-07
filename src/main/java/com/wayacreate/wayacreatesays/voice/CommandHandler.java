package com.wayacreate.wayacreatesays.voice;

import com.wayacreate.wayacreatesays.WayaCreateSaysMod;
import com.wayacreate.wayacreatesays.commands.GameCommands;
import com.wayacreate.wayacreatesays.commands.MobCommands;
import com.wayacreate.wayacreatesays.commands.SpeedrunCommands;
import com.wayacreate.wayacreatesays.commands.AutoSpeedrunCommands;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class CommandHandler {
    private static final Pattern PET_PATTERN = Pattern.compile("(summon|get|spawn) (?:me |a )?(wolf|cat|fox|dog)", Pattern.CASE_INSENSITIVE);
    private static final Pattern SPEEDRUN_FIND_PATTERN = Pattern.compile("find (diamonds|iron|blaze|endermen|lava|nether|fortress|stronghold)", Pattern.CASE_INSENSITIVE);
    private static final Pattern SPEEDRUN_CRAFT_PATTERN = Pattern.compile("craft (pickaxe|sword|bed|compass|bucket)", Pattern.CASE_INSENSITIVE);
    
    public static void handleCommand(String command) {
        WayaCreateSaysMod.LOGGER.info("Processing command: " + command);
        String originalCommand = command.trim();
        command = command.toLowerCase().trim();
        
        // Game commands
        if (matchesAny(command, 
            "help me beat the game", 
            "help me win",
            "how do i beat the game")) {
            GameCommands.helpBeatGame();
        } 
        else if (matchesAny(command, 
            "make a crafting table", 
            "craft a crafting table",
            "give me a crafting table")) {
            GameCommands.makeCraftingTable();
        } 
        else if (matchesAny(command, 
            "mine wood for me", 
            "chop some wood",
            "get me some wood")) {
            GameCommands.mineWood();
        }
        else if (matchesAny(command, 
            "teleport to spawn",
            "go to spawn",
            "take me to spawn")) {
            GameCommands.teleportToSpawn();
        }
        else if (matchesAny(command, 
            "give me diamonds",
            "i want diamonds",
            "get me diamonds")) {
            GameCommands.giveDiamonds();
        }
        // Mob commands
        else if (matchesAny(command, 
            "become an ally to monsters",
            "make friends with monsters",
            "monsters be my friend")) {
            MobCommands.allyWithMonsters();
        } 
        else if (matchesAny(command, 
            "kill that player",
            "eliminate that player",
            "take out that player")) {
            MobCommands.killNearestPlayer();
        } 
        else if (matchesAny(command, 
            "dance",
            "let's dance",
            "dance party")) {
            MobCommands.dance();
        } 
        else if (matchesAny(command, 
            "time stop",
            "stop time",
            "freeze time")) {
            MobCommands.timeStop();
        }
        else if (matchesAny(command, 
            "make villagers trade",
            "trade with villagers",
            "villagers give me stuff")) {
            MobCommands.makeVillagersTrade();
        }
        // Speedrun commands
        else if (matchesAny(command, 
            "find ",
            "where is ",
            "locate ")) {
            Matcher findMatcher = SPEEDRUN_FIND_PATTERN.matcher(command);
            if (findMatcher.find()) {
                String item = findMatcher.group(1);
                SpeedrunCommands.handleSpeedrunCommand("find " + item, getPlayer());
            } else {
                sendMessage("§cPlease specify what to find. Example: 'Simon says find diamonds'");
            }
        }
        else if (matchesAny(command, 
            "craft ",
            "how to craft ",
            "recipe for ")) {
            Matcher craftMatcher = SPEEDRUN_CRAFT_PATTERN.matcher(command);
            if (craftMatcher.find()) {
                String item = craftMatcher.group(1);
                SpeedrunCommands.handleSpeedrunCommand("craft " + item, getPlayer());
            } else {
                sendMessage("§cI can help you craft items. Try 'Simon says craft pickaxe'");
            }
        }
        else if (matchesAny(command, 
            "nether portal",
            "build portal",
            "make nether portal")) {
            SpeedrunCommands.handleSpeedrunCommand("portal", getPlayer());
        }
        else if (matchesAny(command, 
            "prepare for nether",
            "nether preparation",
            "what do i need for nether")) {
            SpeedrunCommands.handleSpeedrunCommand("nether", getPlayer());
        }
        else if (matchesAny(command, 
            "find fortress",
            "locate fortress",
            "where is nether fortress")) {
            SpeedrunCommands.handleSpeedrunCommand("fortress", getPlayer());
        }
        else if (matchesAny(command, 
            "find stronghold",
            "locate stronghold",
            "where is stronghold")) {
            SpeedrunCommands.handleSpeedrunCommand("stronghold", getPlayer());
        }
        else if (matchesAny(command, 
            "prepare for dragon",
            "dragon fight",
            "ender dragon preparation")) {
            SpeedrunCommands.handleSpeedrunCommand("dragon", getPlayer());
        }
        // Auto Speedrun Commands
        else if (matchesAny(command, 
            "auto speedrun",
            "start speedrun",
            "begin speedrun")) {
            AutoSpeedrunCommands.handleAutoSpeedrunCommand(getPlayer());
        }
        else if (matchesAny(command, 
            "stop",
            "stop speedrun",
            "cancel speedrun")) {
            AutoSpeedrunCommands.stopAutoSpeedrun(getPlayer());
        }
        else if (matchesAny(command, 
            "attack dragon",
            "kill dragon",
            "charge")) {
            AutoSpeedrunCommands.attackDragon(getPlayer());
        }
        // Pet commands
        else {
            Matcher petMatcher = PET_PATTERN.matcher(command);
            if (petMatcher.find()) {
                String petType = petMatcher.group(2);
                MobCommands.summonPet(petType);
            } else {
                WayaCreateSaysMod.LOGGER.warn("Unknown command: " + command);
                if (command.length() > 0) {
                    sendMessage("§cI don't understand that command. Try 'Simon says auto speedrun' to start an epic adventure!");
                }
            }
        }
    }
    
    private static boolean matchesAny(String input, String... patterns) {
        for (String pattern : patterns) {
            if (input.matches(".*" + Pattern.quote(pattern) + ".*")) {
                return true;
            }
        }
        return false;
    }
    
    private static void sendMessage(String message) {
        if (net.minecraft.client.MinecraftClient.getInstance().player != null) {
            net.minecraft.client.MinecraftClient.getInstance().player.sendMessage(
                net.minecraft.text.Text.of(message), false);
        }
    }
    
    private static PlayerEntity getPlayer() {
        // Get the server player entity
        if (WayaCreateSaysMod.server != null) {
            List<ServerPlayerEntity> players = WayaCreateSaysMod.server.getPlayerManager().getPlayerList();
            if (!players.isEmpty()) {
                return players.get(0); // Return first player in the list
            }
        }
        return null;
    }
}
