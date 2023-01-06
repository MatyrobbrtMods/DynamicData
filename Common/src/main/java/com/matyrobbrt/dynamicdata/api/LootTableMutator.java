package com.matyrobbrt.dynamicdata.api;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.storage.loot.LootTable;

import java.util.Map;

public interface LootTableMutator extends DataMutator {
    void replaceWithEmpty(ResourceLocation tableId);

    void add(ResourceLocation tableId, LootTable table);
    void add(ResourceLocation tableId, LootTable.Builder table);

    Map<ResourceLocation, LootTable> getAllTables();
}
