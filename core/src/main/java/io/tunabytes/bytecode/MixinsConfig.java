package io.tunabytes.bytecode;

import io.tunabytes.classloader.TunaClassDefiner;

import java.io.IOException;
import java.io.InputStream;
import java.net.URLClassLoader;
import java.util.*;
import java.util.function.BiFunction;
import java.util.function.Function;

import static java.util.Objects.requireNonNull;

final class MixinsConfig {

    private final List<MixinEntry> mixinEntries = new ArrayList<>();
    private final Map<String, Class<?>> neighbors = new LinkedHashMap<>();

    public MixinsConfig(Collection<ClassLoader> classLoaders) {
        for (ClassLoader classLoader : classLoaders) {
            Function<String, InputStream> resourceLoader = name -> classLoader.getResourceAsStream(
                    classLoader instanceof URLClassLoader ? name :
                            "/" + name);
            try {
                InputStream configStream = resourceLoader.apply("mixins.properties");
                if (configStream == null) throw new RuntimeException("mixins.properties not found. Did you add tuna-bytes as an annotation processor?");
                Properties properties = new Properties();
                properties.load(configStream);
                configStream.close();
                properties.forEach((key, value) -> mixinEntries.add(new MixinEntry((String) key, (String) value)));
                if (TunaClassDefiner.requiresNeighbor()) {
                    InputStream neighborsStream = resourceLoader.apply("/mixins-neighbors.properties");
                    if (neighborsStream == null) throw new RuntimeException("mixins-neighbors.properties not found. Did you add tuna-bytes as an annotation processor?");
                    Properties neighborsProps = new Properties();
                    neighborsProps.load(neighborsStream);
                    neighborsStream.close();
                    neighborsProps.forEach((key, value) -> {
                        try {
                            neighbors.put((String) key, Class.forName(String.valueOf(value), true, classLoader));
                        } catch (ClassNotFoundException e) {
                            e.printStackTrace();
                        }
                    });
                }
                return;
            } catch (IOException e) {
                e.printStackTrace();
            } catch (Exception ignored) {}
        }
        throw new NullPointerException("mixins.properties not found. Did you add tuna-bytes as an annotation processor?");
    }

    public Class<?> getNeighbor(String name) {
        return neighbors.get(name.substring(0, name.lastIndexOf('.')));
    }

    public List<MixinEntry> getMixinEntries() {
        return mixinEntries;
    }
}
