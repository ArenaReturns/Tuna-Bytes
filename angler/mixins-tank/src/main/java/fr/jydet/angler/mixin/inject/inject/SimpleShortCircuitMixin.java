package fr.jydet.angler.mixin.inject.inject;

import fr.jydet.angler.mixintargets.SimplePOJO;
import io.tunabytes.Inject;
import io.tunabytes.Mixin;

@Mixin(SimplePOJO.class)
public class SimpleShortCircuitMixin {

    @Inject(method = "noopMethodWithInternalStateChangeCall", at = Inject.At.BEGINNING, keepLastReturn = true)
    public void noopMethodWithInternalStateChangeCall() {
        return; //last returns (implicit in void method) are not kept !
    }
}
