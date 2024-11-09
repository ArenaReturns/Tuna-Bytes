package io.tunabytes.bytecode.introspect;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.ToString;

import java.util.List;
import java.util.Set;

@ToString
@Getter
@AllArgsConstructor
public class MixinInfo {

    private final String mixinName, mixinInternalName;
    private final boolean mixinInterface, mixinEnum, mirrorParent;
    private final List<MixinField> mixinFields;
    private final List<MixinMethod> mixinMethods;
    private final Set<String> deletedEnumValues;
    private final Set<String> interfacesToAdd;

}
