package fr.jydet.angler.mixin.inject.methodselection;

import fr.jydet.angler.State;
import fr.jydet.angler.mixintargets.ObjectWithDuplicateMethods;
import io.tunabytes.Inject;
import io.tunabytes.Mixin;
import io.tunabytes.StackSeparator;

@Mixin(ObjectWithDuplicateMethods.class)
public class UnambiguousMethodSelectionMixin {

    @Inject(method = "b", at = Inject.At.BEGINNING)
    public void noopMethod(String arg1) {
        State.success = true;
    }

    @Inject(method = "b", at = Inject.At.BEGINNING)
    public void noopMethod_2() {
        State.success = true;
    }
}
