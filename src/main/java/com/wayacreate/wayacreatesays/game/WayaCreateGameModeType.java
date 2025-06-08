package com.wayacreate.wayacreatesays.game;

import com.wayacreate.wayacreatesays.WayaCreateSaysMod;
import net.minecraft.entity.attribute.EntityAttributeModifier;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.attribute.EntityAttributes.GENERIC_MAX_HEALTH;
import net.minecraft.entity.attribute.EntityAttributes.GENERIC_MOVEMENT_SPEED;
import net.minecraft.entity.attribute.EntityAttributeModifier.Operation;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import net.minecraft.world.GameMode;

public class WayaCreateGameModeType {
    public static final GameMode WAYACREATE_GAMEMODE = WayaCreateGameMode.INSTANCE;
    
    public static void register() {
        // Register our game mode
        Registry.register(
            Registries.GAME_MODE,
            WayaCreateSaysMod.WAYACREATE_GAMEMODE_ID,
            WAYACREATE_GAMEMODE
        );
        
        WayaCreateSaysMod.LOGGER.info("Registered WayaCreate game mode");
    }
    
    public static void applyGameModeEffects(PlayerEntity player) {
        if (!(player instanceof ServerPlayerEntity)) return;
        
        ServerPlayerEntity serverPlayer = (ServerPlayerEntity) player;
        
        // Apply game mode specific effects
        if (serverPlayer.interactionManager.getGameMode() == WAYACREATE_GAMEMODE) {
            // Enhanced abilities
            if (player.getAttributeInstance(GENERIC_MAX_HEALTH) != null) {
                player.getAttributeInstance(GENERIC_MAX_HEALTH).addTemporaryModifier(
                    new EntityAttributeModifier("wayacreate_health_boost", 10.0, Operation.ADDITION)
                );
            }
            
            if (player.getAttributeInstance(GENERIC_MOVEMENT_SPEED) != null) {
                player.getAttributeInstance(GENERIC_MOVEMENT_SPEED).addTemporaryModifier(
                    new EntityAttributeModifier("wayacreate_speed_boost", 0.1, Operation.MULTIPLY_TOTAL)
                );
            }
            
            player.setHealth(player.getMaxHealth());
            
            // Enable flight
            player.getAbilities().allowFlying = true;
            player.getAbilities().flying = true;
            player.sendAbilitiesUpdate();
            
            // Send welcome message
            player.sendMessage(Text.translatable("gameMode.wayacreate.welcome"));

            // Execute datapack function
            if (serverPlayer.getServer() != null) { // Ensure server is not null
                serverPlayer.getServer().getCommandManager().executeWithPrefix(
                    serverPlayer.getCommandSource().withSilent(),
                    "function wcs:game_control/init_wayacreate_mode"
                );
                WayaCreateSaysMod.LOGGER.info("Executed init_wayacreate_mode function for player " + serverPlayer.getName().getString());
            } else {
                WayaCreateSaysMod.LOGGER.warn("Could not execute init_wayacreate_mode function: serverPlayer.getServer() is null for " + serverPlayer.getName().getString());
            }
        }
    }
}
