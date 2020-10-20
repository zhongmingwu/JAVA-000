package me.zhongmingwu.week01;

public class Hello {
    private boolean aBoolean;
    private byte aByte;
    private char aChar;
    private short aShort;
    private int anInt;
    private long aLong;
    private float aFloat;
    private double aDouble;

    public Hello(boolean aBoolean, byte aByte, char aChar, short aShort, int anInt, long aLong, float aFloat, double aDouble) {
        this.aBoolean = aBoolean;
        this.aByte = aByte;
        this.aChar = aChar;
        this.aShort = aShort;
        this.anInt = anInt;
        this.aLong = aLong;
        this.aFloat = aFloat;
        this.aDouble = aDouble;
    }

    public void testArithmetic() {
        int a = aByte + aChar;
        int b = aChar - aShort;
        long c = anInt * aLong;
        float d = aLong / aFloat;
    }

    public int testIf() {
        if (aByte > 0b101) {
            return 1;
        }
        return -1;
    }

    public void testFor() {
        int sum = 0;
        for (int i = 0; i < aChar; i++) {
            sum += i;
        }
    }
}