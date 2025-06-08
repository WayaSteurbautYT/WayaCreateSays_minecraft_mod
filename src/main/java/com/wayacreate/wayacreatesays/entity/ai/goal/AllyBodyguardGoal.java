package com.wayacreate.wayacreatesays.entity.ai.goal;

import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.goal.Goal;
import net.minecraft.entity.ai.TargetPredicate;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.entity.mob.PathAwareEntity;
import net.minecraft.entity.monster.Monster;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtElement;
import net.minecraft.util.Hand;
import net.minecraft.util.math.Box;
// import net.minecraft.server.world.ServerWorld; // Not directly used, world is available via mob.getWorld()
import java.util.UUID;
import java.util.EnumSet;
import java.util.List;
import java.util.Comparator;

public class AllyBodyguardGoal extends Goal {
    private final PathAwareEntity mob;
    private PlayerEntity owner;
    private LivingEntity currentTarget;
    private int attackCooldownTicks;
    private static final int FOLLOW_DISTANCE_SQ_MIN = 16; // 4*4 blocks
    private static final int FOLLOW_DISTANCE_SQ_MAX = 100; // 10*10 blocks, was 64 (8*8)
    private int targetSearchCooldownTicks;

    private final TargetPredicate attackableTargetPredicate;

    public AllyBodyguardGoal(PathAwareEntity mob) {
        this.mob = mob;
        this.setControls(EnumSet.of(Goal.Control.MOVE, Goal.Control.LOOK, Goal.Control.TARGET));
        this.targetSearchCooldownTicks = 0;
        // Define what kind of entities this bodyguard can attack.
        // Exclude other players unless specific conditions met (e.g. Manhunt mode - future)
        // For now, allow attacking any LivingEntity that is a Monster or attacking the owner.
        this.attackableTargetPredicate = TargetPredicate.createAttackable().setBaseMaxDistance(32.0D)
            .setPredicate(target -> target != this.owner && !(target instanceof PlayerEntity && !isHostilePlayer(player, (PlayerEntity)target)) );
            // The PlayerEntity check is complex, defer full PVP rules. For now, mainly monsters.
    }

    // Helper to define if a player target is hostile (e.g. in manhunt, or pvp enabled etc.)
    // For now, assume bodyguard does not attack other players unless they attack owner.
    private boolean isHostilePlayer(PlayerEntity bodyguardOwner, PlayerEntity potentialTarget) {
        // TODO: Implement logic for Manhunt teams or other PVP conditions
        return false; // By default, don't target other players
    }


    private boolean hasBodyguardRole() {
        NbtCompound nbt = new NbtCompound();
        this.mob.writeNbt(nbt);
        if (nbt.contains("WCS_AllyData", NbtElement.COMPOUND_TYPE)) {
            NbtCompound allyData = nbt.getCompound("WCS_AllyData");
            return "BODYGUARD".equals(allyData.getString("Role"));
        }
        return false;
    }

    private PlayerEntity getOwner() {
        if (this.owner != null && this.owner.isAlive()) return this.owner; // Cache owner if still valid

        NbtCompound nbt = new NbtCompound();
        this.mob.writeNbt(nbt);
        if (nbt.contains("OwnerUUID")) {
            try {
                UUID ownerUuid = UUID.fromString(nbt.getString("OwnerUUID"));
                return this.mob.getWorld().getPlayerByUuid(ownerUuid);
            } catch (IllegalArgumentException e) {
                // Invalid UUID format
                return null;
            }
        }
        return null;
    }

    @Override
    public boolean canStart() {
        if (!hasBodyguardRole() || this.mob.isSleeping()) return false;

        this.owner = getOwner();
        if (this.owner == null || !this.owner.isAlive()) return false;

        if (this.targetSearchCooldownTicks > 0) {
            this.targetSearchCooldownTicks--;
        } else {
            this.targetSearchCooldownTicks = 20 + this.mob.getWorld().random.nextInt(20); // Search every 1-2 seconds

            // 1. Prioritize owner's attacker
            LivingEntity ownerAttacker = this.owner.getAttacker();
            if (ownerAttacker != null && ownerAttacker.isAlive() && this.mob.canTarget(ownerAttacker) && this.attackableTargetPredicate.test(this.mob, ownerAttacker)) {
                this.currentTarget = ownerAttacker;
                return true;
            }

            // 2. If owner safe, look for nearby monsters to proactively engage
            // Search around the owner
            Box searchBox = this.owner.getBoundingBox().expand(16.0D, 8.0D, 16.0D);
            List<Monster> monsters = this.mob.getWorld().getEntitiesByClass(Monster.class, searchBox,
                monster -> this.mob.canTarget(monster) && this.attackableTargetPredicate.test(this.mob, monster) && monster.canSee(this.owner) && !monster.isTeammate(this.owner));

            if (!monsters.isEmpty()) {
                monsters.sort(Comparator.comparingDouble(m -> m.squaredDistanceTo(this.owner)));
                this.currentTarget = monsters.get(0);
                return true;
            }
        }

        // 3. If no target from above, check if we need to follow owner
        if (this.mob.squaredDistanceTo(this.owner) > FOLLOW_DISTANCE_SQ_MAX) {
            this.currentTarget = null; // Ensure no lingering combat target if just following
            return true;
        }

        // Check if current target is still valid (could have been set by targetSearchCooldown logic)
        return this.currentTarget != null && this.currentTarget.isAlive() && this.mob.canTarget(this.currentTarget);
    }

