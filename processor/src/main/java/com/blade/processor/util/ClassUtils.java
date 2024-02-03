package com.blade.processor.util;

import com.blade.processor.Constant;
import com.squareup.javapoet.ClassName;

public class ClassUtils {

    public static ClassName getFetcherInterface() {
        return ClassName.get(Constant.FETCHER_PACKAGE_NAME,
                Constant.FETCHER_CLASS_NAME);
    }

    public static ClassName getSourceFetchers() {
        return ClassName.get(Constant.SOURCE_FETCHERS_PACKAGE_NAME,
                Constant.SOURCE_FETCHER_CLASS_NAME);
    }

    public static ClassName getNullable() {
        return ClassName.get(Constant.NULLABLE_PACKAGE_NAME,
                Constant.NULLABLE_CLASS_NAME);
    }

    public static ClassName getInjectorInterface() {
        return ClassName.get(Constant.INJECTOR_PACKAGE_NAME, Constant.INJECTOR_CLASS_NAME);
    }

    public static ClassName getFetchHolder() {
        return ClassName.get(Constant.FETCHER_HOLDER_PACKAGE_NAME, Constant.FETCHER_HOLDER_CLASS_NAME);
    }

    public static ClassName getUtils() {
        return ClassName.get(Constant.UTILS_PACKAGE_NAME, Constant.UTILS_CLASS_NAME);
    }
}
