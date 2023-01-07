package fr.jydet.angler.mixin.inject.mirror;

import fr.jydet.angler.mixintargets.SimplePOJO;
import io.tunabytes.Inject;
import io.tunabytes.Mirror;
import io.tunabytes.Mixin;

@Mixin(SimplePOJO.class)
public class MirrorUnknownFieldMixin {

    @Mirror boolean sussybakka;

    @Inject(method = "noopMethod", at = Inject.At.BEGINNING)
    public void noopMethod() {
        sussybakka = true;
    }
}
