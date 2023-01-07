package fr.jydet.angler.mixin.inject.methodselection.hierarchical;

import fr.jydet.angler.State;
import fr.jydet.angler.mixintargets.ParentOfPOJO;
import io.tunabytes.Inject;
import io.tunabytes.Mixin;

@Mixin(ParentOfPOJO.class)
public class OverridenParentMixin {
    @Inject(method = "parentNoopMethod", at = Inject.At.END)
    public void parentNoopMethod() {
        State.success = true;
    }
}

