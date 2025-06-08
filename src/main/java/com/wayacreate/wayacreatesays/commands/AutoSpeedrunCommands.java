package com.wayacreate.wayacreatesays.commands;

import net.minecraft.block.Blocks;
import net.minecraft.command.argument.EntityAnchorArgumentType;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.goal.RevengeGoal;
import net.minecraft.entity.boss.dragon.EnderDragonEntity;
import net.minecraft.entity.mob.*;
import net.minecraft.entity.passive.*;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.advancement.Advancement;
import net.minecraft.advancement.AdvancementProgress;
import net.minecraft.advancement.PlayerAdvancementTracker;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.text.Text;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

import java.util.List;
import java.util.function.Predicate;
import java.util.Map;
import java.util.HashMap;
import java.util.UUID;

public class AutoSpeedrunCommands {
    private static final int SEARCH_RADIUS = 32;
    private static final Map<UUID, PendingAdvancement> pendingAdvancements = new HashMap<>();

    private record PendingAdvancement(ServerPlayerEntity player, String advancementId, int nextStage) {}
    private static boolean isAutoSpeedrunActive = false;
    private static int autoSpeedrunStage = 0;

    public static boolean getIsAutoSpeedrunActive() {
        return isAutoSpeedrunActive;
    }

    public static int getCurrentStage() {
        return autoSpeedrunStage;
    }

    private static void waitForAdvancementAndProgress(ServerPlayerEntity player, String advancementId, int nextStage) {
        if (player == null) {
            System.err.println("waitForAdvancementAndProgress: Player is null");
            return;
        }
        pendingAdvancements.put(player.getUuid(), new PendingAdvancement(player, advancementId, nextStage));
        player.sendMessage(Text.of("§eWaiting for advancement: §a" + advancementId), false);
    }
    
    public static void handleAutoSpeedrunCommand(PlayerEntity player) {
        if (isAutoSpeedrunActive) {
            player.sendMessage(Text.of("§cAuto Speedrun is already active! Say 'WayaCreate says stop' to cancel."), false);
            return;
        }
        
        isAutoSpeedrunActive = true;
        autoSpeedrunStage = 1;
        player.sendMessage(Text.of("§a§lWayaCreate's Auto Speedrun ACTIVATED! §r§eLet's beat this game!"), false);
        player.sendMessage(Text.of("§6Stage 1: §eAssembling the dream team..."), false);
        
        // Start the automatic progression
        progressAutoSpeedrun(player);
    }
    
    public static void stopAutoSpeedrun(PlayerEntity player) {
        if (!isAutoSpeedrunActive) return;
        
        isAutoSpeedrunActive = false;
        autoSpeedrunStage = 0;
        player.sendMessage(Text.of("§cWayaCreate says: Auto Speedrun has been stopped."), false);
    }
    
