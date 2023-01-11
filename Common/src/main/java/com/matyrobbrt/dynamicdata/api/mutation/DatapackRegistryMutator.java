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

/**
 * A mutator for data pack registries. This is a replacement for the pre-1.19.3
 * built-in registries.
 */
public interface DatapackRegistryMutator<R> extends DataMutator {
    /**
     * {@return the registry this mutator mutates}
     */
    WritableRegistry<R> getRegistry();

    /**
     * {@return the key of the registry this mutator mutates}
     */
    ResourceKey<? extends Registry<R>> getRegistryKey();

    /**
     * @see net.minecraft.resources.RegistryOps.RegistryInfoLookup#lookup(ResourceKey)
     */
    <T> Optional<RegistryOps.RegistryInfo<T>> lookup(ResourceKey<? extends Registry<? extends T>> registryKey);

    /**
     * @see net.minecraft.resources.RegistryOps.RegistryInfoLookup#lookup(ResourceKey)
     */
    <T> Optional<HolderLookup.RegistryLookup<T>> registryLookup(ResourceKey<? extends Registry<? extends T>> registryKey);

    /**
     * Registers an object to the registry.
     *
     * @param id    the ID
     * @param value the object to register
     */
    void register(ResourceLocation id, R value);

    /**
     * Registers a JSON-represented object to the registry. <br>
     * This method will parse the JSON element and then register its result.
     *
     * @param id   the ID
     * @param json the object JSON
     */
    void register(ResourceLocation id, JsonElement json);

    /**
     * Invokes the {@code consumer} only if this mutator is mutating a registry with the specified {@code registryKey}.
     */
    <Z> void mutateIfIsRegistry(ResourceKey<? extends Registry<Z>> registryKey, Consumer<DatapackRegistryMutator<Z>> consumer);
}