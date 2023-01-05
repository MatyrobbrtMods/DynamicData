package com.matyrobbrt.dynamicdata.impl;

import com.google.common.base.Suppliers;
import com.matyrobbrt.dynamicdata.api.DynamicDataAPI;
import com.matyrobbrt.dynamicdata.api.plugin.DynamicDataPlugin;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.function.Consumer;
import java.util.function.Supplier;

public class DDAPIImpl implements DynamicDataAPI {
    public static final Supplier<DDAPIImpl> INSTANCE = Suppliers.memoize(() -> (DDAPIImpl) DynamicDataAPI.INSTANCE);

    private final List<DynamicDataPlugin> plugins = new CopyOnWriteArrayList<>();
    @Override
    public void addPlugin(DynamicDataPlugin plugin) {
        plugins.add(plugin);
    }

    public void fireForPlugins(Consumer<? super DynamicDataPlugin> consumer) {
        plugins.forEach(consumer);
    }
}
