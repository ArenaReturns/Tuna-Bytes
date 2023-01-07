package io.tunabytes.bytecode;

import org.objectweb.asm.ClassReader;

import java.io.IOException;
import java.io.InputStream;
import java.util.Set;

public final class MixinEntry {

    private final String mixinClass;
    private final String targetClass;
    private ClassLoader classLoader = Thread.currentThread().getContextClassLoader();

    MixinEntry(String mixinClass, String targetClass) {
        this.mixinClass = mixinClass;
        this.targetClass = targetClass;
    }

    public String getMixinClass() {
        return mixinClass;
    }

    public String getTargetClass() {
        return targetClass;
    }

    public ClassReader mixinReader(Set<ClassLoader> classLoaders) {
        return getClassReader(classLoaders, mixinClass);
    }

    public ClassReader targetReader(Set<ClassLoader> classLoaders) {
        return getClassReader(classLoaders, targetClass);
    }

    private ClassReader getClassReader(Set<ClassLoader> classLoaders, String clazz) {
        for (ClassLoader loader : classLoaders) {
            try (InputStream stream = loader.getResourceAsStream(clazz.replace('.', '/') + ".class")) {
                if (stream != null) {
                    classLoader = loader;
                    return new ClassReader(stream);
                }
            } catch (IOException e) {
                throw new ReadClassException(clazz, e);
            }
        }
        throw new IllegalStateException("Class not found: " + targetClass + ". Make sure you specify any additional classloaders in MixinsBootstrap.init(...)!");
    }

    public ClassLoader getClassLoader() {
        return classLoader;
    }
}
