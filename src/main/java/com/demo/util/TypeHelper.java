package com.demo.util;

public abstract class TypeHelper {

    public static int unsignedByteToInt(byte srcValue) {
        return srcValue & 0xFF;
    }
}
