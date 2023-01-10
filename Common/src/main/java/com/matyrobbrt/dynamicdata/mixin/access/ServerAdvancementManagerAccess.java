package com.matyrobbrt.dynamicdata.mixin.access;

import net.minecraft.advancements.AdvancementList;
import net.minecraft.server.ServerAdvancementManager;
import net.minecraft.world.level.storage.loot.PredicateManager;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(ServerAdvancementManager.class)
public interface ServerAdvancementManagerAccess {
    @Accessor("advancements")
    AdvancementList getAdvancements();

    @Accessor("predicateManager")
    PredicateManager getPredicateManager();
}