    private static void progressAutoSpeedrun(PlayerEntity player) {
        if (!isAutoSpeedrunActive) return;
        
        World world = player.getWorld();
        ServerPlayerEntity serverPlayer = null;
        if (player instanceof ServerPlayerEntity) {
            serverPlayer = (ServerPlayerEntity) player;
        } else {
            // This should not happen if called from server-side packet handler
            // or if the initial player object in handleAutoSpeedrunCommand was ServerPlayerEntity
            System.err.println("AutoSpeedrunCommands.progressAutoSpeedrun: player is not ServerPlayerEntity!");
            return;
        }
        
        switch (autoSpeedrunStage) {
            case 1: // Stage 1: Gather Resources
                // player.sendMessage(Text.of("§6Stage 1: §eGathering resources..."), false); // Old message
                serverPlayer.getServer().getCommandManager().executeWithPrefix(
                    serverPlayer.getCommandSource().withSilent(),
                    "function wcs:guidance/stage_1_get_wood"
                );
                
                // Spawn helper mobs
                spawnHelperMobs(world, player.getBlockPos());
                
                // Give player basic tools
                player.giveItemStack(new ItemStack(Items.IRON_PICKAXE));
                player.giveItemStack(new ItemStack(Items.IRON_SWORD));
                player.giveItemStack(new ItemStack(Items.COOKED_BEEF, 16));
                
                // Progress to next stage after advancement
                waitForAdvancementAndProgress(serverPlayer, "wcs:autospeedrun/stage_1_complete", 2);
                break;
                
            case 2: // Stage 2: Nether Preparation
                // player.sendMessage(Text.of("§6Stage 2: §ePreparing for the Nether..."), false); // Old message
                serverPlayer.getServer().getCommandManager().executeWithPrefix(
                    serverPlayer.getCommandSource().withSilent(),
                    "function wcs:guidance/stage_12_build_nether_portal"
                );
                
                // Build nether portal automatically
                buildNetherPortal(world, player.getBlockPos().north(3));
                
                // Spawn nether guide
                spawnNetherGuide(world, player.getBlockPos());
                
                waitForAdvancementAndProgress(serverPlayer, "wcs:autospeedrun/stage_2_complete", 3);
                break;
                
            case 3: // Stage 3: Nether Exploration
                // player.sendMessage(Text.of("§6Stage 3: §eConquering the Nether..."), false); // Old message
                serverPlayer.getServer().getCommandManager().executeWithPrefix(
                    serverPlayer.getCommandSource().withSilent(),
                    "function wcs:guidance/stage_13_find_fortress"
                );
                
                // Spawn blaze hunter
                spawnBlazeHunter(world, player.getBlockPos());
                
                // Spawn fortress guide
                spawnFortressGuide(world, player.getBlockPos());
                
                waitForAdvancementAndProgress(serverPlayer, "wcs:autospeedrun/stage_3_complete", 4);
                break;
                
            case 4: // Stage 4: The End
                // player.sendMessage(Text.of("§6Stage 4: §ePreparing for The End..."), false); // Old message
                serverPlayer.getServer().getCommandManager().executeWithPrefix(
                    serverPlayer.getCommandSource().withSilent(),
                    "function wcs:guidance/stage_17_activate_end_portal"
                );
                
                // Spawn ender dragon specialist
                spawnDragonSpecialist(world, player.getBlockPos());
                
                // Build end portal
                buildEndPortal(world, player.getBlockPos().south(3));
                
                waitForAdvancementAndProgress(serverPlayer, "wcs:autospeedrun/stage_4_complete", 5);
                break;
                
            case 5: // Final Stage: Dragon Fight
                // serverPlayer.getServer().getCommandManager().executeWithPrefix(serverPlayer.getCommandSource().withSilent(), "function wcs:guidance/stage_19_defeat_ender_dragon"); // Old
                // Ensure the player is actually in the End dimension and the dragon fight is active.
                // The attackDragon command might still be useful to initiate the fight if it's not automatic.
                // For now, we just wait for the kill.
                player.sendMessage(Text.of("§6§lFINAL STAGE: §c§lDEFEAT THE ENDER DRAGON!"), false);
                waitForAdvancementAndProgress(serverPlayer, "wcs:autospeedrun/stage_5_dragon_defeated", 6); // Stage 6: Completion
                break;
            case 6: // Speedrun Complete
                // This case is reached when stage_5_dragon_defeated is achieved.
                // The onAdvancementProgress would have set autoSpeedrunStage = 6 and called this.
                stopAutoSpeedrun(player); // Stop the speedrun state
                player.sendMessage(Text.of("§a§lWayaCreate's Auto Speedrun: §r§6CONGRATULATIONS, YOU BEAT THE GAME!"), false);
                // No further progression needed.
                break;
        }
    }
    
    private static void spawnHelperMobs(World world, BlockPos pos) {
        // Miner
        VillagerEntity miner = EntityType.VILLAGER.create(world);
        if (miner != null) {
            miner.setPosition(pos.getX() + 1, pos.getY(), pos.getZ());
            miner.setCustomName(Text.of("§6Miner Mike"));
            miner.setCustomNameVisible(true);
            miner.equipStack(net.minecraft.entity.EquipmentSlot.MAINHAND, new ItemStack(Items.DIAMOND_PICKAXE));
            world.spawnEntity(miner);
        }
        
        // Warrior
        ZombieEntity warrior = EntityType.ZOMBIE.create(world);
        if (warrior != null) {
            warrior.setPosition(pos.getX() - 1, pos.getY(), pos.getZ());
            warrior.setCustomName(Text.of("§cWarrior Will"));
            warrior.setCustomNameVisible(true);
            warrior.equipStack(net.minecraft.entity.EquipmentSlot.MAINHAND, new ItemStack(Items.DIAMOND_SWORD));
            warrior.equipStack(net.minecraft.entity.EquipmentSlot.HEAD, new ItemStack(Items.IRON_HELMET));
            warrior.equipStack(net.minecraft.entity.EquipmentSlot.CHEST, new ItemStack(Items.IRON_CHESTPLATE));
            world.spawnEntity(warrior);
        }
        
        // Archer
        SkeletonEntity archer = EntityType.SKELETON.create(world);
        if (archer != null) {
            archer.setPosition(pos.getX(), pos.getY(), pos.getZ() + 1);
            archer.setCustomName(Text.of("§aArcher Andy"));
            archer.setCustomNameVisible(true);
            archer.equipStack(net.minecraft.entity.EquipmentSlot.MAINHAND, new ItemStack(Items.BOW));
            world.spawnEntity(archer);
        }
    }
    
