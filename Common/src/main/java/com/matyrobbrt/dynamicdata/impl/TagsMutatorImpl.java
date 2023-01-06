package com.matyrobbrt.dynamicdata.impl;

import com.matyrobbrt.dynamicdata.api.TagsMutator;
import com.matyrobbrt.dynamicdata.extensions.ExtendedTagLoader;
import com.matyrobbrt.dynamicdata.mixin.access.TagEntryAccess;
import com.mojang.logging.LogUtils;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagEntry;
import net.minecraft.tags.TagKey;
import net.minecraft.tags.TagLoader;
import org.slf4j.Logger;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.function.Consumer;
import java.util.stream.Stream;

public record TagsMutatorImpl<T>(Registry<T> registry, Map<ResourceLocation, List<TagLoader.EntryWithSource>> entries) implements TagsMutator<T> {

    @Override
    public Registry<T> getRegistry() {
        return registry;
    }

    @Override
    public Tag<T> getOrCreateTag(ResourceLocation id) {
        return new TagImpl<>(id, registry, entries.computeIfAbsent(id, $ -> new ArrayList<>()));
    }

    @Override
    public Tag<T> getOrCreateTag(TagKey<T> key) {
        return this.getOrCreateTag(key.location());
    }

    @Override
    public void removeTag(ResourceLocation id) {
        this.entries.remove(id);
    }

    @Override
    public void removeTag(TagKey<T> key) {
        this.entries.remove(key.location());
    }

    @Override
    public Stream<Tag<T>> listTags() {
        return entries.keySet().stream().map(this::getOrCreateTag);
    }

    @Override
    @SuppressWarnings("unchecked")
    public <Z> void mutateIfIsRegistry(ResourceKey<? extends Registry<Z>> registryKey, Consumer<TagsMutator<Z>> consumer) {
        if (getRegistry().key() == registryKey) {
            consumer.accept((TagsMutator<Z>) this);
        }
    }

    public record TagImpl<T>(ResourceLocation id, Registry<T> registry, List<TagLoader.EntryWithSource> entries) implements TagsMutator.Tag<T> {
        @Override
        public ResourceLocation getId() {
            return id;
        }

        @Override
        public Tag<T> addEntry(TagEntry entry) {
            entries.add(new TagLoader.EntryWithSource(entry, "dynamicdata"));
            return this;
        }

        @Override
        public Tag<T> addEntry(T value) {
            return this.addEntry(TagEntry.element(Objects.requireNonNull(registry.getKey(value))));
        }

        @Override
        public Tag<T> addOptionalEntry(T value) {
            return this.addEntry(TagEntry.optionalElement(Objects.requireNonNull(registry.getKey(value))));
        }

        @Override
        public Tag<T> addOptionalEntry(ResourceLocation valueId) {
            return this.addEntry(TagEntry.optionalElement(valueId));
        }

        @Override
        public Tag<T> addEntry(ResourceLocation valueId) {
            return this.addEntry(TagEntry.element(valueId));
        }

        @Override
        public Tag<T> addTag(ResourceLocation tag) {
            return this.addEntry(TagEntry.tag(tag));
        }

        @Override
        public Tag<T> addOptionalTag(ResourceLocation optionalTag) {
            return this.addEntry(TagEntry.optionalTag(optionalTag));
        }

        @Override
        public Tag<T> addTag(TagKey<T> tag) {
            return this.addEntry(TagEntry.tag(tag.location()));
        }

        @Override
        public Tag<T> addOptionalTag(TagKey<T> tag) {
            return this.addEntry(TagEntry.optionalTag(tag.location()));
        }

        @Override
        public Tag<T> removeTag(ResourceLocation tag) {
            this.entries.removeIf(source -> ((TagEntryAccess) source.entry()).dynamicdata$matches(true, tag));
            return this;
        }

        @Override
        public Tag<T> removeTag(TagKey<T> tag) {
            return this.removeTag(tag.location());
        }

        @Override
        public Tag<T> removeEntry(T value) {
            return this.removeEntry(Objects.requireNonNull(registry.getKey(value)));
        }

        @Override
        public Tag<T> removeEntry(ResourceLocation value) {
            this.entries.removeIf(source -> ((TagEntryAccess) source.entry()).dynamicdata$matches(false, value));
            return this;
        }

        @Override
        public Stream<TagEntry> listEntries() {
            return entries.stream().map(TagLoader.EntryWithSource::entry);
        }

        @Override
        public List<TagLoader.EntryWithSource> getEntries() {
            return entries;
        }

        @Override
        public Tag<T> clear() {
            getEntries().clear();
            return this;
        }
    }

    private static final Logger LOG = LogUtils.getLogger();
    public static <T> void mutate(ExtendedTagLoader<T> loader, Map<ResourceLocation, List<TagLoader.EntryWithSource>> entries) {
        if (loader.dynamicdata$getRegistry() == null) return;
        DDAPIImpl.INSTANCE.get().fireMutation(new TagsMutatorImpl<>(loader.dynamicdata$getRegistry(), entries));
        LOG.debug("Modified tags for registry {}.", loader.dynamicdata$getRegistry().key());
        loader.dynamicdata$clearReference();
    }
}
