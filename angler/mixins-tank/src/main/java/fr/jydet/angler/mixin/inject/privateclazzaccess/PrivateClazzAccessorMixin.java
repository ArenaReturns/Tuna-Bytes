package fr.jydet.angler.mixin.inject.privateclazzaccess;


import fr.jydet.angler.State;
import fr.jydet.angler.mixintargets.SimplePOJO;
import io.tunabytes.Mixin;
import io.tunabytes.Overwrite;
import io.tunabytes.PrivateClassAccessor;

@Mixin(SimplePOJO.class)
public abstract class PrivateClazzAccessorMixin {

    @PrivateClassAccessor("java.lang.Boolean")
    public Class<?> boolClassAccessor() {
        return Integer.class;
    }

    @Overwrite
    public void noopMethod() {
        State.success = boolClassAccessor() == Boolean.class;
    }
}
