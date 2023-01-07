package fr.jydet.angler.mixin.inject.inject;

import fr.jydet.angler.State;
import fr.jydet.angler.mixintargets.SimplePOJO;
import io.tunabytes.Inject;
import io.tunabytes.Mixin;


@Mixin(SimplePOJO.class)
public class SimpleInjectionBeforeEachReturn {
    @Inject(method = "noopMethod", at = Inject.At.BEFORE_EACH_RETURN)
    public void noopMethod() {
        State.success = true;
    }

    @Inject(method = "noopMethodWithInternalStateChangeCall", at = Inject.At.BEFORE_EACH_RETURN)
    public void noopMethodWithInternalStateChangeCall() {
        State.success = true;
    }

    @Inject(method = "multiReturnWithInternalStateChangeCall", at = Inject.At.BEFORE_EACH_RETURN)
    public void multiReturnWithInternalStateChangeCall() {
        State.success = true;
    }
}