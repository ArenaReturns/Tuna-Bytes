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

/**
 * Tests simple mixins (At.BEGINNING, At.ENDING, At.BEFORE_EACH_RETURN)
 */
public class SimpleMixinTests {

    @Before
    public void cleanup() {
        Utils.cleanup();
    }

    /** BEGINNING INJECTION **/

    @Test
    public void test_injectBeginning_inEmptyMethod() throws IOException {
        URLClassLoader cl = compileAndLoad(getFilesFromMixinsTank("SimpleInjectionBeginning.java"));
        launchMixins(cl);
        assertNumberOfClassesMixified(1);
        Assert.assertFalse(State.success);
        new SimplePOJO().noopMethod();
        Assert.assertTrue(State.success);
    }

    @Test
    public void test_injectBeginning_inEmptyMethod_withImplicitName() throws IOException {
        URLClassLoader cl = compileAndLoad(getFilesFromMixinsTank("SimpleInjectionBeginningImplicit.java"));
        launchMixins(cl);
        assertNumberOfClassesMixified(1);
        Assert.assertFalse(State.success);
        new SimplePOJO().noopMethod();
        Assert.assertTrue(State.success);
    }

    @Test
    public void test_injectBeginning_inEmptyMethod_withExplicitName() throws IOException {
        URLClassLoader cl = compileAndLoad(getFilesFromMixinsTank("SimpleInjectionBeginningExplicit.java"));
        launchMixins(cl);
        assertNumberOfClassesMixified(1);
        Assert.assertFalse(State.success);
        new SimplePOJO().noopMethod();
        Assert.assertTrue(State.success);
    }

    @Test
    public void test_injectBeginning_inEmptyMethod_noMixin() {
        Assert.assertFalse(State.success);
        new SimplePOJO().noopMethod();
        Assert.assertFalse(State.success);
    }

    @Test
    public void test_injectBeginning_inNonEmptyMethod() throws IOException {
        URLClassLoader cl = compileAndLoad(getFilesFromMixinsTank("SimpleInjectionBeginning.java"));
        launchMixins(cl);
        assertNumberOfClassesMixified(1);
        Assert.assertFalse(State.success);
        SimplePOJO simplePOJO = new SimplePOJO();

        // When the state change, the mixin was already run since the injection is at the
        //   beginning of the method
        simplePOJO.getState().addObserver((o, arg) -> Assert.assertTrue(State.success));
        Assert.assertFalse(simplePOJO.getState().hasChanged());
        simplePOJO.noopMethodWithInternalStateChangeCall();
        Assert.assertTrue(State.success);
    }

    @Test
    public void test_injectBeginning_inNonEmptyMethod_noMixin() {
        Assert.assertFalse(State.success);
        new SimplePOJO().noopMethodWithInternalStateChangeCall();
        Assert.assertFalse(State.success);
    }

    @Test
    public void test_injectEnding_shortCircuit() throws IOException {
        URLClassLoader cl = compileAndLoad(getFilesFromMixinsTank("SimpleShortCircuitMixin.java"));
        launchMixins(cl);
        assertNumberOfClassesMixified(1);

        SimplePOJO simplePOJO = new SimplePOJO();
        Assert.assertFalse(simplePOJO.getState().hasChanged());
        simplePOJO.noopMethodWithInternalStateChangeCall();
        Assert.assertFalse(simplePOJO.getState().hasChanged());
    }

    /** ENDING INJECTION **/

    @Test
    public void test_injectEnding_inEmptyMethod() throws IOException {
        URLClassLoader cl = compileAndLoad(getFilesFromMixinsTank("SimpleInjectionEnding.java"));
        launchMixins(cl);
        assertNumberOfClassesMixified(1);
        Assert.assertFalse(State.success);
        new SimplePOJO().noopMethod();
        Assert.assertTrue(State.success);
    }

    // noMixin test skipped => same as test_injectEnding_inNonEmptyMethod_noMixin()

    @Test
    public void test_injectEnding_inNonEmptyMethod() throws IOException {
        URLClassLoader cl = compileAndLoad(getFilesFromMixinsTank("SimpleInjectionEnding.java"));
        launchMixins(cl);
        assertNumberOfClassesMixified(1);
        Assert.assertFalse(State.success);
        SimplePOJO simplePOJO = new SimplePOJO();

        // When the state change, the mixin has NOT ran yet since the injection is at the
        //   ending of the method
        simplePOJO.getState().addObserver((o, arg) -> Assert.assertFalse(State.success));
        Assert.assertFalse(simplePOJO.getState().hasChanged());
        simplePOJO.noopMethodWithInternalStateChangeCall();
        Assert.assertTrue(State.success);
    }

    // noMixin test skipped => same as test_injectEnding_inNonEmptyMethod_noMixin()

    /** BEFORE_EACH_RETURN **/

    // noMixin test skipped => same as test_injectEnding_inNonEmptyMethod_noMixin()

    @Test
    public void test_injectBeforeReturns_inEmptyMethod() throws IOException {
        URLClassLoader cl = compileAndLoad(getFilesFromMixinsTank("SimpleInjectionBeforeEachReturn.java"));
        launchMixins(cl);
        assertNumberOfClassesMixified(1);
        Assert.assertFalse(State.success);
        new SimplePOJO().noopMethod();
        Assert.assertTrue(State.success);
    }

    // noMixin test skipped => same as test_injectEnding_inNonEmptyMethod_noMixin()

    @Test
    public void test_injectBeforeReturns_inMultiReturnsMethod() throws IOException {
        URLClassLoader cl = compileAndLoad(getFilesFromMixinsTank("SimpleInjectionBeforeEachReturn.java"));
        launchMixins(cl);
        assertNumberOfClassesMixified(1);
        SimplePOJO simplePOJO = new SimplePOJO();
        for (int validState : simplePOJO.getValidStates()) {
            State.success = false;
            //we check that we didnt broke the method
            Assert.assertEquals(validState, simplePOJO.multiReturnWithInternalStateChangeCall(validState));
            Assert.assertTrue(State.success);
        }
    }

    @Test
    public void test_injectBeforeReturns_inMultiReturnsMethod_noMixin() {
        SimplePOJO simplePOJO = new SimplePOJO();
        for (int validState : simplePOJO.getValidStates()) {
            State.success = false;
            //we check that we didnt break the method
            Assert.assertEquals(validState, simplePOJO.multiReturnWithInternalStateChangeCall(validState));
            Assert.assertFalse(State.success);
        }
    }

    // BEGIN Rewrite

  @Test
  public void test_rewrite_noop() throws IOException {
    URLClassLoader cl = compileAndLoad(getFilesFromMixinsTank("NoopRewrite.java"));
    launchMixins(cl);
    assertNumberOfClassesMixified(1);
    SimplePOJO simplePOJO = new SimplePOJO();
    simplePOJO.getState().addObserver((origin, v) -> Assert.fail("Should have been rewritten !"));
    simplePOJO.noopMethodWithInternalStateChangeCall();
  }
  //END Rewrite
}
