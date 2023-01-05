package com.matyrobbrt.dynamicdata.impl.recipe;

import com.matyrobbrt.dynamicdata.api.recipe.RecipeMutator;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeType;

import java.util.Map;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.function.Supplier;

public record RecipeMutatorImpl(Supplier<Map<ResourceLocation, Recipe<?>>> supplier) implements RecipeMutator {

    @Override
    public void add(Recipe<?> recipe) {
        supplier.get().put(recipe.getId(), recipe);
    }

    @Override
    public void add(ResourceLocation recipeId, Function<ResourceLocation, Recipe<?>> recipeFunction) {
        supplier.get().put(recipeId, recipeFunction.apply(recipeId));
    }

    @Override
    public void removeMatching(Predicate<Recipe<?>> predicate) {
        supplier.get().entrySet().removeIf(resourceLocationRecipeEntry -> predicate.test(resourceLocationRecipeEntry.getValue()));
    }

    @Override
    public void removeAllOfType(RecipeType<?> recipeType) {
        this.removeMatching(recipe -> recipe.getType() == recipeType);
    }

    @Override
    @SuppressWarnings("unchecked")
    public <T extends Recipe<?>> void removeOfTypeMatching(RecipeType<T> recipeType, Predicate<T> tester) {
        this.removeMatching(recipe -> {
            if (recipe.getType() == recipeType) {
                return tester.test((T) recipe);
            }
            return false;
        });
    }

    @Override
    public void remove(Recipe<?> recipe) {
        this.remove(recipe.getId());
    }

    @Override
    public void remove(ResourceLocation recipeId) {
        supplier.get().remove(recipeId);
    }

    @Override
    public void removeAllWithResult(Item result) {
        this.removeMatching(recipe -> recipe.getResultItem().is(result));
    }

    @Override
    public void replace(ResourceLocation id, Function<ResourceLocation, Recipe<?>> replacementFactory) {
        supplier.get().put(id, replacementFactory.apply(id));
    }

    @Override
    public void replace(Recipe<?> toReplace, Function<ResourceLocation, Recipe<?>> replacementFactory) {
        this.replace(toReplace.getId(), replacementFactory);
    }

    @Override
    public Map<ResourceLocation, Recipe<?>> getAllRecipes() {
        return supplier.get();
    }

}
