package com.wayacreate.wayacreatesays.mixin;

import com.wayacreate.wayacreatesays.entity.ai.goal.AllyMinerGoal;
import com.wayacreate.wayacreatesays.entity.ai.goal.AllyCrafterGoal;
import com.wayacreate.wayacreatesays.entity.ai.goal.AllyBodyguardGoal; // Added import
import net.minecraft.entity.EntityType;
import net.minecraft.entity.mob.HostileEntity;
import net.minecraft.entity.mob.PathAwareEntity;
import net.minecraft.entity.mob.ZombieEntity;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ZombieEntity.class)
public abstract class ZombieEntityMixin extends HostileEntity {
    protected ZombieEntityMixin(EntityType<? extends HostileEntity> entityType, World world) {
        super(entityType, world);
    }

    @Inject(method = "initGoals", at = @At("TAIL"))
    protected void wayacreate_addZombieCustomGoals(CallbackInfo ci) {
        // Zombies are PathAwareEntity, so this cast is safe.
        // goalSelector is initialized in MobEntity, a superclass of HostileEntity.
        PathAwareEntity self = (PathAwareEntity)this;
        // Priority 6, can be adjusted. Zombie attack goals are usually higher priority (e.g., 2-4).
        self.goalSelector.add(6, new AllyMinerGoal(self));

        // Add AllyCrafterGoal
        // Priority 7, lower than mining and typical attack/wander goals.
        self.goalSelector.add(7, new AllyCrafterGoal(self));

        // Add AllyBodyguardGoal
        // Priority 4, to be higher than Miner (6) and Crafter (7), but potentially lower than core attack goals (e.g. ZombieAttackGoal is 2)
        // If it's too high, it might override their self-preservation or basic zombie behaviors.
        // If too low, they might not protect effectively.
        self.goalSelector.add(4, new AllyBodyguardGoal(self));
    }
}
