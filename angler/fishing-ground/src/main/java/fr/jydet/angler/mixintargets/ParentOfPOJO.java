package fr.jydet.angler.mixintargets;

import fr.jydet.angler.InternalState;
import lombok.Getter;

@Getter
public class ParentOfPOJO {
    public byte publicParentField = 0;
    public byte protectedParentField = 0;
    private byte privateParentField = 0;

    private InternalState state = new InternalState();

    public void noopMethodWithInternalStateChangeCall() {
        state.change();
    }

    public void parentNoopMethod() {

    }

    public void overridenMethod() {
        publicParentField++;
    }
}
