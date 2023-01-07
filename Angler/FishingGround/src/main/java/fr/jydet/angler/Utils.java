package fr.jydet.angler;

import io.tunabytes.bytecode.MixinsBootstrap;
import org.apache.commons.io.FileUtils;
import org.junit.Assert;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Collections;
import java.util.concurrent.atomic.AtomicBoolean;

public final class Utils {
    public static final File TEMP_DIR;
    public static final File MIXINIFIED_DIR;

    static {
        TEMP_DIR = new File("./tmp");
        TEMP_DIR.mkdir();
        MIXINIFIED_DIR = new File(TEMP_DIR, "MIXINIFIED_OUTPUT");
        cleanup();
    }

    /**
     * Launch the MixinsBootstrap with the given classLoader, will fail if no mixin was found
     * <p>
     * @see fr.jydet.angler.MixinCompiler#compileAndLoad(File)
     * @param cl a classloader capable of loading mixins
     */
    public static void launchMixins(ClassLoader cl) {
        AtomicBoolean noMixinsFound = new AtomicBoolean(true);
        //Dump all the mixinified classes
        MixinsBootstrap.mixinedClassHook = (clazzName, mixinedClazzWriter) -> {
            noMixinsFound.set(false);
            System.out.println("Clazz '" + clazzName + "' mixinified ! (∩｀-´)⊃━☆ﾟ.*･｡ﾟ");
            dumpBytecode(clazzName, mixinedClazzWriter.toByteArray());
        };
        MixinsBootstrap.init(false, Collections.singleton(cl));
        if (noMixinsFound.get()) {
            Assert.fail("No mixins found !");
        }
    }

    private static void dumpBytecode(String name, byte[] bytes) {
        File f = new File(MIXINIFIED_DIR, name.replace("[^A-z]", "_") + ".class");
        try (FileOutputStream fos = new FileOutputStream(f)) {
            fos.write(bytes);
            System.out.println("Dump creation successful at '" + f.getAbsolutePath() + "'");
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Assert the number of classes generated after the mixin(s) appliances
     * @param number
     */
    public static void assertNumberOfClassesMixified(int number) {
        Assert.assertEquals(number, MIXINIFIED_DIR.list().length);
    }

    /**
     * Remove all generated classes/mixins
     */
    public static void cleanup() {
        try {
            FileUtils.cleanDirectory(TEMP_DIR);
            MIXINIFIED_DIR.mkdir();
        } catch (IOException e) {
            e.printStackTrace();
            Assert.fail(e.getMessage());
        }
    }

    public static void main(String[] args) {
        cleanup();
    }
}
