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

public class StackManipulationMixinTests {
    @Before
    public void cleanup() {
        Utils.cleanup();
    }

    @Test
    public void test_accessFirstParameters() throws IOException {
        URLClassLoader cl = compileAndLoad(getFilesFromMixinsTank("AccessFirstParametersMixin.java"));
        launchMixins(cl);
        assertNumberOfClassesMixified(1);
        Assert.assertNull(State.storage);
        new SimplePOJO().noopMethodWithArgs("expected", -1);
        Assert.assertEquals("expected", State.storage);

        State.storage = null;
        new SimplePOJO().noopMethodWithArgsAndLocals("expected", -1);
        Assert.assertEquals("expected", State.storage);
    }

    @Test
    public void test_accessSecondParameters() throws IOException {
        URLClassLoader cl = compileAndLoad(getFilesFromMixinsTank("AccessSecondParametersMixin.java"));
        launchMixins(cl);
        assertNumberOfClassesMixified(1);
        Assert.assertNull(State.storage);
        new SimplePOJO().noopMethodWithArgs("expected", 44);
        Assert.assertEquals(44, State.storage);

        State.storage = null;
        new SimplePOJO().noopMethodWithArgsAndLocals("expected", 44);
        Assert.assertEquals(44, State.storage);
    }

    @Test
    public void test_accessNonExistentParameters_noopMethod() throws IOException {
        URLClassLoader cl = compileAndLoad(getFilesFromMixinsTank("AccessNonExistentParametersNoopMethodMixin.java"));
        launchMixins(cl);
        try {
            new SimplePOJO().noopMethod();
            Assert.fail("Mixin should make this class crash !");
        } catch (VerifyError expected) { }
    }

    @Test
    public void test_accessNonExistentParameters_otherMethod() throws IOException {
        URLClassLoader cl = compileAndLoad(getFilesFromMixinsTank("AccessNonExistentParametersNoopMethodMixin.java"));
        launchMixins(cl);
        try {
            new SimplePOJO().noopMethodWithInternalStateChangeCall();
            Assert.fail("Mixin should make this class crash !");
        } catch (VerifyError expected) { }
    }

    @Test
    public void test_accessNonExistentParameters_noopMethodWithArgs() throws IOException {
        URLClassLoader cl = compileAndLoad(getFilesFromMixinsTank("AccessNonExistentParametersNoopMethodWithArgsMixin.java"));
        launchMixins(cl);
        try {
            new SimplePOJO().noopMethodWithArgs("", -1);
            Assert.fail("Mixin should make this class crash !");
        } catch (VerifyError expected) { }
    }

    @Test
    public void test_accessNonExistentParameters_noopMethodWithArgsAndLocals() throws IOException {
        URLClassLoader cl = compileAndLoad(getFilesFromMixinsTank("AccessNonExistentParametersNoopMethodWithArgsAndLocalsMixin.java"));
        launchMixins(cl);
        try {
            new SimplePOJO().noopMethodWithArgsAndLocals("", -1);
            Assert.fail("Mixin should make this class crash !");
        } catch (VerifyError expected) { }
    }
}
