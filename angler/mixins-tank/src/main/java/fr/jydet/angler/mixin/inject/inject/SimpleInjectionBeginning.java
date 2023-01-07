package fr.jydet.angler.mixin.inject.inject;

import fr.jydet.angler.State;
import fr.jydet.angler.mixintargets.SimplePOJO;
import io.tunabytes.Inject;
import io.tunabytes.Mixin;

@Mixin(SimplePOJO.class)
public class SimpleInjectionBeginning {
    @Inject(method = "noopMethod", at = Inject.At.BEGINNING)
    public void noopMethod() {
        State.success = true;
    }

    @Inject(method = "noopMethodWithInternalStateChangeCall", at = Inject.At.BEGINNING)
    public void noopMethodWithInternalStateChangeCall() {
        State.success = true;
    }
}
