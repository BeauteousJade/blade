package com.blade.processor.util;

import com.squareup.javapoet.AnnotationSpec;
import com.squareup.javapoet.MethodSpec;
import com.squareup.javapoet.ParameterSpec;

import java.lang.reflect.Type;

import javax.lang.model.element.Modifier;

public class MethodUtils {

    public static MethodSpec.Builder buildOverrideMethod(String methodName, Modifier modifier, Type returnType, ParameterSpec... parameterSpecs) {
        MethodSpec.Builder builder = MethodSpec.methodBuilder(methodName)
                .addModifiers(modifier)
                .addAnnotation(AnnotationSpec.builder(Override.class).build())
                .returns(returnType);
        if (!ArrayUtils.isEmpty(parameterSpecs)) {
            for (ParameterSpec parameterSpec : parameterSpecs) {
                builder.addParameter(parameterSpec);
            }
        }
        return builder;
    }

    public static MethodSpec.Builder buildConstructorMethod(Modifier modifier, ParameterSpec... parameterSpecs) {
        MethodSpec.Builder builder = MethodSpec.constructorBuilder()
                .addModifiers(modifier);
        if (!ArrayUtils.isEmpty(parameterSpecs)) {
            for (ParameterSpec parameterSpec : parameterSpecs) {
                builder.addParameter(parameterSpec);
            }
        }
        return builder;
    }
}
