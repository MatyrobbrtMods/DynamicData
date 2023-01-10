package com.matyrobbrt.dynamicdata.impl;

import com.google.common.base.Suppliers;
import com.google.common.collect.ListMultimap;
import com.google.common.collect.Multimaps;
import com.matyrobbrt.dynamicdata.api.mutation.DataMutator;
import com.matyrobbrt.dynamicdata.api.DynamicDataAPI;
import com.matyrobbrt.dynamicdata.api.plugin.DynamicDataPlugin;

import java.util.Arrays;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.function.Consumer;
import java.util.function.Supplier;

public class DDAPIImpl implements DynamicDataAPI {
    public static final Supplier<DDAPIImpl> INSTANCE = Suppliers.memoize(() -> (DDAPIImpl) DynamicDataAPI.INSTANCE);

    private final List<DynamicDataPlugin> plugins = new CopyOnWriteArrayList<>();
    private final ListMultimap<Class<?>, Consumer<DataMutator>> mutatorConsumers = Multimaps.newListMultimap(new ConcurrentHashMap<>(), CopyOnWriteArrayList::new);

    @Override
    public void registerPlugin(DynamicDataPlugin plugin) {
        plugins.add(plugin);
        plugin.collectMutatorListeners(new DynamicDataPlugin.MutatorCollector() {
            @Override
            @SuppressWarnings("all")
            public <T extends DataMutator> void accept(Class<? super T> type, Consumer<T> consumer) {
                mutatorConsumers.put(type, (Consumer) consumer);
            }
        });
    }

    public <T extends DataMutator> void fireMutation(T mutator) {
        final Class<?> mutatorType = Arrays.stream(mutator.getClass().getInterfaces()).filter(DataMutator.class::isAssignableFrom).findFirst().orElseThrow();
        mutatorConsumers.get(mutatorType).forEach(cons -> cons.accept(mutator));
    }

    public void forEachPlugin(Consumer<? super DynamicDataPlugin> consumer) {
        plugins.forEach(consumer);
    }
}
