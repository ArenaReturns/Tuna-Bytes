package fr.jydet.angler.suite;

import fr.jydet.angler.Utils;
import fr.jydet.angler.mixintargets.POJOWithParent;
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

public class MirrorMethodsMixinTests {

    @Before
    public void cleanup() {
        Utils.cleanup();
    }

    @Test
    public void test_mirrorInternalMethodMixin_noMixin() {
        SimplePOJO simplePOJO = new SimplePOJO();
        Assert.assertFalse(simplePOJO.getState().hasChanged());
        simplePOJO.noopMethod();
        Assert.assertFalse(simplePOJO.getState().hasChanged());
    }

    @Test
    public void test_mirrorInternalMethodMixin() throws IOException {
        URLClassLoader cl = compileAndLoad(getFilesFromMixinsTank("MirrorInternalMethodMixin.java"));
        launchMixins(cl);
        assertNumberOfClassesMixified(1);
        SimplePOJO simplePOJO = new SimplePOJO();
        Assert.assertFalse(simplePOJO.getState().hasChanged());
        simplePOJO.noopMethod();
        Assert.assertTrue(simplePOJO.getState().hasChanged());
    }

    @Test
    public void test_mirrorInternalMethodValueMixin() throws IOException {
        URLClassLoader cl = compileAndLoad(getFilesFromMixinsTank("MirrorInternalMethodValueMixin.java"));
        launchMixins(cl);
        assertNumberOfClassesMixified(1);
        SimplePOJO simplePOJO = new SimplePOJO();
        Assert.assertFalse(simplePOJO.getState().hasChanged());
        simplePOJO.noopMethod();
        Assert.assertTrue(simplePOJO.getState().hasChanged());
    }

    @Test
    public void test_mirrorUnknownInternalMethod() throws IOException {
        URLClassLoader cl = compileAndLoad(getFilesFromMixinsTank("MirrorUnknownMethodMixin.java"));
        launchMixins(cl);
        try {
            new SimplePOJO().noopMethod();
            Assert.fail("Mixin should make this class crash !");
        } catch (NoSuchMethodError expected) { }
    }

    @Test
    public void test_mirrorParentInternalMethod() throws IOException {
        URLClassLoader cl = compileAndLoad(getFilesFromMixinsTank("MirrorParentMethodMixin.java"));
        launchMixins(cl);
        assertNumberOfClassesMixified(1);
        POJOWithParent pojoWithParent = new POJOWithParent();
        Assert.assertFalse(pojoWithParent.getState().hasChanged());
        pojoWithParent.noopMethod();
        Assert.assertTrue(pojoWithParent.getState().hasChanged());
    }

    @Test
    public void test_mirrorUseFinalMethod() throws IOException {
        URLClassLoader cl = compileAndLoad(getFilesFromMixinsTank("MirrorUseFinalMethodMixin.java"));
        launchMixins(cl);
        assertNumberOfClassesMixified(1);
        SimplePOJO simplePOJO = new SimplePOJO();
        Assert.assertFalse(simplePOJO.getState().hasChanged());
        simplePOJO.noopMethod();
        Assert.assertTrue(simplePOJO.getState().hasChanged());
    }

}
