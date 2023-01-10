package com.matyrobbrt.dynamicdata.api.mutation;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.storage.loot.LootTable;

import java.util.Map;

/**
 * A mutator for loot tables.
 *
 * @see net.minecraft.world.level.storage.loot.LootTables
 */
public interface LootTableMutator extends DataMutator {
    /**
     * Replaces the table with the specified ID with an empty loot table.
     *
     * @param tableId the ID of the table to replace
     */
    void replaceWithEmpty(ResourceLocation tableId);

    /**
     * Adds a loot table.
     *
     * @param tableId the ID of the loot table
     * @param table   the loot table
     */
    void add(ResourceLocation tableId, LootTable table);

    /**
     * Adds a loot table.
     *
     * @param tableId the ID of the loot table
     * @param table   the loot table builder
     */
    void add(ResourceLocation tableId, LootTable.Builder table);

    /**
     * {@return all currently known loot tables}
     */
    Map<ResourceLocation, LootTable> getAllTables();
}
