package com.matyrobbrt.dynamicdata.util.ref;

import sun.misc.Unsafe;

import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandles;
import java.lang.invoke.MethodType;
import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.List;

public class Reflection {
    public static final Unsafe UNSAFE;
    public static final MethodHandles.Lookup TRUSTED_LOOKUP;

    public static final MethodHandle NEW_LOOKUP;

    static {
        try {
            final Class<?> unsafeClass = Class.forName("sun.misc.Unsafe");
            final Field unsafeField = unsafeClass.getDeclaredField("theUnsafe");
            unsafeField.setAccessible(true);
            UNSAFE = (Unsafe) unsafeField.get(null);

            final Field implLookup = MethodHandles.Lookup.class.getDeclaredField("IMPL_LOOKUP");
            TRUSTED_LOOKUP = (MethodHandles.Lookup) UNSAFE.getObject(UNSAFE.staticFieldBase(implLookup), UNSAFE.staticFieldOffset(implLookup));

            NEW_LOOKUP = TRUSTED_LOOKUP.findConstructor(MethodHandles.Lookup.class, MethodType.methodType(void.class, Class.class));
        } catch (Exception e) {
            throw new RuntimeException("No unsafe?", e);
        }
    }

    public static MethodHandles.Lookup getLookup(Class<?> target) throws Throwable {
        return (MethodHandles.Lookup) NEW_LOOKUP.invokeExact(target);
    }

    public static <R, T> FieldHandle<R, T> fieldHandle(Class<R> clazz, String... possibleNames) {
        final List<String> possible = List.of(possibleNames);
        final Field field = Arrays.stream(clazz.getDeclaredFields()).filter(it ->
                possible.contains(it.getName())).findFirst().orElseThrow(() -> new IllegalArgumentException("No field with the name " + Arrays.toString(possibleNames) + " was found in " + clazz));
        final long offset = UNSAFE.objectFieldOffset(field);
        return new FieldHandle<>() {
            @Override
            @SuppressWarnings("unchecked")
            public T get(R object) {
                return (T) UNSAFE.getObject(object, offset);
            }

            @Override
            public void set(R object, T value) {
                UNSAFE.putObject(object, offset, value);
            }
        };
    }
}
