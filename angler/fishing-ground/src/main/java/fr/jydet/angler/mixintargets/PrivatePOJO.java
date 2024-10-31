package fr.jydet.angler.mixintargets;

import fr.jydet.angler.InternalState;

public class PrivatePOJO {

    private InternalState internalState = new InternalState();
    private boolean UppercaseField = true;
    private static boolean staticField = true;
    private final boolean finalField = true;
    
    private void internalMethodWithInternalStateChangeCall() {
        internalState.change();
    }
    
    public InternalState getInternalStateBackDoor() {
        return internalState;
    }
}
