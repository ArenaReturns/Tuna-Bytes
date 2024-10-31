package fr.jydet.angler;

import java.util.Observable;

public class InternalState extends Observable {
    private int i = 0;
    private Runnable onChange;

    public void change() {
        setChanged();
        i++;
    }

    public int getChange() {
        return i;
    }

    public boolean hasChanged() {
        return i != 0;
    }
    
    public void reset() {
        i = 0;
    }
}
