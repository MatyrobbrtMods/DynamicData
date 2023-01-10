package com.matyrobbrt.dynamicdata.impl;

import com.google.common.base.Suppliers;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Maps;
import com.google.gson.JsonElement;
import com.matyrobbrt.dynamicdata.api.mutation.LootTableMutator;
import com.matyrobbrt.dynamicdata.api.ReloadListeners;
import com.matyrobbrt.dynamicdata.mixin.access.LootTablesAccess;
import com.mojang.logging.LogUtils;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.util.profiling.ProfilerFiller;
import net.minecraft.world.level.storage.loot.LootTable;
import net.minecraft.world.level.storage.loot.LootTables;
import org.slf4j.Logger;

import java.util.Map;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Supplier;

public record LootTableMutatorImpl(Supplier<Map<ResourceLocation, LootTable>> lootTables) implements LootTableMutator {

    @Override
    public void replaceWithEmpty(ResourceLocation tableId) {
        this.add(tableId, LootTable.EMPTY);
    }

    @Override
    public void add(ResourceLocation tableId, LootTable table) {
        lootTables.get().put(tableId, table);
    }

    @Override
    public void add(ResourceLocation tableId, LootTable.Builder table) {
        this.add(tableId, table.build());
    }

    @Override
    public Map<ResourceLocation, LootTable> getAllTables() {
        return lootTables.get();
    }

    private static final Logger LOG = LogUtils.getLogger();
    @RegisterRLL(stage = ReloadListeners.Stage.POST)
    private static void onLootTablesLoad(LootTables manager, ResourceManager resourceManager, ProfilerFiller profiler, Map<ResourceLocation, JsonElement> data) {
        final LootTablesAccess access = (LootTablesAccess) manager;
        final AtomicReference<Map<ResourceLocation, LootTable>> maybeMutated = new AtomicReference<>();
        final LootTableMutatorImpl mutator = new LootTableMutatorImpl(Suppliers.memoize(() -> {
            final Map<ResourceLocation, LootTable> copy = Maps.newHashMap(access.getTables());
            maybeMutated.setPlain(copy);
            return copy;
        }));

        DDAPIImpl.INSTANCE.get().fireMutation(mutator);

        final Map<ResourceLocation, LootTable> mutated = maybeMutated.getPlain();
        if (mutated != null) {
            access.setTables(ImmutableMap.copyOf(mutated));
            LOG.info("Modified loot tables.");
        }
    }
}
