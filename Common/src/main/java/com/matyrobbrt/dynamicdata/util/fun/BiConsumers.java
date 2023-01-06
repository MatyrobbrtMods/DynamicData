package com.matyrobbrt.dynamicdata.util.fun;

import java.util.function.BiConsumer;

public class BiConsumers {
    public static <A, B, T extends Throwable> ThrowingBiConsumer<A, B, T> throwing(ThrowingBiConsumer<A, B, T> biConsumer) {
        return biConsumer;
    }

    @FunctionalInterface
    public interface ThrowingBiConsumer<A, B, T extends Throwable> {
        void accept(A a, B b) throws T;

        @SuppressWarnings("unchecked")
        default BiConsumer<A, B> catching(ExceptionCatcher<A, B, T> catcher) {
            return (a, b) -> {
                try {
                    this.accept(a, b);
                } catch (Throwable throwable) {
                    try {
                        catcher.catchEx(a, b, (T) throwable);
                    } catch (ClassCastException exception) {
                        throw new RuntimeException(throwable);
                    }
                }
            };
        }
    }

    @FunctionalInterface
    public interface ExceptionCatcher<A, B, T extends Throwable> {
        void catchEx(A a, B b, T exception);
    }
}
