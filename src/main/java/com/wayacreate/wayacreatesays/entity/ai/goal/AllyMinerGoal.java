package com.wayacreate.wayacreatesays.entity.ai.goal;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.entity.ai.goal.Goal;
import net.minecraft.entity.mob.PathAwareEntity;
import net.minecraft.item.ItemStack;
// import net.minecraft.item.Items; // Not directly used, but good if checking for tools
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtElement; // For NbtCompound.COMPOUND_TYPE
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundEvents;
import net.minecraft.tag.BlockTags;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
// import net.minecraft.util.math.Vec3i; // Not directly used
import java.util.ArrayList;
import java.util.Collections;
import java.util.EnumSet;
import java.util.List;

public class AllyMinerGoal extends Goal {
    private final PathAwareEntity mob;
    private BlockPos targetBlockPos;
    private int miningTicks;
    private static final int MAX_MINING_TICKS = 80; // Approx 4 seconds, can be adjusted
    private int idleTicks;

    public AllyMinerGoal(PathAwareEntity mob) {
        this.mob = mob;
        this.setControls(EnumSet.of(Goal.Control.MOVE, Goal.Control.LOOK));
        this.idleTicks = mob.getWorld().random.nextInt(100) + 100; // Initial idle time (5-10s)
    }

    private boolean hasMinerRole() {
        NbtCompound nbt = new NbtCompound();
        this.mob.writeNbt(nbt);
        if (nbt.contains("WCS_AllyData", NbtElement.COMPOUND_TYPE)) { // Use NbtElement.COMPOUND_TYPE (10)
            NbtCompound allyData = nbt.getCompound("WCS_AllyData");
            return "MINER".equals(allyData.getString("Role"));
        }
        return false;
    }

    @Override
    public boolean canStart() {
        if (!hasMinerRole() || this.mob.isSleeping() || !this.mob.getNavigation().isIdle()) {
            return false;
        }
        if (idleTicks > 0) {
            idleTicks--;
            return false;
        }

        this.targetBlockPos = findTargetBlock();
        return this.targetBlockPos != null;
    }

    @Override
    public boolean shouldContinue() {
        return this.targetBlockPos != null && hasMinerRole() && !this.mob.isSleeping() &&
               !isTargetBlockMissingOrUnmineable() && this.miningTicks < MAX_MINING_TICKS; // Use < not <=
    }

    @Override
    public void start() {
        this.miningTicks = 0;
        this.mob.getNavigation().startMovingTo(this.targetBlockPos.getX() + 0.5D,
                                             this.targetBlockPos.getY(),
                                             this.targetBlockPos.getZ() + 0.5D, 0.7D); // Adjusted speed
        // Example: mob.sendSystemMessage(Text.literal("Going to mine at " + targetBlockPos));
    }

    @Override
    public void stop() {
        this.mob.getNavigation().stop();
        this.targetBlockPos = null;
        this.miningTicks = 0;
        this.idleTicks = 200 + this.mob.getWorld().random.nextInt(200); // Cooldown before searching again (10-20s)
    }

    @Override
    public void tick() {
        if (this.targetBlockPos == null) return; // Should be caught by shouldContinue, but good practice

        this.mob.getLookControl().lookAt(this.targetBlockPos.getX() + 0.5D,
                                       this.targetBlockPos.getY() + 0.5D,
                                       this.targetBlockPos.getZ() + 0.5D);

        if (this.mob.getPos().isInRange(this.targetBlockPos.toCenterPos(), 2.5D)) { // Adjusted range
            if (isTargetBlockMissingOrUnmineable()) {
                 this.targetBlockPos = null;
                 return;
            }

            this.miningTicks++;
            this.mob.swingHand(Hand.MAIN_HAND);

            if (this.miningTicks % 20 == 0) {
                BlockState state = this.mob.getWorld().getBlockState(this.targetBlockPos);
                this.mob.getWorld().playSound(null, this.targetBlockPos, state.getBlock().getSoundGroup(state).getHitSound(), this.mob.getSoundCategory(), 0.5f, 1.5f);
            }

            if (this.miningTicks >= MAX_MINING_TICKS) {
                mineBlock();
                this.targetBlockPos = null;
            }
        } else {
            if (this.mob.getNavigation().isIdle()) {
                 this.mob.getNavigation().startMovingTo(this.targetBlockPos.getX() + 0.5D,
                                             this.targetBlockPos.getY(),
                                             this.targetBlockPos.getZ() + 0.5D, 0.7D);
            }
        }
    }

