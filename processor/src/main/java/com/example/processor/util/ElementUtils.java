package com.example.processor.util;

import javax.lang.model.element.Element;
import javax.lang.model.util.Elements;

public class ElementUtils {

    public static String getPackageName(Elements elementUtils, Element element) {
        return elementUtils.getPackageOf(element).getQualifiedName().toString();
    }
}
