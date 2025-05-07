package fr.jydet.angler.mixin.inject.inject;

import fr.jydet.angler.State;
import fr.jydet.angler.mixintargets.SimplePOJO;
import io.tunabytes.Inject;
import io.tunabytes.Inject.At;
import io.tunabytes.Mixin;

//@Mixin(SimplePOJO.class)
public class InjectCallInternalInterface {
    
    public interface Logic {
        static void success() {
            State.success = true;
        }
    }


    @Inject(at = At.BEGINNING)
    public void noopMethod() {
        Logic.success();
    }
}
