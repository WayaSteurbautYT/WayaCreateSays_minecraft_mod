package com.wayacreate.wayacreatesays.mixin;

import com.wayacreate.wayacreatesays.entity.ai.goal.AllyMinerGoal;
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
    }
}
