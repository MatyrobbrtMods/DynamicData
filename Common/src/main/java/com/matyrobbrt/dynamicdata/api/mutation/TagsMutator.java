package com.matyrobbrt.dynamicdata.api.mutation;

import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagEntry;
import net.minecraft.tags.TagKey;
import net.minecraft.tags.TagLoader;

import java.util.List;
import java.util.function.Consumer;
import java.util.stream.Stream;

/**
 * A mutator for tags.
 *
 * @param <T> the type of the registry whose tags this mutator can mutate
 * @see net.minecraft.tags.TagManager
 */
public interface TagsMutator<T> extends DataMutator {
    /**
     * {@return the registry whose tags are being mutated}
     */
    Registry<T> getRegistry();

    /**
     * Gets or creates a tag for the given {@code id}.
     *
     * @param id the id of the tag
     * @return the tag
     */
    Tag<T> getOrCreateTag(ResourceLocation id);

    /**
     * Gets or creates a tag for the given {@code key}.
     *
     * @param key the key of the tag
     * @return the tag
     */
    Tag<T> getOrCreateTag(TagKey<T> key);

    /**
     * Completely removes a tag.
     *
     * @param id the ID of the tag
     */
    void removeTag(ResourceLocation id);

    /**
     * Completely removes a tag.
     *
     * @param key the key of the tag
     */
    void removeTag(TagKey<T> key);

    /**
     * {@return a stream of all currently known tags}
     */
    Stream<Tag<T>> listTags();

    /**
     * Invokes the {@code consumer} only if this mutator is mutating a registry with the specified {@code registryKey}.
     */
    <Z> void mutateIfIsRegistry(ResourceKey<? extends Registry<Z>> registryKey, Consumer<TagsMutator<Z>> consumer);

    /**
     * A tag entry.
     *
     * @param <T> the type of the contents of the tag
     */
    interface Tag<T> {
        /**
         * Adds an entry to the tag.
         *
         * @param entry the entry
         * @return the builder instance
         */
        Tag<T> addEntry(TagEntry entry);

        /**
         * Adds a required value to the tag.
         *
         * @param value the value
         * @return the builder instance
         */
        Tag<T> addEntry(T value);

        /**
         * Adds an optional value to the tag.
         *
         * @param value the value
         * @return the builder instance
         */
        Tag<T> addOptionalEntry(T value);

        /**
         * Adds a required entry to the tag.
         *
         * @param valueId the ID of the entry
         * @return the builder instance
         */
        Tag<T> addEntry(ResourceLocation valueId);

        /**
         * Adds an optional entry to the tag.
         *
         * @param valueId the ID of the entry
         * @return the builder instance
         */
        Tag<T> addOptionalEntry(ResourceLocation valueId);

        /**
         * Adds a required child tag.
         *
         * @param tag the ID of the tag
         * @return the builder instance
         */
        Tag<T> addTag(ResourceLocation tag);

        /**
         * Adds an optional child tag.
         *
         * @param optionalTag the ID of the tag
         * @return the builder instance
         */
        Tag<T> addOptionalTag(ResourceLocation optionalTag);

        /**
         * Adds a required child tag.
         *
         * @param tag the key of the tag
         * @return the builder instance
         */
        Tag<T> addTag(TagKey<T> tag);

        /**
         * Adds an optional child tag.
         *
         * @param optionalTag the ID of the tag
         * @return the builder instance
         */
        Tag<T> addOptionalTag(TagKey<T> optionalTag);

        /**
         * Removes an entry from the tag.
         *
         * @param value the entry to remove
         * @return the builder instance
         */
        Tag<T> removeEntry(T value);

        /**
         * Removes an entry from the tag.
         *
         * @param value the ID of the entry to remove
         * @return the builder instance
         */
        Tag<T> removeEntry(ResourceLocation value);

        /**
         * Removes a child tag from this tag.
         *
         * @param tag the ID of the tag to remove
         * @return the builder instance
         */
        Tag<T> removeTag(ResourceLocation tag);

        /**
         * Removes a child tag from this tag.
         *
         * @param tag the key of the tag to remove
         * @return the builder instance
         */
        Tag<T> removeTag(TagKey<T> tag);

        /**
         * {@return a stream of all currently known entries in this tag}
         */
        Stream<TagEntry> listEntries();

        /**
         * {@return all currently known entries in this tag}
         */
        List<TagLoader.EntryWithSource> getEntries();

        /**
         * Clears the contents of the tag.
         *
         * @return the builder instance
         */
        Tag<T> clear();

        /**
         * {@return the ID of the tag}
         */
        ResourceLocation getId();
    }
}
