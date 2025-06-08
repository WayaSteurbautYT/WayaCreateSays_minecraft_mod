package com.wayacreate.wayacreatesays.commands;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import net.minecraft.command.argument.EntityArgumentType;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.loot.LootTable;
import net.minecraft.loot.context.LootContextParameterSet;
import net.minecraft.loot.context.LootContextParameters;
import net.minecraft.loot.context.LootContextTypes;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import java.util.Collection;
import java.util.List;

import static net.minecraft.server.command.CommandManager.argument;
import static net.minecraft.server.command.CommandManager.literal;

public class ForceDropCommand {
    public static void register(CommandDispatcher<ServerCommandSource> dispatcher) {
        dispatcher.register(literal("forcedrop")
            .requires(source -> source.hasPermissionLevel(2)) // OP level 2
            .then(argument("targets", EntityArgumentType.entities())
                .executes(context -> executeForceDrop(context, EntityArgumentType.getEntities(context, "targets")))
            )
        );
    }

    private static int executeForceDrop(CommandContext<ServerCommandSource> context, Collection<? extends Entity> targets) throws CommandSyntaxException {
        ServerCommandSource source = context.getSource();
        ServerWorld world = source.getWorld();
        // Get player for loot context; this might be null if executed by a command block or console.
        // LootContextParameterSet builder handles null player for KILLER_ENTITY if optional.
        PlayerEntity player = source.getPlayer(); // Can be null, handle appropriately if LootContext requires non-null.
                                               // Player is required for playerAttack damage source.
                                               // If player is null, we might need a different damage source or adapt.
                                               // Forcing via player is often what's desired for player-specific drops.

        if (player == null) { // Ensure player context for playerAttack
            source.sendError(Text.literal("This command must be run by a player to simulate player kill context."));
            return 0;
        }

        int successfulDrops = 0;

        for (Entity target : targets) {
            if (target instanceof LivingEntity) {
                LivingEntity livingTarget = (LivingEntity) target;
                Identifier lootTableId = livingTarget.getLootTable();

                // Skip if lootTable is EMPTY (e.g. for some special entities or if modded)
                if (lootTableId == LootTable.EMPTY_ID) {
                    continue;
                }

                LootTable lootTable = world.getServer().getLootManager().getLootTable(lootTableId);

                LootContextParameterSet.Builder paramsBuilder = new LootContextParameterSet.Builder(world)
                        .add(LootContextParameters.THIS_ENTITY, livingTarget)
                        .add(LootContextParameters.ORIGIN, livingTarget.getPos())
                        .add(LootContextParameters.DAMAGE_SOURCE, world.getDamageSources().playerAttack(player))
                        .addOptional(LootContextParameters.KILLER_ENTITY, player) // KILLER_ENTITY is the direct cause
                        .addOptional(LootContextParameters.LAST_DAMAGE_PLAYER, player); // LAST_DAMAGE_PLAYER is for attribution

                // To consider looting level, one would typically need the tool used.
                // float luck = (player != null) ? player.getLuck() : 0.0F;
                // paramsBuilder.add(LootContextParameters.LUCK, luck);
                // If a tool is involved, e.g. player.getMainHandStack(), then:
                // paramsBuilder.addOptional(LootContextParameters.TOOL, player.getMainHandStack());

                LootContextParameterSet lootContextParameterSet = paramsBuilder.build(LootContextTypes.ENTITY);
                List<ItemStack> generatedLoot = lootTable.generateLoot(lootContextParameterSet);

                for (ItemStack stack : generatedLoot) {
                    livingTarget.dropStack(stack);
                }
                if (!generatedLoot.isEmpty()) { // Count only if loot was actually generated and dropped
                    successfulDrops++;
                }
            }
        }

        if (successfulDrops > 0) {
            source.sendFeedback(() -> Text.literal("Forced drops for " + successfulDrops + " entity/entities."), true);
        } else {
            source.sendFeedback(() -> Text.literal("No valid living entities found, they have no loot, or no loot was generated."), false);
        }
        return successfulDrops;
    }
}