    private static void buildNetherPortal(World world, BlockPos pos) {
        // Simple obsidian frame for nether portal
        for (int y = 0; y < 4; y++) {
            world.setBlockState(pos.add(0, y, 0), Blocks.OBSIDIAN.getDefaultState());
            world.setBlockState(pos.add(3, y, 0), Blocks.OBSIDIAN.getDefaultState());
        }
        for (int x = 1; x < 3; x++) {
            world.setBlockState(pos.add(x, 0, 0), Blocks.OBSIDIAN.getDefaultState());
            world.setBlockState(pos.add(x, 3, 0), Blocks.OBSIDIAN.getDefaultState());
        }
        
        // Light the portal
        world.setBlockState(pos.add(1, 1, 0), Blocks.NETHER_PORTAL.getDefaultState());
        world.setBlockState(pos.add(2, 1, 0), Blocks.NETHER_PORTAL.getDefaultState());
        world.setBlockState(pos.add(1, 2, 0), Blocks.NETHER_PORTAL.getDefaultState());
        world.setBlockState(pos.add(2, 2, 0), Blocks.NETHER_PORTAL.getDefaultState());
        
        world.playSound(null, pos, SoundEvents.BLOCK_FIRE_AMBIENT, SoundCategory.BLOCKS, 1.0f, 1.0f);
    }
    
    private static void spawnNetherGuide(World world, BlockPos pos) {
        PiglinBruteEntity guide = EntityType.PIGLIN_BRUTE.create(world);
        if (guide != null) {
            guide.setPosition(pos.getX(), pos.getY(), pos.getZ() - 2);
            guide.setCustomName(Text.of("§6Nether Guide"));
            guide.setCustomNameVisible(true);
            guide.setPersistent();
            guide.setInvulnerable(true);
            world.spawnEntity(guide);
            
            // Make the guide point to the portal
            guide.lookAt(EntityAnchorArgumentType.EntityAnchor.EYES, new Vec3d(pos.getX(), pos.getY(), pos.getZ() + 5));
        }
    }
    
    private static void spawnBlazeHunter(World world, BlockPos pos) {
        SnowGolemEntity hunter = EntityType.SNOW_GOLEM.create(world);
        if (hunter != null) {
            hunter.setPosition(pos.getX(), pos.getY(), pos.getZ() - 2);
            hunter.setCustomName(Text.of("§bBlaze Hunter"));
            hunter.setCustomNameVisible(true);
            hunter.setPersistent();
            hunter.setInvulnerable(true);
            world.spawnEntity(hunter);
        }
    }
    
    private static void spawnFortressGuide(World world, BlockPos pos) {
        WitherSkeletonEntity guide = EntityType.WITHER_SKELETON.create(world);
        if (guide != null) {
            guide.setPosition(pos.getX() + 2, pos.getY(), pos.getZ() - 2);
            guide.setCustomName(Text.of("§8Fortress Guide"));
            guide.setCustomNameVisible(true);
            guide.setPersistent();
            guide.setInvulnerable(true);
            world.spawnEntity(guide);
        }
    }
    
    private static void spawnDragonSpecialist(World world, BlockPos pos) {
        EndermanEntity specialist = EntityType.ENDERMAN.create(world);
        if (specialist != null) {
            specialist.setPosition(pos.getX(), pos.getY(), pos.getZ() - 3);
            specialist.setCustomName(Text.of("§5Dragon Specialist"));
            specialist.setCustomNameVisible(true);
            specialist.setPersistent();
            specialist.setInvulnerable(true);
            world.spawnEntity(specialist);
        }
    }
    
