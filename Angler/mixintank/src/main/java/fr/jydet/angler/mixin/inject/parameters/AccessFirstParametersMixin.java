package fr.jydet.angler.mixin.inject.parameters;

import fr.jydet.angler.State;
import fr.jydet.angler.mixintargets.SimplePOJO;
import io.tunabytes.Inject;
import io.tunabytes.Mixin;

@Mixin(SimplePOJO.class)
public class AccessFirstParametersMixin {

    @Inject(method = "noopMethodWithArgs", at = Inject.At.END)
    public void noopMethodWithArgs(String arg) {
        State.storage = arg;
    }

    @Inject(method = "noopMethodWithArgsAndLocals", at = Inject.At.END)
    public void noopMethodWithArgsAndLocals(String arg) {
        State.storage = arg;
    }
}
