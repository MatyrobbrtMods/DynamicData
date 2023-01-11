package com.matyrobbrt.dynamicdata.impl.recipe;

import com.google.common.base.Suppliers;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Maps;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.matyrobbrt.dynamicdata.api.ReloadListeners;
import com.matyrobbrt.dynamicdata.api.mutation.recipe.RecipeBuilders;
import com.matyrobbrt.dynamicdata.api.mutation.recipe.RecipeMutator;
import com.matyrobbrt.dynamicdata.impl.DDAPIImpl;
import com.matyrobbrt.dynamicdata.impl.RegisterRLL;
import com.matyrobbrt.dynamicdata.mixin.access.RecipeManagerAccess;
import com.mojang.logging.LogUtils;
import net.minecraft.data.recipes.FinishedRecipe;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.util.profiling.ProfilerFiller;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeManager;
import net.minecraft.world.item.crafting.RecipeType;
import org.slf4j.Logger;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.function.Supplier;

public record RecipeMutatorImpl(Supplier<Map<ResourceLocation, Recipe<?>>> supplier, RecipeBuilders builders) implements RecipeMutator {

    @Override
    public void add(Recipe<?> recipe) {
        supplier.get().put(recipe.getId(), recipe);
    }

    @Override
    public void add(ResourceLocation recipeId, Function<ResourceLocation, Recipe<?>> recipeFunction) {
        supplier.get().put(recipeId, recipeFunction.apply(recipeId));
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
                DATAGEN_RECIPE_ADVANCEMENTS.put(recipe.getAdvancementId(), advJson);
            }
        };
    }

    @Override
    public RecipeBuilders getBuilders() {
        return builders;
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
    public <T extends Recipe<?>> void removeOfTypeMatching(RecipeType<T> recipeType, Predicate<T> predicate) {
        this.removeMatching(recipe -> {
            if (recipe.getType() == recipeType) {
                return predicate.test((T) recipe);
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

    public static final Logger LOG = LogUtils.getLogger();
    public static final Map<ResourceLocation, JsonObject> DATAGEN_RECIPE_ADVANCEMENTS = new HashMap<>();
    @RegisterRLL(stage = ReloadListeners.Stage.POST)
    private static void onRecipesLoad(RecipeManager manager, ResourceManager resourceManager, ProfilerFiller profiler, Map<ResourceLocation, JsonElement> data) {
        final RecipeManagerAccess mac = (RecipeManagerAccess) manager;
        final AtomicReference<Map<ResourceLocation, Recipe<?>>> maybeMutated = new AtomicReference<>();
        final RecipeMutatorImpl mutator = new RecipeMutatorImpl(Suppliers.memoize(() -> {
            final Map<ResourceLocation, Recipe<?>> copy = Maps.newHashMap(mac.getByName());
            maybeMutated.setPlain(copy);
            return copy;
        }), new RecipeBuildersImpl());

        DDAPIImpl.INSTANCE.get().fireMutation(mutator);

        final Map<ResourceLocation, Recipe<?>> mutated = maybeMutated.getPlain();
        if (mutated != null) {
            final Map<RecipeType<?>, ImmutableMap.Builder<ResourceLocation, Recipe<?>>> mutatedAll = Maps.newHashMap();
            mutated.forEach((resourceLocation, recipe) -> mutatedAll.computeIfAbsent(recipe.getType(), $ -> ImmutableMap.builder()).put(resourceLocation, recipe));

            mac.setByName(ImmutableMap.copyOf(mutated));
            mac.setRecipes(mutatedAll.entrySet().stream().collect(ImmutableMap.toImmutableMap(Map.Entry::getKey, entry -> entry.getValue().build())));
            LOG.info("Modified recipes.");
        }
    }
}
