package com.matyrobbrt.dynamicdata.impl.recipe;

import com.google.common.collect.ImmutableMap;
import com.google.gson.JsonObject;
import com.matyrobbrt.dynamicdata.api.mutation.recipe.AddRecipeMutator;
import com.matyrobbrt.dynamicdata.api.mutation.recipe.RecipeBuilders;
import net.minecraft.data.recipes.FinishedRecipe;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeManager;
import net.minecraft.world.item.crafting.RecipeType;

import java.util.Map;
import java.util.function.Consumer;
import java.util.function.Function;

public record AddRecipeMutatorImpl(
        Map<RecipeType<?>, ImmutableMap.Builder<ResourceLocation, Recipe<?>>> byType,
        ImmutableMap.Builder<ResourceLocation, Recipe<?>> byId, RecipeBuilders builders
) implements AddRecipeMutator {

    @Override
    public void add(Recipe<?> recipe) {
        byType.computeIfAbsent(recipe.getType(), $ -> ImmutableMap.builder()).put(recipe.getId(), recipe);
        byId.put(recipe.getId(), recipe);
    }

    @Override
    public void add(ResourceLocation recipeId, Function<ResourceLocation, Recipe<?>> recipeFunction) {
        this.add(recipeFunction.apply(recipeId));
    }

    @Override
    public void add(ResourceLocation recipeId, JsonObject json) {
        this.add(RecipeManager.fromJson(recipeId, json));
    }

    @Override
    public Consumer<FinishedRecipe> getFinishedRecipeConsumer() {
        return recipe -> {
            add(recipe.getId(), recipe.serializeRecipe());
            final JsonObject advJson = recipe.serializeAdvancement();
            if (advJson != null && advJson.get("criteria").getAsJsonObject().keySet().stream().anyMatch(it -> !it.equals("has_the_recipe"))) {
                RecipeMutatorImpl.DATAGEN_RECIPE_ADVANCEMENTS.put(recipe.getAdvancementId(), advJson);
            }
        };
    }

    @Override
    public RecipeBuilders getBuilders() {
        return builders;
    }
}
