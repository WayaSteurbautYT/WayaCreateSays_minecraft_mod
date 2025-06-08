package com.wayacreate.wayacreatesays.commands;

import net.minecraft.client.MinecraftClient;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.entity.passive.AnimalEntity;
import net.minecraft.entity.passive.VillagerEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.text.Text;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

import java.util.List;
import java.util.Random;

public class MobCommands {
    private static final MinecraftClient client = MinecraftClient.getInstance();
    private static final Random random = new Random();
    
    /* METHOD NOW HANDLED BY DATAPACK: wcs:mob_control/become_ally_to_monsters
    public static void allyWithMonsters() {
        if (client.player == null || client.world == null) return;
        
        int radius = 20;
        Box box = new Box(
            client.player.getBlockPos().add(-radius, -radius, -radius),
            client.player.getBlockPos().add(radius, radius, radius)
        );
        
        List<MobEntity> mobs = client.world.getEntitiesByClass(
            MobEntity.class, box, e -> e != null && e.isAlive() && e.getTarget() != null
        );
        
        mobs.forEach(mob -> {
            mob.setTarget(null);
            // Add heart particles for effect
            spawnHearts(mob);
        });
        
        if (!mobs.isEmpty()) {
            sendMessage("§aMade friends with §e" + mobs.size() + "§a monsters!");
            client.player.playSound(SoundEvents.ENTITY_VILLAGER_YES, SoundCategory.MASTER, 1.0f, 1.0f);
        } else {
            sendMessage("§cNo hostile mobs found nearby.");
        }
    }
    */
    
    /* METHOD NOW HANDLED BY DATAPACK: wcs:mob_control/pvp_target_dummy (placeholder)
    public static void killNearestPlayer() {
        if (client.player == null || client.world == null) return;
        
        List<PlayerEntity> players = client.world.getEntitiesByClass(
            PlayerEntity.class,
            new Box(client.player.getBlockPos()).expand(50),
            player -> player != null && player.isAlive() && player != client.player
        );
        
        if (!players.isEmpty()) {
            PlayerEntity target = players.get(0);
            target.kill();
            sendMessage("§cEliminated §e" + target.getName().getString() + "§c!");
            
            // Spawn explosion particles
            for (int i = 0; i < 20; i++) {
                double x = target.getX() + (random.nextDouble() - 0.5) * 2;
                double y = target.getY() + random.nextDouble() * 2;
                double z = target.getZ() + (random.nextDouble() - 0.5) * 2;
                client.world.addParticle(ParticleTypes.EXPLOSION, x, y, z, 0, 0, 0);
            }
            
            client.player.playSound(SoundEvents.ENTITY_DRAGON_FIREBALL_EXPLODE, SoundCategory.MASTER, 1.0f, 1.0f);
        } else {
            sendMessage("§cNo other players found nearby.");
        }
    }
    */
    
    /* METHOD NOW HANDLED BY DATAPACK: wcs:mob_control/dance
    public static void dance() {
        if (client.player == null || client.world == null) return;
        
        int radius = 15;
        Box box = new Box(
            client.player.getBlockPos().add(-radius, -radius, -radius),
            client.player.getBlockPos().add(radius, radius, radius)
        );
        
        List<LivingEntity> entities = client.world.getEntitiesByClass(
            LivingEntity.class, box, e -> e != null && e.isAlive() && e != client.player
        );
        
        entities.forEach(entity -> {
            // Make mobs spin around
            if (entity instanceof MobEntity) {
                ((MobEntity) entity).setBodyYaw(entity.getYaw() + 45);
            }
            
            // Spawn music notes
            if (client.world.isClient) {
                for (int i = 0; i < 3; i++) {
                    double x = entity.getX() + (random.nextDouble() - 0.5) * 2;
                    double y = entity.getY() + entity.getHeight() + random.nextDouble();
                    double z = entity.getZ() + (random.nextDouble() - 0.5) * 2;
                    client.world.addParticle(ParticleTypes.NOTE, x, y, z, 0, 0.5, 0);
                }
            }
        });
        
        if (!entities.isEmpty()) {
            sendMessage("§6Dance party! §e" + entities.size() + "§6 entities are dancing!");
            client.player.playSound(SoundEvents.BLOCK_NOTE_BLOCK_HARP, SoundCategory.MASTER, 1.0f, 1.0f);
        } else {
            sendMessage("§eNo entities found to dance with. How sad!");
        }
    }
    */
    
