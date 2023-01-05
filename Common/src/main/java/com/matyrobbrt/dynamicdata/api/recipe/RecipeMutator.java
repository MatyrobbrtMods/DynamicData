package com.matyrobbrt.dynamicdata.api.recipe;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeType;

import java.util.Map;
import java.util.function.Function;
import java.util.function.Predicate;

public interface RecipeMutator {
    void add(Recipe<?> recipe);
    void add(ResourceLocation recipeId, Function<ResourceLocation, Recipe<?>> recipeFunction);

    void removeMatching(Predicate<Recipe<?>> predicate);
    void removeAllOfType(RecipeType<?> recipeType);
    <T extends Recipe<?>> void removeOfTypeMatching(RecipeType<T> recipeType, Predicate<T> tester);

    void remove(Recipe<?> recipe);
    void remove(ResourceLocation recipeId);

    void removeAllWithResult(Item result);

    // TODO maybe the factories should take the old recipe?
    void replace(ResourceLocation id, Function<ResourceLocation, Recipe<?>> replacementFactory);
    void replace(Recipe<?> toReplace, Function<ResourceLocation, Recipe<?>> replacementFactory);

    Map<ResourceLocation, Recipe<?>> getAllRecipes();
}
