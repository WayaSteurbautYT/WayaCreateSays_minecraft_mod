package com.wayacreate.wayacreatesays.commands;

import net.minecraft.block.Blocks;
import net.minecraft.client.MinecraftClient;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.text.Text;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.World;

public class GameCommands {
    private static final MinecraftClient client = MinecraftClient.getInstance();
    
    public static void helpBeatGame() {
        if (client.player != null) {
            sendMessage("§aLet's beat Minecraft together! Here's what we'll do:");
            sendMessage("1. §6Gather wood and stone§r");
            sendMessage("2. §6Build a shelter§r");
            sendMessage("3. §6Find food§r");
            sendMessage("4. §6Mine for diamonds§r");
            sendMessage("5. §6Enter the Nether§r");
            sendMessage("6. §6Defeat the Ender Dragon§r");
            sendMessage("§aSay 'Simon says help me with [step]' for specific guidance!");
        }
    }
    
    public static void makeCraftingTable() {
        if (client.player == null || client.world == null) return;
        
        PlayerInventory inventory = client.player.getInventory();
        int woodSlot = -1;
        
        // Find wood in hotbar
        for (int i = 0; i < 9; i++) {
            ItemStack stack = inventory.getStack(i);
            if (stack.isOf(Items.OAK_PLANKS) || 
                stack.isOf(Items.BIRCH_PLANKS) ||
                stack.isOf(Items.SPRUCE_PLANKS) ||
                stack.isOf(Items.JUNGLE_PLANKS) ||
                stack.isOf(Items.ACACIA_PLANKS) ||
                stack.isOf(Items.DARK_OAK_PLANKS) ||
                stack.isOf(Items.MANGROVE_PLANKS) ||
                stack.isOf(Items.CHERRY_PLANKS) ||
                stack.isOf(Items.BAMBOO_PLANKS) ||
                stack.isOf(Items.CRIMSON_PLANKS) ||
                stack.isOf(Items.WARPED_PLANKS)) {
                woodSlot = i;
                break;
            }
        }
        
        if (woodSlot != -1) {
            inventory.selectedSlot = woodSlot;
            BlockPos pos = client.player.getBlockPos();
            client.interactionManager.interactBlock(client.player, client.world, client.player.getActiveHand(), 
                new net.minecraft.util.hit.BlockHitResult(
                    client.player.getPos(),
                    Direction.UP,
                    pos,
                    false
                )
            );
            sendMessage("§aCrafted a crafting table for you!");
        } else {
            sendMessage("§cYou need wood planks to make a crafting table!");
        }
    }
    
    public static void mineWood() {
        if (client.player == null || client.world == null) return;
        
        // Check if player has an axe
        boolean hasAxe = client.player.getMainHandStack().getItem() instanceof net.minecraft.item.AxeItem ||
                       client.player.getOffHandStack().getItem() instanceof net.minecraft.item.AxeItem;
        
        if (!hasAxe) {
            sendMessage("§cYou need an axe to mine wood efficiently!");
            return;
        }
        
        // Find nearest tree/log
        BlockPos playerPos = client.player.getBlockPos();
        BlockPos.Mutable checkPos = new BlockPos.Mutable();
        
        for (int x = -5; x <= 5; x++) {
            for (int y = -3; y <= 3; y++) {
                for (int z = -5; z <= 5; z++) {
                    checkPos.set(playerPos.getX() + x, playerPos.getY() + y, playerPos.getZ() + z);
                    if (isLog(client.world.getBlockState(checkPos).getBlock())) {
                        // Found a log, mine it
                        client.interactionManager.attackBlock(checkPos, Direction.UP);
                        sendMessage("§aMining wood for you!");
                        return;
                    }
                }
            }
        }
        
        sendMessage("§cNo wood found nearby! Try standing near a tree.");
    }
    
    public static void stopTime() {
        if (client.player == null || client.world == null) return;
        
        // Toggle time stop effect
        if (client.world.isClient) {
            client.world.setTimeOfDay(6000); // Set to noon
            sendMessage("§6ZA WARUDO! TOKI WO TOMARE!§r Time has stopped!");
            // Play a cool sound effect
            client.player.playSound(SoundEvents.ENTITY_ENDER_DRAGON_GROWL, SoundCategory.MASTER, 1.0f, 0.5f);
        }
    }
    
    public static void teleportToSpawn() {
        if (client.player != null) {
            client.player.requestRespawn();
            sendMessage("§aTeleporting you to spawn!");
        }
    }
    
    public static void giveDiamonds() {
        if (client.player != null && client.interactionManager != null) {
            client.interactionManager.executeCommand("give @s minecraft:diamond 64");
            sendMessage("§bHere are some diamonds!");
        }
    }
    
    private static boolean isLog(net.minecraft.block.Block block) {
        return block == Blocks.OAK_LOG || block == Blocks.BIRCH_LOG || 
               block == Blocks.SPRUCE_LOG || block == Blocks.JUNGLE_LOG ||
               block == Blocks.ACACIA_LOG || block == Blocks.DARK_OAK_LOG ||
               block == Blocks.MANGROVE_LOG || block == Blocks.CHERRY_LOG ||
               block == Blocks.CRIMSON_STEM || block == Blocks.WARPED_STEM;
    }
    
    private static void sendMessage(String message) {
        if (client.player != null) {
            client.player.sendMessage(Text.of(message), false);
        }
    }
}
