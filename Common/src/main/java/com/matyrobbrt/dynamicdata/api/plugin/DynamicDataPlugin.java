package com.matyrobbrt.dynamicdata.api.plugin;

import com.matyrobbrt.dynamicdata.api.mutation.DataMutator;
import com.matyrobbrt.dynamicdata.api.mutation.DatapackRegistryMutator;
import com.matyrobbrt.dynamicdata.api.mutation.TagsMutator;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceKey;

import java.util.function.Consumer;

public interface DynamicDataPlugin {
    void collectMutatorListeners(MutatorCollector collector);

    interface MutatorCollector {
        <T extends DataMutator> void accept(Class<? super T> type, Consumer<T> consumer);

        default <T> void acceptTagMutator(ResourceKey<? extends Registry<T>> registryKey, Consumer<TagsMutator<T>> mutator) {
            this.<TagsMutator<?>>accept(TagsMutator.class, m -> m.mutateIfIsRegistry(registryKey, mutator));
        }

        default <T> void acceptDatapackRegistryMutator(ResourceKey<? extends Registry<T>> registryKey, Consumer<DatapackRegistryMutator<T>> mutator) {
            this.<DatapackRegistryMutator<?>>accept(DatapackRegistryMutator.class, m -> m.mutateIfIsRegistry(registryKey, mutator));
        }
    }
}
