package fr.jydet.angler.mixin.inject.mirror;

import fr.jydet.angler.mixintargets.SimplePOJO;
import io.tunabytes.Accessor;
import io.tunabytes.Definalize;
import io.tunabytes.Inject;
import io.tunabytes.Mirror;
import io.tunabytes.Mixin;

import java.util.concurrent.atomic.AtomicBoolean;

@Mixin(SimplePOJO.class)
public class MirrorAssignInternalFieldMixin {

    @Mirror AtomicBoolean internalAtomicBoolean;

    @Inject(method = "noopMethod", at = Inject.At.BEGINNING)
    public void noopMethod() {
        internalAtomicBoolean = new AtomicBoolean(true);
    }
}