    @Override
    public boolean shouldContinue() {
        if (this.owner == null || !this.owner.isAlive() || !hasBodyguardRole() || this.mob.isSleeping()) {
            return false;
        }

        if (this.currentTarget != null) { // Actively targeting something
            if (!this.currentTarget.isAlive() || !this.mob.canTarget(this.currentTarget)) {
                this.currentTarget = null; // Target lost or invalid
                this.mob.setTarget(null);
                // Don't return false immediately, canStart will be called again by GoalSelector
                // and might decide to follow owner or find a new target.
                // To force re-evaluation or switch to follow, this should effectively mean canStart needs to run.
                // The goal system will re-evaluate canStart if shouldContinue is false.
                // If currentTarget becomes null, the next canStart will determine if we follow or find new target.
                return false; // Force re-evaluation by canStart
            }
            return true; // Continue attacking current target
        }

        // If no current target, should we continue (to follow)?
        // Yes, if we are too far or if canStart finds a new target.
        // This state (no currentTarget but goal continues) means we are in "follow owner" mode.
        // Or, if canStart() finds a new target, it will set currentTarget and this will be true.
        return this.mob.squaredDistanceTo(this.owner) > FOLLOW_DISTANCE_SQ_MIN || canStart();
    }

    @Override
    public void start() {
        this.attackCooldownTicks = 0;
        if (this.currentTarget != null) {
            this.mob.setTarget(this.currentTarget);
            this.mob.getNavigation().startMovingTo(this.currentTarget, 1.05D); // Slightly faster for attacking
        } else if (this.owner != null) {
            this.mob.setTarget(null);
            this.mob.getNavigation().startMovingTo(this.owner, 0.8D);
        }
    }

    @Override
    public void stop() {
        this.mob.setTarget(null);
        this.currentTarget = null; // Clear specific target for this goal
        this.mob.getNavigation().stop();
        // Owner remains cached for next canStart
    }

    @Override
    public void tick() {
        if (this.owner == null || !this.owner.isAlive()) {
            this.mob.setTarget(null); // Ensure mob stops targeting if owner is gone
            this.currentTarget = null;
            return; // Owner lost, goal should stop soon via shouldContinue
        }

        this.mob.getLookControl().lookAt(this.currentTarget != null && this.currentTarget.isAlive() ? this.currentTarget : this.owner,
                                       30.0F, 30.0F);

        if (this.currentTarget != null) {
            if (!this.currentTarget.isAlive() || !this.mob.canTarget(this.currentTarget)) {
                this.currentTarget = null;
                this.mob.setTarget(null);
                // Re-evaluate next tick (canStart will run if shouldContinue returns false due to this)
                return;
            }

            this.mob.getNavigation().startMovingTo(this.currentTarget, 1.05D);
            // Check if within melee attack range
            if (this.mob.squaredDistanceTo(this.currentTarget) < getAttackIntervalRangeSq(this.currentTarget)) {
                if (this.attackCooldownTicks <= 0) {
                    this.mob.swingHand(Hand.MAIN_HAND);
                    this.mob.tryAttack(this.currentTarget);
                    this.attackCooldownTicks = 20;
                }
            }
        } else { // No current combat target, so follow owner
            this.mob.getNavigation().startMovingTo(this.owner, 0.8D);
            if (this.mob.squaredDistanceTo(this.owner) < FOLLOW_DISTANCE_SQ_MIN && this.mob.getNavigation().isIdle()) {
                // If very close and idle, stop active pathfinding to prevent jittering.
                // Could also make it wander around owner. For now, just stop.
                 this.mob.getNavigation().stop();
            }
        }

        if (this.attackCooldownTicks > 0) {
            this.attackCooldownTicks--;
        }
    }

    protected double getAttackIntervalRangeSq(LivingEntity target) {
        // MobEntity.getSquaredAttackDistance is protected. Replicate logic or use approximation.
        // Approximate based on widths: (mob.width * 2 + target.width)^2 is too large.
        // Attack range is typically mob width + target width + some fixed reach.
        // Let's use a value like (mob.width + target.width + 0.5)^2 or a fixed value.
        // A common melee range is about 2-3 blocks. Squared: 4-9.
        // For simplicity and consistency:
        return Math.pow(this.mob.getWidth() + target.getWidth() + 1.0D, 2.0D); // +1 block reach
        // Or a fixed value like 9.0 (3 blocks) can work for many mobs.
        // For now, this dynamic calculation is fine.
    }
}
