package fr.jydet.angler.mixin.inject.inject;

import fr.jydet.angler.State;
import fr.jydet.angler.mixintargets.SimplePOJO;
import io.tunabytes.Inject;
import io.tunabytes.Mixin;


@Mixin(SimplePOJO.class)
public class SimpleInjectionEnding {
    @Inject(method = "noopMethod", at = Inject.At.END)
    public void noopMethod() {
        State.success = true;
    }

    @Inject(method = "noopMethodWithInternalStateChangeCall", at = Inject.At.END)
    public void noopMethodWithInternalStateChangeCall() {
        State.success = true;
    }

    @Inject(method = "multiReturnWithInternalStateChangeCall", at = Inject.At.END)
    public void multiReturnWithInternalStateChangeCall() {
        State.success = true;
    }
}