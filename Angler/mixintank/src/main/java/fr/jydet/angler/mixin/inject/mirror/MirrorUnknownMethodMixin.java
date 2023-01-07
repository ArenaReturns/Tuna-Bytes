package fr.jydet.angler.mixin.inject.mirror;

import fr.jydet.angler.mixintargets.SimplePOJO;
import io.tunabytes.Inject;
import io.tunabytes.Mirror;
import io.tunabytes.Mixin;

@Mixin(SimplePOJO.class)
public abstract class MirrorUnknownMethodMixin {

    @Mirror public abstract void sussybakka();

    @Inject(method = "noopMethod", at = Inject.At.BEGINNING)
    public void noopMethod() {
        sussybakka();
    }
}
