package com.matyrobbrt.dynamicdata.api.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Annotate a class implementing {@link com.matyrobbrt.dynamicdata.api.plugin.DynamicDataPlugin} with a no-arg
 * constructor in order to automatically register it {@linkplain com.matyrobbrt.dynamicdata.api.DynamicDataAPI#registerPlugin as a plugin}.
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface RegisterPlugin {
}
