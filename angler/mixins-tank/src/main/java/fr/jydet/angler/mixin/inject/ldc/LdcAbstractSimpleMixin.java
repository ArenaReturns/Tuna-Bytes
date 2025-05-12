package fr.jydet.angler.mixin.inject.ldc;

import fr.jydet.angler.State;
import fr.jydet.angler.mixintargets.SimplePOJO;
import io.tunabytes.LdcSwap;
import io.tunabytes.Mixin;

@Mixin(SimplePOJO.class)
public abstract class LdcAbstractSimpleMixin {

    @LdcSwap(targetLdc = "error", newLdc = "ok")
    public abstract void ldcTest();
}
