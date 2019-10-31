package com.blade.processor.util;

import com.blade.processor.Constant;
import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.ParameterizedTypeName;
import com.squareup.javapoet.TypeName;

public class FieldUtils {

    /**
     * 返回FieldParam的泛型类型
     *
     * @param typeName
     * @return
     */
    public static TypeName getFieldParam(TypeName typeName) {
        return ParameterizedTypeName.get(ClassName.get(Constant.FIELD_PARAM_PACKAGE_NAME,
                Constant.FIELD_PARAM_CLASS_NAME), typeName);
    }

    /**
     * 返回DeepProviderFieldParam的泛型类型
     *
     * @param typeName
     * @return
     */
    public static TypeName getDeepProvideFieldParam(TypeName typeName) {
        return ParameterizedTypeName.get(ClassName.get(Constant.DEEP_PROVIDER_PARAM_PACKAGE_NAME,
                Constant.DEEP_PROVIDER_PARAM_CLASS_NAME), typeName);
    }

    /**
     * 返回Fetcher的泛型类型
     *
     * @param typeName
     * @return
     */
    public static TypeName getFetcher(TypeName typeName) {
        return ParameterizedTypeName.get(ClassName.get(Constant.FETCHER_PACKAGE_NAME,
                Constant.FETCHER_CLASS_NAME), typeName);
    }
}
