package com.matyrobbrt.dynamicdata;

import net.minecraftforge.fml.common.Mod;

@Mod(Constants.MOD_ID)
public class DynamicData {
    
    public DynamicData() {
        DynamicDataSetup.onModInit();
    }
}