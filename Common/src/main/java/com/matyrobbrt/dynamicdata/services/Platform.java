package com.matyrobbrt.dynamicdata.services;

import java.lang.annotation.Annotation;
import java.util.Map;
import java.util.ServiceLoader;

public interface Platform {
    Platform INSTANCE = ServiceLoader.load(Platform.class).iterator().next();

    Map<String, Map<String, Object>> findClassesWithAnnotation(Class<? extends Annotation> annotationClass, AnnotationLookup lookup);
    Map<MethodTarget, Map<String, Object>> findMethodsWithAnnotation(Class<? extends Annotation> annotationClass, AnnotationLookup lookup);

    int getModCount();

    boolean isClient();

    enum AnnotationLookup {
        EVERYWHERE,
        ONLY_US
    }

    record MethodTarget(String className, String desc, String name) {}
}
