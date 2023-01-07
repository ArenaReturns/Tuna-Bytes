package fr.jydet.angler.mixin.inject.mirror;

import fr.jydet.angler.mixintargets.SimplePOJO;
import io.tunabytes.Inject;
import io.tunabytes.Mirror;
import io.tunabytes.Mixin;

import java.util.concurrent.atomic.AtomicBoolean;

@Mixin(SimplePOJO.class)
public abstract class MirrorInternalMethodMixin {

    @Mirror public abstract void internalMethodWithInternalStateChangeCall();

    @Inject(method = "noopMethod", at = Inject.At.BEGINNING)
    public void noopMethod() {
        internalMethodWithInternalStateChangeCall();
    }
}
