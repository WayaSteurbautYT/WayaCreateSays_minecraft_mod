package com.wayacreate.wayacreatesays.commands;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.context.CommandContext;
import com.wayacreate.wayacreatesays.WayaCreateSaysMod;
import com.wayacreate.wayacreatesays.game.WayaCreateGameModeType;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;

import static net.minecraft.server.command.CommandManager.literal;

public class WayaCreateCommand {
    
    public static void register(CommandDispatcher<ServerCommandSource> dispatcher) {
        dispatcher.register(
            literal("wayacreate")
                .requires(source -> source.hasPermissionLevel(2)) // Requires OP level 2
                .executes(WayaCreateCommand::setWayaCreateMode)
        );
        
        // Also register under /gamemode wayacreate
        dispatcher.register(
            literal("gamemode")
                .requires(source -> source.hasPermissionLevel(2))
                .then(literal("wayacreate")
                    .executes(context -> setWayaCreateMode(context.getSource().withLevel(2))))
        );

        // Register other commands
        TimeStopCommand.register(dispatcher);
        // If MobArmyCommands, AutoSpeedrunCommands etc. have static register methods, they could be called here too.
        // For now, only adding TimeStopCommand as per current subtask.
    }
    
    private static int setWayaCreateMode(CommandContext<ServerCommandSource> context) {
        return setWayaCreateMode(context.getSource());
    }
    
    private static int setWayaCreateMode(ServerCommandSource source) {
        ServerPlayerEntity player = source.getPlayer();
        
        if (player == null) {
            source.sendError(Text.literal("This command can only be used by players"));
            return 0;
        }
        
        // Set the player's game mode to WayaCreate Mode
        player.changeGameMode(WayaCreateGameModeType.WAYACREATE_GAMEMODE);
        
        // Apply game mode specific effects
        WayaCreateGameModeType.applyGameModeEffects(player);
        
        // Send success message
        player.sendMessage(
            Text.translatable("gameMode.set.self", 
                Text.translatable("gameMode.wayacreate")
            ).formatted(Formatting.GREEN)
        );
        
        // Broadcast to other players
        if (source.getWorld().getGameRules().getBoolean(net.minecraft.world.GameRules.SEND_COMMAND_FEEDBACK)) {
            source.getServer().getPlayerManager().broadcast(
                Text.translatable("gameMode.changed", 
                    source.getPlayer().getDisplayName(),
                    Text.translatable("gameMode.wayacreate")
                ),
                false
            );
        }
        
        return 1;
    }
}
