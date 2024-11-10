package fr.jydet.angler.mixin.inject.inject;

import fr.jydet.angler.State;
import fr.jydet.angler.mixintargets.SimplePOJO;
import io.tunabytes.Inject;
import io.tunabytes.Mixin;

@Mixin(SimplePOJO.class)
public class InjectReplaceWithoutLineNumber {

    @Inject(method = "noopMethodWithInternalStateChangeCall", at = Inject.At.REPLACE_LINE)
    public void noopMethodWithInternalStateChangeCall() {
        State.success = true;
    }
}
