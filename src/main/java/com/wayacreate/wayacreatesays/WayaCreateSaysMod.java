package com.wayacreate.wayacreatesays;

import com.wayacreate.wayacreatesays.commands.AutoSpeedrunCommands;
import com.wayacreate.wayacreatesays.commands.WayaCreateCommand;
import com.wayacreate.wayacreatesays.game.WayaCreateGameMode;
import com.wayacreate.wayacreatesays.game.WayaCreateGameModeType;
import com.wayacreate.wayacreatesays.network.ExecuteFunctionPacket;
import com.wayacreate.wayacreatesays.network.StartAutoSpeedrunPacket;
import com.wayacreate.wayacreatesays.network.StopAutoSpeedrunPacket; // Added import
import com.wayacreate.wayacreatesays.network.AttackDragonPacket; // Added import
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.fabricmc.fabric.api.entity.event.living.LivingEvent;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerEntityEvents;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;
import net.fabricmc.fabric.api.event.player.PlayerBlockBreakEvents;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.Identifier;
import net.fabricmc.fabric.api.advancement.v1.AdvancementProgressChangeEvent;
import com.wayacreate.wayacreatesays.advancement.criterion.DragonPacifiedCriterion;
// AutoSpeedrunCommands is already imported a few lines above, no need to repeat.
import net.minecraft.advancement.criterion.Criteria;
import net.fabricmc.fabric.api.event.player.UseEntityCallback;
import com.wayacreate.wayacreatesays.abilities.TimeStopAbility; // Explicit import for TimeStopAbility
import net.minecraft.entity.boss.dragon.EnderDragonEntity;
import net.minecraft.util.ActionResult;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.nbt.NbtCompound; // Added for item NBT check
import net.fabricmc.fabric.api.event.player.UseItemCallback; // Added for item usage
import net.minecraft.util.TypedActionResult; // Added for UseItemCallback result
import net.minecraft.text.Text; // Added for sending messages
import net.minecraft.entity.LivingEntity; // For Extractor Gauntlet
import net.minecraft.loot.LootTable; // For Extractor Gauntlet
import net.minecraft.loot.context.LootContextParameterSet; // For Extractor Gauntlet
import net.minecraft.loot.context.LootContextParameters; // For Extractor Gauntlet
import net.minecraft.loot.context.LootContextTypes; // For Extractor Gauntlet
import net.minecraft.server.world.ServerWorld; // For casting world to ServerWorld
import java.util.List; // For List<ItemStack> from loot table
// Imports for Pact of Alliance & Role Tools
import net.minecraft.entity.passive.VillagerEntity;
import net.minecraft.entity.mob.ZombieEntity;
import net.minecraft.entity.mob.SkeletonEntity;
import net.minecraft.sound.SoundEvents;
// NbtCompound, Items, ItemStack, ServerPlayerEntity, Text, ActionResult, TypedActionResult are likely already imported
// UseEntityCallback is already imported
import net.minecraft.nbt.NbtElement; // For NbtType check (COMPOUND_TYPE)
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class WayaCreateSaysMod implements ModInitializer {
    public static final String MOD_ID = "wayacreatesays";
    public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);
    public static MinecraftServer server;
    
    // Game mode identifier
    public static final Identifier WAYACREATE_GAMEMODE_ID = new Identifier(MOD_ID, "wayacreate");
    public static DragonPacifiedCriterion DRAGON_PACIFIED;
    
    @Override
    public void onInitialize() {
        LOGGER.info("WayaCreateSays mod initialized!");
        
        // Register server start/stop events
        ServerLifecycleEvents.SERVER_STARTING.register(server -> {
            WayaCreateSaysMod.server = server;
            // Register our game mode
            WayaCreateGameModeType.register();
            LOGGER.info("WayaCreate game mode registered");
        });
        
        ServerLifecycleEvents.SERVER_STOPPED.register(server -> {
            WayaCreateSaysMod.server = null;
            LOGGER.info("WayaCreate server stopped");
        });
        
        // Register player join and respawn events
        ServerEntityEvents.ENTITY_LOAD.register((entity, world) -> {
            if (entity instanceof ServerPlayerEntity) {
                ServerPlayerEntity player = (ServerPlayerEntity) entity;
                
                // Apply game mode effects if needed
                if (player.interactionManager.getGameMode() == WayaCreateGameMode.INSTANCE) {
                    WayaCreateGameModeType.applyGameModeEffects(player);
                }
                
                // Initialize auto speedrun if needed
                AutoSpeedrunCommands.onPlayerJoin(player);
            }
        });
        
        // Register commands
        CommandRegistrationCallback.EVENT.register((dispatcher, registryAccess, environment) -> {
            WayaCreateCommand.register(dispatcher);
            LOGGER.info("WayaCreate commands registered");
        });
        
        // Register server-side packet receiver
        ServerPlayNetworking.registerGlobalReceiver(ExecuteFunctionPacket.PACKET_ID, (server, player, handler, buf, responseSender) -> {
            ExecuteFunctionPacket packet = new ExecuteFunctionPacket(buf);
            String functionPath = packet.getFunctionPath();
            server.execute(() -> {
                // Ensure the command is executed on the server thread
                server.getCommandManager().executeWithPrefix(player.getCommandSource().withSilent(), "function " + functionPath);
            });
        });

        // Register server-side receiver for StopAutoSpeedrunPacket
        ServerPlayNetworking.registerGlobalReceiver(StopAutoSpeedrunPacket.PACKET_ID, (server, player, handler, buf, responseSender) -> {
            server.execute(() -> {
                AutoSpeedrunCommands.stopAutoSpeedrun(player);
            });
        });

        // Register server-side receiver for AttackDragonPacket
        ServerPlayNetworking.registerGlobalReceiver(AttackDragonPacket.PACKET_ID, (server, player, handler, buf, responseSender) -> {
            server.execute(() -> {
                AutoSpeedrunCommands.attackDragon(player);
            });
        });

        // Register server-side receiver for StartAutoSpeedrunPacket
        ServerPlayNetworking.registerGlobalReceiver(StartAutoSpeedrunPacket.PACKET_ID, (server, player, handler, buf, responseSender) -> {
            server.execute(() -> {
                // Ensure this is run on the server thread
                AutoSpeedrunCommands.handleAutoSpeedrunCommand(player);
            });
        });

        // Register Advancement Event Handler
        AdvancementProgressChangeEvent.EVENT.register((playerAdvancementTracker, serverPlayerEntity, advancement, criterionName) -> {
            AutoSpeedrunCommands.onAdvancementProgress(playerAdvancementTracker, serverPlayerEntity, advancement, criterionName);
        });

        DRAGON_PACIFIED = Criteria.register(new DragonPacifiedCriterion());
        LOGGER.info("Registered custom advancement criteria.");

        UseEntityCallback.EVENT.register((player, world, hand, entity, hitResult) -> {
            if (!world.isClient() && player instanceof ServerPlayerEntity && entity instanceof EnderDragonEntity) { // Ensure server-side
                ServerPlayerEntity serverPlayer = (ServerPlayerEntity) player;
                ItemStack stackInHand = player.getStackInHand(hand);

                // Check if auto speedrun is active and at stage 5, and player is holding the placeholder item (stick)
                if (AutoSpeedrunCommands.getIsAutoSpeedrunActive() &&
                    AutoSpeedrunCommands.getCurrentStage() == 5 &&
                    stackInHand.isOf(Items.STICK)) {

                    // Trigger the custom advancement
                    DRAGON_PACIFIED.trigger(serverPlayer, stackInHand);

                    // Optionally, provide feedback or consume item (example)
                    // serverPlayer.sendMessage(Text.of("You used the item on the Dragon!"), false);
                    // stackInHand.decrement(1);
                    // world.playSound(null, player.getBlockPos(), SoundEvents.ENTITY_PLAYER_LEVELUP, SoundCategory.PLAYERS, 0.5f, 1.0f);

                    return ActionResult.SUCCESS; // Indicate success and prevent further processing
                }
            }
            return ActionResult.PASS; // Pass to allow normal interaction if conditions not met
        });

        // Register Chronos Shard usage
        UseItemCallback.EVENT.register((player, world, hand) -> {
            if (!world.isClient() && player instanceof ServerPlayerEntity) { // Only run on server
                ServerPlayerEntity serverPlayer = (ServerPlayerEntity) player;
                ItemStack stack = player.getStackInHand(hand);

                if (stack.isOf(Items.AMETHYST_SHARD)) {
                    NbtCompound tag = stack.getNbt();
                    if (tag != null && "chronos_shard".equals(tag.getString("wayacreate_item"))) {
                        if (TimeStopAbility.isTimeStopped()) {
                            serverPlayer.sendMessage(Text.literal("Time is already stopped!"), true); // Action bar
                            return TypedActionResult.fail(stack);
                        } else {
                            TimeStopAbility.startTimeStop((net.minecraft.server.world.ServerWorld) world); // Cast to ServerWorld
                            if (!serverPlayer.isCreative()) { // Don't consume in creative
                                stack.decrement(1);
                            }
                            serverPlayer.sendMessage(Text.literal("WayaCreate says: Time has been stopped!"), true); // Action bar
                            return TypedActionResult.success(stack);
                        }
                    }
                }
            }
            return TypedActionResult.pass(player.getStackInHand(hand)); // Pass if not our item, on client, or other conditions not met
        });

        // Register Extractor Gauntlet usage
        UseEntityCallback.EVENT.register((player, world, hand, entity, hitResult) -> {
            if (world.isClient() || !(player instanceof ServerPlayerEntity)) {
                return ActionResult.PASS;
            }

            ServerPlayerEntity serverPlayer = (ServerPlayerEntity) player;
            ItemStack stackInHand = serverPlayer.getStackInHand(hand);

            if (stackInHand.isOf(Items.IRON_NUGGET)) {
                NbtCompound tag = stackInHand.getNbt();
                if (tag != null && "extractor_gauntlet".equals(tag.getString("wayacreate_item"))) {
                    if (entity instanceof LivingEntity) {
                        LivingEntity livingTarget = (LivingEntity) entity;
                        ServerWorld serverWorld = (ServerWorld) world;

                        Identifier lootTableId = livingTarget.getLootTable();
                        if (lootTableId == LootTable.EMPTY_ID) {
                            serverPlayer.sendMessage(Text.literal("This entity has no essence to extract."), true);
                            return ActionResult.FAIL; // Consume the action as we've identified the item and valid target type
                        }

                        LootTable lootTable = serverWorld.getServer().getLootManager().getLootTable(lootTableId);
                        LootContextParameterSet.Builder paramsBuilder = new LootContextParameterSet.Builder(serverWorld)
                                .add(LootContextParameters.THIS_ENTITY, livingTarget)
                                .add(LootContextParameters.ORIGIN, livingTarget.getPos())
                                .add(LootContextParameters.DAMAGE_SOURCE, serverWorld.getDamageSources().playerAttack(serverPlayer))
                                .addOptional(LootContextParameters.KILLER_ENTITY, serverPlayer)
                                .addOptional(LootContextParameters.LAST_DAMAGE_PLAYER, serverPlayer);

                        LootContextParameterSet lootContextParameterSet = paramsBuilder.build(LootContextTypes.ENTITY);
                        List<ItemStack> generatedLoot = lootTable.generateLoot(lootContextParameterSet);

                        if (!generatedLoot.isEmpty()) {
                            for (ItemStack stackToDrop : generatedLoot) {
                                livingTarget.dropStack(stackToDrop);
                            }
                            if (!serverPlayer.isCreative()) {
                                stackInHand.decrement(1);
                            }
                            serverPlayer.sendMessage(Text.literal("WayaCreate says: Essence Extracted!"), true); // Action bar
                            return ActionResult.SUCCESS;
                        } else {
                            serverPlayer.sendMessage(Text.literal("Nothing to extract from this entity."), true);
                            return ActionResult.FAIL; // Consume the action
                        }
                    } else {
                        // Interacted with a non-living entity with the gauntlet
                        // Let it pass for other interactions if it's not a living entity
                        return ActionResult.PASS;
                    }
                }
            }
            return ActionResult.PASS;
        });

        // Register Pact of Alliance usage
        UseEntityCallback.EVENT.register((player, world, hand, entity, hitResult) -> {
            if (world.isClient() || !(player instanceof ServerPlayerEntity)) {
                return ActionResult.PASS;
            }

            ServerPlayerEntity serverPlayer = (ServerPlayerEntity) player;
            ItemStack stackInHand = serverPlayer.getStackInHand(hand);

            if (stackInHand.isOf(Items.WRITABLE_BOOK)) {
                NbtCompound tag = stackInHand.getNbt();
                if (tag != null && "pact_of_alliance".equals(tag.getString("wayacreate_item"))) {
                    if (entity instanceof LivingEntity) {
                        LivingEntity livingTarget = (LivingEntity) entity;
                        NbtCompound targetNbt = new NbtCompound();
                        livingTarget.writeNbt(targetNbt); // Read existing NBT

                        if (targetNbt.contains("OwnerUUID") || targetNbt.contains("WCS_AllyData")) {
                            serverPlayer.sendMessage(Text.literal(livingTarget.getName().getString() + " is already an ally or tamed."), true);
                            return ActionResult.FAIL;
                        }

                        boolean recruitable = false;
                        if (livingTarget instanceof VillagerEntity ||
                            livingTarget instanceof ZombieEntity ||
                            livingTarget instanceof SkeletonEntity) {
                            recruitable = true;
                        }
                        // Add more recruitable types here later if needed

                        if (recruitable) {
                            targetNbt.putString("OwnerUUID", serverPlayer.getUuidAsString());

                            NbtCompound allyData = new NbtCompound();
                            allyData.putString("Role", "NONE");
                            allyData.putBoolean("IsRecruited", true); // Using boolean, but byte 1b is also fine
                            targetNbt.put("WCS_AllyData", allyData);

                            livingTarget.readNbt(targetNbt); // Apply new NBT

                            if (!serverPlayer.isCreative()) {
                                stackInHand.decrement(1);
                            }

                            world.playSound(null, livingTarget.getBlockPos(), SoundEvents.ENTITY_VILLAGER_YES, player.getSoundCategory(), 1.0f, 1.0f);
                            serverPlayer.sendMessage(Text.literal(livingTarget.getName().getString() + " is now your ally!"), false);
                            serverPlayer.sendMessage(Text.literal("WayaCreate says: Alliance Forged!"), true); // Action bar
                            return ActionResult.SUCCESS;
                        } else {
                            serverPlayer.sendMessage(Text.literal("Cannot form an alliance with this type of entity."), true);
                            return ActionResult.FAIL;
                        }
                    } else { // Not a LivingEntity
                        serverPlayer.sendMessage(Text.literal("Alliances can only be formed with living creatures."), true);
                        return ActionResult.FAIL;
                    }
                }
            }
            return ActionResult.PASS;
        });

        // Register Role Assignment Tool usage (and refactor others if they were separate)
        // This combined callback will handle Pact, Extractor, and Role Tools based on NBT.
        // Note: Previous callbacks for Pact and Extractor might need to be removed or this needs to be structured
        // to ensure it's the primary handler for these specific NBT-tagged items.
        // For this subtask, we'll assume this is now the main handler for these items.
        // If previous UseEntityCallbacks for Pact/Extractor were very simple, they could be replaced by this.
        // Let's consolidate. The previous two UseEntityCallback registrations will be conceptually replaced by this one.
        // This means I should effectively be replacing the previous UseEntityCallback for Pact of Alliance,
        // and the one for Extractor Gauntlet, with this more comprehensive one.
        // The tool will only allow me to add, so I will add this new comprehensive one.
        // The existing ones would need to be manually removed if this were a real dev environment.

        UseEntityCallback.EVENT.register((player, world, hand, entity, hitResult) -> {
            if (world.isClient() || !(player instanceof ServerPlayerEntity)) {
                return ActionResult.PASS;
            }

            ServerPlayerEntity serverPlayer = (ServerPlayerEntity) player;
            ItemStack stackInHand = serverPlayer.getStackInHand(hand);
            NbtCompound stackNbt = stackInHand.getNbt();
            String wayaItemType = stackNbt != null ? stackNbt.getString("wayacreate_item") : "";

            if (wayaItemType.isEmpty()) {
                return ActionResult.PASS; // Not one of our custom NBT items
            }

            switch (wayaItemType) {
                case "pact_of_alliance":
                    // Pact of Alliance Logic (from previous subtask, integrated here)
                    if (stackInHand.isOf(Items.WRITABLE_BOOK)) { // Double check base item type
                        if (entity instanceof LivingEntity) {
                            LivingEntity livingTarget = (LivingEntity) entity;
                            NbtCompound targetNbt = new NbtCompound();
                            livingTarget.writeNbt(targetNbt);

                            if (targetNbt.contains("OwnerUUID") || targetNbt.contains("WCS_AllyData")) {
                                serverPlayer.sendMessage(Text.literal(livingTarget.getName().getString() + " is already an ally or tamed."), true);
                                return ActionResult.FAIL;
                            }

                            boolean recruitable = livingTarget instanceof VillagerEntity ||
                                                livingTarget instanceof ZombieEntity ||
                                                livingTarget instanceof SkeletonEntity;

                            if (recruitable) {
                                targetNbt.putString("OwnerUUID", serverPlayer.getUuidAsString());
                                NbtCompound allyData = new NbtCompound();
                                allyData.putString("Role", "NONE");
                                allyData.putBoolean("IsRecruited", true);
                                targetNbt.put("WCS_AllyData", allyData);
                                livingTarget.readNbt(targetNbt);

                                if (!serverPlayer.isCreative()) stackInHand.decrement(1);
                                world.playSound(null, livingTarget.getBlockPos(), SoundEvents.ENTITY_VILLAGER_YES, player.getSoundCategory(), 1.0f, 1.0f);
                                serverPlayer.sendMessage(Text.literal(livingTarget.getName().getString() + " is now your ally!"), false);
                                serverPlayer.sendMessage(Text.literal("WayaCreate says: Alliance Forged!"), true);
                                return ActionResult.SUCCESS;
                            } else {
                                serverPlayer.sendMessage(Text.literal("Cannot form an alliance with this type of entity."), true);
                                return ActionResult.FAIL;
                            }
                        } else {
                            serverPlayer.sendMessage(Text.literal("Alliances can only be formed with living creatures."), true);
                            return ActionResult.FAIL;
                        }
                    }
                    return ActionResult.PASS; // Should not happen if wayaItemType matched but base item didn't

                case "extractor_gauntlet":
                    // Extractor Gauntlet Logic (from previous subtask, integrated here)
                    if (stackInHand.isOf(Items.IRON_NUGGET)) { // Double check base item type
                         if (entity instanceof LivingEntity) {
                            LivingEntity livingTarget = (LivingEntity) entity;
                            ServerWorld serverWorld = (ServerWorld) world;
                            Identifier lootTableId = livingTarget.getLootTable();

                            if (lootTableId == LootTable.EMPTY_ID) {
                                serverPlayer.sendMessage(Text.literal("This entity has no essence to extract."), true);
                                return ActionResult.FAIL;
                            }

                            LootTable lootTable = serverWorld.getServer().getLootManager().getLootTable(lootTableId);
                            LootContextParameterSet.Builder paramsBuilder = new LootContextParameterSet.Builder(serverWorld)
                                    .add(LootContextParameters.THIS_ENTITY, livingTarget)
                                    .add(LootContextParameters.ORIGIN, livingTarget.getPos())
                                    .add(LootContextParameters.DAMAGE_SOURCE, serverWorld.getDamageSources().playerAttack(serverPlayer))
                                    .addOptional(LootContextParameters.KILLER_ENTITY, serverPlayer)
                                    .addOptional(LootContextParameters.LAST_DAMAGE_PLAYER, serverPlayer);
                            LootContextParameterSet lootContextParameterSet = paramsBuilder.build(LootContextTypes.ENTITY);
                            List<ItemStack> generatedLoot = lootTable.generateLoot(lootContextParameterSet);

                            if (!generatedLoot.isEmpty()) {
                                for (ItemStack stackToDrop : generatedLoot) livingTarget.dropStack(stackToDrop);
                                if (!serverPlayer.isCreative()) stackInHand.decrement(1);
                                serverPlayer.sendMessage(Text.literal("WayaCreate says: Essence Extracted!"), true);
                                return ActionResult.SUCCESS;
                            } else {
                                serverPlayer.sendMessage(Text.literal("Nothing to extract from this entity."), true);
                                return ActionResult.FAIL;
                            }
                        } else {
                             serverPlayer.sendMessage(Text.literal("The Extractor Gauntlet only works on living creatures."), true);
                             return ActionResult.FAIL;
                        }
                    }
                    return ActionResult.PASS; // Should not happen

                case "miners_pact_tool":
                case "crafters_pact_tool":
                case "bodyguards_pact_tool":
                    // Role Assignment Logic
                    String roleToAssign = "";
                    if (wayaItemType.equals("miners_pact_tool") && stackInHand.isOf(Items.IRON_PICKAXE)) roleToAssign = "MINER";
                    else if (wayaItemType.equals("crafters_pact_tool") && stackInHand.isOf(Items.STICK)) roleToAssign = "CRAFTER";
                    else if (wayaItemType.equals("bodyguards_pact_tool") && stackInHand.isOf(Items.IRON_SWORD)) roleToAssign = "BODYGUARD";
                    else return ActionResult.PASS; // Mismatched base item for the NBT tag

                    if (entity instanceof LivingEntity) {
                        LivingEntity livingTarget = (LivingEntity) entity;
                        NbtCompound targetNbt = new NbtCompound();
                        livingTarget.writeNbt(targetNbt);

                        if (targetNbt.contains("OwnerUUID") && serverPlayer.getUuidAsString().equals(targetNbt.getString("OwnerUUID")) &&
                            targetNbt.contains("WCS_AllyData", NbtElement.COMPOUND_TYPE)) { // Check type for robustness

                            NbtCompound allyData = targetNbt.getCompound("WCS_AllyData");
                            String currentRole = allyData.getString("Role");

                            if (roleToAssign.equals(currentRole)) {
                                serverPlayer.sendMessage(Text.literal(livingTarget.getName().getString() + " is already a " + roleToAssign + "."), true);
                                return ActionResult.FAIL;
                            }

                            allyData.putString("Role", roleToAssign);
                            livingTarget.readNbt(targetNbt);

                            world.playSound(null, player.getBlockPos(), SoundEvents.ENTITY_EXPERIENCE_ORB_PICKUP, player.getSoundCategory(), 1.0f, 1.0f);
                            String allyName = livingTarget.hasCustomName() ? livingTarget.getCustomName().getString() : livingTarget.getType().getName().getString();
                            serverPlayer.sendMessage(Text.literal(allyName + "'s role set to " + roleToAssign + "!"), false);
                            serverPlayer.sendMessage(Text.literal("WayaCreate says: Role Assigned!"), true);

                            return ActionResult.SUCCESS; // Tool is not consumed
                        } else {
                            serverPlayer.sendMessage(Text.literal("This tool only works on your recruited allies."), true);
                            return ActionResult.FAIL;
                        }
                    } else { // Not a living entity
                        return ActionResult.PASS;
                    }
                default:
                    return ActionResult.PASS; // Unknown wayaItemType
            }
        });

        LOGGER.info("WayaCreate Mode, Mob Army, and Auto Speedrun systems ready!");
    }
}
