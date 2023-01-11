package com.matyrobbrt.dynamicdata.api.mutation.recipe;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeType;

import java.util.Map;
import java.util.function.Function;
import java.util.function.Predicate;

/**
 * A mutator used to mutate recipes. <br>
 * Unlike {@linkplain AddRecipeMutator}, this mutator can view all recipes and remove them. <br>
 * It is preferred you use {@link AddRecipeMutator} if you only need to add recipes as it will not create
 * mutable map copies to inject the recipes, and it will instead {@linkplain com.google.common.collect.ImmutableMap.Builder#put(Object, Object) add them to the builder}.
 *
 * @see net.minecraft.world.item.crafting.RecipeManager
 */
public interface RecipeMutator extends AddRecipeMutator {
    /**
     * Removes all recipes matching the given {@code predicate}.
     */
    void removeMatching(Predicate<Recipe<?>> predicate);

    /**
     * Removes all recipes of the given {@code recipeType}.
     */
    void removeAllOfType(RecipeType<?> recipeType);

    /**
     * Removes all recipes of the given {@code recipeType} that also match the {@code predicate}.
     */
    <T extends Recipe<?>> void removeOfTypeMatching(RecipeType<T> recipeType, Predicate<T> predicate);

    /**
     * Removes a recipe.
     */
    void remove(Recipe<?> recipe);

    /**
     * Removes a recipe.
     *
     * @param recipeId the ID of the recipe to remove
     */
    void remove(ResourceLocation recipeId);

    /**
     * Removes all recipes whose {@linkplain Recipe#getResultItem() result} matches the given {@code result}.
     */
    void removeAllWithResult(Item result);

    // TODO maybe the factories should take the old recipe?

    /**
     * Replaces a recipe with another one.
     *
     * @param id                 the ID of the recipe to replace
     * @param replacementFactory a function creating the replacement recipe
     */
    void replace(ResourceLocation id, Function<ResourceLocation, Recipe<?>> replacementFactory);

    /**
     * Replaces a recipe with another one.
     *
     * @param toReplace          the recipe to replace
     * @param replacementFactory a function creating the replacement recipe
     */
    void replace(Recipe<?> toReplace, Function<ResourceLocation, Recipe<?>> replacementFactory);

    /**
     * {@return all recipes}
     */
    Map<ResourceLocation, Recipe<?>> getAllRecipes();
}
