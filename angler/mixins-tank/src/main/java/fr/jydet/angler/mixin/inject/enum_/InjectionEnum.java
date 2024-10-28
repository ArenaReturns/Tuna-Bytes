package fr.jydet.angler.mixin.inject.enum_;

import fr.jydet.angler.mixintargets.ParametrizedEnum;
import io.tunabytes.EnumOverwrite;
import io.tunabytes.Mixin;
import io.tunabytes.Mirror;

@Mixin(value = ParametrizedEnum.class, enumTarget = true)
public class InjectionEnum {
    @EnumOverwrite
    public static final InjectionEnum ENUM_CONSTANT_3 = new InjectionEnum("ENUM_CONSTANT_3", 2, "success", 1, true, null, new Object[] {true, false}, (byte) 2);
    //@Mirror
    InjectionEnum(String internalName, int internalId, String test, int test1, boolean test2, Object test3, Object[] test4, byte test5) {

    }
}
