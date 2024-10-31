package fr.jydet.angler.suite;

import static fr.jydet.angler.MixinCompiler.compileAndLoad;
import static fr.jydet.angler.MixinCompiler.getFilesFromMixinsTank;
import static fr.jydet.angler.Utils.assertNumberOfClassesMixified;
import static fr.jydet.angler.Utils.launchMixins;

import fr.jydet.angler.InternalState;
import fr.jydet.angler.Utils;
import fr.jydet.angler.mixin.inject.PrivatePOJOAccessor;
import fr.jydet.angler.mixintargets.PrivatePOJO;
import fr.jydet.angler.mixintargets.SimplePOJO;
import java.net.URLClassLoader;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

public class AccessorsTests {
    @Before
    public void cleanup() {
        Utils.cleanup();
    }

    @Test
    public void test_accessors_common_cases() throws Exception {
        URLClassLoader cl = compileAndLoad(getFilesFromMixinsTank("PrivatePOJOAccessor.java"));
        launchMixins(cl);
        assertNumberOfClassesMixified(1);

        PrivatePOJO result = new PrivatePOJO();
        Assert.assertNotNull(result);
        InternalState state = result.getInternalStateBackDoor();
        Assert.assertFalse(state.hasChanged());

        Class<?>[] privateProjoAccessors = PrivatePOJO.class.getInterfaces();
        Assert.assertEquals(1, privateProjoAccessors.length);
        Assert.assertEquals(PrivatePOJOAccessor.class, privateProjoAccessors[0]);

        ((PrivatePOJOAccessor) result).invokeInternalMethodWithInternalStateChangeCall();
        Assert.assertTrue(state.hasChanged());
        
        state.reset();
        Assert.assertFalse(state.hasChanged());
        ((PrivatePOJOAccessor) result).callInternalMethodWithInternalStateChangeCall();
        Assert.assertTrue(state.hasChanged());
        
        Assert.assertSame(state, ((PrivatePOJOAccessor) result).getInternalState());
        Assert.assertSame(state, ((PrivatePOJOAccessor) result).isInternalState());

        state.reset();
        Assert.assertFalse(state.hasChanged());
        InternalState newInternalState = new InternalState();
        ((PrivatePOJOAccessor) result).setInternalState(newInternalState);
        Assert.assertSame(newInternalState, result.getInternalStateBackDoor());
    }

    @Test
    public void test_accessors_edge_cases() throws Exception {
        URLClassLoader cl = compileAndLoad(getFilesFromMixinsTank("PrivatePOJOAccessor.java"));
        launchMixins(cl);
        assertNumberOfClassesMixified(1);

        PrivatePOJO result = new PrivatePOJO();
        Assert.assertNotNull(result);
        InternalState state = result.getInternalStateBackDoor();
        Assert.assertFalse(state.hasChanged());

        Class<?>[] privateProjoAccessors = PrivatePOJO.class.getInterfaces();
        Assert.assertEquals(1, privateProjoAccessors.length);
        Assert.assertEquals(PrivatePOJOAccessor.class, privateProjoAccessors[0]);

        Assert.assertTrue(((PrivatePOJOAccessor) result).getUppercaseField());
        Assert.assertTrue(((PrivatePOJOAccessor) result).getStaticField());
        Assert.assertTrue(((PrivatePOJOAccessor) result).getFinalField());
        Assert.assertSame(state, ((PrivatePOJOAccessor) result).customNameGetter());
        
        state.reset();
        Assert.assertFalse(state.hasChanged());
        ((PrivatePOJOAccessor) result).customNameCall();
        Assert.assertTrue(state.hasChanged());
    }
}
