package com.wayacreate.wayacreatesays.entity.ai.goal;

import net.minecraft.entity.ai.goal.Goal;
import net.minecraft.entity.mob.PiglinBrain; // For potential future interaction checks
import net.minecraft.entity.mob.PiglinEntity;
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

public class OfferGiftToWorshippedPlayerPiglinGoal extends Goal {
    private final PiglinEntity piglin;
    private PlayerEntity targetPlayer;
    // private int executionCooldown; // Kept for potential future use
    private int offerGiftCooldown;
    private static final int MAX_OFFER_GIFT_COOLDOWN_BASE = 1800; // e.g., 1.5 minutes
    private static final int MAX_OFFER_GIFT_COOLDOWN_RANDOM = 1800; // e.g., additional 1.5 minutes random

    public OfferGiftToWorshippedPlayerPiglinGoal(PiglinEntity piglin) {
        this.piglin = piglin;
        this.setControls(EnumSet.of(Goal.Control.MOVE, Goal.Control.LOOK));
        // Initial random cooldown: 0 to MAX_OFFER_GIFT_COOLDOWN_BASE / 2
        this.offerGiftCooldown = piglin.getWorld().random.nextInt(MAX_OFFER_GIFT_COOLDOWN_BASE / 2);
    }

    @Override
    public boolean canStart() {
        if (this.piglin.isBaby() || !this.piglin.isAdult()) { // Must be an adult
            return false;
        }
        // Consider Piglin specific states:
        // if (this.piglin.getBrain().hasActivity(Activity.ADMIRE_ITEM) || this.piglin.getBrain().hasActivity(Activity.RIDE_HOGLIN) etc.) {
        // return false;
        // }
        // Piglins might not offer gifts if zombifying
        if (this.piglin.isConvertingToZombified()) {
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
        return this.targetPlayer != null &&
               this.targetPlayer.isAlive() &&
               this.piglin.isAdult() && // Ensure still an adult
               !this.piglin.isConvertingToZombified() && // Stop if starts zombifying
               this.piglin.squaredDistanceTo(this.targetPlayer) < 256.0; // 16*16 blocks
    }

    @Override
    public void start() {
        // this.executionCooldown = 0; // Reset if used
        // Potential: Interrupt admiring behavior if this goal is more important for worshipped player
        // this.piglin.getBrain().forget(MemoryModuleType.ADMIRING_ITEM);
        this.piglin.getNavigation().startMovingTo(this.targetPlayer, 0.85D); // Slightly faster than villager
    }

    @Override
    public void stop() {
        this.targetPlayer = null;
        this.piglin.getNavigation().stop();
        this.offerGiftCooldown = MAX_OFFER_GIFT_COOLDOWN_BASE +
                                 piglin.getWorld().random.nextInt(MAX_OFFER_GIFT_COOLDOWN_RANDOM);
    }

    @Override
    public void tick() {
        if (this.targetPlayer == null) {
            return;
        }

        this.piglin.getLookControl().lookAt(this.targetPlayer, 30.0F, 30.0F);

        if (this.piglin.getNavigation().isIdle()) {
            if (this.piglin.squaredDistanceTo(this.targetPlayer) > 9.0) { // 3*3 blocks for interaction
                this.piglin.getNavigation().startMovingTo(this.targetPlayer, 0.85D);
            }
        }

        if (this.piglin.squaredDistanceTo(this.targetPlayer) <= 9.0) {
            offerGift();
            this.targetPlayer = null;
        }
        // this.executionCooldown = Math.max(0, this.executionCooldown -1); // Decrement if used
    }

    private PlayerEntity findWorshippedPlayer() {
        if (this.piglin.getWorld().isClient) {
            return null;
        }

        // Use world.getPlayers() as this goal is not directly tied to Brain memory modules yet
        List<PlayerEntity> players = this.piglin.getWorld().getPlayers();
        for (PlayerEntity player : players) {
            if (player.getScoreboardTags().contains("is_piglin_worshipped") &&
                this.piglin.canSee(player) && // Basic visibility
                this.piglin.isInRange(player, 16.0)) { // Check range (16.0 for distance, 256.0 for squaredDistance)

                // If the Piglin is already attacking this player (for reasons other than no gold),
                // then don't try to offer a gift. The PiglinBrainMixin handles the "no gold" case.
                if (PiglinBrain.isAttacking(this.piglin, player)) {
                    continue; // Skip this player if already hostile for other reasons
                }
                return player;
            }
        }
        return null;
    }

    private void offerGift() {
        if (!(this.piglin.getWorld() instanceof ServerWorld)) {
            return;
        }
        ServerWorld serverWorld = (ServerWorld) this.piglin.getWorld();

        Identifier lootTableId = new Identifier("wcs", "gameplay/piglin_worship_gifts");
        LootTable lootTable = serverWorld.getServer().getLootManager().getLootTable(lootTableId);

        LootContextParameterSet lootContextParameterSet = new LootContextParameterSet.Builder(serverWorld)
            .add(LootContextParameters.THIS_ENTITY, this.piglin)
            .add(LootContextParameters.ORIGIN, this.piglin.getPos())
            .build(LootContextTypes.GIFT);

        List<ItemStack> generatedLoot = lootTable.generateLoot(lootContextParameterSet);

        if (!generatedLoot.isEmpty()) {
            for (ItemStack stack : generatedLoot) {
                // Piglins have a specific way of "giving" items, often by throwing.
                // PiglinBrain.throwItems(this.piglin, List.of(stack)); might be more thematic.
                // For now, simple dropStack.
                this.piglin.dropStack(stack, 0.5f);
            }
            this.piglin.playSound(SoundEvents.ENTITY_PIGLIN_ADMIRING_ITEM, 1.0F, 1.0F);
            // Could also trigger Activity.ADMIRE_ITEM if they gave something valuable (like gold)
            // this.piglin.getBrain().remember(MemoryModuleType.ADMIRING_ITEM, true, ADMIRING_DURATION);
        }
    }
}
