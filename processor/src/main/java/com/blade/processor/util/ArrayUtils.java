package com.blade.processor.util;

public class ArrayUtils {

    public static <T> boolean isEmpty(T... ts) {
        return ts == null || ts.length == 0;
    }
}
