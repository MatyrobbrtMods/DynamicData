package com.matyrobbrt.dynamicdata.impl;

import com.matyrobbrt.dynamicdata.api.ReloadListeners;
import com.matyrobbrt.dynamicdata.api.mutation.SplashMutator;
import com.matyrobbrt.dynamicdata.mixin.access.SplashManagerAccess;
import net.minecraft.client.resources.SplashManager;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.util.profiling.ProfilerFiller;

import java.util.List;

public record SplashMutatorImpl(List<String> splashes) implements SplashMutator {

    @Override
    public void addSplash(String splash) {
        splashes.add(splash);
    }

    @Override
    public void removeSlash(String splash) {
        splashes.remove(splash);
    }

    @Override
    public List<String> getSplashes() {
        return splashes;
    }

    @Override
    public void clear() {
        splashes.clear();
    }

    @RegisterRLL(stage = ReloadListeners.Stage.POST, clientOnly = true)
    static void onSplashesLoad(SplashManager manager, ResourceManager resourceManager, ProfilerFiller profiler, List<String> data) {
        final List<String> splashes = ((SplashManagerAccess)(manager)).getSplashes();
        DDAPIImpl.INSTANCE.get().fireMutation(new SplashMutatorImpl(splashes));
    }
}
