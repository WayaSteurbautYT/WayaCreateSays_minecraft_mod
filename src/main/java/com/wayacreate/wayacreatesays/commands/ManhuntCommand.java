package com.wayacreate.wayacreatesays.commands;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld; // Added for getSpeedrunner
import net.minecraft.scoreboard.Scoreboard;
import net.minecraft.scoreboard.Team;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.entity.player.PlayerEntity; // Added for helper methods
import net.minecraft.item.ItemStack; // Added for giving compass
import net.minecraft.item.Items; // Added for compass item

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
// import java.util.UUID; // Not used for speedrunnerUUID currently
// import java.util.stream.Collectors; // Not used in current version

import static net.minecraft.server.command.CommandManager.literal;
// Optional: import argument for specifying speedrunner
// import static net.minecraft.server.command.CommandManager.argument;
// import net.minecraft.command.argument.EntityArgumentType;


public class ManhuntCommand {
    private static final String SPEEDRUNNER_TEAM_NAME = "Speedrunners";
    private static final String MANHUNTER_TEAM_NAME = "Manhunters";
    private static boolean manhuntActive = false;
    // private static UUID speedrunnerUUID = null; // Not actively used, team membership is key

    public static void register(CommandDispatcher<ServerCommandSource> dispatcher) {
        dispatcher.register(literal("manhunt")
            .requires(source -> source.hasPermissionLevel(2)) // OP command
            .then(literal("start")
                // Future: .then(argument("runner", EntityArgumentType.player()) ...)
                .executes(ManhuntCommand::startGame)
            )
            .then(literal("stop")
                .executes(ManhuntCommand::stopGameCommand) // Changed to stopGameCommand
            )
        );
    }

    private static Team getOrCreateTeam(Scoreboard scoreboard, String teamName, Formatting color) {
        Team team = scoreboard.getTeam(teamName);
        if (team == null) {
            team = scoreboard.addTeam(teamName);
            team.setDisplayName(Text.literal(teamName));
            team.setColor(color);
            team.setFriendlyFireAllowed(false);
            team.setCollisionRule(Team.CollisionRule.PUSH_OTHER_TEAMS);
        }
        return team;
    }

    private static void clearTeams(Scoreboard scoreboard) {
        Team speedrunnerTeam = scoreboard.getTeam(SPEEDRUNNER_TEAM_NAME);
        if (speedrunnerTeam != null) {
            List<String> playersInTeam = new ArrayList<>(speedrunnerTeam.getPlayerList());
            for (String playerName : playersInTeam) {
                scoreboard.removePlayerFromTeam(playerName, speedrunnerTeam);
            }
        }
        Team manhunterTeam = scoreboard.getTeam(MANHUNTER_TEAM_NAME);
        if (manhunterTeam != null) {
            List<String> playersInTeam = new ArrayList<>(manhunterTeam.getPlayerList());
             for (String playerName : playersInTeam) {
                scoreboard.removePlayerFromTeam(playerName, manhunterTeam);
            }
        }
    }

