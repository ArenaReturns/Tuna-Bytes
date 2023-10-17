package fr.jydet.angler.mixintargets;

import fr.jydet.angler.InternalState;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class SimplePOJO {
    private AtomicBoolean internalAtomicBoolean = new AtomicBoolean(false);
    private final AtomicBoolean finalInternalAtomicBoolean = new AtomicBoolean(false);
    private InternalState state = new InternalState();
    private Object storage;

    public void noopMethod() {

    }

    public void noopMethodWithArgs(String arg1, int arg2) {

    }
    
    public void printMethod() {
        System.out.println("print");
    }

    private int i = 0;

    public void noopMethodWithArgsAndLocals(String arg1, int arg2) {
        int i2 = 1 + arg2;
        label:
        {
            int i3 = i * 2;
            internalDoNotMixin(i3);
            break label;
        }
        String s = arg1 + i2;
        i = s.getBytes().length;
    }

    public void internalDoNotMixin(int a) {

    }

    public final void finalMethodWithInternalStateChangeCall() {
        state.change();
    }

    private void internalMethodWithInternalStateChangeCall() {
        state.change();
    }

    public void noopMethodWithInternalStateChangeCall() {
        state.change();
    }

    final int[] validStates = new int[] {1, 2, 3, 4, 5, 6};

    /**
     * A method with multiple return used depending of the choosed path
     * @param path must be a value in validStates
     * @return the same value as passed in parameter
     */
    public int multiReturnWithInternalStateChangeCall(int path) {
        try {
            int i = 2;
            switch (path) {
                case 1: return 1;
                case 2: i--;
                case 4: return i * 2;
            }

            if (path % 5 == 0) {
                return 5;
            } else
            if (path % 6 == 0) {
                throw new Exception();
            }

        } catch (Exception e) {
            return 6;
        }

        return 3;
    }

}
