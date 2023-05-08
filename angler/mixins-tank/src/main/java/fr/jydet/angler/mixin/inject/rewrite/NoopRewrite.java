package fr.jydet.angler.mixin.inject.rewrite;

import fr.jydet.angler.NoopRuntimeRewriter;
import fr.jydet.angler.mixintargets.SimplePOJO;
import io.tunabytes.Mixin;
import io.tunabytes.Rewrite;


@Mixin(SimplePOJO.class)
public abstract class NoopRewrite {
    @Rewrite(runtimeRewriter = NoopRuntimeRewriter.class)
    public abstract void noopMethodWithInternalStateChangeCall();
}
