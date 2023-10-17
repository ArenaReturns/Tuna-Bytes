package fr.jydet.angler.mixin.inject.inject;

import static io.tunabytes.AtQuery.INVOKE;

import fr.jydet.angler.State;
import fr.jydet.angler.mixintargets.SimplePOJO;
import io.tunabytes.AtQuery;
import io.tunabytes.Inject;
import io.tunabytes.Mixin;

@Mixin(SimplePOJO.class)
public class InjectionQuery {
    @Inject(atQuery = @AtQuery(where = INVOKE, target = "println"))
    public void printMethod() {
        State.success = true;
    }
}
