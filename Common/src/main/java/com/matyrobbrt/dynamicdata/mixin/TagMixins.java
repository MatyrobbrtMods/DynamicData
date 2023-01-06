package com.matyrobbrt.dynamicdata.mixin;

import com.matyrobbrt.dynamicdata.extensions.ExtendedTagLoader;
import com.matyrobbrt.dynamicdata.impl.TagsMutatorImpl;
import net.minecraft.core.Registry;
import net.minecraft.core.RegistryAccess;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.tags.TagLoader;
import net.minecraft.tags.TagManager;
import org.spongepowered.asm.mixin.Debug;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Executor;

@Debug(export = true)
@Mixin(TagManager.class)
abstract class TagManagerMixin {
    @Unique
    private static final Map<ResourceKey<? extends Registry<?>>, Registry<?>> DYNAMICDATA$KEY_TO_REGISTRY = new HashMap<>();

    @Inject(at = @At("HEAD"), method = "createLoader")
    private <T> void dynamicdata$storeKeyToRegistry(ResourceManager $$0, Executor $$1, RegistryAccess.RegistryEntry<T> $$2, CallbackInfoReturnable<TagManager.LoadResult<T>> cir) {
        DYNAMICDATA$KEY_TO_REGISTRY.put($$2.key(), $$2.value());
    }

    // lambda$createLoader$4(Lnet/minecraft/resources/ResourceKey;Lnet/minecraft/tags/TagLoader;Lnet/minecraft/server/packs/resources/ResourceManager;)Lnet/minecraft/tags/TagManager$LoadResult;
    @Inject(
            at = @At(value = "INVOKE", target = "Lnet/minecraft/tags/TagLoader;loadAndBuild(Lnet/minecraft/server/packs/resources/ResourceManager;)Ljava/util/Map;"),
            method = {"lambda$createLoader$4", "method_33181", "m_203920_"},
            remap = false
    )
    @SuppressWarnings("unchecked")
    private static <T> void dynamicdata$storeRegistry(ResourceKey<? extends Registry<T>> key, TagLoader<T> loader, ResourceManager manager, CallbackInfoReturnable<TagManager.LoadResult<T>> cir) {
        ((ExtendedTagLoader<T>) loader).dynamicdata$setRegistry((Registry<T>) DYNAMICDATA$KEY_TO_REGISTRY.get(key), () -> DYNAMICDATA$KEY_TO_REGISTRY.remove(key));
    }
}

@Debug(export = true)
@Mixin(TagLoader.class)
abstract class TagLoaderMixin<T> implements ExtendedTagLoader<T> {
    @Unique
    private Registry<T> dynamicdata$registry;
    @Unique
    private Runnable dynamicdata$referenceRemover;

    @Inject(at = @At("RETURN"), method = "load")
    private void dynamicdata$modify(ResourceManager $$0, CallbackInfoReturnable<Map<ResourceLocation, List<TagLoader.EntryWithSource>>> cir) {
        TagsMutatorImpl.mutate(this, cir.getReturnValue());
    }

    @Override
    public Registry<T> dynamicdata$getRegistry() {
        return dynamicdata$registry;
    }

    @Override
    public void dynamicdata$setRegistry(Registry<T> reg, Runnable remover) {
        this.dynamicdata$registry = reg;
        this.dynamicdata$referenceRemover = remover;
    }

    @Override
    public void dynamicdata$clearReference() {
        this.dynamicdata$referenceRemover.run();
    }
}