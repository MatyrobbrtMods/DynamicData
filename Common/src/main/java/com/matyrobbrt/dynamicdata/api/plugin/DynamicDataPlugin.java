package com.matyrobbrt.dynamicdata.api.plugin;

import com.matyrobbrt.dynamicdata.api.mutation.DataMutator;
import com.matyrobbrt.dynamicdata.api.mutation.DatapackRegistryMutator;
import com.matyrobbrt.dynamicdata.api.mutation.TagsMutator;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceKey;

import java.util.function.Consumer;

/**
 * The main class to implement to create a Dynamic Data plugin. All communications between a mod and DynamicData is done through this class.
 *
 * @see com.matyrobbrt.dynamicdata.api.annotation.RegisterPlugin
 * @see com.matyrobbrt.dynamicdata.api.DynamicDataAPI#registerPlugin(DynamicDataPlugin)
 */
public interface DynamicDataPlugin {
    /**
     * This method is called when DynamicData collects your plugin's data mutators.
     *
     * @param collector the mutator collector
     */
    void collectMutatorListeners(MutatorCollector collector);

    interface MutatorCollector {
        /**
         * Adds a mutator of the given type.
         *
         * @param type     the type of the mutator
         * @param consumer the consumer to invoke on the mutator when its data is reloaded
         * @param <T>      the type of the mutator
         */
        <T extends DataMutator> void accept(Class<? super T> type, Consumer<T> consumer);

        /**
         * Adds a tag mutator.
         *
         * @param registryKey the key of the registry whose tags to mutate
         * @param mutator     the consumer to invoke when the tags of the specified registry are loaded
         * @param <T>         the type of the registry
         */
        default <T> void acceptTagMutator(ResourceKey<? extends Registry<T>> registryKey, Consumer<TagsMutator<T>> mutator) {
            this.<TagsMutator<?>>accept(TagsMutator.class, m -> m.mutateIfIsRegistry(registryKey, mutator));
        }

        /**
         * Adds a datapack registry mutator.
         *
         * @param registryKey the key of the datapack registry to mutate
         * @param mutator     the consumer to invoke when the contents of the registry are mutated
         * @param <T>         the type of the registry
         */
        default <T> void acceptDatapackRegistryMutator(ResourceKey<? extends Registry<T>> registryKey, Consumer<DatapackRegistryMutator<T>> mutator) {
            this.<DatapackRegistryMutator<?>>accept(DatapackRegistryMutator.class, m -> m.mutateIfIsRegistry(registryKey, mutator));
        }
    }
}
