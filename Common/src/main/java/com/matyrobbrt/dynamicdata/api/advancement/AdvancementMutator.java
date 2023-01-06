package com.matyrobbrt.dynamicdata.api.advancement;

import com.matyrobbrt.dynamicdata.api.DataMutator;
import net.minecraft.advancements.Advancement;
import net.minecraft.advancements.AdvancementList;
import net.minecraft.resources.ResourceLocation;

import java.util.Set;

public interface AdvancementMutator extends DataMutator {
    AdvancementList getAdvancements();

    void remove(Advancement advancement);
    void remove(ResourceLocation id);
    void remove(Set<ResourceLocation> ids);

    void add(Advancement advancement);
    void add(ResourceLocation id, Advancement.Builder builder);
}
