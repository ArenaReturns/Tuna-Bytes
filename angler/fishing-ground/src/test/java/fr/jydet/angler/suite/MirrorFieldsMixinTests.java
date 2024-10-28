package fr.jydet.angler.suite;

import fr.jydet.angler.State;
import fr.jydet.angler.Utils;
import fr.jydet.angler.mixintargets.POJOWithParent;
import fr.jydet.angler.mixintargets.SimplePOJO;
import io.tunabytes.bytecode.InvalidMixinException;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.io.IOException;
import java.net.URLClassLoader;

import static fr.jydet.angler.MixinCompiler.*;
import static fr.jydet.angler.Utils.assertNumberOfClassesMixified;
import static fr.jydet.angler.Utils.launchMixins;

public class MirrorFieldsMixinTests {

    @Before
    public void cleanup() {
        Utils.cleanup();
    }

    @Test
    public void test_mirrorInternalFieldMixin_noMixin() {
        SimplePOJO simplePOJO = new SimplePOJO();
        Assert.assertFalse(simplePOJO.getInternalAtomicBoolean().get());
        simplePOJO.noopMethod();
        Assert.assertFalse(simplePOJO.getInternalAtomicBoolean().get());
    }

    @Test
    public void test_mirrorInternalFieldMixin() throws IOException {
        URLClassLoader cl = compileAndLoad(getFilesFromMixinsTank("MirrorInternalFieldMixin.java"));
        launchMixins(cl);
        assertNumberOfClassesMixified(1);
        SimplePOJO simplePOJO = new SimplePOJO();
        Assert.assertFalse(simplePOJO.getInternalAtomicBoolean().get());
        simplePOJO.noopMethod();
        Assert.assertTrue(simplePOJO.getInternalAtomicBoolean().get());
    }

    @Test
    public void test_mirrorInternalFieldMixin_qualified() throws IOException {
        URLClassLoader cl = compileAndLoad(getFilesFromMixinsTank("MirrorQualifiedInternalFieldMixin.java"));
        launchMixins(cl);
        assertNumberOfClassesMixified(1);
        SimplePOJO simplePOJO = new SimplePOJO();
        Assert.assertFalse(simplePOJO.getInternalAtomicBoolean().get());
        simplePOJO.noopMethod();
        Assert.assertTrue(simplePOJO.getInternalAtomicBoolean().get());
    }

    @Test
    public void test_mirrorAssignInternalField() throws IOException {
        URLClassLoader cl = compileAndLoad(getFilesFromMixinsTank("MirrorAssignInternalFieldMixin.java"));
        launchMixins(cl);
        assertNumberOfClassesMixified(1);
        SimplePOJO simplePOJO = new SimplePOJO();
        Assert.assertFalse(simplePOJO.getInternalAtomicBoolean().get());
        simplePOJO.noopMethod();
        Assert.assertTrue(simplePOJO.getInternalAtomicBoolean().get());
    }

    @Test
    public void test_mirrorUnknownInternalField() throws IOException {
        URLClassLoader cl = compileAndLoad(getFilesFromMixinsTank("MirrorUnknownFieldMixin.java"));
        launchMixins(cl);
        try {
            new SimplePOJO().noopMethod();
            Assert.fail("Mixin should make this class crash !");
        } catch (NoSuchFieldError expected) { }
    }

    @Test
    public void test_mirrorIsFinalField() throws IOException {
        URLClassLoader cl = compileAndLoad(getFilesFromMixinsTank("ErrorMirrorFinalMixin.java"));
        try {
            launchMixins(cl);
            Assert.fail("Mixin should be detected as invalid: A @Mirror field must not be final !");
        } catch (InvalidMixinException expected) { }
    }

    @Test
    public void test_mirrorParentInternalField() throws IOException {
        URLClassLoader cl = compileAndLoad(getFilesFromMixinsTank("MirrorParentFieldMixin.java"));
        launchMixins(cl);
        assertNumberOfClassesMixified(1);
        POJOWithParent pojoWithParent = new POJOWithParent();
        Assert.assertEquals(0, pojoWithParent.getProtectedParentField());
        Assert.assertEquals(0, pojoWithParent.getPublicParentField());
        Assert.assertEquals(0, pojoWithParent.getPrivateParentField());
        pojoWithParent.noopMethod();
        Assert.assertEquals(1, pojoWithParent.getProtectedParentField());
        Assert.assertEquals(1, pojoWithParent.getPublicParentField());
        Assert.assertEquals(0, pojoWithParent.getPrivateParentField());
    }

    @Test
    public void test_mirrorAssignFinalInternalField() throws IOException {
        URLClassLoader cl = compileAndLoad(getFilesFromMixinsTank("MirrorAssignFinalInternalFieldMixin.java"));
        launchMixins(cl);
        assertNumberOfClassesMixified(1);
        SimplePOJO simplePOJO = new SimplePOJO();
        Assert.assertFalse(simplePOJO.getFinalInternalAtomicBoolean().get());
        simplePOJO.noopMethod();
        Assert.assertTrue(simplePOJO.getFinalInternalAtomicBoolean().get());
    }

    @Test
    public void test_mirrorReferenceFinalInternalFieldMixin() throws IOException {
        URLClassLoader cl = compileAndLoad(getFilesFromMixinsTank("MirrorReferenceFinalInternalFieldMixin.java"));
        launchMixins(cl);
        assertNumberOfClassesMixified(1);
        SimplePOJO simplePOJO = new SimplePOJO();
        Assert.assertFalse(simplePOJO.getFinalInternalAtomicBoolean().get());
        simplePOJO.noopMethod();
        Assert.assertTrue(simplePOJO.getFinalInternalAtomicBoolean().get());
    }


    @Test
    public void test_accessLocalParametersMixin() throws IOException {
        URLClassLoader cl = compileAndLoad(getFilesFromMixinsTank("AccessLocalParametersMixin.java"));
        launchMixins(cl);
        assertNumberOfClassesMixified(1);
        SimplePOJO simplePOJO = new SimplePOJO();
        Assert.assertNull(State.storage);
        simplePOJO.noopMethodWithArgsAndLocals("expected", 911);
        Assert.assertEquals(911 + 1, State.storage);
    }

}
