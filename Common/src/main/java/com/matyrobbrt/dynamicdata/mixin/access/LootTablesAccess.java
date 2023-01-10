package com.matyrobbrt.dynamicdata.mixin.access;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.storage.loot.LootTable;
import net.minecraft.world.level.storage.loot.LootTables;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.gen.Accessor;

import java.util.Map;

@Mixin(LootTables.class)
public interface LootTablesAccess {
    @Accessor("tables")
    Map<ResourceLocation, LootTable> getTables();

    @Mutable
    @Accessor("tables")
    void setTables(Map<ResourceLocation, LootTable> tables);
}
