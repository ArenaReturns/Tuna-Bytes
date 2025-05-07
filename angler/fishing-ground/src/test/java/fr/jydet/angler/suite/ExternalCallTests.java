package fr.jydet.angler.suite;

import static fr.jydet.angler.MixinCompiler.compileAndLoad;
import static fr.jydet.angler.MixinCompiler.getFilesFromMixinsTank;
import static fr.jydet.angler.Utils.assertNumberOfClassesMixified;
import static fr.jydet.angler.Utils.launchMixins;

import fr.jydet.angler.State;
import fr.jydet.angler.Utils;
import fr.jydet.angler.mixintargets.SimplePOJO;
import java.io.IOException;
import java.net.URLClassLoader;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

public class ExternalCallTests {

        @Before
        public void cleanup() {
            Utils.cleanup();
        }

        @Test
        public void test_overwritecall_newMethod() throws IOException {
            URLClassLoader cl = compileAndLoad(getFilesFromMixinsTank("OverwriteCallMethod.java"));
            launchMixins(cl);
            assertNumberOfClassesMixified(1);
            State.success = false;
            
            new SimplePOJO().noopMethod();
            Assert.assertTrue(State.success);
        }

    @Test
    public void test_overwritecall_innerClass() throws IOException {
        URLClassLoader cl = compileAndLoad(getFilesFromMixinsTank("OverwriteCallInternalInnerClass.java"));
        launchMixins(cl);
        assertNumberOfClassesMixified(1);
        State.success = false;

        new SimplePOJO().noopMethod();
        Assert.assertTrue(State.success);
    }

    @Test
    public void test_overwritecall_interface() throws IOException {
        URLClassLoader cl = compileAndLoad(getFilesFromMixinsTank("OverwriteCallInternalInterface.java"));
        launchMixins(cl);
        assertNumberOfClassesMixified(1);
        State.success = false;

        new SimplePOJO().noopMethod();
        Assert.assertTrue(State.success);
    }

    @Test
    public void test_injectcall_newMethod() throws IOException {
        URLClassLoader cl = compileAndLoad(getFilesFromMixinsTank("InjectCallMethod.java"));
        launchMixins(cl);
        assertNumberOfClassesMixified(1);
        State.success = false;

        new SimplePOJO().noopMethod();
        Assert.assertTrue(State.success);
    }

    @Test
    public void test_injectcall_innerClass() throws IOException {
        URLClassLoader cl = compileAndLoad(getFilesFromMixinsTank("InjectCallInternalInnerClass.java"));
        launchMixins(cl);
        assertNumberOfClassesMixified(1);
        State.success = false;

        new SimplePOJO().noopMethod();
        Assert.assertTrue(State.success);
    }

    @Test
    public void test_injectcall_interface() throws IOException {
        URLClassLoader cl = compileAndLoad(getFilesFromMixinsTank("InjectCallInternalInterface.java"));
        launchMixins(cl);
        assertNumberOfClassesMixified(1);
        State.success = false;

        new SimplePOJO().noopMethod();
        Assert.assertTrue(State.success);
    }
}