    private static void buildEndPortal(World world, BlockPos pos) {
        // Create end portal frame (for demonstration, in real mod this would be the actual portal)
        for (int x = 0; x < 5; x++) {
            for (int z = 0; z < 5; z++) {
                if ((x == 0 || x == 4) && (z == 0 || z == 4)) continue;
                if (x == 0 || x == 4 || z == 0 || z == 4) {
                    world.setBlockState(pos.add(x, 0, z), Blocks.END_PORTAL_FRAME.getDefaultState());
                }
            }
        }
        
        // Fill with end portal blocks in the center
        for (int x = 1; x < 4; x++) {
            for (int z = 1; z < 4; z++) {
                world.setBlockState(pos.add(x, 0, z), Blocks.END_PORTAL.getDefaultState());
            }
        }
    }
    
    public static void attackDragon(PlayerEntity player) {
        if (!isAutoSpeedrunActive || autoSpeedrunStage != 5) {
            player.sendMessage(Text.of("§cYou need to start the auto speedrun first! Say 'WayaCreate says auto speedrun'"), false);
            return;
        }
        
        player.sendMessage(Text.of("§4§lCHAAARGE! For WayaCreate!"), false);
        
        // Make all helper mobs target the dragon
        List<LivingEntity> helpers = player.getWorld().getEntitiesByClass(
            LivingEntity.class,
            player.getBoundingBox().expand(50),
            e -> e.hasCustomName() && e != player
        );
        
        // Spawn the dragon if not already present
        if (player.getWorld().getEntitiesByClass(EnderDragonEntity.class, player.getBoundingBox().expand(100), e -> true).isEmpty()) {
            EnderDragonEntity dragon = EntityType.ENDER_DRAGON.create(player.getWorld());
            if (dragon != null) {
                dragon.refreshPositionAndAngles(
                    player.getX() + 20,
                    player.getY() + 20,
                    player.getZ() + 20,
                    0, 0
                );
                player.getWorld().spawnEntity(dragon);
                
                // Make all helpers target the dragon
                for (LivingEntity helper : helpers) {
                    if (helper instanceof MobEntity) {
                        ((MobEntity) helper).targetSelector.add(1, new RevengeGoal((MobEntity) helper));
                        ((MobEntity) helper).setTarget(dragon);
                    }
                }
            }
        }
        // For the final stage, we might want a specific "dragon defeated" advancement
        // waitForAdvancementAndProgress(serverPlayer, "wcs:autospeedrun/dragon_defeated", 6); // or some terminal stage
        player.sendMessage(Text.of("§aAuto Speedrun Stage 5 Complete. Waiting for dragon defeat (manual for now)."), false);

    }
    
    // scheduleNextStage method removed

    // This method should be registered to listen for AdvancementProgressChangeEvent
    // For example, in your main mod class:
    // AdvancementProgressChangeEvent.EVENT.register(AutoSpeedrunCommands::onAdvancementProgress);
    public static void onAdvancementProgress(PlayerAdvancementTracker tracker, ServerPlayerEntity player, Advancement advancement, String criterionName) {
        UUID playerUuid = player.getUuid();
        if (pendingAdvancements.containsKey(playerUuid)) {
            PendingAdvancement pending = pendingAdvancements.get(playerUuid);
            // Check if the completed advancement is the one we are waiting for
            if (pending.advancementId().equals(advancement.getId().toString())) {
                AdvancementProgress progress = tracker.getProgress(advancement);
                if (progress.isDone()) { // Check if the specific criterion or the whole advancement is done
                    pendingAdvancements.remove(playerUuid);
                    autoSpeedrunStage = pending.nextStage();
                    player.sendMessage(Text.of("§aAdvancement '" + pending.advancementId() + "' completed! Progressing to stage " + autoSpeedrunStage), false);

                    // Ensure progression happens on the server thread
                    player.getServer().execute(() -> {
                        if (player.isAlive() && isAutoSpeedrunActive) {
                            progressAutoSpeedrun(player);
                        }
                    });
                }
            }
        }
    }
    
    public static void onPlayerJoin(PlayerEntity player) {
        if (isAutoSpeedrunActive) {
            player.sendMessage(Text.of("§6§lWayaCreate's Auto Speedrun is currently active! Current stage: " + autoSpeedrunStage), false);
        }
    }
}
