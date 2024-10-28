package fr.jydet.angler.mixin.inject.returns;

import fr.jydet.angler.State;
import fr.jydet.angler.mixintargets.ReturnTestPOJO;
import io.tunabytes.Inject;
import io.tunabytes.Inject.At;
import io.tunabytes.Mixin;

@Mixin(ReturnTestPOJO.class)
public class InjectReturnsMixin {

    @Inject(at = At.BEGINNING)
    public void noopMethod() {
        State.success = true;
    }
}
