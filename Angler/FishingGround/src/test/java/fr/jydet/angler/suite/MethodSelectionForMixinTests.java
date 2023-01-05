package fr.jydet.angler.suite;

import fr.jydet.angler.State;
import fr.jydet.angler.mixintargets.ObjectWithDuplicateMethods;
import io.tunabytes.bytecode.InvalidMixinException;
import org.junit.Assert;
import org.junit.Test;

import java.io.IOException;
import java.net.URLClassLoader;

import static fr.jydet.angler.MixinCompiler.compileAndLoad;
import static fr.jydet.angler.MixinCompiler.getFilesFromMixinsTank;
import static fr.jydet.angler.Utils.assertNumberOfClassesMixified;
import static fr.jydet.angler.Utils.launchMixins;

public class MethodSelectionForMixinTests {

    @Test
    public void test_noMethodSelection() throws IOException {
        URLClassLoader cl = compileAndLoad(getFilesFromMixinsTank("NoMethodSelectionMixin.java"));
        try {
            launchMixins(cl);
            Assert.fail("Mixin compilation should have failed !");
        } catch (InvalidMixinException expected) { }
    }

    @Test
    public void test_noMethodSelectionWithParameter() throws IOException {
        URLClassLoader cl = compileAndLoad(getFilesFromMixinsTank("UnambiguousMethodSelectionMixin.java"));
        launchMixins(cl);
        assertNumberOfClassesMixified(1);
        Assert.assertFalse(State.success);
        new ObjectWithDuplicateMethods().b();
        Assert.assertTrue(State.success);

        State.success = false;
        new ObjectWithDuplicateMethods().b("");
        Assert.assertTrue(State.success);
    }

    @Test
    public void test_NoMethodSelectionWithParams() throws IOException {
        URLClassLoader cl = compileAndLoad(getFilesFromMixinsTank("NoMethodSelectionWithParamMixin.java"));
        try {
            launchMixins(cl);
            Assert.fail("Mixin compilation should have failed !");
        } catch (InvalidMixinException expected) { expected.printStackTrace();}
    }

    @Test
    public void test_illegalDoubleStackSeparator() throws IOException {
        URLClassLoader cl = compileAndLoad(getFilesFromMixinsTank("IllegalDoubleStackSeparatorMixin.java"));
        try {
            launchMixins(cl);
            Assert.fail("Mixin compilation should have failed !");
        } catch (InvalidMixinException expected) { }
    }
}
