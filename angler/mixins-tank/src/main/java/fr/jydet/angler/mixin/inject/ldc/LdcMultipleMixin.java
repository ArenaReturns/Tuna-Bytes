package fr.jydet.angler.mixin.inject.ldc;

import fr.jydet.angler.State;
import fr.jydet.angler.mixintargets.SimplePOJO;
import io.tunabytes.LdcSwap;
import io.tunabytes.Mixin;

@Mixin(SimplePOJO.class)
public class LdcMultipleMixin {

    @LdcSwap(targetLdc = "error_", newLdc = "")
    @LdcSwap(targetLdc = "_error", newLdc = "")
    public void ldcTest2() {

    }
}