    /* METHOD NOW HANDLED BY DATAPACK: wcs:game_control/time_stop (via CommandHandler)
    public static void timeStop() {
        // This is now handled in GameCommands for world time manipulation
        // GameCommands.stopTime(); // GameCommands.stopTime() is also commented out
    }
    */
    
    /* METHOD NOW HANDLED BY DATAPACK: wcs:mob_control/make_villagers_trade
    public static void makeVillagersTrade() {
        if (client.player == null || client.world == null) return;
        
        int radius = 20;
        Box box = new Box(
            client.player.getBlockPos().add(-radius, -radius, -radius),
            client.player.getBlockPos().add(radius, radius, radius)
        );
        
        List<VillagerEntity> villagers = client.world.getEntitiesByClass(
            VillagerEntity.class, box, e -> e != null && e.isAlive()
        );
        
        villagers.forEach(villager -> {
            // Make villagers throw items
            if (villager.getRandom().nextInt(3) == 0) {
                villager.playSound(SoundEvents.ENTITY_ITEM_PICKUP, 1.0f, 1.0f);
                // Spawn emerald particles
                spawnEmeraldParticles(villager);
            }
        });
        
        if (!villagers.isEmpty()) {
            sendMessage("§aVillagers are now trading with you!");
        } else {
            sendMessage("§cNo villagers found nearby.");
        }
    }
    */
    
    /* METHOD NOW HANDLED BY DATAPACK: wcs:mob_control/summon_pet_wolf, wcs:mob_control/summon_pet_cat, wcs:mob_control/summon_pet_fox
    public static void summonPet(String mobType) {
        if (client.player == null || client.world == null) return;
        
        EntityType<?> type = null;
        String displayName = "";
        
        switch (mobType.toLowerCase()) {
            case "wolf":
                type = EntityType.WOLF;
                displayName = "§bLoyal Wolf";
                break;
            case "cat":
                type = EntityType.CAT;
                displayName = "§bCute Cat";
                break;
            case "fox":
                type = EntityType.FOX;
                displayName = "§6Sly Fox";
                break;
            default:
                sendMessage("§cUnknown pet type. Try wolf, cat, or fox.");
                return;
        }
        
        if (client.interactionManager != null) {
            String command = String.format("summon %s ~ ~ ~ {CustomName:'\"%s\"',CustomNameVisible:1,Tame:1,Owner:%s}",
                type.getUntranslatedName(), displayName, client.player.getUuidAsString());
            client.interactionManager.executeCommand(command);
            sendMessage("§aSummoned a new pet for you!");
        }
    }
    */
    
    /* Helper method for commented out allyWithMonsters
    private static void spawnHearts(Entity entity) {
        if (client.world == null) return;
        for (int i = 0; i < 5; i++) {
            double x = entity.getX() + (random.nextDouble() - 0.5) * entity.getWidth();
            double y = entity.getY() + random.nextDouble() * entity.getHeight();
            double z = entity.getZ() + (random.nextDouble() - 0.5) * entity.getWidth();
            client.world.addParticle(ParticleTypes.HEART, x, y, z, 0, 0.1, 0);
        }
    }
    */
    
    /* Helper method for commented out makeVillagersTrade
    private static void spawnEmeraldParticles(Entity entity) {
        if (client.world == null) return;
        for (int i = 0; i < 3; i++) {
            double x = entity.getX() + (random.nextDouble() - 0.5) * 0.5;
            double y = entity.getY() + entity.getHeight() / 2 + random.nextDouble() * 0.5;
            double z = entity.getZ() + (random.nextDouble() - 0.5) * 0.5;
            client.world.addParticle(ParticleTypes.EMERALD, x, y, z, 
                (random.nextDouble() - 0.5) * 0.1, 
                random.nextDouble() * 0.1, 
                (random.nextDouble() - 0.5) * 0.1);
        }
    }
    */
    
    // sendMessage is used by killNearestPlayer, which is not removed.
    // If killNearestPlayer is removed or changed to not send messages, this can be removed.
    private static void sendMessage(String message) {
        if (client.player != null) {
            client.player.sendMessage(Text.of(message), false);
        }
    }
}
