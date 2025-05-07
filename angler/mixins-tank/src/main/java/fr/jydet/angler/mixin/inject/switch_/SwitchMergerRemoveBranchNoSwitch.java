package fr.jydet.angler.mixin.inject.switch_;

import fr.jydet.angler.mixintargets.SimplePOJO;
import io.tunabytes.Mixin;
import io.tunabytes.SwitchMerger;

@Mixin(SimplePOJO.class)
public abstract class SwitchMergerRemoveBranchNoSwitch {
    @SwitchMerger(removeBranches = {1})
    public abstract void noopMethod();
}
