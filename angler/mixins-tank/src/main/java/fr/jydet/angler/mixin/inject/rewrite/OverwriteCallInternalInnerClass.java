package fr.jydet.angler.mixin.inject.rewrite;

import fr.jydet.angler.State;
import fr.jydet.angler.mixin.inject.rewrite.OverwriteCallInternalInterface.Logic;
import fr.jydet.angler.mixintargets.SimplePOJO;
import io.tunabytes.Mixin;
import io.tunabytes.Overwrite;

@Mixin(SimplePOJO.class)
public class OverwriteCallInternalInnerClass {

    public final static class Logic {
        public static void success() {
            State.success = true;
        }
    }

    @Overwrite
    public void noopMethod() {
        Logic.success();
    }
}
