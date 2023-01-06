package com.matyrobbrt.dynamicdata.extensions;

import net.minecraft.core.Registry;

public interface ExtendedTagLoader<T> {
    Registry<T> dynamicdata$getRegistry();
    void dynamicdata$setRegistry(Registry<T> registry, Runnable remover);

    void dynamicdata$clearReference();
}
