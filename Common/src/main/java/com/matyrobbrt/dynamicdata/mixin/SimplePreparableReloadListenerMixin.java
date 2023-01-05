package com.matyrobbrt.dynamicdata.mixin;

import com.matyrobbrt.dynamicdata.api.ReloadListeners;
import com.matyrobbrt.dynamicdata.impl.recipe.ReloadListenersImpl;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.server.packs.resources.SimplePreparableReloadListener;
import net.minecraft.util.profiling.ProfilerFiller;
import org.spongepowered.asm.mixin.Debug;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Debug(export = true)
@Mixin(SimplePreparableReloadListener.class)
public class SimplePreparableReloadListenerMixin<T> {
    // m_10789_
    // lambda$reload$1(Lnet/minecraft/server/packs/resources/ResourceManager;Lnet/minecraft/util/profiling/ProfilerFiller;Ljava/lang/Object;)V

    @Inject(at = @At("HEAD"), method = {"lambda$reload$1", "method_18790"})
    private void dynamicdata$preApply(ResourceManager resourceManager, ProfilerFiller profiler, T data, CallbackInfo ci) {
        this.dynamicdata$invokeApplyListeners(ReloadListeners.Stage.PRE, resourceManager, profiler, data);
    }

    @Inject(at = @At("TAIL"), method = {"lambda$reload$1", "method_18790"})
    private void dynamicdata$postApply(ResourceManager resourceManager, ProfilerFiller profiler, T data, CallbackInfo ci) {
        this.dynamicdata$invokeApplyListeners(ReloadListeners.Stage.POST, resourceManager, profiler, data);
    }

    @Unique
    @SuppressWarnings({"DataFlowIssue", "unchecked", "rawtypes"})
    private void dynamicdata$invokeApplyListeners(ReloadListeners.Stage stage, ResourceManager manager, ProfilerFiller profiler, T data) {
        ((ReloadListenersImpl) ReloadListeners.INSTANCE).invoke(stage, (SimplePreparableReloadListener) (Object)this, manager, profiler, data);
    }
}
