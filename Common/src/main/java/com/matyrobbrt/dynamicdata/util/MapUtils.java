package com.matyrobbrt.dynamicdata.util;

import com.google.common.collect.ImmutableMap;
import com.mojang.logging.LogUtils;
import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import org.slf4j.Logger;

import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandles;
import java.lang.reflect.Field;
import java.util.Map;

public final class MapUtils {
    private static final Logger LOGGER = LogUtils.getLogger();
    private static final MethodHandle ENTRIES_GETTER;
    private static final MethodHandle SIZE_GETTER;

    static {
        try {
            Field field = ImmutableMap.Builder.class.getDeclaredField("entries");
            field.setAccessible(true);
            ENTRIES_GETTER = MethodHandles.lookup().unreflectGetter(field);
            field = ImmutableMap.Builder.class.getDeclaredField("size");
            field.setAccessible(true);
            SIZE_GETTER = MethodHandles.lookup().unreflectGetter(field);
        } catch (NoSuchFieldException | IllegalAccessException e) {
            LOGGER.error("Could not access ImmutableMap$Builder entries or size fields, which is necessary for the Recipe API.");
            throw new IllegalStateException(e);
        }
    }

    /**
     * Builds a mutable map from an immutable map.
     * <p>This exists only because a builder will throw if a value is added 2 times. And copying a map is a bit bad.</p>
     *
     * @param builder the builder
     * @param <K>     the key type
     * @param <V>     the value type
     * @return a mutable map
     */
    @SuppressWarnings("unchecked")
    public static <K, V> Map<K, V> specialBuild(ImmutableMap.Builder<K, V> builder) {
        try {
            var entries = (Map.Entry<K, V>[]) ENTRIES_GETTER.invoke(builder);
            int size = (int) SIZE_GETTER.invoke(builder);
            var map = new Object2ObjectOpenHashMap<K, V>(size);

            for (var entry : entries) {
                if (entry == null) {
                    continue;
                }

                map.put(entry.getKey(), entry.getValue());
            }

            return map;
        } catch (Throwable throwable) {
            LOGGER.error("Could not get values of ImmutableMap$Builder entries or size fields.");
            throw new IllegalStateException(throwable);
        }
    }
}
