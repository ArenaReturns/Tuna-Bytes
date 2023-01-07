package fr.jydet.angler.mixin.inject.parameters;

import fr.jydet.angler.State;
import fr.jydet.angler.mixintargets.SimplePOJO;
import io.tunabytes.Inject;
import io.tunabytes.Mixin;

@Mixin(SimplePOJO.class)
public class AccessNonExistentParametersNoopMethodMixin {

    @Inject(method = "noopMethod", at = Inject.At.END)
    public void noopMethod(Object x) {
        State.storage = x;
    }

}
