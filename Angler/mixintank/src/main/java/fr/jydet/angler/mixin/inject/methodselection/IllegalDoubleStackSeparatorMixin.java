package fr.jydet.angler.mixin.inject.methodselection;

import fr.jydet.angler.mixintargets.ObjectWithDuplicateMethods;
import io.tunabytes.Inject;
import io.tunabytes.Mixin;
import io.tunabytes.StackSeparator;

@Mixin(ObjectWithDuplicateMethods.class)
public class IllegalDoubleStackSeparatorMixin {

    @Inject(method = "b", at = Inject.At.BEGINNING)
    public void noopMethod(@StackSeparator String arg1, @StackSeparator int i) {
        System.out.println("This will not work ! Double @StackSeparator !");
    }
}
