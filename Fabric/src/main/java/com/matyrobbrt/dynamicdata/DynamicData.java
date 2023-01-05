package com.matyrobbrt.dynamicdata;

import net.fabricmc.api.ModInitializer;

public class DynamicData implements ModInitializer {
    
    @Override
    public void onInitialize() {
        DynamicDataSetup.onModInit();
    }
}
