package com.blade.processor.util;

import javax.lang.model.element.Element;
import javax.lang.model.util.Elements;

public class ElementUtils {

    public static String getPackageName(Elements elementUtils, Element element) {
        return elementUtils.getPackageOf(element).getQualifiedName().toString();
    }

    public static String getSimpleType(String typeString) {
        int lastIndex = typeString.lastIndexOf(".");
        if (lastIndex != -1) {
            return typeString.substring(lastIndex + 1);
        } else {
            return typeString;
        }
    }

    public static String getTypePackage(String typeString) {
        int lastIndex = typeString.lastIndexOf(".");
        if (lastIndex != -1) {
            return typeString.substring(0, lastIndex);

        } else {
            // 没有包名
            return "";
        }
    }
}
