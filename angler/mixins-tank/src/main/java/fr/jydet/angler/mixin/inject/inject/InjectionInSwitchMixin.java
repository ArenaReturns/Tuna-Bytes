package fr.jydet.angler.mixin.inject.inject;

import fr.jydet.angler.mixintargets.SimplePOJO;
import io.tunabytes.Inject;
import io.tunabytes.Mixin;

@Mixin(SimplePOJO.class)
public class InjectionInSwitchMixin {

    @Inject(method = "methodWithSwitch", at = Inject.At.AFTER_LINE, lineNumber = 92)
    public void methodWithSwitch(int arg) {
        System.out.println("injected :^)");
    }
}
