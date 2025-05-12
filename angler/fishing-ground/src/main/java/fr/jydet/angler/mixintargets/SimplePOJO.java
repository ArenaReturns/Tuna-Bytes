package fr.jydet.angler.mixintargets;

import fr.jydet.angler.InternalState;
import fr.jydet.angler.State;
import fr.jydet.angler.mixintargets.Message.Message1;
import fr.jydet.angler.mixintargets.Message.Message2;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.concurrent.ThreadLocalRandom;
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

    public String ldcTest() {
        return "error";
    }

    public String ldcTest2() {
        return "error_" + ThreadLocalRandom.current().nextInt() + "_error";
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
    
    public void multiVariableCastedSwitch(Message o) {
        switch (o.getId()) {
            case 1: {
                Message1 msg = (Message1) o;
                System.out.println(msg.getField1());
                break;
            }
            case 2: {
                Message2 msg = (Message2) o;
                System.out.println(msg.getField2());
                break;
            }
        }
    }

}
