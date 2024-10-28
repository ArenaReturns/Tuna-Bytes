package fr.jydet.angler.suite;

import static fr.jydet.angler.MixinCompiler.compileAndLoad;
import static fr.jydet.angler.MixinCompiler.getFilesFromMixinsTank;
import static fr.jydet.angler.Utils.assertNumberOfClassesMixified;
import static fr.jydet.angler.Utils.launchMixins;

import fr.jydet.angler.State;
import fr.jydet.angler.Utils;
import fr.jydet.angler.mixintargets.Message;
import fr.jydet.angler.mixintargets.SimplePOJO;
import java.net.URLClassLoader;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

public class OverwriteTests {
    @Before
    public void cleanup() {
        Utils.cleanup();
    }

    @Test
    public void test_switch_overwrite() throws Exception {
        URLClassLoader cl = compileAndLoad(getFilesFromMixinsTank("OverwriteSwitch.java"));
        launchMixins(cl);
        assertNumberOfClassesMixified(1);
        Assert.assertFalse(State.success);
        new SimplePOJO().multiVariableCastedSwitch(new Message.Message1());
        Assert.assertTrue(State.success);

        State.success = false;
        new SimplePOJO().multiVariableCastedSwitch(new Message.Message2());
        Assert.assertTrue(State.success);
    }
}
