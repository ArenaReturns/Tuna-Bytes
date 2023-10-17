package fr.jydet.angler.mixin.inject.inject;

import fr.jydet.angler.State;
import fr.jydet.angler.mixintargets.SimplePOJO;
import io.tunabytes.Inject;
import io.tunabytes.Mixin;

@Mixin(SimplePOJO.class)
public class SimpleInjectionBeginningExplicit {
    @Inject(method = "noopMethod", at = Inject.At.BEGINNING)
    public void setStateToSuccessMixin() {
        State.success = true;
    }
}
