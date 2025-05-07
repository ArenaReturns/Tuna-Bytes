package fr.jydet.angler.mixin.inject.inject;

import fr.jydet.angler.State;
import fr.jydet.angler.mixintargets.SimplePOJO;
import io.tunabytes.Inject;
import io.tunabytes.Inject.At;
import io.tunabytes.Mixin;
import io.tunabytes.Overwrite;

@Mixin(SimplePOJO.class)
public class InjectCallMethod {
    
    @Inject(at = At.BEGINNING)
    public void noopMethod() {
        InjectCallMethod.success();
    }
    
    static void success() {
        State.success = true;
    }
}
