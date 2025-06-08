package com.wayacreate.wayacreatesays.entity.ai.goal;

import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.goal.Goal;
import net.minecraft.entity.passive.VillagerEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.loot.LootTable;
import net.minecraft.loot.context.LootContextParameterSet;
import net.minecraft.loot.context.LootContextParameters;
import net.minecraft.loot.context.LootContextTypes;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.Identifier;
import java.util.EnumSet;
import java.util.List;

public class OfferGiftToWorshippedPlayerGoal extends Goal {
    private final VillagerEntity villager;
    private PlayerEntity targetPlayer;
    // private int executionCooldown; // Not strictly necessary with current logic, but can be kept for future use
    private int offerGiftCooldown; // Ticks until next gift attempt
    private static final int MAX_OFFER_GIFT_COOLDOWN_BASE = 1200; // e.g., 1 minute (1200 ticks)
    private static final int MAX_OFFER_GIFT_COOLDOWN_RANDOM = 1200; // e.g., additional 1 minute random

    public OfferGiftToWorshippedPlayerGoal(VillagerEntity villager) {
        this.villager = villager;
        this.setControls(EnumSet.of(Goal.Control.MOVE, Goal.Control.LOOK));
        // Initial random cooldown: 0 to MAX_OFFER_GIFT_COOLDOWN_BASE / 2 to allow quicker first gift
        this.offerGiftCooldown = villager.getWorld().random.nextInt(MAX_OFFER_GIFT_COOLDOWN_BASE / 2);
    }

    @Override
    public boolean canStart() {
        if (this.villager.isSleeping() || this.villager.isBaby()) {
            return false;
        }
        if (this.offerGiftCooldown > 0) {
            this.offerGiftCooldown--;
            return false;
        }

        this.targetPlayer = findWorshippedPlayer();
        return this.targetPlayer != null;
    }

    @Override
    public boolean shouldContinue() {
        // Check if target is still valid and villager is capable
        return this.targetPlayer != null &&
               this.targetPlayer.isAlive() &&
               !this.villager.isSleeping() &&
               this.villager.squaredDistanceTo(this.targetPlayer) < 256.0; // Within 16*16 blocks
    }

    @Override
    public void start() {
        // this.executionCooldown = 0; // Reset if used
        this.villager.getNavigation().startMovingTo(this.targetPlayer, 0.6D);
    }

    @Override
    public void stop() {
        this.targetPlayer = null;
        this.villager.getNavigation().stop();
        // Reset cooldown: base + random additional
        this.offerGiftCooldown = MAX_OFFER_GIFT_COOLDOWN_BASE +
                                 villager.getWorld().random.nextInt(MAX_OFFER_GIFT_COOLDOWN_RANDOM);
    }

    @Override
    public void tick() {
        if (this.targetPlayer == null) {
            return; // Should not happen if shouldContinue is implemented correctly
        }

        this.villager.getLookControl().lookAt(this.targetPlayer, 30.0F, 30.0F);

        if (this.villager.getNavigation().isIdle()) {
            // If navigation stopped (e.g. reached destination or failed) but not close enough, try again or stop.
            if (this.villager.squaredDistanceTo(this.targetPlayer) > 9.0) { // 3*3 blocks for interaction range
                 // If too far, try to move closer again. If already trying, this won't do much.
                 // Consider adding a timeout for pathfinding attempts if it gets stuck.
                this.villager.getNavigation().startMovingTo(this.targetPlayer, 0.6D);
            }
        }

        // Offer gift only if close enough
        if (this.villager.squaredDistanceTo(this.targetPlayer) <= 9.0) { // Within 3 blocks
            offerGift();
            // Setting targetPlayer to null will cause shouldContinue to return false, leading to stop() being called.
            this.targetPlayer = null;
        }
        // this.executionCooldown = Math.max(0, this.executionCooldown -1); // Decrement if used
    }

    private PlayerEntity findWorshippedPlayer() {
        if (this.villager.getWorld().isClient) { // Should not run on client, but good practice
            return null;
        }
        // Consider limiting how often this search runs if it's expensive.
        // For now, it runs when offerGiftCooldown is 0.
        List<? extends PlayerEntity> players = this.villager.getWorld().getPlayers();
        for (PlayerEntity player : players) {
            if (player.getScoreboardTags().contains("is_villager_worshipped") &&
                this.villager.canSee(player) && // Check for visibility
                this.villager.squaredDistanceTo(player) < 256.0) { // Within 16 blocks (256 = 16*16)
                return player;
            }
        }
        return null;
    }

    private void offerGift() {
        if (!(this.villager.getWorld() instanceof ServerWorld)) {
            return;
        }
        ServerWorld serverWorld = (ServerWorld) this.villager.getWorld();

        Identifier lootTableId = new Identifier("wcs", "gameplay/villager_worship_gifts");
        LootTable lootTable = serverWorld.getServer().getLootManager().getLootTable(lootTableId);

        // Using GIFT context type
        LootContextParameterSet lootContextParameterSet = new LootContextParameterSet.Builder(serverWorld)
            .add(LootContextParameters.THIS_ENTITY, this.villager)
            .add(LootContextParameters.ORIGIN, this.villager.getPos())
            .build(LootContextTypes.GIFT);

        List<ItemStack> generatedLoot = lootTable.generateLoot(lootContextParameterSet);

        for (ItemStack stack : generatedLoot) {
            this.villager.dropStack(stack, 0.5f); // Drop towards player slightly offset
        }

        if (!generatedLoot.isEmpty()) { // Play sound only if a gift was actually given
            this.villager.playSound(SoundEvents.ENTITY_VILLAGER_YES, 1.0F, 1.0F);
        }
        // Cooldown is reset in stop() which is called implicitly after targetPlayer is set to null
    }
}
