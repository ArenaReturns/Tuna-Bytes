package fr.jydet.angler.mixin.inject.methodselection;

import fr.jydet.angler.mixintargets.ObjectWithDuplicateMethods;
import io.tunabytes.Inject;
import io.tunabytes.Mixin;

@Mixin(ObjectWithDuplicateMethods.class)
public class NoMethodSelectionMixin {

    @Inject(method = "sussybakka", at = Inject.At.BEGINNING)
    public void noopMethod() {
        System.out.println("This will not work ! Target method does not exists !");
    }
}
