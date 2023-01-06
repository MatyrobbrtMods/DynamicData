package com.matyrobbrt.dynamicdata.util.ref;

public interface FieldHandle<R, T> {
    T get(R object);

    void set(R object, T value);
}
