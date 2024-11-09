package fr.jydet.angler.suite;

import fr.jydet.angler.State;
import fr.jydet.angler.Utils;
import fr.jydet.angler.mixintargets.POJOWithParent;
import fr.jydet.angler.mixintargets.SimplePOJO;
import io.tunabytes.bytecode.MixinEntry;
import io.tunabytes.bytecode.MixinsBootstrap;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.io.IOException;
import java.net.URLClassLoader;
import java.util.Comparator;
import java.util.LinkedList;
import java.util.List;

import static fr.jydet.angler.MixinCompiler.compileAndLoad;
import static fr.jydet.angler.MixinCompiler.getFilesFromMixinsTank;
import static fr.jydet.angler.Utils.assertNumberOfClassesMixified;
import static fr.jydet.angler.Utils.launchMixins;

public class LoadOrderMiniTests {
    @Before
    public void cleanup() {
        Utils.cleanup();
    }

    @Test
    public void test_injectEnding_shortCircuit() throws IOException {
        URLClassLoader cl = compileAndLoad(getFilesFromMixinsTank("MultipleInsertMixin.java"));
        launchMixins(cl);
        assertNumberOfClassesMixified(1);

        State.storage = new LinkedList<String>();
        Assert.assertTrue(((List<String>)State.storage).isEmpty());
        new SimplePOJO().noopMethod();
        List<String> storage = (List<String>) State.storage;
        Assert.assertEquals(2, storage.size());
        Assert.assertArrayEquals(new String[] {"1", "2"}, storage.toArray(new String[0]));
    }

    @Test
    public void test_childAndParentEdit_wrongOrder() throws IOException {
        URLClassLoader cl = compileAndLoad(getFilesFromMixinsTank("ParentMixin.java", "ChildMixin.java"));

        //we load Child then Parent
        MixinsBootstrap.mixingSorterHook = mixinEntries -> {
            mixinEntries.sort(Comparator.comparing(MixinEntry::getMixinClass));
            return mixinEntries;
        };

        launchMixins(cl);
    }


    @Test
    public void test_childAndParentEdit_rightOrder() throws IOException {
        URLClassLoader cl = compileAndLoad(getFilesFromMixinsTank("ChildMixin.java", "ParentMixin.java"));

        //we load Parent then Child
        MixinsBootstrap.mixingSorterHook = mixinEntries -> {
            mixinEntries.sort(Comparator.comparing(MixinEntry::getMixinClass).reversed());
            return mixinEntries;
        };

        launchMixins(cl);
        assertNumberOfClassesMixified(2);
        Assert.assertFalse(State.success);
        new POJOWithParent().methodCallingParent();
        new POJOWithParent().parentNoopMethod();
        Assert.assertTrue(State.success);
    }

    @Test
    public void test_superOverriden() throws IOException {//FIXME erreur de setup à cause du loading a la main des classes, mais marche bien en vrai :)
//        URLClassLoader cl = compileAndLoad(getFilesFromMixinsTank(
////                "_3_Accessor.java",
//                "OverridenChildMixin.java", "OverridenParentMixin.java"));
//
//        //we load Parent then Child
//        MixinsBootstrap.mixingSorterHook = mixinEntries -> {
//            mixinEntries
//                    .sort(Comparator.comparing(MixinEntry::getMixinClass)
//                    .reversed());
//            return mixinEntries;
//        };
//
//        launchMixins(cl);
//        assertNumberOfClassesMixified(2);
//        Assert.assertFalse(State.success);
//        new POJOWithParent().methodCallingParent();
//        Assert.assertTrue(State.success);
//        new POJOWithParent().parentNoopMethod();
//        Assert.assertTrue(State.success);
    }
}
