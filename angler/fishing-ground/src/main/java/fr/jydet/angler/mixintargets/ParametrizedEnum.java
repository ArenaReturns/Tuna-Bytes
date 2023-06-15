package fr.jydet.angler.mixintargets;

public enum ParametrizedEnum {
    ENUM_CONSTANT_1("test", 1, true, null, new Object[] {true, false}, (byte) 2),
    ENUM_CONSTANT_2(null, 1, true, null, new Object[] {true, false}, (byte) 2);

    String test;

    int test1;
    boolean test2;
    Object test3;
    Object[] test4;
    byte test5;

    ParametrizedEnum(String test, int test1, boolean test2, Object test3, Object[] test4, byte test5) {
        this.test = test;
        this.test1 = test1;
        this.test2 = test2;
        this.test3 = test3;
        this.test4 = test4;
        this.test5 = test5;
    }

    public String getTest() {
        return test;
    }

    public int getTest1() {
        return test1;
    }

    public boolean isTest2() {
        return test2;
    }

    public Object getTest3() {
        return test3;
    }

    public Object[] getTest4() {
        return test4;
    }

    public byte getTest5() {
        return test5;
    }
}
