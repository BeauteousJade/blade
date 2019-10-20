package com.blade.inject.utils;

public class Utils {

    @SuppressWarnings("unchecked")
    public static <T> T checkNoNull(Object t, boolean supportNull) {
        if (supportNull || t != null) {
            return (T) t;
        }
        throw new IllegalArgumentException("");
    }

    @SuppressWarnings("unchecked")
    public static <T> T getPrimitive(T t, Object defaultValue) {
        if (t != null) {
            return t;
        }
        return (T) defaultValue;
    }
}
