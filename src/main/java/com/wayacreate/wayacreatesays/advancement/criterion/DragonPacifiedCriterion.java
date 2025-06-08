package com.wayacreate.wayacreatesays.advancement.criterion;

import com.google.gson.JsonObject;
import com.google.gson.JsonSyntaxException;
import com.wayacreate.wayacreatesays.WayaCreateSaysMod;
import net.minecraft.advancement.criterion.AbstractCriterion;
import net.minecraft.advancement.criterion.AbstractCriterionConditions;
import net.minecraft.item.ItemStack;
import net.minecraft.predicate.entity.AdvancementEntityPredicateDeserializer;
import net.minecraft.predicate.entity.AdvancementEntityPredicateSerializer;
import net.minecraft.predicate.item.ItemPredicate;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.Identifier;
import net.minecraft.util.JsonHelper;

public class DragonPacifiedCriterion extends AbstractCriterion<DragonPacifiedCriterion.Conditions> {
    public static final Identifier ID = new Identifier(WayaCreateSaysMod.MOD_ID, "dragon_pacified_with_essence_of_waya");

    @Override
    public Identifier getId() {
        return ID;
    }

    @Override
    public Conditions conditionsFromJson(JsonObject jsonObject, AdvancementEntityPredicateDeserializer advancementEntityPredicateDeserializer) {
        ItemPredicate itemPredicate = ItemPredicate.fromJson(jsonObject.get("item"));
        if (itemPredicate == null) {
            // Handle cases where "item" might be missing or malformed, though ItemPredicate.fromJson usually returns ANY if null
            throw new JsonSyntaxException("Missing or malformed 'item' field in criterion conditions");
        }
        return new Conditions(itemPredicate);
    }

    public void trigger(ServerPlayerEntity player, ItemStack itemStack) {
        this.trigger(player, conditions -> conditions.test(itemStack));
    }

    public static class Conditions extends AbstractCriterionConditions {
        private final ItemPredicate item;

        public Conditions(ItemPredicate item) {
            super(ID, null); // No entity predicate for the criterion itself
            this.item = item;
        }

        public static Conditions create(ItemPredicate itemPredicate) {
            return new Conditions(itemPredicate);
        }

        public boolean test(ItemStack stack) {
            return this.item.test(stack);
        }

        @Override
        public JsonObject toJson(AdvancementEntityPredicateSerializer predicateSerializer) {
            JsonObject jsonObject = super.toJson(predicateSerializer);
            if (this.item != ItemPredicate.ANY) { // Avoid serializing "ANY" predicate if not necessary
                 jsonObject.add("item", this.item.toJson());
            }
            return jsonObject;
        }
    }
}
