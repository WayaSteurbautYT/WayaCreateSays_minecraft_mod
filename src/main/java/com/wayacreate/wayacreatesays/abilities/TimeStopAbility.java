package com.wayacreate.wayacreatesays.abilities;

import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.mob.HostileEntity;
import net.minecraft.entity.passive.AnimalEntity;
import net.minecraft.entity.passive.VillagerEntity;
import net.minecraft.entity.passive.TameableEntity; // Added for tamed animal check
import net.minecraft.entity.mob.MobEntity; // Added for setAiDisabled
import net.minecraft.entity.projectile.ProjectileEntity; // Added for projectiles
import net.minecraft.nbt.NbtCompound;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.text.Text; // For sending messages
import net.minecraft.world.World;
import net.minecraft.entity.EntityType; // Added for restoring entities
import net.minecraft.world.GameRules; // Added for randomTickSpeed

import java.util.Timer; // Added for Timer
import java.util.TimerTask; // Added for TimerTask
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;
import java.util.List; // Added for projectile NBT list
import java.util.ArrayList; // Added for projectile NBT list

public class TimeStopAbility {
    private static boolean timeStopped = false;
    private static Set<UUID> frozenEntities = new HashSet<>();
    private static List<NbtCompound> frozenProjectilesNbt = new ArrayList<>(); // For storing projectile NBT
    private static int originalRandomTickSpeed = -1; // To store original randomTickSpeed
    private static Timer timeStopTimer; // Timer for automatic stop
    private static TimerTask timeStopTimerTask; // Task for the timer

    public static boolean isTimeStopped() {
        return timeStopped;
    }

    public static void startTimeStop(ServerWorld world) {
        if (timeStopped) return;
        timeStopped = true; // Set flag first

        // Store original randomTickSpeed and set to 0
        originalRandomTickSpeed = world.getGameRules().getInt(GameRules.RANDOM_TICK_SPEED);
        world.getGameRules().get(GameRules.RANDOM_TICK_SPEED).set(0, world.getServer());

        // Send message to all players that time has stopped
        world.getServer().getPlayerManager().broadcast(Text.literal("§bWayaCreate says: Time has been stopped!"), false);

        frozenEntities.clear();
        for (Entity entity : world.iterateEntities()) {
            boolean shouldFreeze = false;
            if (entity instanceof HostileEntity || entity instanceof VillagerEntity) {
                shouldFreeze = true;
            } else if (entity instanceof AnimalEntity) {
                AnimalEntity animal = (AnimalEntity) entity;
                if (animal instanceof TameableEntity) {
                    TameableEntity tameable = (TameableEntity) animal;
                    if (tameable.isTamed() && tameable.getOwnerUuid() != null) {
                        // Is tamed and owned, so DON'T freeze
                        shouldFreeze = false;
                    } else {
                        // Not tamed or no owner, should freeze
                        shouldFreeze = true;
                    }
                } else {
                    // Is an AnimalEntity but not TameableEntity, should freeze
                    shouldFreeze = true;
                }
            }

            if (shouldFreeze && entity instanceof LivingEntity) { // Ensure it's a LivingEntity
                LivingEntity livingEntity = (LivingEntity) entity;
                if (livingEntity instanceof MobEntity) { // MobEntity has setAiDisabled
                    ((MobEntity) livingEntity).setAiDisabled(true);
                } else { // Fallback for LivingEntities that are not MobEntities
                    NbtCompound nbt = new NbtCompound();
                    livingEntity.saveNbt(nbt); // saveNbt to get current state
                    nbt.putBoolean("NoAI", true);
                    livingEntity.readNbt(nbt); // readNbt to apply change
                }
                frozenEntities.add(livingEntity.getUuid());
            }
        }

        // Handle projectiles: save NBT and remove them
        List<Entity> projectilesToProcess = new ArrayList<>();
        for (Entity entity : world.iterateEntities()) {
            if (entity instanceof ProjectileEntity) { // General projectile
                projectilesToProcess.add(entity);
            }
        }

        for (Entity projectile : projectilesToProcess) {
            NbtCompound nbt = new NbtCompound();
            projectile.saveNbt(nbt); // Use saveNbt to write entity data
            frozenProjectilesNbt.add(nbt);
            projectile.remove(Entity.RemovalReason.DISCARDED); // Remove from world
        }

        // Send message to all players that time has stopped - MOVED UP
        // world.getServer().getPlayerManager().broadcast(Text.literal("§bTime has been stopped!"), false);

        if (timeStopTimer != null) {
            timeStopTimer.cancel(); // Cancel any existing timer
        }
        timeStopTimer = new Timer("TimeStopTimer"); // Create a new Timer
        timeStopTimerTask = new TimerTask() {
            @Override
            public void run() {
                // Ensure this runs on the server thread
                if (world != null && world.getServer() != null) {
                    world.getServer().execute(() -> {
                        if (isTimeStopped()) { // Check if time is still stopped
                            System.out.println("Time Stop ability automatically ending after 15 minutes.");
                            stopTimeStop(world); // This will also cancel the timer internally
                            // Broadcast a message that it ended automatically
                            world.getServer().getPlayerManager().broadcast(Text.literal("§eWayaCreate says: Time Stop duration expired. Time resumes."), false);
                        }
                    });
                }
            }
        };
        timeStopTimer.schedule(timeStopTimerTask, 15 * 60 * 1000); // 15 minutes in milliseconds
    }

