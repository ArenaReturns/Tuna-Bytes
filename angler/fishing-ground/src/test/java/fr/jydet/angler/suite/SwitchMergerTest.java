package fr.jydet.angler.suite;


import fr.jydet.angler.Utils;
import io.tunabytes.bytecode.InvalidMixinException;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import java.io.FileNotFoundException;
import java.net.URLClassLoader;

import static fr.jydet.angler.MixinCompiler.compileAndLoad;
import static fr.jydet.angler.MixinCompiler.getFilesFromMixinsTank;
import static fr.jydet.angler.Utils.launchMixins;

public class SwitchMergerTest {

    @Before
    public void cleanup() {
        Utils.cleanup();
    }
    
    @Test
    public void testSwitchMerger_noSwitch() throws FileNotFoundException {
        URLClassLoader cl = compileAndLoad(getFilesFromMixinsTank("SwitchMergerNoSwitch.java"));
        try {
            launchMixins(cl);
            Assert.fail("Mixin should create a runtime error when loading it !");
        } catch (InvalidMixinException expected) {}
    }

    @Test
    public void testSwitchMerger_abstract() throws FileNotFoundException {
        URLClassLoader cl = compileAndLoad(getFilesFromMixinsTank("SwitchMergerAbstract.java"));
        try {
            launchMixins(cl);
            Assert.fail("Mixin should create a runtime error when loading it !");
        } catch (InvalidMixinException expected) {}
    }

    @Test
    public void testSwitchMerger_removeBranchOnly() throws FileNotFoundException {
        URLClassLoader cl = compileAndLoad(getFilesFromMixinsTank("SwitchMergerRemoveBranchNoSwitch.java"));
        try {
            launchMixins(cl);
            Assert.fail("Mixin should create a runtime error when loading it !");
        } catch (InvalidMixinException expected) {}
    }
}
