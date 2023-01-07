package fr.jydet.angler.mixin.inject.order;

import fr.jydet.angler.State;
import fr.jydet.angler.mixintargets.SimplePOJO;
import io.tunabytes.Inject;
import io.tunabytes.Mixin;

import java.util.List;

/**
 * A mixin that edit the same method twice with insert At.END
 * <p>
 * Result:
 * <pre>
 *   public void noopMethod() {
 *       ((List)State.storage).add("1");
 *       ((List)State.storage).add("2");
 *   }
 * </pre>
 */
@Mixin(SimplePOJO.class)
public class MultipleInsertMixin {

    //applied first
    @Inject(method = "noopMethod", at = Inject.At.END)
    public void noopMethod_1() {
        ((List<String>) State.storage).add("1");
    }

    //applied after
    @Inject(method = "noopMethod", at = Inject.At.END)
    public void noopMethod_2() {
        ((List<String>) State.storage).add("2");
    }


}
