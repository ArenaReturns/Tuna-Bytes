package fr.jydet.angler.mixin.inject.switch_;

import fr.jydet.angler.mixintargets.SimplePOJO;
import io.tunabytes.Mixin;
import io.tunabytes.SwitchMerger;

@Mixin(SimplePOJO.class)
public class SwitchMergerNoSwitch {
    @SwitchMerger
    public void noopMethod() {
        
    }
}
