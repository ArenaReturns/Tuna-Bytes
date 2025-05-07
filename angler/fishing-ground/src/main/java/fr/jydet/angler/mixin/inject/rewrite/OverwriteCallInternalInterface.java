package fr.jydet.angler.mixin.inject.rewrite;

import fr.jydet.angler.State;
import fr.jydet.angler.mixintargets.SimplePOJO;
import io.tunabytes.Mixin;
import io.tunabytes.Overwrite;

//@Mixin(SimplePOJO.class)
public class OverwriteCallInternalInterface {
    
    public interface Logic {
        static void success() {
            State.success = true;
        }
    }

    @Overwrite
    public void noopMethod() {
        Logic.success();
    }
}
