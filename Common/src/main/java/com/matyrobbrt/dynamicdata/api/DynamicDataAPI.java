package com.matyrobbrt.dynamicdata.api;

import com.matyrobbrt.dynamicdata.api.plugin.DynamicDataPlugin;
import com.matyrobbrt.dynamicdata.impl.DDAPIImpl;

public interface DynamicDataAPI {
    DynamicDataAPI INSTANCE = new DDAPIImpl();

    void addPlugin(DynamicDataPlugin plugin);
}
