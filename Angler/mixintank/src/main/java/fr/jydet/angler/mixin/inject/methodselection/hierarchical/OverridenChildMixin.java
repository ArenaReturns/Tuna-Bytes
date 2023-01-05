package fr.jydet.angler.mixin.inject.methodselection.hierarchical;

import fr.jydet.angler.State;
import fr.jydet.angler.mixintargets.POJOWithParent;
import fr.jydet.angler.mixintargets.ParentOfPOJO;
import io.tunabytes.Mixin;
import io.tunabytes.Overwrite;

@Mixin(value = POJOWithParent.class, withFakeParentAccessor = true)
public abstract class OverridenChildMixin extends ParentOfPOJO {

    @Overwrite("overridenMethod")
    public void overridenMethod() {
        super.overridenMethod();
        State.success = true;
    }
}

/*
Other solution with an accessor

@Mixin(ParentOfPOJO.class)
public interface _3_Accessor {
    @io.tunabytes.Accessor
    void callOverridenMethod();
}

@Mixin(value = POJOWithParent.class)
public abstract class OverridenChildMixin {

    @Overwrite("overridenMethod")
    public void overridenMethod() {
        ((_3_Accessor) this).callOverridenMethod();
        State.success = true;
    }
}
 */
