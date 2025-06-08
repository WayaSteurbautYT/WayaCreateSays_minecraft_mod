package com.wayacreate.wayacreatesays.mixin;

import com.wayacreate.wayacreatesays.entity.ai.goal.OfferGiftToWorshippedPlayerPiglinGoal;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.mob.AbstractPiglinEntity; // Correct superclass for PiglinEntity
import net.minecraft.entity.mob.PiglinEntity;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(PiglinEntity.class)
public abstract class PiglinEntityMixin extends AbstractPiglinEntity {
    // Constructor matching the superclass AbstractPiglinEntity
    protected PiglinEntityMixin(EntityType<? extends AbstractPiglinEntity> entityType, World world) {
        super(entityType, world);
    }

    @Inject(method = "initGoals", at = @At("TAIL"))
    protected void wayacreate_addCustomPiglinGoals(CallbackInfo ci) {
        // `this` refers to the PiglinEntity instance.
        // PiglinEntity inherits goalSelector from MobEntity -> LivingEntity.
        // We need to cast `this` to PiglinEntity for the goal constructor.
        PiglinEntity self = (PiglinEntity)(Object)this;

        // Ensure goalSelector is available (it should be, as PiglinEntity extends HostileEntity -> MobEntity)
        if (self.goalSelector != null) {
             self.goalSelector.add(5, new OfferGiftToWorshippedPlayerPiglinGoal(self));
             // Priority 5 is an example, similar to the Villager version.
             // This needs to be balanced with PiglinBrain activities if they also drive movement/interaction.
             // If GoalSelector and Brain clash, this might not work as expected or might be overridden by Brain.
        }
    }
}
