package fr.jydet.angler.mixin.inject.returns;

import fr.jydet.angler.State;
import fr.jydet.angler.mixintargets.ReturnTestPOJO;
import io.tunabytes.Inject;
import io.tunabytes.Inject.At;
import io.tunabytes.Mixin;
import io.tunabytes.ShortCircuit;

@Mixin(ReturnTestPOJO.class)
public class ShortCircuitMixin {

    @Inject(at = At.BEGINNING, keepLastReturn = false)
    public void noopMethod() {
        if ("true".equals(State.storage)) {
            ShortCircuit.return_(true);
        } else if ("false".equals(State.storage)) {
            ShortCircuit.return_(false);
        }
        return;
    }
}
