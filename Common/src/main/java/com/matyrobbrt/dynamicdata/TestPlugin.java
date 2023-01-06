package com.matyrobbrt.dynamicdata;

import com.matyrobbrt.dynamicdata.api.LootTableMutator;
import com.matyrobbrt.dynamicdata.api.SplashMutator;
import com.matyrobbrt.dynamicdata.api.advancement.AdvancementMutator;
import com.matyrobbrt.dynamicdata.api.annotation.RegisterPlugin;
import com.matyrobbrt.dynamicdata.api.plugin.DynamicDataPlugin;
import com.matyrobbrt.dynamicdata.api.recipe.RecipeMutator;
import net.minecraft.advancements.Advancement;
import net.minecraft.advancements.AdvancementRewards;
import net.minecraft.advancements.critereon.EntityPredicate;
import net.minecraft.advancements.critereon.InventoryChangeTrigger;
import net.minecraft.advancements.critereon.ItemPredicate;
import net.minecraft.advancements.critereon.MinMaxBounds;
import net.minecraft.core.NonNullList;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.ItemTags;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.CraftingBookCategory;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.item.crafting.ShapelessRecipe;
import net.minecraft.world.level.storage.loot.LootPool;
import net.minecraft.world.level.storage.loot.LootTable;
import net.minecraft.world.level.storage.loot.entries.LootItem;
import net.minecraft.world.level.storage.loot.providers.number.ConstantValue;
import net.minecraft.world.level.storage.loot.providers.number.UniformGenerator;

@RegisterPlugin
public class TestPlugin implements DynamicDataPlugin {

    @Override
    public void collectMutatorListeners(MutatorCollector collector) {
        collector.accept(RecipeMutator.class, mutator -> {
            mutator.removeAllWithResult(Items.ANDESITE);

            mutator.removeAllOfType(RecipeType.BLASTING);
            mutator.replace(new ResourceLocation("crafting_table"), id -> new ShapelessRecipe(
                    id, "", CraftingBookCategory.MISC, Items.CRAFTING_TABLE.getDefaultInstance(),
                    NonNullList.of(Ingredient.EMPTY, Ingredient.of(Items.BIRCH_BOAT), Ingredient.of(Items.ACACIA_DOOR))
            ));

            mutator.remove(new ResourceLocation("diorite"));
        });

        collector.accept(AdvancementMutator.class, mutator -> {
            mutator.remove(new ResourceLocation("end/dragon_breath"));

            mutator.add(new ResourceLocation("custom_advancement"), Advancement.Builder.advancement()
                    .addCriterion("has_jplanks", new InventoryChangeTrigger.TriggerInstance(
                            EntityPredicate.Composite.ANY, MinMaxBounds.Ints.ANY,
                            MinMaxBounds.Ints.ANY, MinMaxBounds.Ints.ANY,
                            new ItemPredicate[]{ ItemPredicate.Builder.item().of(Items.JUNGLE_PLANKS).build() }
                    ))
                    .rewards(AdvancementRewards.Builder.loot(new ResourceLocation("blocks/allium"))));
        });

        collector.acceptTagMutator(Registries.ITEM, mutator -> {
            mutator.getOrCreateTag(ItemTags.PLANKS).clear();

            mutator.getOrCreateTag(new ResourceLocation("test:my_custom_tag"))
                    .addEntry(Items.ANDESITE)
                    .addEntry(Items.BAMBOO_STAIRS)
                    .addTag(ItemTags.BANNERS);

            mutator.getOrCreateTag(ItemTags.STONE_CRAFTING_MATERIALS)
                    .clear()
                    .addEntry(Items.BLUE_ORCHID)
                    .addTag(ItemTags.BEDS);
        });

        collector.accept(LootTableMutator.class, mutator -> {
            mutator.replaceWithEmpty(new ResourceLocation("blocks/crafting_table"));

            mutator.add(new ResourceLocation("blocks/birch_planks"), LootTable.lootTable()
                    .withPool(LootPool.lootPool()
                            .setRolls(ConstantValue.exactly(12))
                            .add(LootItem.lootTableItem(Items.ACACIA_DOOR)))
                    .withPool(LootPool.lootPool()
                            .setRolls(UniformGenerator.between(0, 1))
                            .add(LootItem.lootTableItem(Items.STRING))));
        });

        collector.accept(SplashMutator.class, mutator -> {
            mutator.clear();
            mutator.addSplash("Su5eD is concerning!");
            mutator.addSplash("Curle didn't break me (yet)!");
        });
    }
}
