package com.wayacreate.wayacreatesays.entity.ai.goal;

import net.minecraft.block.Blocks;
import net.minecraft.entity.ItemEntity;
import net.minecraft.entity.ai.goal.Goal;
import net.minecraft.entity.mob.PathAwareEntity;
import net.minecraft.inventory.CraftingInventory; // For recipe matching (though needs dummy)
import net.minecraft.inventory.Inventory; // For recipe interface
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtElement;
import net.minecraft.recipe.*;
import net.minecraft.screen.PlayerScreenHandler; // For dummy CraftingInventory
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.Hand;
import net.minecraft.util.collection.DefaultedList;
import net.minecraft.util.math.BlockPos;
import java.util.ArrayList;
import java.util.Collections;
import java.util.EnumSet;
import java.util.List;
import java.util.Optional;
// import java.util.stream.Collectors; // Not used in current version

public class AllyCrafterGoal extends Goal {
    private final PathAwareEntity mob;
    private ItemStack recipeOutputToCraft; // What the mob aims to craft
    private CraftingRecipe locatedRecipe; // The recipe being used
    private List<ItemEntity> claimedIngredientEntities; // ItemEntities claimed for the recipe

    private BlockPos craftingTablePos;
    private int craftingTicks;
    private static final int MAX_CRAFTING_TICKS = 100;
    private int idleTicks;
    private enum State { SCANNING, MOVING_TO_TABLE, CRAFTING, FINISHED }
    private State currentState;

    public AllyCrafterGoal(PathAwareEntity mob) {
        this.mob = mob;
        this.setControls(EnumSet.of(Goal.Control.MOVE, Goal.Control.LOOK));
        this.idleTicks = mob.getWorld().random.nextInt(200) + 200; // Start with 10-20s idle
        this.currentState = State.SCANNING;
        this.claimedIngredientEntities = new ArrayList<>();
    }

    private boolean hasCrafterRole() {
        NbtCompound nbt = new NbtCompound();
        this.mob.writeNbt(nbt);
        if (nbt.contains("WCS_AllyData", NbtElement.COMPOUND_TYPE)) {
            NbtCompound allyData = nbt.getCompound("WCS_AllyData");
            return "CRAFTER".equals(allyData.getString("Role"));
        }
        return false;
    }

    @Override
    public boolean canStart() {
        if (!hasCrafterRole() || this.mob.isSleeping() || !this.mob.getNavigation().isIdle()) {
            return false;
        }
        if (idleTicks > 0 && currentState == State.SCANNING) {
            idleTicks--;
            return false;
        }
        if (currentState != State.SCANNING) return false;

        return findCraftingTask(); // Sets recipeOutputToCraft, locatedRecipe, claimedIngredientEntities
    }

    @Override
    public boolean shouldContinue() {
        return hasCrafterRole() && !this.mob.isSleeping() && currentState != State.FINISHED && currentState != State.SCANNING;
    }

    @Override
    public void start() {
        this.craftingTicks = 0;
        if (this.locatedRecipe != null && !this.claimedIngredientEntities.isEmpty()) {
            this.craftingTablePos = findCraftingTable();
            if (this.craftingTablePos != null) {
                this.currentState = State.MOVING_TO_TABLE;
                this.mob.getNavigation().startMovingTo(this.craftingTablePos.getX() + 0.5D,
                                                     this.craftingTablePos.getY(),
                                                     this.craftingTablePos.getZ() + 0.5D, 0.7D); // Speed
            } else {
                // No table found, reset and wait
                stop();
            }
        } else {
             // Should not happen if canStart was true
            stop();
        }
    }

    @Override
    public void stop() {
        this.mob.getNavigation().stop();
        this.recipeOutputToCraft = null;
        this.locatedRecipe = null;
        this.claimedIngredientEntities.clear();
        this.craftingTablePos = null;
        this.craftingTicks = 0;
        this.currentState = State.SCANNING;
        this.idleTicks = 400 + this.mob.getWorld().random.nextInt(400); // Cooldown 20-40s
    }

    @Override
    public void tick() {
        switch (currentState) {
            case MOVING_TO_TABLE:
                if (this.craftingTablePos == null) { stop(); return; } // Should have been caught by start()
                this.mob.getLookControl().lookAt(this.craftingTablePos.getX() + 0.5D, this.craftingTablePos.getY() + 0.5D, this.craftingTablePos.getZ() + 0.5D);
                if (this.mob.getBlockPos().isWithinDistance(this.craftingTablePos, 2.8D)) { // Slightly larger dist for table
                    currentState = State.CRAFTING;
                    this.craftingTicks = 0;
                } else if (this.mob.getNavigation().isIdle()) {
                     this.mob.getNavigation().startMovingTo(this.craftingTablePos.getX() + 0.5D,
                                                         this.craftingTablePos.getY(),
                                                         this.craftingTablePos.getZ() + 0.5D, 0.7D);
                }
                break;
            case CRAFTING:
                if (this.craftingTablePos == null || !this.mob.getWorld().getBlockState(this.craftingTablePos).isOf(Blocks.CRAFTING_TABLE)) {
                    // Crafting table broken or missing
                    stop(); return;
                }
                this.craftingTicks++;
                this.mob.swingHand(Hand.MAIN_HAND);
                if (this.craftingTicks % 25 == 0) { // Sound every 1.25s
                    this.mob.getWorld().playSound(null, this.craftingTablePos, SoundEvents.UI_STONECUTTER_TAKE_RESULT, this.mob.getSoundCategory(), 0.6f, 1.2f);
                }
                if (this.craftingTicks >= MAX_CRAFTING_TICKS) {
                    performCrafting();
                    currentState = State.FINISHED;
                }
                break;
            default: // SCANNING or FINISHED, do nothing in tick, handled by canStart/shouldContinue/stop
                break;
        }
    }

