package com.matyrobbrt.dynamicdata.api.mutation;

import com.google.gson.JsonElement;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.Registry;
import net.minecraft.core.WritableRegistry;
import net.minecraft.resources.RegistryOps;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;

import java.util.Optional;
import java.util.function.Consumer;

public interface DatapackRegistryMutator<R> extends DataMutator {
    WritableRegistry<R> getRegistry();
    ResourceKey<? extends Registry<R>> getRegistryKey();

    <T> Optional<RegistryOps.RegistryInfo<T>> lookup(ResourceKey<? extends Registry<? extends T>> registryKey);
    <T> Optional<HolderLookup.RegistryLookup<T>> registryLookup(ResourceKey<? extends Registry<? extends T>> registryKey);

    void register(ResourceLocation id, R value);
    void register(ResourceLocation id, JsonElement json);

    <Z> void mutateIfIsRegistry(ResourceKey<? extends Registry<Z>> registryKey, Consumer<DatapackRegistryMutator<Z>> consumer);
}