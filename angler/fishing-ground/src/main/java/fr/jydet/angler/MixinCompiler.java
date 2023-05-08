package fr.jydet.angler;

import com.google.testing.compile.Compilation;
import com.google.testing.compile.JavaFileObjects;
import io.tunabytes.ap.MixinsProcessor;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;
import org.junit.Assert;

import javax.tools.JavaFileObject;
import java.io.File;
import java.io.FileNotFoundException;
import java.net.URL;
import java.net.URLClassLoader;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static com.google.testing.compile.CompilationSubject.assertThat;
import static com.google.testing.compile.Compiler.javac;

public class MixinCompiler {
    public static final String COMPILED_OUT_DIR = "CLASS_OUTPUT";
    private static File getFileFromMixinsTank(String className) throws FileNotFoundException {
      File directory = new File("../mixins-tank/src/");
      if (! directory.exists()) directory = new File("Angler/mixins-tank/src/");
      return FileUtils.listFiles(directory, new String[]{"java"}, true)
                .stream()
                .filter(File::isFile)
                .filter(f -> f.getName().equals(className))
                .findAny().orElseThrow(() -> new FileNotFoundException(className));
    }

    public static List<File> getFilesFromMixinsTank(String... classNames) throws FileNotFoundException {
        List<File> list = new ArrayList<>();
        for (String className : classNames) {
            File fileFromMixinsTank = getFileFromMixinsTank(className);
            list.add(fileFromMixinsTank);
        }
        return list;
    }

    public static URLClassLoader compileAndLoad(List<File> sourceFiles) {
        try {
            List<String> toLoad = compile(sourceFiles);

            URLClassLoader compiledClassLoader = getCompiledClassLoader();
            for (String fullName : toLoad) {
                Class.forName(fullName, true, compiledClassLoader);
            }
            return compiledClassLoader;
        } catch (Exception e) {
            throw fatalError(e);
        }
    }

    public static URLClassLoader compileAndLoad(File sourceFile) {
        try {
            return compileAndLoad(Collections.singletonList(sourceFile));
        } catch (Exception e) {
            throw fatalError(e);
        }
    }

    private static List<String> compile(List<File> sourceFiles) {
        if (sourceFiles.size() == 0) {
            throw new IllegalArgumentException("No sources provided !");
        }
        try {
            Map<String, String> toCompile = new HashMap<>();
            List<String> out = new ArrayList<>();
            for (File sourceFile : sourceFiles) {
                if (sourceFile == null) throw new IllegalArgumentException("SourceFile cannot be null !");
                List<String> elements = Files.readAllLines(sourceFile.toPath());
                if (elements.size() == 0) throw new IllegalArgumentException("SourceFile '" + sourceFile.getAbsolutePath() + "' cannot be empty");
                String classData = String.join("\n", elements);
                String name = FilenameUtils.removeExtension(sourceFile.getName());
                toCompile.put(name, classData);

                String s = elements.get(0);
                String packageName = "";
                if (s.startsWith("package ")) {
                    String sanitizedPackageName = s.substring(8)
                            .replaceAll("\\s|;", "")
                            .replaceAll("(?s)/\\*.*?\\*/", "")
                            .replaceAll("\\/\\*([\\S\\s]+?)\\*\\/", "");
                    packageName = sanitizedPackageName + ".";
                }

                out.add(packageName + sourceFile.getName().substring(0, sourceFile.getName().lastIndexOf('.')));
            }
            compile(toCompile);
            return out;
        } catch (Exception e) {
            throw fatalError(e);
        }
    }
    public static Compilation compile(String name, String source) {
        HashMap<String, String> map = new HashMap<>();
        map.put(name, source);
        return compile(map);
    }

    public static Compilation compile(Map<String, String> sourcesByName) {
        Compilation compilation = javac()
            .withProcessors(new MixinsProcessor())
            .compile(sourcesByName.entrySet()
                    .stream()
                    .map(e -> JavaFileObjects.forSourceString(e.getKey(), e.getValue()))
                .collect(Collectors.toList())
            );

        assertThat(compilation).succeededWithoutWarnings();

        compilation.generatedFiles()
            .stream()
            .filter(f -> f.getKind() != JavaFileObject.Kind.SOURCE)
            .forEach(mixinData -> {
                try {
                    File file = new File(Utils.TEMP_DIR, mixinData.getName());
                    file.getParentFile().mkdirs();
                    Files.copy(mixinData.openInputStream(),
                            file.toPath());
                } catch (Exception e) {
                    e.printStackTrace();
                    Assert.fail(e.getMessage());
                }
            });
        return compilation;
    }

    public static URLClassLoader getCompiledClassLoader() throws Exception {
        return new URLClassLoader(new URL[]{
                new File(Utils.TEMP_DIR, COMPILED_OUT_DIR).toURI().toURL()
        }, MixinCompiler.class.getClassLoader());
    }

    private static RuntimeException fatalError(Exception e) {
        e.printStackTrace();
        Assert.fail();
        return new RuntimeException(e);
    }

}
