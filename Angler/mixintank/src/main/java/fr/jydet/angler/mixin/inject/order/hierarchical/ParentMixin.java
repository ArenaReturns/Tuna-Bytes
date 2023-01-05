package fr.jydet.angler.mixin.inject.order.hierarchical;

import fr.jydet.angler.State;
import fr.jydet.angler.mixintargets.ParentOfPOJO;
import io.tunabytes.Inject;
import io.tunabytes.Mixin;
import io.tunabytes.Overwrite;

@Mixin(ParentOfPOJO.class)
public class ParentMixin {
    @Overwrite
    public void overridenMethod() {
        State.success = true;
    }
}

