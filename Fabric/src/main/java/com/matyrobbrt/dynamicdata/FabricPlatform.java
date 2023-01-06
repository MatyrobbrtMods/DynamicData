package com.matyrobbrt.dynamicdata;

import com.matyrobbrt.dynamicdata.services.Platform;
import net.fabricmc.api.EnvType;
import net.fabricmc.loader.api.FabricLoader;
import org.objectweb.asm.AnnotationVisitor;
import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.Type;
import org.objectweb.asm.TypePath;

import java.io.IOException;
import java.lang.annotation.Annotation;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.stream.Stream;

public class FabricPlatform implements Platform {
    @Override
    public Map<String, Map<String, Object>> findClassesWithAnnotation(Class<? extends Annotation> annotationClass, AnnotationLookup lookup) {
        final Map<String, Map<String, Object>> data = new HashMap<>();

        final String desc = Type.getDescriptor(annotationClass);
        try {
            visitAllClasses(new BaseClassVisitor() {
                @Override
                public AnnotationVisitor visitAnnotation(String descriptor, boolean visible) {
                    if (descriptor.equals(desc)) {
                        return new AnnVisitor(data.computeIfAbsent(className, k -> new HashMap<>()));
                    }
                    return null;
                }
            }, lookup);
        } catch (Exception exception) {
            throw new RuntimeException(exception);
        }

        return data;
    }

    @Override
    public Map<MethodTarget, Map<String, Object>> findMethodsWithAnnotation(Class<? extends Annotation> annotationClass, AnnotationLookup lookup) {
        final Map<MethodTarget, Map<String, Object>> data = new HashMap<>();

        final String desc = Type.getDescriptor(annotationClass);
        try {
            visitAllClasses(new BaseClassVisitor() {
                @Override
                public MethodVisitor visitMethod(int access, String name, String mdesc, String signature, String[] exceptions) {
                    return new MethodVisitor(Opcodes.ASM9) {
                        @Override
                        public AnnotationVisitor visitAnnotation(String descriptor, boolean visible) {
                            if (descriptor.equals(desc)) {
                                return new AnnVisitor(data.computeIfAbsent(new MethodTarget(
                                        className, mdesc, name
                                ), k -> new HashMap<>()));
                            }
                            return null;
                        }
                    };
                }
            }, lookup);
        } catch (Exception exception) {
            throw new RuntimeException(exception);
        }

        return data;
    }

    @Override
    public int getModCount() {
        return FabricLoader.getInstance().getAllMods().size();
    }

    @Override
    public boolean isClient() {
        return FabricLoader.getInstance().getEnvironmentType() == EnvType.CLIENT;
    }

    private void visitAllClasses(ClassVisitor visitor, AnnotationLookup lookup) throws IOException {
        final Iterator<Path> dirsToSearch = dirs(lookup).iterator();
        while (dirsToSearch.hasNext()) {
            try (final Stream<Path> classes = Files.find(dirsToSearch.next(), Integer.MAX_VALUE, (path, basicFileAttributes) -> path.toString().endsWith(".class"))) {
                final Iterator<Path> itr = classes.iterator();
                while (itr.hasNext()) {
                    final Path classPath = itr.next();
                    new ClassReader(Files.readAllBytes(classPath)).accept(visitor, ClassReader.SKIP_DEBUG | ClassReader.SKIP_CODE | ClassReader.SKIP_FRAMES);
                }
            }
        }
    }

    private Stream<Path> dirs(AnnotationLookup lookup) {
        return (lookup == AnnotationLookup.EVERYWHERE ? FabricLoader.getInstance().getAllMods()
                .stream() : FabricLoader.getInstance().getModContainer(Constants.MOD_ID).stream())
                .flatMap(it -> it.getRootPaths().stream());
    }

    private static final class AnnVisitor extends AnnotationVisitor {
        private final Map<String, Object> map;

        private AnnVisitor(Map<String, Object> map) {
            super(Opcodes.ASM9);
            this.map = map;
        }

        @Override
        public void visit(String name, Object value) {
            map.put(name, value);
        }

        @Override
        @SuppressWarnings({"unchecked", "rawtypes"})
        public void visitEnum(String name, String descriptor, String value) {
            try {
                final Class clazz = Class.forName(Type.getType(descriptor).getClassName());
                map.put(name, Enum.valueOf(clazz, value));
            } catch (ClassNotFoundException ignored) {
            }
        }
    }

    private static abstract class BaseClassVisitor extends ClassVisitor {

        protected BaseClassVisitor() {
            super(Opcodes.ASM9);
        }

        protected String className;

        @Override
        public void visit(int version, int access, String name, String signature, String superName, String[] interfaces) {
            className = Type.getObjectType(name).getClassName();
        }
    }
}
