package com.wayacreate.wayacreatesays.commands;

import net.minecraft.client.MinecraftClient;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.sound.SoundEvents;
import net.minecraft.text.Text;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class SpeedrunCommands {
    private static final MinecraftClient client = MinecraftClient.getInstance();
    
    public static void handleSpeedrunCommand(String command, PlayerEntity player) {
        if (player == null) return;
        
        String[] parts = command.toLowerCase().split("\\s+");
        if (parts.length < 1) return;
        
        String action = parts[0];
        
        switch (action) {
            case "find":
                if (parts.length > 1) {
                    findItem(parts[1], player);
                } else {
                    player.sendMessage(Text.of("§cPlease specify what to find. Example: 'find diamonds'"), false);
                }
                break;
            case "craft":
                if (parts.length > 1) {
                    craftItem(parts[1], player);
                } else {
                    player.sendMessage(Text.of("§cPlease specify what to craft. Example: 'craft pickaxe'"), false);
                }
                break;
            case "portal":
                createNetherPortal(player);
                break;
            case "nether":
                prepareForNether(player);
                break;
            case "fortress":
                findNetherFortress(player);
                break;
            case "stronghold":
                findStronghold(player);
                break;
            case "dragon":
                prepareForDragon(player);
                break;
            default:
                player.sendMessage(Text.of("§cUnknown speedrun command. Try 'find', 'craft', 'portal', 'nether', 'fortress', 'stronghold', or 'dragon'"), false);
        }
    }
    
    private static void findItem(String itemName, PlayerEntity player) {
        if (player == null) return;
        
        // Logic to guide player to find common items
        String message = "§eI can help you find " + itemName + ". §r";
        
        switch (itemName.toLowerCase()) {
            case "diamonds":
                message += "Mine at Y=-58 to -64 for best results. Listen for lava sounds! " +
                         "You'll need an iron or better pickaxe to mine them.";
                break;
            case "iron":
                message += "Check caves at Y=16 or dig down to Y=-32. Look for shipwrecks or village blacksmiths! " +
                         "You can also find iron in chests in mineshafts and strongholds.";
                break;
            case "blaze":
                message += "Find a Nether Fortress and look for Blaze spawners. You'll need a bow or fire resistance! " +
                         "Blazes drop Blaze Rods, which are essential for brewing and Eyes of Ender.";
                break;
            case "endermen":
                message += "Look in the Nether or at night in the Overworld. Don't look them in the eyes! " +
                         "They drop Ender Pearls, which are needed to find the Stronghold.";
                break;
            case "lava":
                message += "Check surface pools or dig down to Y=11. Be careful not to fall in! " +
                         "You can use water to turn lava into obsidian or cobblestone.";
                break;
            case "nether":
                message += "To get to the Nether, build a Nether Portal using obsidian (4x5 frame, corners optional). " +
                         "You'll need a flint and steel to light it. Find lava and use water to make obsidian.";
                break;
            case "fortress":
                message += "Nether Fortresses generate along north-south lines. Move east or west from your portal to find one. " +
                         "They're made of dark bricks and contain Blaze spawners and Wither Skeletons.";
                break;
            case "stronghold":
                message += "To find the Stronghold, you'll need Eyes of Ender (Blaze Powder + Ender Pearl). " +
                         "Throw them and follow the direction they float. They'll point downward when above the Stronghold.";
                break;
            default:
                message = "§cI don't know where to find " + itemName + " yet. Try asking about diamonds, iron, blaze, endermen, or lava.";
        }
        
        player.sendMessage(Text.of(message), false);
    }
    
    private static void craftItem(String itemName, PlayerEntity player) {
        if (player == null) return;
        
        // Logic to help with crafting
        String message = "§eTo craft " + itemName + ": §r";
        
        switch (itemName.toLowerCase()) {
            case "pickaxe":
                message += "Place 3 planks or ingots in the top row and 2 sticks down from the middle. " +
                         "You'll need a crafting table. Different materials have different durability and mining levels.";
                break;
            case "sword":
                message += "Place 2 planks/ingots vertically with a stick below. " +
                         "Swords are essential for combat and deal more damage than bare hands.";
                break;
            case "bed":
                message += "3 wool of the same color on top, 3 planks below. Essential for setting spawn! " +
                         "Beds also explode in the Nether and End, which can be used against the Ender Dragon.";
                break;
            case "compass":
                message += "Redstone in the middle, iron ingots in the N, S, E, W positions. " +
                         "Points to your spawn point. Doesn't work in the Nether or End.";
                break;
            case "bucket":
                message += "Iron ingots in a V shape. Great for carrying water or lava. " +
                         "You can use water to create obsidian, put out fires, or create cobblestone generators.";
                break;
            case "flintandsteel":
            case "flint and steel":
                message += "Iron ingot and flint diagonally. Used to light Nether Portals and set things on fire. " +
                         "You can get flint by breaking gravel.";
                break;
            case "eye of ender":
                message += "Blaze Powder + Ender Pearl. Used to locate and activate the End Portal. " +
                         "You'll need 12 to activate the portal and a few more to find it.";
                break;
            default:
                message = "§cI can't help with crafting " + itemName + " yet. Try: pickaxe, sword, bed, compass, bucket, flint and steel, or eye of ender.";
        }
        
        player.sendMessage(Text.of(message), false);
    }
    
    private static void createNetherPortal(PlayerEntity player) {
        if (player == null) return;
        
        // Guide to build a nether portal
        String message = "§eTo build a Nether Portal:§r\n" +
                       "1. Create a 4x5 obsidian frame (4 blocks wide, 5 blocks tall, corners optional)\n" +
                       "2. You'll need 10 obsidian total\n" +
                       "3. Light it with flint and steel\n\n" +
                       "§eTips:\n" +
                       "- Find a lava pool and pour water on it to create obsidian\n" +
                       "- You can make obsidian with 1 bucket of water and 1 bucket of lava\n" +
                       "- Flint and steel requires 1 iron ingot and 1 flint (from gravel)";
        
        player.sendMessage(Text.of(message), false);
        
        // Check if player has flint and steel or the materials to make it
        boolean hasFlint = player.getInventory().containsAny(stack -> stack.isOf(Items.FLINT));
        boolean hasIron = player.getInventory().containsAny(stack -> stack.isOf(Items.IRON_INGOT));
        
        if (!hasFlint || !hasIron) {
            player.sendMessage(Text.of("§cYou'll need flint and an iron ingot to make flint and steel! Try 'Simon says craft flint and steel'"), false);
        }
    }
    
    private static void prepareForNether(PlayerEntity player) {
        if (player == null) return;
        
        // Essential items for the Nether
        String message = "§eNether Preparation Checklist:§r\n" +
                       "§a✓§r Full set of iron armor (at least)\n" +
                       "§a✓§r Stone or better tools (bring spares)\n" +
                       "§a✓§r Plenty of food (cooked meat recommended)\n" +
                       "§a✓§r Bow and arrows (for Ghasts and Blazes)\n" +
                       "§a✓§r 10+ obsidian (for return portal)\n" +
                       "§a✓§r Flint and steel (in case portal breaks)\n" +
                       "§a✓§r Building blocks (cobblestone recommended - immune to Ghasts)\n" +
                       "§a✓§r Bucket of water (can extinguish fire and create obsidian)\n\n" +
                       "§eTips:\n" +
                       "- Build a safe shelter around your portal in the Nether\n" +
                       "- Don't break blocks below you in the Nether\n" +
                       "- Be careful of Ghasts - they can break your portal!";
        
        player.sendMessage(Text.of(message), false);
    }
    
    private static void findNetherFortress(PlayerEntity player) {
        if (player == null) return;
        
        // Tips for finding a Nether Fortress
        String message = "§eFinding a Nether Fortress:§r\n" +
                       "1. Nether Fortresses generate along north-south lines\n" +
                       "2. Move east or west from your portal to find one\n" +
                       "3. They're made of dark bricks with nether wart gardens\n\n" +
                       "§eWhat to bring:\n" +
                       "- Bow and arrows (for Blazes and Ghasts)\n" +
                       "- Stone or better sword (for Wither Skeletons)\n" +
                       "- Fire Resistance potions (highly recommended)\n" +
                       "- Food and building blocks\n" +
                       "- A way to mark your path (cobblestone or torches)\n\n" +
                       "§eLoot to look for:\n" +
                       "- Blaze Rods (from Blazes) - essential for brewing and Eyes of Ender\n" +
                       "- Wither Skeleton Skulls (rare drop) - needed to summon the Wither\n" +
                       "- Nether Wart - for brewing potions";
        
        player.sendMessage(Text.of(message), false);
    }
    
    private static void findStronghold(PlayerEntity player) {
        if (player == null) return;
        
        // Guide to finding the Stronghold
        String message = "§eFinding the Stronghold:§r\n" +
                       "1. You'll need Eyes of Ender (Blaze Powder + Ender Pearl)\n" +
                       "2. Throw an Eye of Ender and follow the direction it floats\n" +
                       "3. The Eye will point downward when above the Stronghold\n" +
                       "4. You'll need 12 Eyes of Ender to activate the End Portal\n\n" +
                       "§ePreparation Checklist:\n" +
                       "- Diamond gear (at least iron)\n" +
                       "- Plenty of food\n" +
                       "- Blocks for building and bridging\n" +
                       "- Water bucket (for MLG saves and Endermen)\n" +
                       "- Ender Pearls (for quick movement and escaping danger)\n" +
                       "- Potions (Fire Resistance, Strength, Speed)\n" +
                       "- Bow and lots of arrows\n" +
                       "- Pickaxe (for mining through walls if needed)";
        
        player.sendMessage(Text.of(message), false);
    }
    
    private static void prepareForDragon(PlayerEntity player) {
        if (player == null) return;
        
        // Preparation for the Ender Dragon fight
        String message = "§eEnder Dragon Fight Preparation:§r\n" +
                       "§a✓§r Diamond gear (at least iron)\n" +
                       "§a✓§r Bow with Power V and at least 2 stacks of arrows\n" +
                       "§a✓§r 4+ Ender Pearls (for escaping danger and reaching towers)\n" +
                       "§a✓§r Slow Falling potions (prevents fall damage from dragon's knockback)\n" +
                       "§a✓§r Water bucket (for MLG saves and Endermen)\n" +
                       "§a✓§r Blocks to pillar up (at least 64)\n" +
                       "§a✓§r 6+ beds (for damaging the dragon in Phase 2 - be careful!)\n" +
                       "§a✓§r Golden Apples (for emergency healing)\n" +
                       "§a✓§r Potions (Strength II, Speed II, Fire Resistance)\n\n" +
                       "§eFight Strategy:\n" +
                       "1. Destroy the End Crystals on top of the obsidian pillars first\n" +
                       "2. Some crystals are in iron cages - break the iron bars or shoot through them\n" +
                       "3. Use the pillars for cover from the dragon's attacks\n" +
                       "4. In Phase 2, the dragon will perch on the exit portal - use beds to damage it\n" +
                       "5. Watch out for Endermen - wear a pumpkin on your head to avoid their gaze";
        
        player.sendMessage(Text.of(message), false);
    }
}
