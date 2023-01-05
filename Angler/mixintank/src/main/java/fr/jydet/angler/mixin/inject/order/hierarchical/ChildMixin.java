package fr.jydet.angler.mixin.inject.order.hierarchical;

import fr.jydet.angler.State;
import fr.jydet.angler.mixintargets.POJOWithParent;
import fr.jydet.angler.mixintargets.ParentOfPOJO;
import io.tunabytes.Inject;
import io.tunabytes.Mixin;

@Mixin(POJOWithParent.class)
public class ChildMixin {
    @Inject(method = "methodCallingParent", at = Inject.At.END)
    public void methodCallingParent() {
        State.success = true;
    }
}

