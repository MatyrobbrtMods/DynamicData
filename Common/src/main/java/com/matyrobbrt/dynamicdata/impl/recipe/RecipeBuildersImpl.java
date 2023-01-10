package com.matyrobbrt.dynamicdata.impl.recipe;

import com.matyrobbrt.dynamicdata.api.mutation.recipe.RecipeBuilders;
import com.matyrobbrt.dynamicdata.extensions.ExtendedRecipeBuilder;
import net.minecraft.data.recipes.RecipeCategory;
import net.minecraft.data.recipes.ShapedRecipeBuilder;
import net.minecraft.data.recipes.ShapelessRecipeBuilder;
import net.minecraft.data.recipes.UpgradeRecipeBuilder;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.ItemLike;

public class RecipeBuildersImpl implements RecipeBuilders {
    @Override
    public ShapedRecipeBuilder shaped(RecipeCategory category, ItemLike output) {
        return shaped(category, output, 1);
    }

    @Override
    public ShapedRecipeBuilder shaped(RecipeCategory category, ItemLike output, int outputCount) {
        return markRuntimeGenerated(ShapedRecipeBuilder.shaped(category, output, outputCount));
    }

    @Override
    public ShapelessRecipeBuilder shapeless(RecipeCategory category, ItemLike output) {
        return shapeless(category, output, 1);
    }

    @Override
    public ShapelessRecipeBuilder shapeless(RecipeCategory category, ItemLike output, int outputCount) {
        return markRuntimeGenerated(ShapelessRecipeBuilder.shapeless(category, output, outputCount));
    }

    @Override
    public UpgradeRecipeBuilder smithing(Ingredient base, Ingredient addition, RecipeCategory category, Item result) {
        return markRuntimeGenerated(UpgradeRecipeBuilder.smithing(base, addition, category, result));
    }

    @Override
    public <T> T markRuntimeGenerated(T builder) {
        ((ExtendedRecipeBuilder) builder).dynamicdata$markAsRuntimeGenerated();
        return builder;
    }
}
