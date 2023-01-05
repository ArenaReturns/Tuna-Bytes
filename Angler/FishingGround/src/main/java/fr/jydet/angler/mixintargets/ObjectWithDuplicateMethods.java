package fr.jydet.angler.mixintargets;

public class ObjectWithDuplicateMethods {

    public boolean b() {
        return true;
    }

    public boolean b(String arg1) {
        return true;
    }

    public void b(String arg1, byte[] arg2) {
    }
}
