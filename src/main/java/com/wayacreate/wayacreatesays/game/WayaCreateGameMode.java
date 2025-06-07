package com.wayacreate.wayacreatesays.game;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.world.GameMode;
import net.minecraft.world.World;

public class WayaCreateGameMode extends GameMode {
    public static final WayaCreateGameMode INSTANCE = new WayaCreateGameMode();
    
    private WayaCreateGameMode() {
        super("wayacreate", GameMode.CREATIVE);
    }
    
    @Override
    public void setWorld(World world) {
        // Custom world setup if needed
    }
    
    @Override
    public void updatePlayerAbilities(PlayerEntity player) {
        // Enable flight and other abilities
        player.getAbilities().allowFlying = true;
        player.getAbilities().flying = true;
        player.getAbilities().setFlySpeed(0.1f);
        player.getAbilities().setWalkSpeed(0.2f);
        player.sendAbilitiesUpdate();
    }
    
    @Override
    public boolean isSurvivalLike() {
        return false;
    }
    
    @Override
    public boolean isCreative() {
        return true;
    }
    
    @Override
    public boolean hasExperienceBar() {
        return false;
    }
    
    @Override
    public boolean hasLimitedInteractions() {
        return false;
    }
    
    @Override
    public boolean isBlockBreakingRestricted(PlayerEntity player) {
        return false;
    }
    
    @Override
    public boolean isPvpDisabled() {
        return false;
    }
    
    @Override
    public boolean shouldDamagePlayer(PlayerEntity player) {
        return false;
    }
    
    @Override
    public boolean shouldDispense(boolean bl) {
        return true;
    }
    
    @Override
    public boolean shouldDropLoot() {
        return true;
    }
    
    @Override
    public boolean shouldInteract(PlayerEntity player) {
        return true;
    }
    
    @Override
    public boolean shouldModifyWorld() {
        return true;
    }
    
    @Override
    public void setGameMode(ServerPlayerEntity player) {
        // Set player abilities and game mode properties
        player.getAbilities().allowFlying = true;
        player.getAbilities().flying = true;
        player.getAbilities().allowModifyWorld = true;
        player.getAbilities().invulnerable = true;
        player.sendAbilitiesUpdate();
        
        // Send game mode change message
        player.sendMessage(Text.literal("§6§lWayaCreate Mode §e- Voice commands enabled! Try saying 'WayaCreate says help'"));
    }
    
    @Override
    public Text getSimpleTranslatableName() {
        return Text.translatable("gameMode.wayacreate");
    }
    
    @Override
    public boolean isSurvivalLike() {
        return true;
    }
    
    @Override
    public boolean isCreative() {
        return true;
    }
    
    @Override
    public boolean hasExperienceBar() {
        return true;
    }
    
    @Override
    public boolean hasLimitedResources() {
        return false;
    }
    
    @Override
    public int getId() {
        return 5; // Make sure this ID is unique and not used by other mods
    }
}

// Helper class to register the game mode
class WayaCreateGameModeType {
    public static final GameMode WAYACREATE = new WayaCreateGameMode();
    
    public static void register() {
        // Register the game mode with the game mode registry
        GameMode.register("wayacreate", WAYACREATE);
    }
}
