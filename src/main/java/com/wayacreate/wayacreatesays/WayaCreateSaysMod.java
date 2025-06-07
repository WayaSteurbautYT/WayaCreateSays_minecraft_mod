package com.wayacreate.wayacreatesays;

import com.wayacreate.wayacreatesays.commands.AutoSpeedrunCommands;
import com.wayacreate.wayacreatesays.commands.WayaCreateCommand;
import com.wayacreate.wayacreatesays.game.WayaCreateGameMode;
import com.wayacreate.wayacreatesays.game.WayaCreateGameModeType;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.fabricmc.fabric.api.entity.event.living.LivingEvent;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerEntityEvents;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;
import net.fabricmc.fabric.api.event.player.PlayerBlockBreakEvents;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.Identifier;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class WayaCreateSaysMod implements ModInitializer {
    public static final String MOD_ID = "wayacreatesays";
    public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);
    public static MinecraftServer server;
    
    // Game mode identifier
    public static final Identifier WAYACREATE_GAMEMODE_ID = new Identifier(MOD_ID, "wayacreate");
    
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
        
        LOGGER.info("WayaCreate Mode, Mob Army, and Auto Speedrun systems ready!");
    }
}
