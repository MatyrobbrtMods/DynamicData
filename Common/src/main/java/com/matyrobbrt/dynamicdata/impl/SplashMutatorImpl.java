package com.matyrobbrt.dynamicdata.impl;

import com.matyrobbrt.dynamicdata.api.ReloadListeners;
import com.matyrobbrt.dynamicdata.api.SplashMutator;
import com.matyrobbrt.dynamicdata.util.ref.FieldHandle;
import com.matyrobbrt.dynamicdata.util.ref.Reflection;
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

    private static final FieldHandle<SplashManager, List<String>> SPLASHES_FIELD = Reflection.fieldHandle(SplashManager.class, "splashes", "f_118862_", "field_17906");
    @RegisterRLL(stage = ReloadListeners.Stage.POST, clientOnly = true)
    static void onSplashesLoad(SplashManager manager, ResourceManager resourceManager, ProfilerFiller profiler, List<String> data) {
        final List<String> splashes = SPLASHES_FIELD.get(manager);
        DDAPIImpl.INSTANCE.get().fireMutation(new SplashMutatorImpl(splashes));
    }
}