    private boolean isTargetBlockMissingOrUnmineable() {
        if (this.targetBlockPos == null) return true;
        BlockState state = this.mob.getWorld().getBlockState(this.targetBlockPos);
        // Also check if block is unbreakable (e.g. bedrock)
        return state.isAir() || !isMineableBlock(state) || state.getHardness(this.mob.getWorld(), this.targetBlockPos) < 0;
    }

    private BlockPos findTargetBlock() {
        List<BlockPos> potentialTargets = new ArrayList<>();
        BlockPos mobPos = this.mob.getBlockPos();
        // Search in a 17x11x17 area (8 blocks out, 5 blocks up/down)
        for (int y = -5; y <= 5; y++) {
            for (int x = -8; x <= 8; x++) {
                for (int z = -8; z <= 8; z++) {
                    BlockPos currentPos = mobPos.add(x, y, z);
                    BlockState state = this.mob.getWorld().getBlockState(currentPos);
                    if (isMineableBlock(state) && state.getHardness(this.mob.getWorld(), currentPos) >= 0) { // Check if breakable
                        potentialTargets.add(currentPos);
                    }
                }
            }
        }
        if (potentialTargets.isEmpty()) return null;
        Collections.shuffle(potentialTargets);
        // Prioritize closer blocks slightly, or by Y-level? For now, random.
        return potentialTargets.get(0);
    }

    private boolean isMineableBlock(BlockState state) {
        // Using BlockTags for broader compatibility
        if (state.isIn(BlockTags.LOGS) ||
            state.isIn(BlockTags.STONE_ORE_REPLACEABLES) || // Catches stone, deepslate, etc.
            state.isIn(BlockTags.COAL_ORES) ||
            state.isIn(BlockTags.IRON_ORES) ||
            state.isIn(BlockTags.COPPER_ORES) ||
            state.isIn(BlockTags.GOLD_ORES) ||
            state.isIn(BlockTags.LAPIS_ORES) ||
            state.isIn(BlockTags.DIAMOND_ORES) ||
            state.isIn(BlockTags.EMERALD_ORES) ||
            state.isIn(BlockTags.REDSTONE_ORES) ||
            state.isOf(Blocks.NETHERRACK) || // Example Nether block
            state.isOf(Blocks.NETHER_GOLD_ORE) ||
            state.isOf(Blocks.NETHER_QUARTZ_ORE) ||
            state.isOf(Blocks.ANCIENT_DEBRIS) || // Ambitious!
            state.isOf(Blocks.DIRT) || state.isOf(Blocks.GRAVEL) || state.isOf(Blocks.SAND) // Common bulk blocks
            ) {
            return true;
        }
        return false;
    }

    private void mineBlock() {
        if (this.targetBlockPos != null && this.mob.getWorld() instanceof ServerWorld && !this.mob.getWorld().isClient) {
            ServerWorld world = (ServerWorld) this.mob.getWorld();
            BlockState blockState = world.getBlockState(this.targetBlockPos);

            // Consider if the mob "has" a pickaxe for tool context. For now, ItemStack.EMPTY
            ItemStack simulatedTool = ItemStack.EMPTY;
            // If mob has a pickaxe in main hand: this.mob.getMainHandStack();

            List<ItemStack> drops = Block.getDroppedStacks(blockState, world, this.targetBlockPos, world.getBlockEntity(this.targetBlockPos), this.mob, simulatedTool);

            world.breakBlock(this.targetBlockPos, false, this.mob);

            for (ItemStack drop : drops) {
                this.mob.dropStack(drop, 0.5f);
            }
            world.playSound(null, this.targetBlockPos, blockState.getBlock().getSoundGroup(blockState).getBreakSound(), this.mob.getSoundCategory(), 1.0f, 1.0f);
        }
    }
}
