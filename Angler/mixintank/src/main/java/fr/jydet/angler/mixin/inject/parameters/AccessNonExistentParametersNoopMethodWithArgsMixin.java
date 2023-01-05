package fr.jydet.angler.mixin.inject.parameters;

import fr.jydet.angler.State;
import fr.jydet.angler.mixintargets.SimplePOJO;
import io.tunabytes.Inject;
import io.tunabytes.Mixin;

@Mixin(SimplePOJO.class)
public class AccessNonExistentParametersNoopMethodWithArgsMixin {

    @Inject(method = "noopMethodWithArgs", at = Inject.At.END)
    public void noopMethodWithArgs(String arg, int i, Object x) {
        State.storage = x;
    }

}
