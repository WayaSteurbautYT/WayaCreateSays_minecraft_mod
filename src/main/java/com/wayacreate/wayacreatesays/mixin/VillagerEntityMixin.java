package com.wayacreate.wayacreatesays.mixin;

import com.wayacreate.wayacreatesays.entity.ai.goal.OfferGiftToWorshippedPlayerGoal;
import com.wayacreate.wayacreatesays.entity.ai.goal.AllyMinerGoal;
import com.wayacreate.wayacreatesays.entity.ai.goal.AllyCrafterGoal; // Added import
import net.minecraft.entity.EntityType;
import net.minecraft.entity.mob.PathAwareEntity;
import net.minecraft.entity.passive.MerchantEntity;
import net.minecraft.entity.passive.VillagerEntity;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(VillagerEntity.class)
public abstract class VillagerEntityMixin extends MerchantEntity {
    // Required constructor for MerchantEntity superclass
    public VillagerEntityMixin(EntityType<? extends MerchantEntity> entityType, World world) {
        super(entityType, world);
    }

    @Inject(method = "initGoals", at = @At("TAIL"))
    protected void onInitGoals(CallbackInfo ci) {
        // `this` refers to the VillagerEntity instance.
        // We need to cast `this` to VillagerEntity because we are in a class that extends MerchantEntity,
        // but the goal specifically requires a VillagerEntity.
        // The (Object) cast is a common way to satisfy the compiler when dealing with mixin targets.
        VillagerEntity self = (VillagerEntity)(Object)this;
        self.goalSelector.add(5, new OfferGiftToWorshippedPlayerGoal(self));
        // Priority 5 is an example; it might need adjustment based on interactions with other villager goals.
        // Lower numbers are higher priority. Common villager goals:
        // 0: SwimGoal
        // 1: BreakDoorGoal (panic)
        // 1: PanicGoal
        // ...
        // 5: WanderAroundFarGoal / FollowCustomerGoal / LookAtCustomerGoal
        // 6: LookAtEntityGoal (Player)
        // 7: WanderAroundGoal
        // Placing it at 5 means it's reasonably important, contending with other interaction/work goals.

        // Add AllyMinerGoal. Villagers are PathAwareEntity.
        // Priority 6, could be adjusted. Villagers have work goals (like Farmer Villager working) around priority 3-5.
        // Making it slightly lower priority than gift offering and core job/interaction goals.
        self.goalSelector.add(6, new AllyMinerGoal(self));

        // Add AllyCrafterGoal
        // Priority 7, making it lower than general work/mining/gifting.
        self.goalSelector.add(7, new AllyCrafterGoal(self));
    }
}
