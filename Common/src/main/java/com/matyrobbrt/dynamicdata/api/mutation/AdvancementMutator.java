package com.matyrobbrt.dynamicdata.api.mutation;

import com.google.gson.JsonObject;
import net.minecraft.advancements.Advancement;
import net.minecraft.advancements.AdvancementList;
import net.minecraft.resources.ResourceLocation;

import java.util.Set;

/**
 * A mutator for advancements.
 *
 * @see net.minecraft.server.ServerAdvancementManager
 */
public interface AdvancementMutator extends DataMutator {
    /**
     * {@return the advancements}
     */
    AdvancementList getAdvancements();

    /**
     * Removes the given advancement.
     */
    void remove(Advancement advancement);

    /**
     * Removes the advancement with the given {@code id}.
     */
    void remove(ResourceLocation id);

    /**
     * Removes all advancements whose IDs are in the given {@code ids}.
     */
    void remove(Set<ResourceLocation> ids);

    /**
     * Adds an advancement.
     */
    void add(Advancement advancement);

    /**
     * Adds an advancement.
     */
    void add(ResourceLocation id, Advancement.Builder builder);

    /**
     * Adds an advancement.
     *
     * @param id   the ID of the advancement
     * @param json the JSON representation of the advancement
     */
    void add(ResourceLocation id, JsonObject json);
}
