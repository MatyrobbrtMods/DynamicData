package com.matyrobbrt.dynamicdata.impl;

import com.google.gson.JsonElement;
import com.matyrobbrt.dynamicdata.api.DatapackRegistryMutator;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.Registry;
import net.minecraft.core.WritableRegistry;
import net.minecraft.resources.RegistryOps;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;

import java.util.Optional;
import java.util.function.BiConsumer;
import java.util.function.Consumer;

public record DatapackRegistryMutatorImpl<R>(WritableRegistry<R> registry, RegistryOps.RegistryInfoLookup lookup, BiConsumer<ResourceLocation, JsonElement> registerJson, Consumer<R> onRegistered) implements DatapackRegistryMutator<R> {
    @Override
    public WritableRegistry<R> getRegistry() {
        return registry;
    }

    @Override
    public ResourceKey<? extends Registry<R>> getRegistryKey() {
        return registry.key();
    }

    @Override
    public <T> Optional<RegistryOps.RegistryInfo<T>> lookup(ResourceKey<? extends Registry<? extends T>> registryKey) {
        return lookup.lookup(registryKey);
    }

    @Override
    public <T> Optional<HolderLookup.RegistryLookup<T>> registryLookup(ResourceKey<? extends Registry<? extends T>> registryKey) {
        return lookup(registryKey).filter(it -> it.owner() instanceof HolderLookup.RegistryLookup<T>).map(it -> (HolderLookup.RegistryLookup<T>) it.owner());
    }

    @Override
    public void register(ResourceLocation id, R value) {
        Registry.register(registry, id, value);
        onRegistered.accept(value);
    }

    @Override
    public void register(ResourceLocation id, JsonElement json) {
        registerJson.accept(id, json);
    }

    @Override
    @SuppressWarnings("unchecked")
    public <Z> void mutateIfIsRegistry(ResourceKey<? extends Registry<Z>> registryKey, Consumer<DatapackRegistryMutator<Z>> consumer) {
        if (registryKey == getRegistryKey()) {
            consumer.accept((DatapackRegistryMutator<Z>) this);
        }
    }
}
