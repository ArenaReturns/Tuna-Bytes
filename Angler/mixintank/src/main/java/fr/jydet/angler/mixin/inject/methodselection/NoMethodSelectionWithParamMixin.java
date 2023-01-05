package fr.jydet.angler.mixin.inject.methodselection;

import fr.jydet.angler.State;
import fr.jydet.angler.mixintargets.ObjectWithDuplicateMethods;
import io.tunabytes.Inject;
import io.tunabytes.Mixin;

@Mixin(ObjectWithDuplicateMethods.class)
public class NoMethodSelectionWithParamMixin {

    @Inject(method = "b", at = Inject.At.BEGINNING)
    public void noopMethod(Object arg1) {
        System.out.println("This will not work ! Method does not exist !");
    }
}
