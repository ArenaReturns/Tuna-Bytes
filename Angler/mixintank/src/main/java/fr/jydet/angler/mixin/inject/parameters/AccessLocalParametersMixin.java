package fr.jydet.angler.mixin.inject.parameters;

import fr.jydet.angler.State;
import fr.jydet.angler.mixintargets.SimplePOJO;
import io.tunabytes.Inject;
import io.tunabytes.Mixin;

import java.nio.charset.StandardCharsets;

@Mixin(SimplePOJO.class)
public class AccessLocalParametersMixin {

    @Inject(method = "noopMethodWithArgsAndLocals", at = Inject.At.END)
    public void noopMethodWithArgsAndLocals(String arg, int arg2, int local1) {
        State.storage = local1;
    }
}
