package fr.jydet.angler.mixin.inject.mirror;

import fr.jydet.angler.mixintargets.POJOWithParent;
import fr.jydet.angler.mixintargets.SimplePOJO;
import io.tunabytes.Inject;
import io.tunabytes.Mirror;
import io.tunabytes.Mixin;

@Mixin(POJOWithParent.class)
public class MirrorParentFieldMixin {

    @Mirror byte publicParentField;
    @Mirror byte protectedParentField;

    @Inject(method = "noopMethod", at = Inject.At.BEGINNING)
    public void noopMethod() {
        protectedParentField = 1;
        publicParentField = 1;
    }
}
