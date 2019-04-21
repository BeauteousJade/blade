package com.blade.processor.util;

public class StringUtils {

    private static final String EMPTY_STRING = "";

    public static String emptyIfNull(String id) {
        if (id != null) {
            return id;
        }
        return EMPTY_STRING;
    }

    public static boolean isEmpty(String string) {
        return string == null || string.equals("");
    }
}
