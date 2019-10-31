package com.blade.processor.util;

public class StringUtils {

    public static boolean isEmpty(String string) {
        return string == null || string.length() == 0;
    }

    public static String formatAndroidParamName(String name) {
        char firstChar = name.charAt(0);
        if (firstChar == 'm') {
            return name + "Param";
        } else {
            if (firstChar >= 'a' && firstChar <= 'z') {
                return "m" + String.valueOf(firstChar).toUpperCase() + name.substring(1) + "Param";
            } else {
                return "m" + name + "Param";
            }
        }
    }

    public static String classNameToParameterName(String className) {
        return String.valueOf(className.charAt(0)).toLowerCase() + className.substring(1);
    }
}
