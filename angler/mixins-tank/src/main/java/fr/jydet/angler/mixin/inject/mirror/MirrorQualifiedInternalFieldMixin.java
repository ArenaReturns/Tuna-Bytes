package fr.jydet.angler.mixin.inject.mirror;

import fr.jydet.angler.mixintargets.SimplePOJO;
import io.tunabytes.Inject;
import io.tunabytes.Mirror;
import io.tunabytes.Mixin;

import java.util.concurrent.atomic.AtomicBoolean;

@Mixin(SimplePOJO.class)
public class MirrorQualifiedInternalFieldMixin {

    @Mirror
    AtomicBoolean internalAtomicBoolean;

    @Inject(method = "noopMethod", at = Inject.At.BEGINNING)
    public void noopMethod() {
        this.internalAtomicBoolean.set(true);
    }
}
