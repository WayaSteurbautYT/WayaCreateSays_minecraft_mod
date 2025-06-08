package com.wayacreate.wayacreatesays.voice;

import com.wayacreate.wayacreatesays.WayaCreateSaysMod;
import com.wayacreate.wayacreatesays.WayaCreateSaysMod;
import com.wayacreate.wayacreatesays.commands.GameCommands;
import com.wayacreate.wayacreatesays.commands.MobCommands;
import com.wayacreate.wayacreatesays.commands.SpeedrunCommands;
import com.wayacreate.wayacreatesays.commands.AutoSpeedrunCommands;
import com.wayacreate.wayacreatesays.network.ClientNetworking; // Added import

import net.minecraft.entity.player.PlayerEntity; // For AutoSpeedrunCommands and SpeedrunCommands if they still need it
import java.util.List; // For AutoSpeedrunCommands if it still needs it
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
            ClientNetworking.sendFunctionCommand("wcs:guidance/stage_0_intro");
        } 
        else if (matchesAny(command, 
            "make a crafting table", 
            "craft a crafting table",
            "give me a crafting table")) {
            ClientNetworking.sendFunctionCommand("wcs:game_control/make_crafting_table");
        } 
        else if (matchesAny(command, 
            "mine wood for me", 
            "chop some wood",
            "get me some wood")) {
            ClientNetworking.sendFunctionCommand("wcs:game_control/mine_wood");
        }
        else if (matchesAny(command, 
            "teleport to spawn",
            "go to spawn",
            "take me to spawn")) {
            ClientNetworking.sendFunctionCommand("wcs:game_control/teleport_spawn");
        }
        else if (matchesAny(command, 
            "give me diamonds",
            "i want diamonds",
            "get me diamonds")) {
            ClientNetworking.sendFunctionCommand("wcs:game_control/give_diamonds_cheat");
        }
        // Mob commands
        else if (matchesAny(command, 
            "become an ally to monsters",
            "make friends with monsters",
            "monsters be my friend")) {
            ClientNetworking.sendFunctionCommand("wcs:mob_control/become_ally_to_monsters");
        } 
        else if (matchesAny(command, 
            "kill that player",
            "eliminate that player",
            "take out that player")) {
            ClientNetworking.sendFunctionCommand("wcs:mob_control/pvp_target_dummy");
        } 
        else if (matchesAny(command, 
            "dance",
            "let's dance",
            "dance party")) {
            ClientNetworking.sendFunctionCommand("wcs:mob_control/dance");
        } 
        else if (matchesAny(command, 
            "time stop",
            "stop time",
            "freeze time")) {
            ClientNetworking.sendFunctionCommand("wcs:game_control/time_stop");
        }
        else if (matchesAny(command, 
            "make villagers trade",
            "trade with villagers",
            "villagers give me stuff")) {
            ClientNetworking.sendFunctionCommand("wcs:mob_control/make_villagers_trade");
        }
        // Speedrun commands
        else if (matchesAny(command, 
            "find ",
            "where is ",
            "locate ")) {
            Matcher findMatcher = SPEEDRUN_FIND_PATTERN.matcher(command);
            if (findMatcher.find()) {
                String item = findMatcher.group(1).toLowerCase();
                if (item.equals("stronghold")) {
                    ClientNetworking.sendFunctionCommand("wcs:game_control/find_stronghold");
                } else {
                    // Retain existing behavior for other "find" commands (text help)
                    // This needs review due to getPlayer() removal.
                    WayaCreateSaysMod.LOGGER.info("Original find command for: " + item + " - needs review due to getPlayer() removal. SpeedrunCommands.handleSpeedrunCommand would have been called.");
                    // If SpeedrunCommands can work without PlayerEntity for text help, that's ideal.
                }
            } else {
                WayaCreateSaysMod.LOGGER.info("Please specify what to find. (Original sendMessage call '§cPlease specify what to find. Example: 'Simon says find diamonds'')");
            }
        }
        else if (matchesAny(command, 
            "craft ",
            "how to craft ",
            "recipe for ")) {
            Matcher craftMatcher = SPEEDRUN_CRAFT_PATTERN.matcher(command);
            if (craftMatcher.find()) {
                String item = craftMatcher.group(1);
                // SpeedrunCommands.handleSpeedrunCommand("craft " + item, getPlayer());
                WayaCreateSaysMod.LOGGER.info("Original craft command for: " + item + " - needs review due to getPlayer() removal. SpeedrunCommands.handleSpeedrunCommand would have been called.");
            } else {
                // sendMessage("§cI can help you craft items. Try 'Simon says craft pickaxe'");
                WayaCreateSaysMod.LOGGER.info("I can help you craft items. (Original sendMessage call '§cI can help you craft items. Try 'Simon says craft pickaxe'')");
            }
        }
        else if (matchesAny(command, 
            "nether portal",
            "build portal",
            "make nether portal")) {
            ClientNetworking.sendFunctionCommand("wcs:game_control/build_nether_portal");
        }
        else if (matchesAny(command, 
            "prepare for nether",
            "nether preparation",
            "what do i need for nether")) {
            // SpeedrunCommands.handleSpeedrunCommand("nether", getPlayer());
            WayaCreateSaysMod.LOGGER.info("Original nether prep command - needs review due to getPlayer() removal. SpeedrunCommands.handleSpeedrunCommand would have been called.");
        }
        else if (matchesAny(command, 
            "find fortress",
            "locate fortress",
            "where is nether fortress")) {
            // SpeedrunCommands.handleSpeedrunCommand("fortress", getPlayer());
            WayaCreateSaysMod.LOGGER.info("Original find fortress command - needs review due to getPlayer() removal. SpeedrunCommands.handleSpeedrunCommand would have been called.");
        }
        else if (matchesAny(command, 
            "find stronghold",
            "locate stronghold",
            "where is stronghold")) {
            ClientNetworking.sendFunctionCommand("wcs:game_control/find_stronghold");
        }
        else if (matchesAny(command, 
            "prepare for dragon",
            "dragon fight",
            "ender dragon preparation")) {
            // SpeedrunCommands.handleSpeedrunCommand("dragon", getPlayer());
            WayaCreateSaysMod.LOGGER.info("Original dragon prep command - needs review due to getPlayer() removal. SpeedrunCommands.handleSpeedrunCommand would have been called.");
        }
        // Auto Speedrun Commands
        else if (matchesAny(command, 
            "auto speedrun",
            "start speedrun",
            "begin speedrun")) {
            // As per instructions, call the datapack for guidance AND trigger Java logic via packet.
            ClientNetworking.sendFunctionCommand("wcs:guidance/stage_0_intro");
            ClientNetworking.sendStartAutoSpeedrunPacket();
            // The log about AutoSpeedrunCommands.handleAutoSpeedrunCommand is removed as it's now triggered by the packet.
        }
        else if (matchesAny(command, 
            "stop",
            "stop speedrun",
            "cancel speedrun")) {
            ClientNetworking.sendStopAutoSpeedrunPacket();
        }
        else if (matchesAny(command, 
            "attack dragon",
            "kill dragon",
            "charge")) {
            ClientNetworking.sendAttackDragonPacket();
        }
        // Automate commands
        else if (matchesAny(command,
            "automate mining",
            "villagers mine for me",
            "get villagers to mine")) {
            ClientNetworking.sendFunctionCommand("wcs:game_control/automate_mining_stub");
        }
        else if (matchesAny(command,
            "automate crafting",
            "villagers craft for me",
            "get villagers to craft")) {
            ClientNetworking.sendFunctionCommand("wcs:game_control/automate_crafting_stub");
        }
        // Pet commands
        else {
            Matcher petMatcher = PET_PATTERN.matcher(command);
            if (petMatcher.find()) {
                String petType = petMatcher.group(2).toLowerCase();
                switch (petType) {
                    case "wolf":
                    case "dog": // Assuming "dog" means "wolf"
                        ClientNetworking.sendFunctionCommand("wcs:mob_control/summon_pet_wolf");
                        break;
                    case "cat":
                        ClientNetworking.sendFunctionCommand("wcs:mob_control/summon_pet_cat");
                        break;
                    case "fox":
                        ClientNetworking.sendFunctionCommand("wcs:mob_control/summon_pet_fox");
                        break;
                    default:
                        WayaCreateSaysMod.LOGGER.warn("Unknown pet type: " + petType);
                        break;
                }
            } else {
                WayaCreateSaysMod.LOGGER.warn("Unknown command: " + command);
                if (command.length() > 0) { // Check if command is not empty
                     WayaCreateSaysMod.LOGGER.info("Unknown command received: " + command + " (Original sendMessage: §cI don't understand that command. Try 'Simon says auto speedrun' to start an epic adventure!)");
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
    // getPlayer() and sendMessage() removed.
    // PlayerEntity and List imports are kept for now as AutoSpeedrunCommands and SpeedrunCommands might still use them.
    // They will be cleaned up if those classes are simplified enough.
}
