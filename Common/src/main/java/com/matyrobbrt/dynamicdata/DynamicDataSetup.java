package com.matyrobbrt.dynamicdata;

import com.matyrobbrt.dynamicdata.util.PluginCollector;

public class DynamicDataSetup {

    static void onModInit() {
        PluginCollector.collect();
    }
}
