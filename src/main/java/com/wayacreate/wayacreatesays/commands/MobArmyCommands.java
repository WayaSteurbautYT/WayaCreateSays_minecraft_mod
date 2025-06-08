package com.wayacreate.wayacreatesays.commands;

import net.minecraft.block.Blocks;
import net.minecraft.entity.*;
import net.minecraft.entity.ai.goal.*;
import net.minecraft.entity.mob.*;
import net.minecraft.entity.passive.*;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.text.Text;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

import java.util.*;
import java.util.stream.Collectors;

public class MobArmyCommands {
    private static final Map<UUID, List<UUID>> playerArmies = new HashMap<>();
    private static final Random random = new Random();
    
    public static void handleMobCommand(String command, PlayerEntity player) {
        String[] parts = command.toLowerCase().split("\\s+");
        if (parts.length < 2) return;
        
        String action = parts[0];
        String target = parts[1];
        
        switch (action) {
            case "tame":
            case "ally":
            case "recruit":
                recruitMob(player, target, String.join(" ", Arrays.copyOfRange(parts, 2, parts.length)));
                break;
                
            case "drop":
                forceDrop(player, target);
                break;
                
            case "mine":
                mineBlock(player, target);
                break;
                
            case "craft":
                craftItem(player, target);
                break;
                
            case "worship":
                makeWorship(player, target);
                break;
                
            case "follow":
                commandArmy(player, "follow");
                break;
                
            case "stay":
                commandArmy(player, "stay");
                break;
                
            case "attack":
                commandArmy(player, "attack");
                break;
                
            case "dance":
                commandArmy(player, "dance");
                break;
        }
    }
    
    /* METHOD POSSIBLY HANDLED BY DATAPACK: wcs:mob_control/recruit_mob
    private static void recruitMob(PlayerEntity player, String mobType, String name) {
        World world = player.getWorld();
        List<LivingEntity> nearbyMobs = world.getEntitiesByClass(
            LivingEntity.class,
            player.getBoundingBox().expand(10),
            e -> !e.isPlayer() && !isInArmy(e) && e.getType().getTranslationKey().toLowerCase().contains(mobType.toLowerCase())
        );
        
        if (nearbyMobs.isEmpty()) {
            player.sendMessage(Text.of("§cNo " + mobType + " found nearby to recruit!"), false);
            return;
        }
        
        LivingEntity mob = nearbyMobs.get(0);
        UUID playerId = player.getUuid();
        
        // Add to player's army
        playerArmies.computeIfAbsent(playerId, k -> new ArrayList<>()).add(mob.getUuid());
        
        // Customize the mob
        if (!name.isEmpty()) {
            mob.setCustomName(Text.of("§e" + name));
            mob.setCustomNameVisible(true);
        }
        
        // Make mob friendly
        if (mob instanceof HostileEntity) {
            ((HostileEntity) mob).setPersistent();
            ((HostileEntity) mob).setTarget(null);
        }
        
        // Play effect
        world.playSound(null, mob.getBlockPos(), SoundEvents.ENTITY_PLAYER_LEVELUP, SoundCategory.PLAYERS, 1.0f, 1.0f);
        player.sendMessage(Text.of("§a" + mobType + " has joined your army!"), false);
    }
    */
    
    /* METHOD POSSIBLY HANDLED BY DATAPACK: wcs:mob_control/force_mob_drops
    private static void forceDrop(PlayerEntity player, String itemType) {
        World world = player.getWorld();
        List<LivingEntity> targets = new ArrayList<>();
        
        // Find appropriate mobs
        if (itemType.contains("pearl") || itemType.contains("ender")) {
            targets = world.getEntitiesByClass(
                EndermanEntity.class,
                player.getBoundingBox().expand(20),
                e -> !isInArmy(e)
            );
            
            targets.forEach(ender -> {
                ender.dropStack(new ItemStack(Items.ENDER_PEARL, random.nextInt(2) + 1));
                ender.damage(world.getDamageSources().playerAttack(player), 1.0f);
            });
        } 
        // Add more item types as needed
        
        if (targets.isEmpty()) {
            player.sendMessage(Text.of("§cNo valid targets found for " + itemType), false);
        } else {
            player.sendMessage(Text.of("§aForced " + targets.size() + " mobs to drop " + itemType), false);
        }
    }
    */
    