    private boolean findCraftingTask() {
        // For simplicity, player drops a "template" item (e.g., Stone Pickaxe)
        // And also drops all required ingredients nearby.
        List<ItemEntity> nearbyItemEntities = this.mob.getWorld().getEntitiesByClass(ItemEntity.class,
            this.mob.getBoundingBox().expand(10.0D), itemEntity -> !itemEntity.cannotPickup() && itemEntity.isOnGround());

        if (nearbyItemEntities.isEmpty()) return false;

        ItemEntity templateItemEntity = null;
        // Example: Look for a Stone Pickaxe as a template
        for (ItemEntity ie : nearbyItemEntities) {
            if (ie.getStack().isOf(Items.STONE_PICKAXE)) {
                templateItemEntity = ie;
                break;
            }
            // Could expand this to look for other pre-defined "requestable" items
        }

        if (templateItemEntity == null) return false;

        ItemStack potentialRecipeOutput = templateItemEntity.getStack().copy();
        potentialRecipeOutput.setCount(1);

        // Find a recipe that produces this item
        CraftingRecipe foundRecipe = null;
        for (Recipe<?> r : this.mob.getWorld().getRecipeManager().values()) {
            if (r.getType() == RecipeType.CRAFTING && !r.getOutput(this.mob.getWorld().getRegistryManager()).isEmpty()) {
                 if (ItemStack.areItemsEqual(r.getOutput(this.mob.getWorld().getRegistryManager()), potentialRecipeOutput) &&
                     ItemStack.areNbtEqual(r.getOutput(this.mob.getWorld().getRegistryManager()), potentialRecipeOutput) ){ // Check NBT too if template has it
                    foundRecipe = (CraftingRecipe) r;
                    break;
                }
            }
        }

        if (foundRecipe == null) return false;

        // Check if all ingredients are available from the nearby ItemEntities
        DefaultedList<Ingredient> requiredIngredients = foundRecipe.getIngredients();
        List<ItemEntity> availableForRecipe = new ArrayList<>(nearbyItemEntities);
        availableForRecipe.remove(templateItemEntity); // Template item itself is not an ingredient here

        List<ItemEntity> ingredientsToConsume = new ArrayList<>();
        boolean allIngredientsFound = true;

        for (Ingredient requiredIng : requiredIngredients) {
            if (requiredIng.isEmpty()) continue; // Skip empty slots in shaped recipes

            boolean foundThisIngredient = false;
            for (int i = availableForRecipe.size() - 1; i >= 0; i--) {
                ItemEntity availableEntity = availableForRecipe.get(i);
                if (requiredIng.test(availableEntity.getStack())) {
                    ingredientsToConsume.add(availableEntity);
                    availableForRecipe.remove(i); // "Claim" this item stack
                    foundThisIngredient = true;
                    break;
                }
            }
            if (!foundThisIngredient) {
                allIngredientsFound = false;
                break;
            }
        }

        if (allIngredientsFound) {
            this.recipeOutputToCraft = foundRecipe.getOutput(this.mob.getWorld().getRegistryManager()).copy(); // What will actually be crafted
            this.locatedRecipe = foundRecipe;
            this.claimedIngredientEntities.clear();
            this.claimedIngredientEntities.addAll(ingredientsToConsume);
            // templateItemEntity.discard(); // Consume the template item that initiated the craft
            return true;
        }
        return false;
    }

    private BlockPos findCraftingTable() {
        BlockPos mobPos = this.mob.getBlockPos();
        for (int y = -3; y <= 3; y++) {
            for (int x = -10; x <= 10; x++) {
                for (int z = -10; z <= 10; z++) {
                    BlockPos currentPos = mobPos.add(x, y, z);
                    if (this.mob.getWorld().getBlockState(currentPos).isOf(Blocks.CRAFTING_TABLE)) {
                        // Check path availability? For now, just find it.
                        return currentPos;
                    }
                }
            }
        }
        return null; // No crafting table found nearby
    }

    private void performCrafting() {
        if (this.recipeOutputToCraft == null || this.claimedIngredientEntities.isEmpty() || !(this.mob.getWorld() instanceof ServerWorld)) {
            return;
        }

        // Consume ingredients (decrement count of ItemEntities, discard if empty)
        for (ItemEntity ingredientEntity : this.claimedIngredientEntities) {
            ingredientEntity.getStack().decrement(1);
            if(ingredientEntity.getStack().isEmpty()){
                ingredientEntity.discard();
            }
        }

        this.mob.dropStack(this.recipeOutputToCraft.copy(), 0.5f);
        this.mob.playSound(SoundEvents.ENTITY_VILLAGER_WORK_FLETCHER, 1.0F, 1.0F); // Using FLETCHER as example craft sound
        // Could also play block sound of crafting table: SoundEvents.UI_CARTOGRAPHY_TABLE_TAKE_RESULT
    }
}
