package com.matyrobbrt.dynamicdata.impl.advancement;

import com.google.gson.JsonElement;
import com.matyrobbrt.dynamicdata.api.ReloadListeners;
import com.matyrobbrt.dynamicdata.api.advancement.AdvancementMutator;
import com.matyrobbrt.dynamicdata.impl.DDAPIImpl;
import com.matyrobbrt.dynamicdata.impl.RegisterRLL;
import com.matyrobbrt.dynamicdata.util.ref.FieldHandle;
import com.matyrobbrt.dynamicdata.util.ref.Reflection;
import com.mojang.logging.LogUtils;
import net.minecraft.advancements.Advancement;
import net.minecraft.advancements.AdvancementList;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.ServerAdvancementManager;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.util.profiling.ProfilerFiller;
import org.slf4j.Logger;

import java.util.Map;
import java.util.Set;

public record AdvancementMutatorImpl(AdvancementList list) implements AdvancementMutator {
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

    private static final Logger LOG = LogUtils.getLogger();
    private static final FieldHandle<ServerAdvancementManager, AdvancementList> ADVANCEMENTS_FIELD = Reflection.fieldHandle(ServerAdvancementManager.class, "advancements", "f_139326_", "field_24452");
    @RegisterRLL(stage = ReloadListeners.Stage.POST)
    private static void onAdvancementLoad(ServerAdvancementManager manager, ResourceManager resourceManager, ProfilerFiller profiler, Map<ResourceLocation, JsonElement> data) {
        DDAPIImpl.INSTANCE.get().fireMutation(new AdvancementMutatorImpl(ADVANCEMENTS_FIELD.get(manager)));
        LOG.info("Modified advancements.");
    }
}
