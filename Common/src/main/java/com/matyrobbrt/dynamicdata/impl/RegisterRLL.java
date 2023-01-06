package com.matyrobbrt.dynamicdata.impl;

import com.matyrobbrt.dynamicdata.api.ReloadListeners;

public @interface RegisterRLL {
    ReloadListeners.Stage stage();
    boolean clientOnly() default false;
}