    private static void mineBlock(PlayerEntity player, String blockType) {
        // Find nearby villagers with the right profession
        List<VillagerEntity> villagers = player.getWorld().getEntitiesByClass(
            VillagerEntity.class,
            player.getBoundingBox().expand(20),
            v -> isInArmy(v) && v.getVillagerData().getProfession().toString().toLowerCase().contains("toolsmith")
        );
        
        if (villagers.isEmpty()) {
            player.sendMessage(Text.of("§cNo toolsmith villagers in your army!"), false);
            return;
        }
        
        // Find the nearest block of the specified type
        BlockPos targetPos = findNearbyBlock(player, blockType);
        if (targetPos == null) {
            player.sendMessage(Text.of("§cNo " + blockType + " found nearby"), false);
            return;
        }
        
        // Make villagers mine the block
        for (VillagerEntity villager : villagers) {
            villager.getNavigation().startMovingTo(targetPos.getX(), targetPos.getY(), targetPos.getZ(), 1.0);
            // In a real implementation, you'd need to handle the actual mining logic
        }
        
        player.sendMessage(Text.of("§aYour villagers are mining " + blockType + "!"), false);
    }
    
    private static void craftItem(PlayerEntity player, String itemType) {
        // This is a simplified version - in a real mod you'd need to handle actual crafting
        player.sendMessage(Text.of("§aYour villagers are crafting " + itemType + "!"), false);
        
        // Play crafting sound
        player.getWorld().playSound(
            null, 
            player.getBlockPos(), 
            SoundEvents.BLOCK_ANVIL_USE, 
            SoundCategory.PLAYERS, 
            1.0f, 
            1.0f
        );
    }
    
    /* METHOD POSSIBLY HANDLED BY DATAPACK: wcs:mob_control/worship_me
    private static void makeWorship(PlayerEntity player, String mobType) {
        World world = player.getWorld();
        List<MobEntity> worshipers = world.getEntitiesByClass(
            MobEntity.class,
            player.getBoundingBox().expand(20),
            e -> e.getType().getTranslationKey().toLowerCase().contains(mobType.toLowerCase())
        );
        
        for (MobEntity mob : worshipers) {
            // Make mobs look at player
            mob.lookAtEntity(player, 180, 180);
            
            // Play happy sounds based on mob type
            if (mob instanceof PiglinEntity) {
                world.playSound(null, mob.getBlockPos(), SoundEvents.ENTITY_PIGLIN_ADMIRING_ITEM, SoundCategory.NEUTRAL, 1.0f, 1.0f);
            } else if (mob instanceof VillagerEntity) {
                world.playSound(null, mob.getBlockPos(), SoundEvents.ENTITY_VILLAGER_CELEBRATE, SoundCategory.NEUTRAL, 1.0f, 1.0f);
            }
            
            // Add to player's army
            if (!isInArmy(mob)) {
                playerArmies.computeIfAbsent(player.getUuid(), k -> new ArrayList<>()).add(mob.getUuid());
            }
        }
        
        if (!worshipers.isEmpty()) {
            player.sendMessage(Text.of("§a" + worshipers.size() + " " + mobType + "s are now worshiping you!"), false);
        } else {
            player.sendMessage(Text.of("§cNo " + mobType + "s found to worship you"), false);
        }
    }
    */
    
