package com.wayacreate.wayacreatesays.mixin;

import com.wayacreate.wayacreatesays.entity.ai.goal.AllyMinerGoal;
import com.wayacreate.wayacreatesays.entity.ai.goal.AllyCrafterGoal;
import com.wayacreate.wayacreatesays.entity.ai.goal.AllyBodyguardGoal; // Added import
import net.minecraft.entity.EntityType;
import net.minecraft.entity.mob.AbstractSkeletonEntity;
import net.minecraft.entity.mob.PathAwareEntity;
import net.minecraft.entity.mob.SkeletonEntity;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(SkeletonEntity.class)
public abstract class SkeletonEntityMixin extends AbstractSkeletonEntity {
    protected SkeletonEntityMixin(EntityType<? extends AbstractSkeletonEntity> entityType, World world) {
        super(entityType, world);
    }

    @Inject(method = "initGoals", at = @At("TAIL"))
    protected void wayacreate_addSkeletonCustomGoals(CallbackInfo ci) {
        // Skeletons are PathAwareEntity.
        // goalSelector is initialized in MobEntity, a superclass of AbstractSkeletonEntity.
        PathAwareEntity self = (PathAwareEntity)this;
        // Priority 6. Skeleton ranged attack goal is typically around 4.
        self.goalSelector.add(6, new AllyMinerGoal(self));

        // Add AllyCrafterGoal
        // Priority 7, lower than mining and attack goals.
        self.goalSelector.add(7, new AllyCrafterGoal(self));

        // Add AllyBodyguardGoal
        // Priority 4. Skeletons have BowAttackGoal at priority 4.
        // This will make them melee bodyguard. If they should use bow, a different goal is needed.
        // This might compete or interleave with their BowAttackGoal if target matches.
        self.goalSelector.add(4, new AllyBodyguardGoal(self));
    }
}
