package com.matyrobbrt.dynamicdata;

import com.matyrobbrt.dynamicdata.api.annotation.RegisterPlugin;
import com.matyrobbrt.dynamicdata.api.plugin.DynamicDataPlugin;
import com.matyrobbrt.dynamicdata.api.recipe.RecipeMutator;
import net.minecraft.core.NonNullList;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.CraftingBookCategory;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.item.crafting.ShapelessRecipe;

@RegisterPlugin
public class TestPlugin implements DynamicDataPlugin {
    @Override
    public void mutateRecipes(RecipeMutator mutator) {
        mutator.removeAllWithResult(Items.ANDESITE);

        mutator.removeAllOfType(RecipeType.BLASTING);
        mutator.replace(new ResourceLocation("crafting_table"), id -> new ShapelessRecipe(
                id, "", CraftingBookCategory.MISC, Items.CRAFTING_TABLE.getDefaultInstance(),
                NonNullList.of(Ingredient.EMPTY, Ingredient.of(Items.BIRCH_BOAT), Ingredient.of(Items.ACACIA_DOOR))
        ));

        mutator.remove(new ResourceLocation("diorite"));
    }
}
