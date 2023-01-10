package com.matyrobbrt.dynamicdata.api;

import com.matyrobbrt.dynamicdata.api.plugin.DynamicDataPlugin;
import com.matyrobbrt.dynamicdata.impl.DDAPIImpl;

/**
 * The class used to interact with DynamicData.
 *
 * @see #INSTANCE
 */
public interface DynamicDataAPI {
    /**
     * The instance of the API.
     */
    DynamicDataAPI INSTANCE = new DDAPIImpl();

    /**
     * Registers a plugin. This method may be called during mod setup.
     *
     * @param plugin the plugin
     */
    void registerPlugin(DynamicDataPlugin plugin);
}
