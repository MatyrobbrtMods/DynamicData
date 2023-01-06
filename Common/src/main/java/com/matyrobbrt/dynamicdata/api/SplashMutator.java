package com.matyrobbrt.dynamicdata.api;

import java.util.List;

public interface SplashMutator extends DataMutator {
    void addSplash(String splash);
    void removeSlash(String splash);
    List<String> getSplashes();
    void clear();
}
