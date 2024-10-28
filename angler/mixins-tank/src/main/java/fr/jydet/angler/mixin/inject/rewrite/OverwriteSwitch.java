package fr.jydet.angler.mixin.inject.rewrite;

import fr.jydet.angler.State;
import fr.jydet.angler.mixintargets.Message;
import fr.jydet.angler.mixintargets.Message.Message1;
import fr.jydet.angler.mixintargets.Message.Message2;
import fr.jydet.angler.mixintargets.SimplePOJO;
import io.tunabytes.Mixin;
import io.tunabytes.Overwrite;

@Mixin(value = SimplePOJO.class)
public class OverwriteSwitch {

    @Overwrite
    public void multiVariableCastedSwitch(Message o) {
        switch (o.getId()) {
            case 30000: {
                Message1 msg = (Message1) o;
                System.out.println(msg.getField1());
                State.success = true;
                break;
            }
            case 30001: {
                Message2 msg = (Message2) o;
                System.out.println(msg.getField2());
                State.success = true;
                break;
            }
        }
    }
}
