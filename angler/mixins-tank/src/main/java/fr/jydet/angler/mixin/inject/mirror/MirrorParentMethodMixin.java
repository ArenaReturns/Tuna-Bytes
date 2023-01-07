package fr.jydet.angler.mixin.inject.mirror;

import fr.jydet.angler.mixintargets.POJOWithParent;
import io.tunabytes.Inject;
import io.tunabytes.Mirror;
import io.tunabytes.Mixin;

@Mixin(POJOWithParent.class)
public abstract class MirrorParentMethodMixin {

    @Mirror public abstract void noopMethodWithInternalStateChangeCall();

    @Inject(method = "noopMethod", at = Inject.At.BEGINNING)
    public void noopMethod() {
        noopMethodWithInternalStateChangeCall();
    }
}