    private static void commandArmy(PlayerEntity player, String command) {
        List<LivingEntity> army = getPlayerArmy(player);
        if (army.isEmpty()) {
            player.sendMessage(Text.of("§cYou don't have an army yet! Recruit some mobs first."), false);
            return;
        }
        
        switch (command) {
            case "follow": // POSSIBLY HANDLED BY DATAPACK: wcs:mob_control/command_army_follow
                /*
                army.forEach(mob -> {
                    if (mob instanceof TameableEntity) {
                        ((TameableEntity) mob).setOwner(player);
                    }
                    mob.getNavigation().startMovingTo(player, 1.0);
                });
                */
                player.sendMessage(Text.of("§aYour army is following you! (Original implementation commented out)"), false);
                break;
                
            case "stay": // POSSIBLY HANDLED BY DATAPACK: wcs:mob_control/command_army_stay
                /*
                army.forEach(mob -> mob.getNavigation().stop());
                */
                player.sendMessage(Text.of("§aYour army has stopped. (Original implementation commented out)"), false);
                break;
                
            case "attack": // POSSIBLY HANDLED BY DATAPACK: wcs:mob_control/command_army_attack
                /*
                // Find nearest hostile mob
                LivingEntity target = findNearestHostile(player);
                if (target != null) {
                    army.forEach(mob -> {
                        if (mob instanceof MobEntity) {
                            ((MobEntity) mob).setTarget(target);
                        }
                    });
                    player.sendMessage(Text.of("§aYour army is attacking the " + target.getName().getString() + "!"), false);
                } else {
                    player.sendMessage(Text.of("§cNo hostile mobs found to attack!"), false);
                }
                */
                player.sendMessage(Text.of("§aYour army is attacking! (Original implementation commented out)"), false);
                break;
                
            case "dance": // POSSIBLY HANDLED BY DATAPACK: wcs:mob_control/dance
                /*
                army.forEach(mob -> {
                    // Make mobs jump around randomly
                    mob.setVelocity(
                        (random.nextDouble() - 0.5) * 0.5,
                        0.5 + random.nextDouble() * 0.5,
                        (random.nextDouble() - 0.5) * 0.5
                    );
                    world.playSound(null, mob.getBlockPos(), SoundEvents.ENTITY_VILLAGER_CELEBRATE, SoundCategory.NEUTRAL, 1.0f, 1.0f);
                });
                */
                player.sendMessage(Text.of("§aYour army is dancing! (Original implementation commented out)"), false);
                break;
        }
    }
    
    // Helper methods
    private static boolean isInArmy(Entity entity) {
        return playerArmies.values().stream()
            .anyMatch(list -> list.contains(entity.getUuid()));
    }
    
    private static List<LivingEntity> getPlayerArmy(PlayerEntity player) {
        List<LivingEntity> army = new ArrayList<>();
        List<UUID> armyIds = playerArmies.getOrDefault(player.getUuid(), Collections.emptyList());
        
        for (UUID id : armyIds) {
            Entity entity = player.getWorld().getEntity(id);
            if (entity instanceof LivingEntity) {
                army.add((LivingEntity) entity);
            }
        }
        
        return army;
    }
    
    private static BlockPos findNearbyBlock(PlayerEntity player, String blockType) {
        // Simplified - in a real mod you'd need to handle different block types
        int radius = 10;
        BlockPos playerPos = player.getBlockPos();
        
        for (int x = -radius; x <= radius; x++) {
            for (int y = -radius; y <= radius; y++) {
                for (int z = -radius; z <= radius; z++) {
                    BlockPos pos = playerPos.add(x, y, z);
                    if (player.getWorld().getBlockState(pos).getBlock().getTranslationKey().toLowerCase().contains(blockType.toLowerCase())) {
                        return pos;
                    }
                }
            }
        }
        return null;
    }
    
    private static LivingEntity findNearestHostile(PlayerEntity player) {
        return player.getWorld().getEntitiesByClass(
            HostileEntity.class,
            player.getBoundingBox().expand(20),
            e -> !isInArmy(e)
        ).stream().findFirst().orElse(null);
    }
}
