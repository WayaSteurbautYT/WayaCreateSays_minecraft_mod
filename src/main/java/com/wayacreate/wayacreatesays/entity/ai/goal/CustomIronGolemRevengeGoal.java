package com.wayacreate.wayacreatesays.entity.ai.goal;

import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.goal.RevengeGoal;
import net.minecraft.entity.mob.PathAwareEntity;
import net.minecraft.entity.passive.IronGolemEntity;
import net.minecraft.entity.player.PlayerEntity;
// No need for TargetPredicate if not calling canTrack directly with it.

public class CustomIronGolemRevengeGoal extends RevengeGoal {
    // private final IronGolemEntity golem; // 'this.mob' can be cast to IronGolemEntity if needed

    public CustomIronGolemRevengeGoal(IronGolemEntity golem) {
        super(golem, new Class[0]); // Match vanilla IronGolem's RevengeGoal constructor by passing empty Class array
        // this.golem = golem; // Not strictly necessary as 'this.mob' is the golem
    }

    @Override
    public boolean canStart() {
        // super.canStart() checks if mob.getAttacker() is valid and sets 'this.target' (protected field in RevengeGoal)
        // if conditions like revengeTimer and canTrack are met.
        if (!super.canStart()) {
            return false;
        }
        // 'this.target' is the entity that attacked the 'mob' (the golem).
        if (this.target instanceof PlayerEntity && ((PlayerEntity) this.target).getScoreboardTags().contains("is_villager_worshipped")) {
            return false; // Don't start revenge if the golem's attacker (this.target) is a worshipped player.
        }
        return true;
    }

    /**
     * This method is called by the static callForHelp method in RevengeGoal.
     * 'targetEntity' is the mob that should start targeting (our golem).
     * 'attacker' is the entity that attacked the mob that called for help (e.g., a Villager).
     */
    @Override
    protected void setMobEntityTarget(LivingEntity targetEntity, LivingEntity attacker) {
        // Note: In vanilla RevengeGoal, 'targetEntity' is 'this.mob' (our golem).
        // So, 'this.mob' is 'targetEntity'.
        if (attacker instanceof PlayerEntity && ((PlayerEntity) attacker).getScoreboardTags().contains("is_villager_worshipped")) {
            // If the entity that attacked the villager (attacker) is a worshipped player,
            // then our golem (this.mob / targetEntity) should NOT target them.
            return;
        }
        // If the attacker is not a worshipped player, proceed with vanilla behavior.
        super.setMobEntityTarget(targetEntity, attacker);
    }

    @Override
    public boolean shouldContinue() {
        // this.mob.getTarget() is who the golem is actively targeting for this goal,
        // set either by this.start() or by setMobEntityTarget().
        LivingEntity currentTarget = this.mob.getTarget();

        if (currentTarget instanceof PlayerEntity && ((PlayerEntity) currentTarget).getScoreboardTags().contains("is_villager_worshipped")) {
            return false; // Stop retaliating if the current target is a worshipped player
        }
        return super.shouldContinue();
    }
}