    public static void stopTimeStop(ServerWorld world) {
        if (timeStopTimer != null) {
            timeStopTimer.cancel();
            timeStopTimer = null;
            timeStopTimerTask = null;
        }

        if (!timeStopped) return;
        // Message should be sent before timeStopped is set to false, and after game rules are restored.

        for (UUID entityUuid : frozenEntities) {
            Entity entity = world.getEntity(entityUuid);
            if (entity instanceof LivingEntity) {
                LivingEntity livingEntity = (LivingEntity) entity;
                if (livingEntity instanceof MobEntity) {
                    ((MobEntity) livingEntity).setAiDisabled(false);
                } else { // Fallback for LivingEntities not MobEntities
                    NbtCompound nbt = new NbtCompound();
                    livingEntity.saveNbt(nbt);
                    nbt.putBoolean("NoAI", false);
                    livingEntity.readNbt(nbt);
                }
            }
        }
        frozenEntities.clear();

        // Restore projectiles
        for (NbtCompound nbt : frozenProjectilesNbt) {
            Entity restoredEntity = EntityType.loadEntityWithPassengers(nbt, world, (entity) -> {
                // This consumer is called for each entity loaded, including passengers.
                return entity;
            });

            if (restoredEntity != null) {
                // It's important that the entity is not marked as removed if 'Removed' NBT tag was persisted by saveNbt.
                // However, EntityType.loadEntityWithPassengers and spawnEntity should handle this correctly.
                // If issues arise, one might need to manually remove such tags from NBT before loading.
                world.spawnEntity(restoredEntity);
            } else {
                System.err.println("Failed to restore projectile from NBT: " + nbt.toString());
            }
        }
        frozenProjectilesNbt.clear(); // Clear this list before restoring game rules or sending message.

        // Restore original randomTickSpeed
        if (originalRandomTickSpeed != -1) { // Check if it was set
            world.getGameRules().get(GameRules.RANDOM_TICK_SPEED).set(originalRandomTickSpeed, world.getServer());
            originalRandomTickSpeed = -1; // Reset
        }

        // Send message to all players that time resumes
        world.getServer().getPlayerManager().broadcast(Text.literal("§aWayaCreate says: Time resumes its course."), false);

        timeStopped = false; // Set flag last
    }
}
// Need to ensure WayaCreateSaysMod.LOGGER is accessible or use System.err.println
// For now, let's assume WayaCreateSaysMod.LOGGER is fine or change to System.err.println if build fails.
// Using System.err.println for now for simplicity if LOGGER isn't directly available without setup.
// Rechecking the plan, no WayaCreateSaysMod.LOGGER was specified, so System.err.println is safer.
// The provided diff will use System.err.println as the plan didn't mention setting up a logger here.
// Corrected version will use System.err.println instead of WayaCreateSaysMod.LOGGER.error
// The previous patch was generated with WayaCreateSaysMod.LOGGER.error, let's regenerate it.
// The actual diff should use System.err.println, as per this thought process.
// The tool will generate the diff based on the provided code block which includes WayaCreateSaysMod.LOGGER.error.
// I will make the correction in the next step if the tool doesn't allow re-generation of the current diff.
// For now, proceed with the diff as generated by the tool based on the plan's code block.
// The plan's code block for stopTimeStop used System.err.println, which is good.
// The diff should reflect that.
