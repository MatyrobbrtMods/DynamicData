package com.matyrobbrt.dynamicdata.util;

import com.matyrobbrt.dynamicdata.DynamicDataSetup;
import com.matyrobbrt.dynamicdata.api.DynamicDataAPI;
import com.matyrobbrt.dynamicdata.api.ReloadListeners;
import com.matyrobbrt.dynamicdata.api.annotation.RegisterPlugin;
import com.matyrobbrt.dynamicdata.api.plugin.DynamicDataPlugin;
import com.matyrobbrt.dynamicdata.impl.RegisterRLL;
import com.matyrobbrt.dynamicdata.services.Platform;
import com.matyrobbrt.dynamicdata.util.fun.BiConsumers;
import com.matyrobbrt.dynamicdata.util.ref.Reflection;
import com.mojang.logging.LogUtils;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.server.packs.resources.SimplePreparableReloadListener;
import net.minecraft.util.profiling.ProfilerFiller;
import org.slf4j.Logger;

import java.lang.invoke.CallSite;
import java.lang.invoke.LambdaMetafactory;
import java.lang.invoke.MethodHandles;
import java.lang.invoke.MethodType;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

public class PluginCollector {
    private static final Logger LOG = LogUtils.getLogger();

    @SuppressWarnings({"unchecked", "rawtypes"})
    public static void collect() {
        final ThreadPoolExecutor service = (ThreadPoolExecutor)Executors.newFixedThreadPool(andModulo(Platform.INSTANCE.getModCount(), 100));
        service.setKeepAliveTime(2, TimeUnit.MINUTES);
        service.allowCoreThreadTimeOut(true);

        CompletableFuture.supplyAsync(() -> {
            final List<DynamicDataPlugin> plugins = new ArrayList<>();
            Platform.INSTANCE.findClassesWithAnnotation(RegisterPlugin.class, Platform.AnnotationLookup.EVERYWHERE)
                    .forEach(BiConsumers.throwing((String className, Map<String, Object> data) -> {
                        final Class<?> clazz = Class.forName(className, true, DynamicDataSetup.class.getClassLoader());
                        plugins.add((DynamicDataPlugin) clazz.getDeclaredConstructor().newInstance());
                    }).catching((cname, data, ex) -> LOG.error("Could not create exception of type " + cname + ": ", ex)));
            return plugins;
        }, service).thenAccept(plugins -> {
            plugins.forEach(DynamicDataAPI.INSTANCE::registerPlugin);
            LOG.info("Found {} plugins.", plugins.size());
        });

        record RLL<T, L extends SimplePreparableReloadListener<T>>(Class<L> listenerType, ReloadListeners.Stage stage, ReloadListeners.Listener<T, L> listener) {
            public void accept(ReloadListeners listeners) {
                listeners.onSimplePreparableApply(RLL.this.listenerType, RLL.this.stage, RLL.this.listener);
            }
        }

        CompletableFuture.supplyAsync(() -> {
            final List<RLL<?, ?>> rlls = new ArrayList<>();
            Platform.INSTANCE.findMethodsWithAnnotation(RegisterRLL.class, Platform.AnnotationLookup.ONLY_US)
                    .forEach(BiConsumers.throwing((Platform.MethodTarget methodTarget, Map<String, Object> args) -> {
                        if (((boolean)args.getOrDefault("clientOnly", false)) && !Platform.INSTANCE.isClient()) {
                            LOG.info("Skipping ReloadListener {} as it is client only.", methodTarget);
                            return;
                        }

                        final ReloadListeners.Stage stage = ((ReloadListeners.Stage) args.get("stage"));
                        final Class<?> clazz = Class.forName(methodTarget.className(), true, DynamicDataSetup.class.getClassLoader());
                        final MethodType methodType = MethodType.fromMethodDescriptorString(methodTarget.desc(), DynamicDataSetup.class.getClassLoader());
                        final MethodHandles.Lookup lookup = Reflection.getLookup(clazz);
                        final CallSite site = LambdaMetafactory.metafactory(
                                lookup, "invoke", MethodType.methodType(ReloadListeners.Listener.class),
                                MethodType.methodType(void.class, SimplePreparableReloadListener.class, ResourceManager.class, ProfilerFiller.class, Object.class),
                                lookup.findStatic(clazz, methodTarget.name(), methodType),
                                methodType
                        );
                        rlls.add(new RLL<>((Class)methodType.parameterType(0), stage, (ReloadListeners.Listener) site.getTarget().invokeExact()));
                    }).catching((target, data, ex) -> LOG.error("Could not add ReloadListener for method " + target + ": ", ex)));
            return rlls;
        }, service).thenAccept(listeners -> listeners.forEach(l -> l.accept(ReloadListeners.INSTANCE)));
    }

    private static int andModulo(int i, int toDivide) {
        final int modulo = i % toDivide;
        return i / toDivide + Math.max(modulo, 0);
    }
}
