package fr.jydet.angler.mixin.inject;

import fr.jydet.angler.InternalState;
import fr.jydet.angler.mixintargets.PrivatePOJO;
import io.tunabytes.Accessor;
import io.tunabytes.Mixin;
import io.tunabytes.bytecode.introspect.MixinMethod.CallType;

//@Mixin(PrivatePOJO.class)
public interface PrivatePOJOAccessor {
    @Accessor
    InternalState isInternalState();

    @Accessor
    InternalState getInternalState();

    @Accessor
    void setInternalState(InternalState var1);

    @Accessor
    void invokeInternalMethodWithInternalStateChangeCall();

    @Accessor
    void callInternalMethodWithInternalStateChangeCall();
    
    @Accessor(nameStartWithLowercase = false)
    boolean getUppercaseField();
    
    @Accessor
    boolean getStaticField();
    
    @Accessor
    boolean getFinalField();
    
    @Accessor(value = "internalState", type = CallType.GET)
    InternalState customNameGetter();
    
    @Accessor(value = "internalMethodWithInternalStateChangeCall")
    void customNameCall();
}
