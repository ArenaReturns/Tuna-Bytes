package fr.jydet.angler.mixin.inject.inject;

import fr.jydet.angler.mixintargets.SimplePOJO;
import io.tunabytes.Inject;
import io.tunabytes.Mixin;
import io.tunabytes.StackSeparator;

@Mixin(SimplePOJO.class)
public class InjectionInSwitchWithLocalsMixin {

    @Inject(method = "methodWithSwitchAndLocals", at = Inject.At.AFTER_LINE, lineNumber = 121)
    public void methodWithSwitch(int arg, @StackSeparator Object local1, Object local2) {
        System.out.println("injected :^)");
    }
}
