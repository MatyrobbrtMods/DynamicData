package com.matyrobbrt.dynamicdata.api.mutation.recipe;

import com.google.gson.JsonObject;
import com.matyrobbrt.dynamicdata.api.mutation.DataMutator;
import net.minecraft.data.recipes.FinishedRecipe;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.crafting.Recipe;

import java.util.function.Consumer;
import java.util.function.Function;

/**
 * A mutator used <strong>only to add</strong> new recipes. <br>
 * If you want to replace or remove recipes, use {@link RecipeMutator} instead.
 * It is preferred you use this mutator if you only need to add recipes as it will not create
 * mutable map copies to inject the recipes, and it will instead {@linkplain com.google.common.collect.ImmutableMap.Builder#put(Object, Object) add them to the builder}.
 *
 * @see net.minecraft.world.item.crafting.RecipeManager
 */
public interface AddRecipeMutator extends DataMutator {
    /**
     * Adds a recipe.
     */
    void add(Recipe<?> recipe);

    /**
     * Adds a recipe.
     *
     * @param recipeId       the ID of the recipe
     * @param recipeFunction a function that will create the recipe
     */
    void add(ResourceLocation recipeId, Function<ResourceLocation, Recipe<?>> recipeFunction);

    /**
     * Adds a recipe.
     *
     * @param recipeId the ID of the recipe
     * @param json     the JSON representation of the recipe.
     */
    void add(ResourceLocation recipeId, JsonObject json);

    /**
     * {@return a consumer which can be used to save datagenerated recipes}<br>
     * This consumer will also queue the recipe's advancements (if present) to the next advancement mutation cycle.
     */
    Consumer<FinishedRecipe> getFinishedRecipeConsumer();

    /**
     * {@return a helper for creating recipe builders which do not need an advancement linked}
     */
    RecipeBuilders getBuilders();
}
