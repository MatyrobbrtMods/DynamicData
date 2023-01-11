package com.matyrobbrt.dynamicdata.mixin.recipe;

import com.google.common.collect.ImmutableMap;
import com.google.gson.JsonElement;
import com.matyrobbrt.dynamicdata.impl.DDAPIImpl;
import com.matyrobbrt.dynamicdata.impl.recipe.AddRecipeMutatorImpl;
import com.matyrobbrt.dynamicdata.impl.recipe.RecipeBuildersImpl;
import com.matyrobbrt.dynamicdata.impl.recipe.RecipeMutatorImpl;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.util.profiling.ProfilerFiller;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeManager;
import net.minecraft.world.item.crafting.RecipeType;
import org.spongepowered.asm.mixin.Debug;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

import java.util.Map;

@Debug(export = true)
@Mixin(RecipeManager.class)
abstract class MixinRecipeManager {
    @Inject(
            method = "apply",
            at = @At(value = "INVOKE", target = "Ljava/util/Map;entrySet()Ljava/util/Set;", ordinal = 0),
            locals = LocalCapture.CAPTURE_FAILHARD
    )
    private void dynamicdata$addRecipes(Map<ResourceLocation, JsonElement> map, ResourceManager resourceManager, ProfilerFiller profiler, CallbackInfo ci,
                                        Map<RecipeType<?>, ImmutableMap.Builder<ResourceLocation, Recipe<?>>> builderMap,
                                        ImmutableMap.Builder<ResourceLocation, Recipe<?>> globalRecipeMapBuilder) {
        DDAPIImpl.INSTANCE.get().fireMutation(new AddRecipeMutatorImpl(
                builderMap, globalRecipeMapBuilder, new RecipeBuildersImpl()
        ));
        RecipeMutatorImpl.LOG.info("Modified recipes during add stage.");
    }
}
