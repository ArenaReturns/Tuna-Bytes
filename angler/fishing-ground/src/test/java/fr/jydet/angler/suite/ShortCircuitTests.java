package fr.jydet.angler.suite;

import static fr.jydet.angler.MixinCompiler.compileAndLoad;
import static fr.jydet.angler.MixinCompiler.getFilesFromMixinsTank;
import static fr.jydet.angler.Utils.assertNumberOfClassesMixified;
import static fr.jydet.angler.Utils.launchMixins;

import fr.jydet.angler.State;
import fr.jydet.angler.Utils;
import fr.jydet.angler.mixintargets.Message;
import fr.jydet.angler.mixintargets.ReturnTestPOJO;
import fr.jydet.angler.mixintargets.SimplePOJO;
import java.net.URLClassLoader;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

public class ShortCircuitTests {
    @Before
    public void cleanup() {
        Utils.cleanup();
    }

    @Test
    public void test_shortCircuit_noMixin() {
        Assert.assertNull(State.storage);
        Assert.assertThrows(UnsupportedOperationException.class, () -> new ReturnTestPOJO().noopMethod());
        
        State.storage = "true";
        Assert.assertThrows(UnsupportedOperationException.class, () -> new ReturnTestPOJO().noopMethod());
        
        State.storage = "false";
        Assert.assertThrows(UnsupportedOperationException.class, () -> new ReturnTestPOJO().noopMethod());
    }

    @Test
    public void test_shortCircuit() throws Exception {
        URLClassLoader cl = compileAndLoad(getFilesFromMixinsTank("ShortCircuitMixin.java"));
        launchMixins(cl);
        assertNumberOfClassesMixified(1);
        
        Assert.assertNull(State.storage);
        Assert.assertThrows(UnsupportedOperationException.class, () -> new ReturnTestPOJO().noopMethod());
        
        State.storage = "true";
        boolean returnValue = new ReturnTestPOJO().noopMethod();
        Assert.assertTrue(returnValue);

        State.storage = "false";
        returnValue = new ReturnTestPOJO().noopMethod();
        Assert.assertFalse(returnValue);
    }
}
