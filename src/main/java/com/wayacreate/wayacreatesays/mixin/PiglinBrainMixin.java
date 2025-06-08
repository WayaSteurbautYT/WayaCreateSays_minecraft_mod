package com.wayacreate.wayacreatesays.mixin;

import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.mob.PiglinBrain;
// No PiglinEntity import needed here as we're just checking the target.
import net.minecraft.entity.player.PlayerEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(PiglinBrain.class)
public abstract class PiglinBrainMixin {

    @Inject(method = "isPlayerNotWearingGold(Lnet/minecraft/entity/LivingEntity;)Z", at = @At("HEAD"), cancellable = true)
    private static void wayacreate_makeWorshippedPlayerNonHostile(LivingEntity target, CallbackInfoReturnable<Boolean> cir) {
        if (target instanceof PlayerEntity) {
            PlayerEntity player = (PlayerEntity) target;
            if (player.getScoreboardTags().contains("is_piglin_worshipped")) {
                // If the player is worshipped by piglins, treat them as if they are wearing gold.
                // This means isPlayerNotWearingGold should return false for them.
                cir.setReturnValue(false);
            }
        }
    }

    // Further considerations for full non-hostility (deferred to future tasks):
    // - Other triggers for Piglin aggression (e.g., opening chests, mining gold blocks).
    //   These are typically handled by different checks or behaviors in PiglinBrain.
    //   Each would need a similar Mixin if the worshipped player should be exempt.
    // - Preventing Zombified Piglins from being angered if a worshipped player attacks a Piglin.
    //   This involves `PiglinBrain.onGuardedBlockInteracted` or `ZombifiedPiglinEntity.setAngerTime`
    //   and related anger propagation logic.
}
