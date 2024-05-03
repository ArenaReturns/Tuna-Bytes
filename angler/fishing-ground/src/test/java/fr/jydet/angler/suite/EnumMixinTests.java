package fr.jydet.angler.suite;

import fr.jydet.angler.Utils;
import fr.jydet.angler.mixintargets.ParametrizedEnum;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.io.IOException;
import java.net.URLClassLoader;

import static fr.jydet.angler.MixinCompiler.compileAndLoad;
import static fr.jydet.angler.MixinCompiler.getFilesFromMixinsTank;
import static fr.jydet.angler.Utils.assertNumberOfClassesMixified;
import static fr.jydet.angler.Utils.launchMixins;

public class EnumMixinTests {
    @Before
    public void cleanup() {
        Utils.cleanup();
    }

    @Test
    public void test_add_enum_field() throws IOException {
        URLClassLoader cl = compileAndLoad(getFilesFromMixinsTank("InjectionEnum.java"));
        launchMixins(cl);
        assertNumberOfClassesMixified(1);

        Assert.assertEquals(3, ParametrizedEnum.values().length);
        boolean found = false;
        for (ParametrizedEnum value : ParametrizedEnum.values()) {
            if (value.name().equals("ENUM_CONSTANT_3")) {
                found = true;
                Assert.assertEquals("success", value.getTest());
            }
        }
        Assert.assertTrue("Edited enum not found in values()", found);
        try {
            ParametrizedEnum enumConstant3 = ParametrizedEnum.valueOf("ENUM_CONSTANT_3");
            Assert.assertEquals("Edited enum not edited in valueOf()", "success", enumConstant3.getTest());
        } catch (IllegalArgumentException e) {
            Assert.fail("Edited enum not found in valueOf()");
        }
    }

    @Test
    public void test_edit_enum_parameter() throws IOException {
        URLClassLoader cl = compileAndLoad(getFilesFromMixinsTank("InjectionEnum2.java"));
        launchMixins(cl);
        assertNumberOfClassesMixified(1);

        Assert.assertEquals("success", ParametrizedEnum.ENUM_CONSTANT_1.getTest());
        Assert.assertEquals(2, ParametrizedEnum.values().length);
        boolean found = false;
        for (ParametrizedEnum value : ParametrizedEnum.values()) {
            if (value == ParametrizedEnum.ENUM_CONSTANT_1) {
                found = true;
                Assert.assertEquals("success", value.getTest());
            }
        }
        Assert.assertTrue("Edited enum not found in values()", found);
        try {
            ParametrizedEnum enumConstant1 = ParametrizedEnum.valueOf("ENUM_CONSTANT_1");
            Assert.assertEquals("Edited enum not edited in valueOf()", "success", enumConstant1.getTest());
        } catch (IllegalArgumentException e) {
            Assert.fail("Edited enum not found in valueOf()");
        }
    }
}
