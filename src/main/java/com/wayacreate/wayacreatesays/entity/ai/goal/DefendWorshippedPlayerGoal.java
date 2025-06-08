package com.wayacreate.wayacreatesays.entity.ai.goal;

import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.TargetPredicate;
import net.minecraft.entity.ai.goal.TrackTargetGoal;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.entity.monster.Monster; // General interface for hostile mobs
import net.minecraft.entity.passive.IronGolemEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.math.Box;
import java.util.EnumSet;
import java.util.List;

public class DefendWorshippedPlayerGoal extends TrackTargetGoal {
    private final IronGolemEntity golem;
    private PlayerEntity worshippedPlayer;
    // targetEntity is inherited from TrackTargetGoal as 'this.target' (protected LivingEntity target)
    // but we'll use a local one for clarity during selection, then set this.target via this.mob.setTarget()

    // Predicate to further filter potential targets for the golem
    private final TargetPredicate attackableTargetPredicate;

    public DefendWorshippedPlayerGoal(IronGolemEntity golem) {
        // super(PathAwareEntity mob, boolean checkVisibility)
        // checkVisibility = true: canTrack checks for visibility.
        super(golem, true);
        this.golem = golem;
        this.setControls(EnumSet.of(Goal.Control.TARGET));
        // Configure the predicate for entities the golem should attack
        this.attackableTargetPredicate = TargetPredicate.createAttackable().setBaseMaxDistance(32.0D);
    }

    @Override
    public boolean canStart() {
        // 1. Find a worshipped player nearby
        this.worshippedPlayer = findWorshippedPlayerNearby();
        if (this.worshippedPlayer == null) {
            return false;
        }

        // 2. Find a suitable target to attack that is threatening the worshipped player
        LivingEntity potentialThreat = findThreatToWorshippedPlayer();
        if (potentialThreat == null) {
            return false;
        }

        // Set the chosen threat as the target for the golem
        // The 'target' field in TrackTargetGoal is protected.
        // We set it via this.mob.setTarget() in start() or use the followTarget method.
        // For canStart, we just need to confirm a target *can* be set.
        // TrackTargetGoal's start method uses this.target (which is this.targetEntity from its perspective)
        // Let's assign to the inherited 'target' field if accessible, or pass to start()
        // this.target = potentialThreat; // This would be ideal if 'target' was directly settable here
        // Instead, we store it locally and use it in start().
        // The 'target' field in TrackTargetGoal is called 'targetEntity' in some mappings, 'target' in others.
        // It's 'protected LivingEntity target;' in official Mojang mappings.
        // We can use the inherited 'this.target = potentialThreat;'
        this.target = potentialThreat; // Set the target for TrackTargetGoal's logic
        return true; // A valid threat to the worshipped player has been found
    }

    private LivingEntity findThreatToWorshippedPlayer() {
        // Search for entities around the worshipped player
        Box searchBox = new Box(this.worshippedPlayer.getBlockPos()).expand(16.0D);
        List<LivingEntity> potentialTargets = this.golem.getWorld().getEntitiesByClass(LivingEntity.class, searchBox, (entity) ->
            entity.isAlive() && // Must be alive
            entity != this.golem && // Not the golem itself
            entity != this.worshippedPlayer && // Not the worshipped player
            (entity instanceof Monster || // Is a monster (typically hostile)
             (entity instanceof MobEntity && ((MobEntity)entity).getTarget() == this.worshippedPlayer)) // Or is any mob targeting the player
        );

        LivingEntity closestThreat = null;
        double minDistanceSq = Double.MAX_VALUE;

        for (LivingEntity potentialTarget : potentialTargets) {
            // Check if the golem can actually target this entity (visibility, range from golem, etc.)
            // using the predicate we configured.
            if (this.attackableTargetPredicate.test(this.golem, potentialTarget) && this.golem.getVisibilityCache().canSee(potentialTarget)) {
                double distSq = this.golem.squaredDistanceTo(potentialTarget);
                if (distSq < minDistanceSq) {
                    minDistanceSq = distSq;
                    closestThreat = potentialTarget;
                }
            }
        }
        return closestThreat;
    }

    @Override
    public void start() {
        // 'this.target' should have been set in canStart() if it returned true.
        // The mob (golem) will now target 'this.target'.
        this.mob.setTarget(this.target);
        super.start(); // Important to call super.start() for TrackTargetGoal
    }

    @Override
    public boolean shouldContinue() {
        // Continue if golem still has a target, worshipped player is valid, and target is still a threat.
        if (this.worshippedPlayer == null || !this.worshippedPlayer.isAlive()) {
            return false; // Worshipped player lost or dead
        }

        LivingEntity currentMobTarget = this.mob.getTarget();
        if (currentMobTarget == null || !currentMobTarget.isAlive()) {
            return false; // Target lost or dead
        }

        // If target is no longer a threat (e.g., stopped targeting player and is not a Monster)
        // This can get complex. For now, if it's not a Monster, and not targeting the player, maybe stop.
        if (!(currentMobTarget instanceof Monster) &&
            (!(currentMobTarget instanceof MobEntity) || ((MobEntity)currentMobTarget).getTarget() != this.worshippedPlayer)) {
            // If it's not inherently a monster and it's not targeting the player anymore
            return false;
        }

        // TrackTargetGoal's shouldContinue checks if target is still valid and within follow range.
        return super.shouldContinue();
    }

    @Override
    public void stop() {
        super.stop(); // Clears this.mob.setTarget(null) among other things
        this.worshippedPlayer = null;
        // this.target is cleared by super.stop() because it calls this.mob.setTarget(null)
        // and TrackTargetGoal.shouldContinue() checks this.mob.getTarget()
    }

    private PlayerEntity findWorshippedPlayerNearby() {
        List<PlayerEntity> players = this.golem.getWorld().getPlayers(); // Consider getPlayers(TargetPredicate) for efficiency
        for (PlayerEntity player : players) {
            if (player.getScoreboardTags().contains("is_villager_worshipped") &&
                this.golem.squaredDistanceTo(player) < 400.0) { // Within 20 blocks (20*20=400)
                if (this.golem.getVisibilityCache().canSee(player)) { // Golem should see the player it's defending
                     return player;
                }
            }
        }
        return null;
    }
}
