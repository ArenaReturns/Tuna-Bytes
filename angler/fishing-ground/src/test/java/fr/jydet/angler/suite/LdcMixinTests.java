package fr.jydet.angler.suite;

import fr.jydet.angler.Utils;
import fr.jydet.angler.mixintargets.SimplePOJO;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.io.FileNotFoundException;
import java.net.URLClassLoader;

import static fr.jydet.angler.MixinCompiler.compileAndLoad;
import static fr.jydet.angler.MixinCompiler.getFilesFromMixinsTank;
import static fr.jydet.angler.Utils.assertNumberOfClassesMixified;
import static fr.jydet.angler.Utils.launchMixins;

public class LdcMixinTests {

    @Before
    public void cleanup() {
        Utils.cleanup();
    }

    @Test
    public void test_initialLdcState() {
        SimplePOJO pojo = new SimplePOJO();
        Assert.assertEquals("error", pojo.ldcTest());

        String ldcTest2 = pojo.ldcTest2();
        Assert.assertTrue(ldcTest2.startsWith("error_"));
        Assert.assertTrue(ldcTest2.endsWith("_error"));

        Assert.assertThrows(NumberFormatException.class, () -> Integer.parseInt(ldcTest2));
    }

    @Test
    public void test_ldcSimpleReplace() throws FileNotFoundException {
        URLClassLoader cl = compileAndLoad(getFilesFromMixinsTank("LdcSimpleMixin.java"));
        launchMixins(cl);
        assertNumberOfClassesMixified(1);

        SimplePOJO pojo = new SimplePOJO();
        Assert.assertEquals("ok", pojo.ldcTest());
    }

    @Test
    public void test_ldcAbstractSimpleReplace() throws FileNotFoundException {
        URLClassLoader cl = compileAndLoad(getFilesFromMixinsTank("LdcAbstractSimpleMixin.java"));
        launchMixins(cl);
        assertNumberOfClassesMixified(1);

        SimplePOJO pojo = new SimplePOJO();
        Assert.assertEquals("ok", pojo.ldcTest());
    }

    @Test
    public void test_ldcMultipleReplace() throws FileNotFoundException {
        URLClassLoader cl = compileAndLoad(getFilesFromMixinsTank("LdcMultipleMixin.java"));
        launchMixins(cl);
        assertNumberOfClassesMixified(1);

        SimplePOJO pojo = new SimplePOJO();
        String ldcTest2 = pojo.ldcTest2();
        try {
            Integer.parseInt(ldcTest2);
        } catch (NumberFormatException e) {
            Assert.fail("LdcTest2 should be an integer now ! is: " + ldcTest2);
        }
    }
}
