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

public interface TagsMutator<T> extends DataMutator {
    Registry<T> getRegistry();

    Tag<T> getOrCreateTag(ResourceLocation id);
    Tag<T> getOrCreateTag(TagKey<T> key);

    void removeTag(ResourceLocation id);
    void removeTag(TagKey<T> key);

    Stream<Tag<T>> listTags();

    <Z> void mutateIfIsRegistry(ResourceKey<? extends Registry<Z>> registryKey, Consumer<TagsMutator<Z>> consumer);

    interface Tag<T> {
        Tag<T> addEntry(TagEntry entry);

        Tag<T> addEntry(T value);
        Tag<T> addOptionalEntry(T value);
        Tag<T> addEntry(ResourceLocation valueId);
        Tag<T> addOptionalEntry(ResourceLocation valueId);

        Tag<T> addTag(ResourceLocation tag);
        Tag<T> addOptionalTag(ResourceLocation optionalTag);
        Tag<T> addTag(TagKey<T> tag);
        Tag<T> addOptionalTag(TagKey<T> optionalTag);

        Tag<T> removeEntry(T value);
        Tag<T> removeEntry(ResourceLocation value);

        Tag<T> removeTag(ResourceLocation tag);
        Tag<T> removeTag(TagKey<T> tag);

        Stream<TagEntry> listEntries();
        List<TagLoader.EntryWithSource> getEntries();

        Tag<T> clear();

        ResourceLocation getId();
    }
}
