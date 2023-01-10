package com.matyrobbrt.dynamicdata.impl;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.matyrobbrt.dynamicdata.api.ReloadListeners;
import com.matyrobbrt.dynamicdata.api.mutation.AdvancementMutator;
import com.matyrobbrt.dynamicdata.impl.recipe.RecipeMutatorImpl;
import com.matyrobbrt.dynamicdata.mixin.access.ServerAdvancementManagerAccess;
import com.mojang.logging.LogUtils;
import net.minecraft.advancements.Advancement;
import net.minecraft.advancements.AdvancementList;
import net.minecraft.advancements.critereon.DeserializationContext;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.ServerAdvancementManager;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.util.profiling.ProfilerFiller;
import net.minecraft.world.level.storage.loot.PredicateManager;
import org.slf4j.Logger;

import java.util.Map;
import java.util.Set;

public record AdvancementMutatorImpl(AdvancementList list, PredicateManager predicateManager) implements AdvancementMutator {
    @Override
    public AdvancementList getAdvancements() {
        return list;
    }

    @Override
    public void remove(Advancement advancement) {
        list.remove(Set.of(advancement.getId()));
    }

    @Override
    public void remove(ResourceLocation id) {
        list.remove(Set.of(id));
    }

    @Override
    public void remove(Set<ResourceLocation> ids) {
        list.remove(ids);
    }

    @Override
    public void add(Advancement advancement) {
        list.add(Map.of(advancement.getId(), advancement.deconstruct()));
    }

    @Override
    public void add(ResourceLocation id, Advancement.Builder builder) {
        list.add(Map.of(id, builder));
    }

    @Override
    public void add(ResourceLocation id, JsonObject json) {
        this.add(id, Advancement.Builder.fromJson(json, new DeserializationContext(id, predicateManager)));
    }

    private static final Logger LOG = LogUtils.getLogger();
    @RegisterRLL(stage = ReloadListeners.Stage.POST)
    private static void onAdvancementLoad(ServerAdvancementManager manager, ResourceManager resourceManager, ProfilerFiller profiler, Map<ResourceLocation, JsonElement> data) {
        final ServerAdvancementManagerAccess access = (ServerAdvancementManagerAccess) manager;
        final AdvancementMutatorImpl mutator = new AdvancementMutatorImpl(access.getAdvancements(), access.getPredicateManager());

        DDAPIImpl.INSTANCE.get().fireMutation(mutator);
        RecipeMutatorImpl.DATAGEN_RECIPE_ADVANCEMENTS.forEach(mutator::add);
        RecipeMutatorImpl.DATAGEN_RECIPE_ADVANCEMENTS.clear();

        LOG.info("Modified advancements.");
    }
}
