package com.matyrobbrt.dynamicdata.impl.recipe;

import com.google.common.collect.ListMultimap;
import com.google.common.collect.Multimaps;
import com.matyrobbrt.dynamicdata.api.ReloadListeners;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.server.packs.resources.SimplePreparableReloadListener;
import net.minecraft.util.profiling.ProfilerFiller;

import java.util.ArrayList;
import java.util.EnumMap;

public class ReloadListenersImpl implements ReloadListeners {
    private final ListMultimap<Stage, Listener<?, SimplePreparableReloadListener<?>>> listeners = Multimaps.newListMultimap(new EnumMap<>(Stage.class), ArrayList::new);

    @Override
    public <T, L extends SimplePreparableReloadListener<T>> void onSimplePreparableApply(Class<L> listenerType, Stage stage, Listener<T, L> listener) {
        listeners.put(stage, (ls, resourceManager, profiler, data) -> {
            if (listenerType.isInstance(ls)) {
                listener.invokeUnsafe(ls, resourceManager, profiler, data);
            }
        });
    }

    public <T> void invoke(Stage stage, SimplePreparableReloadListener<T> ls, ResourceManager resourceManager, ProfilerFiller profiler, T data) {
        listeners.get(stage).forEach(listener -> listener.invokeUnsafe(ls, resourceManager, profiler, data));
    }

}