    private static int startGame(CommandContext<ServerCommandSource> context) throws CommandSyntaxException {
        ServerCommandSource source = context.getSource();
        if (manhuntActive) {
            source.sendError(Text.literal("WayaCreate says: Manhunt is already active!"));
            return 0;
        }

        List<ServerPlayerEntity> players = new ArrayList<>(source.getServer().getPlayerManager().getPlayerList()); // Mutable list
        if (players.size() < 2) {
            source.sendError(Text.literal("WayaCreate says: Not enough players for Manhunt (minimum 2 required)."));
            return 0;
        }

        Scoreboard scoreboard = source.getServer().getScoreboard();
        clearTeams(scoreboard);

        Team speedrunnerTeam = getOrCreateTeam(scoreboard, SPEEDRUNNER_TEAM_NAME, Formatting.GREEN);
        Team manhunterTeam = getOrCreateTeam(scoreboard, MANHUNTER_TEAM_NAME, Formatting.RED);

        Collections.shuffle(players);
        ServerPlayerEntity speedrunner = players.get(0);

        scoreboard.addPlayerToTeam(speedrunner.getEntityName(), speedrunnerTeam);
        speedrunner.sendMessage(Text.literal("WayaCreate says: You are the Speedrunner! Good luck!").formatted(Formatting.GREEN), false);

        for (int i = 1; i < players.size(); i++) {
            ServerPlayerEntity hunter = players.get(i);
            scoreboard.addPlayerToTeam(hunter.getEntityName(), manhunterTeam);
            hunter.sendMessage(Text.literal("WayaCreate says: You are a Manhunter! Hunt them down!").formatted(Formatting.RED), false);

            // Give compass if they don't have one
            boolean hasCompass = false;
            for (int j = 0; j < hunter.getInventory().size(); j++) {
                if (hunter.getInventory().getStack(j).isOf(Items.COMPASS)) {
                    hasCompass = true;
                    break;
                }
            }
            if (!hasCompass) {
                hunter.getInventory().insertStack(new ItemStack(Items.COMPASS));
                hunter.sendMessage(Text.literal("WayaCreate says: Here's a compass to track the Speedrunner!").formatted(Formatting.GOLD), true); // Action bar
            }
        }

        manhuntActive = true;
        source.getServer().getPlayerManager().broadcast(
            Text.literal("WayaCreate says: Manhunt has started! Speedrunner: " + speedrunner.getName().getString()).formatted(Formatting.YELLOW), false);
        return 1;
    }

    // Renamed from stopGame to avoid conflict with a potential static stopGame(MinecraftServer)
    private static int stopGameCommand(CommandContext<ServerCommandSource> context) {
        ServerCommandSource source = context.getSource();
        if (!isManhuntActive()) { // Use the getter for consistency
            source.sendError(Text.literal("WayaCreate says: Manhunt is not currently active."));
            return 0;
        }
        triggerStopGame(source.getServer());
        source.sendFeedback(() -> Text.literal("WayaCreate says: Manhunt stopped by command."), true); // Command specific feedback
        return 1;
    }

    public static void triggerStopGame(MinecraftServer server) {
        if (!manhuntActive) {
            // Optional: log or handle if called when not active, though usually checked by caller
            return;
        }

        Scoreboard scoreboard = server.getScoreboard();
        clearTeams(scoreboard); // clearTeams is static

        manhuntActive = false;
        // speedrunnerUUID = null; // If using this

        // Generic announcement. Specific win/loss messages handled by event listeners.
        server.getPlayerManager().broadcast(
            Text.literal("WayaCreate says: Manhunt has concluded!").formatted(Formatting.YELLOW), false);
    }

    public static boolean isPlayerSpeedrunner(PlayerEntity player) {
        if (!manhuntActive || player == null || player.getWorld().isClient()) return false; // Check world side
        Team team = player.getScoreboardTeam();
        return team != null && team.getName().equals(SPEEDRUNNER_TEAM_NAME);
    }

    public static boolean isPlayerManhunter(PlayerEntity player) {
        if (!manhuntActive || player == null || player.getWorld().isClient()) return false; // Check world side
        Team team = player.getScoreboardTeam();
        return team != null && team.getName().equals(MANHUNTER_TEAM_NAME);
    }

    public static boolean isManhuntActive() {
        return manhuntActive;
    }

    public static ServerPlayerEntity getSpeedrunner(ServerWorld world) {
        if (!manhuntActive) return null;
        Scoreboard scoreboard = world.getServer().getScoreboard(); // Use getServer() from world
        Team speedrunnerTeam = scoreboard.getTeam(SPEEDRUNNER_TEAM_NAME);
        if (speedrunnerTeam != null && !speedrunnerTeam.getPlayerList().isEmpty()) {
            // Assuming only one speedrunner
            String speedrunnerName = speedrunnerTeam.getPlayerList().iterator().next();
            return world.getServer().getPlayerManager().getPlayer(speedrunnerName);
        }
        return null;
    }
}
