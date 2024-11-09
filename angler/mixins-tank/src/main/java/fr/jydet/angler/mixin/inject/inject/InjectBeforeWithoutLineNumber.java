package fr.jydet.angler.mixin.inject.inject;

import fr.jydet.angler.State;
import fr.jydet.angler.mixintargets.SimplePOJO;
import io.tunabytes.Inject;
import io.tunabytes.Mixin;

@Mixin(SimplePOJO.class)
public class InjectBeforeWithoutLineNumber {

    @Inject(method = "noopMethodWithInternalStateChangeCall", at = Inject.At.BEFORE_LINE)
    public void noopMethodWithInternalStateChangeCall() {
        State.success = true;
    }
}
