package com.matyrobbrt.dynamicdata;

import com.google.common.base.Suppliers;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Maps;
import com.google.gson.JsonElement;
import com.matyrobbrt.dynamicdata.api.DynamicDataAPI;
import com.matyrobbrt.dynamicdata.api.ReloadListeners;
import com.matyrobbrt.dynamicdata.api.annotation.RegisterPlugin;
import com.matyrobbrt.dynamicdata.api.plugin.DynamicDataPlugin;
import com.matyrobbrt.dynamicdata.impl.DDAPIImpl;
import com.matyrobbrt.dynamicdata.impl.RegisterRLL;
import com.matyrobbrt.dynamicdata.impl.recipe.RecipeMutatorImpl;
import com.matyrobbrt.dynamicdata.mixin.access.RecipeManagerAccess;
import com.matyrobbrt.dynamicdata.services.Platform;
import com.matyrobbrt.dynamicdata.util.Reflection;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.server.packs.resources.SimplePreparableReloadListener;
import net.minecraft.util.profiling.ProfilerFiller;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeManager;
import net.minecraft.world.item.crafting.RecipeType;

import java.lang.invoke.CallSite;
import java.lang.invoke.LambdaMetafactory;
import java.lang.invoke.MethodHandles;
import java.lang.invoke.MethodType;
import java.util.Map;
import java.util.concurrent.atomic.AtomicReference;

// TODO logging
public class DynamicDataSetup {

    static void onModInit() {
        Platform.INSTANCE.findClassesWithAnnotation(RegisterPlugin.class, Platform.AnnotationLookup.EVERYWHERE)
                .forEach((className, data) -> {
                    try {
                        final Class<?> clazz = Class.forName(className, true, DynamicDataSetup.class.getClassLoader());
                        DynamicDataAPI.INSTANCE.addPlugin((DynamicDataPlugin) clazz.getDeclaredConstructor().newInstance());
                    } catch (Exception ignored) {
                        System.out.println("error " + ignored); ignored.printStackTrace();
                    }
                });

        try {
            collectRLLs();
        } catch (Throwable e) {
            System.out.println("error: " + e);
            e.printStackTrace();
        }
    }

    @SuppressWarnings({"unchecked", "rawtypes"})
    static void collectRLLs() throws Throwable {
        for (final Map.Entry<Platform.MethodTarget, Map<String, Object>> entry : Platform.INSTANCE.findMethodsWithAnnotation(RegisterRLL.class, Platform.AnnotationLookup.ONLY_US).entrySet()) {
            final var args = entry.getValue();
            final var methodTarget = entry.getKey();
            final ReloadListeners.Stage stage = ((ReloadListeners.Stage) args.get("stage"));
            final Class<?> clazz = Class.forName(methodTarget.className(), true, DynamicDataSetup.class.getClassLoader());
            final MethodType methodType = MethodType.fromMethodDescriptorString(methodTarget.desc(), DynamicDataSetup.class.getClassLoader());
            final MethodHandles.Lookup lookup = Reflection.getLookup(clazz);
            final CallSite site = LambdaMetafactory.metafactory(
                    lookup, "invoke", MethodType.methodType(ReloadListeners.Listener.class),
                    MethodType.methodType(void.class, SimplePreparableReloadListener.class, ResourceManager.class, ProfilerFiller.class, Object.class),
                    lookup.findStatic(clazz, methodTarget.name(), methodType),
                    methodType
            );
            ReloadListeners.INSTANCE.onSimplePreparableApply((Class)methodType.parameterType(0), stage, (ReloadListeners.Listener) site.getTarget().invokeExact());
        }
    }

    @RegisterRLL(stage = ReloadListeners.Stage.POST)
    private static void onRecipesLoad(RecipeManager manager$, ResourceManager resourceManager, ProfilerFiller profiler, Map<ResourceLocation, JsonElement> data) {
        final RecipeManagerAccess manager = (RecipeManagerAccess) manager$;

        final AtomicReference<Map<ResourceLocation, Recipe<?>>> maybeMutated = new AtomicReference<>();
        final RecipeMutatorImpl mutator = new RecipeMutatorImpl(Suppliers.memoize(() -> {
            final Map<ResourceLocation, Recipe<?>> copy = Maps.newHashMap(manager.getByName());
            maybeMutated.setPlain(copy);
            return copy;
        }));

        DDAPIImpl.INSTANCE.get().fireForPlugins(plugin -> plugin.mutateRecipes(mutator));

        final Map<ResourceLocation, Recipe<?>> mutated = maybeMutated.getPlain();
        if (mutated != null) {
            final Map<RecipeType<?>, ImmutableMap.Builder<ResourceLocation, Recipe<?>>> mutatedAll = Maps.newHashMap();
            mutated.forEach((resourceLocation, recipe) -> mutatedAll.computeIfAbsent(recipe.getType(), $ -> ImmutableMap.builder()).put(resourceLocation, recipe));

            manager.setByName(ImmutableMap.copyOf(mutated));
            manager.setRecipes(mutatedAll.entrySet().stream().collect(ImmutableMap.toImmutableMap(Map.Entry::getKey, entry -> entry.getValue().build())));
        }
    }
}
