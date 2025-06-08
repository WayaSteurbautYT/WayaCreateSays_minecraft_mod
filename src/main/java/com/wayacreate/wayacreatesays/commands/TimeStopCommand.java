package com.wayacreate.wayacreatesays.commands;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.context.CommandContext;
import com.wayacreate.wayacreatesays.abilities.TimeStopAbility;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.text.Text;
import static net.minecraft.server.command.CommandManager.literal;

public class TimeStopCommand {
    public static void register(CommandDispatcher<ServerCommandSource> dispatcher) {
        dispatcher.register(literal("timestop")
            .requires(source -> source.hasPermissionLevel(2)) // OP level 2
            .executes(TimeStopCommand::toggleTimeStop)
        );
    }

    private static int toggleTimeStop(CommandContext<ServerCommandSource> context) {
        ServerCommandSource source = context.getSource();
        ServerWorld world = source.getWorld();

        if (TimeStopAbility.isTimeStopped()) {
            TimeStopAbility.stopTimeStop(world);
            // source.sendFeedback(() -> Text.literal("Time resumes its course."), true);
            // Message is now broadcast from TimeStopAbility
        } else {
            TimeStopAbility.startTimeStop(world);
            // source.sendFeedback(() -> Text.literal("Time has been stopped."), true);
            // Message is now broadcast from TimeStopAbility
        }
        // Send feedback to the command executor specifically, if desired, in addition to broadcast
        source.sendFeedback(() -> Text.literal("Time stop toggled."), true);
        return 1;
    }
}
