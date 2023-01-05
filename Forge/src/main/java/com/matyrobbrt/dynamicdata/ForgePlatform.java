package com.matyrobbrt.dynamicdata;

import com.matyrobbrt.dynamicdata.services.Platform;
import net.minecraftforge.fml.ModList;
import net.minecraftforge.fml.loading.moddiscovery.ModAnnotation;
import net.minecraftforge.forgespi.language.ModFileScanData;
import org.objectweb.asm.Type;

import java.lang.annotation.Annotation;
import java.lang.annotation.ElementType;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class ForgePlatform implements Platform {
    @Override
    public Map<String, Map<String, Object>> findClassesWithAnnotation(Class<? extends Annotation> annotationClass, AnnotationLookup lookup) {
        final Type desc = Type.getType(annotationClass);
        return annotations(lookup).filter(it -> it.annotationType().equals(desc) && it.targetType() == ElementType.TYPE)
                .collect(Collectors.toMap(it -> it.clazz().getClassName(), sanitize(ModFileScanData.AnnotationData::annotationData)));
    }

    @Override
    public Map<MethodTarget, Map<String, Object>> findMethodsWithAnnotation(Class<? extends Annotation> annotationClass, AnnotationLookup lookup) {
        final Type desc = Type.getType(annotationClass);
        return annotations(lookup).filter(it -> it.annotationType().equals(desc) && it.targetType() == ElementType.METHOD)
                .collect(Collectors.toMap(it -> {
                    final int indexOfOpening = it.memberName().indexOf('(');
                    return new MethodTarget(it.clazz().getClassName(), it.memberName().substring(indexOfOpening), it.memberName().substring(0, indexOfOpening));
                }, sanitize(ModFileScanData.AnnotationData::annotationData)));
    }

    private Stream<ModFileScanData.AnnotationData> annotations(AnnotationLookup lookup) {
        return (lookup == AnnotationLookup.EVERYWHERE ? ModList.get().getAllScanData().stream()
                .flatMap(it -> it.getAnnotations().stream()) : ModList.get().getModContainerById(Constants.MOD_ID).orElseThrow()
                .getModInfo().getOwningFile().getFile().getScanResult().getAnnotations().stream());
    }

    @SuppressWarnings("all")
    private <T> Function<T, Map<String, Object>> sanitize(Function<? super T, Map<String, Object>> fun) {
        return t -> {
            final Map<String, Object> data = fun.apply(t);
            Map.copyOf(data).forEach((key, val) -> {
                if (val instanceof ModAnnotation.EnumHolder holder) {
                    try {
                        data.put(key, Enum.valueOf((Class) Class.forName(Type.getType(holder.getDesc()).getClassName()), holder.getValue()));
                    } catch (ClassNotFoundException e) {
                        throw new RuntimeException(e);
                    }
                }
            });
            return data;
        };
    }
}
