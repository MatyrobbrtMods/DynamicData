package com.matyrobbrt.dynamicdata.api;

import com.matyrobbrt.dynamicdata.impl.ReloadListenersImpl;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.server.packs.resources.SimplePreparableReloadListener;
import net.minecraft.util.profiling.ProfilerFiller;
import org.jetbrains.annotations.ApiStatus;

/**
 * An API for subscribing to {@link net.minecraft.server.packs.resources.PreparableReloadListener reload listener} events.
 *
 * @see #INSTANCE
 */
public interface ReloadListeners {
    ReloadListeners INSTANCE = new ReloadListenersImpl();

    /**
     * Adds a listener fired before / after a {@link SimplePreparableReloadListener} applies its data to the game.
     *
     * @param listenerType the type of the {@link SimplePreparableReloadListener} to listen for
     * @param stage        when the listener should be fired
     * @param listener     the listener
     * @param <T>          the type of the data
     * @param <L>          the type of the {@link SimplePreparableReloadListener}
     */
    <T, L extends SimplePreparableReloadListener<T>> void onSimplePreparableApply(Class<L> listenerType, Stage stage, Listener<T, L> listener);

    @FunctionalInterface
    interface Listener<T, L extends SimplePreparableReloadListener<? extends T>> {
        /**
         * Invokes the listener.
         *
         * @param listener the {@link SimplePreparableReloadListener} in question
         * @param data     the reload listener's data
         */
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
