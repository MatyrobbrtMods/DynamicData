package com.matyrobbrt.dynamicdata.api.mutation.recipe;

import com.matyrobbrt.dynamicdata.extensions.ExtendedRecipeBuilder;
import net.minecraft.data.recipes.RecipeBuilder;
import net.minecraft.data.recipes.RecipeCategory;
import net.minecraft.data.recipes.ShapedRecipeBuilder;
import net.minecraft.data.recipes.ShapelessRecipeBuilder;
import net.minecraft.data.recipes.UpgradeRecipeBuilder;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.ItemLike;
import org.jetbrains.annotations.ApiStatus;

/**
 * Recipe builder creator. <br>
 * Builders created by this builder will be {@linkplain #markRuntimeGenerated(Object) marked as runtime generated}
 * so that it does not require advacements.
 */
public interface RecipeBuilders {
    ShapedRecipeBuilder shaped(RecipeCategory category, ItemLike output);

    ShapedRecipeBuilder shaped(RecipeCategory category, ItemLike output, int outputCount);

    ShapelessRecipeBuilder shapeless(RecipeCategory category, ItemLike output);

    ShapelessRecipeBuilder shapeless(RecipeCategory category, ItemLike output, int outputCount);

    UpgradeRecipeBuilder smithing(Ingredient base, Ingredient addition, RecipeCategory category, Item result);

    @ApiStatus.Internal
    <T> T markRuntimeGenerated(T builder);
}
