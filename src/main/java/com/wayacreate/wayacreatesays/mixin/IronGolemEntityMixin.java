package com.wayacreate.wayacreatesays.mixin;

import net.minecraft.entity.EntityType;
import net.minecraft.entity.ai.goal.RevengeGoal; // For referencing in comments
// Import custom goals when they are created, e.g.:
import com.wayacreate.wayacreatesays.entity.ai.goal.CustomIronGolemRevengeGoal; // Already imported from previous
import com.wayacreate.wayacreatesays.entity.ai.goal.DefendWorshippedPlayerGoal; // New import
import net.minecraft.entity.passive.GolemEntity;
import net.minecraft.entity.passive.IronGolemEntity;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(IronGolemEntity.class)
public abstract class IronGolemEntityMixin extends GolemEntity {
    protected IronGolemEntityMixin(EntityType<? extends GolemEntity> entityType, World world) {
        super(entityType, world);
    }

    @Inject(method = "initGoals", at = @At("TAIL"))
    protected void wayacreate_addCustomIronGolemGoals(CallbackInfo ci) {
        IronGolemEntity golem = (IronGolemEntity)(Object)this;

        // === Conceptual Plan for Iron Golem Allegiance Modification ===
        // This Mixin will be used to adjust Iron Golem AI to recognize and protect
        // players who have the "is_villager_worshipped" tag.

        // 1. Modify Existing Goals (e.g., RevengeGoal):
        //    - The default RevengeGoal makes Iron Golems attack entities that harm them or villagers.
        //    - This needs to be modified so the golem does NOT retaliate against a worshipped player,
        //      even if that player accidentally harms the golem or a villager (this part is tricky).
        //    - Approach ideas:
        //        a) Remove the default RevengeGoal and add a custom one (e.g., CustomRevengeGoal)
        //           that includes a check for the attacker's worship status.
        //           Example: this.targetSelector.removeGoal(existingRevengeGoalInstance); (Requires getting instance)
        //                    this.targetSelector.add(1, new CustomRevengeGoal(golem, ...));
        //        b) Use a higher-priority goal to make the golem peaceful towards the worshipped player,
        //           potentially by clearing its current target if it's the worshipped player.

        // 2. Add New Goals for Protecting Worshipped Player:
        //    - A new TargetGoal (e.g., DefendWorshippedPlayerGoal) should be added.
        //    - This goal would make the Iron Golem target entities that are hostile towards
        //      a nearby worshipped player.
        //    - "Hostile towards player" could mean:
        //        - Entity is actively targeting the worshipped player.
        //        - Entity is on a "Manhunter" team (if Manhunt mode is integrated).
        //        - Entity is a default hostile mob (Zombie, Skeleton) near the worshipped player.
        //    - Example: this.targetSelector.add(2, new DefendWorshippedPlayerGoal(golem, /* parameters */));

        // 3. Implementation Details for Future Subtasks:
        //    - Custom Goal Classes: Define `CustomRevengeGoal.java` and `DefendWorshippedPlayerGoal.java`.
        //    - Goal Priorities: Carefully select priorities to ensure desired behavior relative to vanilla goals.
        //    - Player Identification: Goals will need to scan for nearby players with the "is_villager_worshipped" tag.
        //    - Target Selection Logic: Implement robust logic within the new goals to select appropriate targets
        //      (e.g., actual attackers of the worshipped player) and to avoid targeting the worshipped player.

        // For this initial subtask, only this shell Mixin is created.
        // The actual AI modification logic will be implemented in subsequent subtasks.
        // Example of what might be done later (commented out):
        // Clear specific vanilla goals if necessary (use with caution):
        // GoalSelectorUtil.removeGoal(this.targetSelector, RevengeGoal.class);
      // this.targetSelector.add(1, new CustomRevengeGoal(golem)); // Corrected constructor
        // this.targetSelector.add(2, new DefendWorshippedPlayerGoal(golem));

        // Attempt to remove existing RevengeGoal instances from the targetSelector.
        // Iron Golems add their RevengeGoal with priority 2.
        boolean removedVanillaRevengeGoal = golem.targetSelector.getGoals().removeIf(prioritizedGoal -> prioritizedGoal.getGoal() instanceof RevengeGoal && !(prioritizedGoal.getGoal() instanceof CustomIronGolemRevengeGoal));

        // Add our custom revenge goal.
        golem.targetSelector.add(2, new CustomIronGolemRevengeGoal(golem));

        // Log if removal failed, as it might indicate conflict or need for different removal strategy
        if (!removedVanillaRevengeGoal) {
            // Consider logging a warning if another RevengeGoal was already there and not removed.
            // System.out.println("WayaCreateSays: Did not remove a vanilla RevengeGoal from IronGolem, custom one added.");
        }

        // Add DefendWorshippedPlayerGoal
        // Priority 1 makes it more important than the CustomRevengeGoal (priority 2)
        // and vanilla ActiveTargetGoal for monsters (usually priority 2 or 3).
        golem.targetSelector.add(1, new DefendWorshippedPlayerGoal(golem));
    }
}
