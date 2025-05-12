package fr.jydet.angler.suite;

import fr.jydet.angler.State;
import fr.jydet.angler.Utils;
import fr.jydet.angler.mixintargets.SimplePOJO;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.io.IOException;
import java.net.URLClassLoader;

import static fr.jydet.angler.MixinCompiler.compileAndLoad;
import static fr.jydet.angler.MixinCompiler.getFilesFromMixinsTank;
import static fr.jydet.angler.Utils.assertNumberOfClassesMixified;
import static fr.jydet.angler.Utils.launchMixins;

public class PrivateClazzAccessorMixinTests {
    @Before
    public void cleanup() {
        Utils.cleanup();
    }

    @Test
    public void test_usingValidPrivateClazzAccessor() throws IOException {
        URLClassLoader cl = compileAndLoad(getFilesFromMixinsTank("PrivateClazzAccessorMixin.java"));
        launchMixins(cl);
        assertNumberOfClassesMixified(1);
        Assert.assertFalse(State.success);
        new SimplePOJO().noopMethod();
        Assert.assertTrue(State.success);
    }
}
