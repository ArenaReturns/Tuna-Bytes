package fr.jydet.angler.mixin.inject.enum_;

import fr.jydet.angler.mixintargets.ParametrizedEnum;
import io.tunabytes.EnumOverwrite;
import io.tunabytes.Mixin;

@Mixin(value = ParametrizedEnum.class, enumTarget = true)
public class InjectionEnum2 {
    @EnumOverwrite
    public static final InjectionEnum2 ENUM_CONSTANT_1 = new InjectionEnum2("ENUM_CONSTANT_1", 0, "success", 1, true, null, new Object[] {true, false}, (byte) 2);
    //@Mirror
    InjectionEnum2(String internalName, int internalId, String test, int test1, boolean test2, Object test3, Object[] test4, byte test5) {

    }
}
