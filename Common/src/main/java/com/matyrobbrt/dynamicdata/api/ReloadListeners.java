package com.matyrobbrt.dynamicdata.api;

import com.matyrobbrt.dynamicdata.impl.ReloadListenersImpl;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.server.packs.resources.SimplePreparableReloadListener;
import net.minecraft.util.profiling.ProfilerFiller;
import org.jetbrains.annotations.ApiStatus;

public interface ReloadListeners {
    ReloadListeners INSTANCE = new ReloadListenersImpl();

    <T, L extends SimplePreparableReloadListener<T>> void onSimplePreparableApply(Class<L> listenerType, Stage stage, Listener<T, L> listener);

    @FunctionalInterface
    interface Listener<T, L extends SimplePreparableReloadListener<? extends T>> {
        void invoke(L listener, ResourceManager resourceManager, ProfilerFiller profiler, T data);

        @ApiStatus.Internal
        @SuppressWarnings("unchecked")
        default void invokeUnsafe(Object listener, ResourceManager manager, ProfilerFiller profilerFiller, Object data) {
            this.invoke((L) listener, manager, profilerFiller, (T) data);
        }
    }

    enum Stage {
        PRE,
        POST
    }
}
