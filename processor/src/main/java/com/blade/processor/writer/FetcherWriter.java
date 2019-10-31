package com.blade.processor.writer;

import com.blade.processor.util.ClassUtils;
import com.blade.processor.util.MethodUtils;
import com.blade.processor.Constant;
import com.blade.processor.entry.ClassEntry;
import com.blade.processor.entry.FieldEntry;
import com.squareup.javapoet.AnnotationSpec;
import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.FieldSpec;
import com.squareup.javapoet.JavaFile;
import com.squareup.javapoet.MethodSpec;
import com.squareup.javapoet.ParameterSpec;
import com.squareup.javapoet.ParameterizedTypeName;
import com.squareup.javapoet.TypeName;
import com.squareup.javapoet.TypeSpec;
import com.squareup.javapoet.TypeVariableName;

import java.io.IOException;
import java.util.List;

import javax.annotation.processing.Filer;
import javax.lang.model.element.Modifier;

import static com.blade.processor.util.ClassUtils.getFetcherInterface;
import static com.blade.processor.util.ClassUtils.getNullable;
import static com.blade.processor.util.ClassUtils.getSourceFetchers;
import static com.blade.processor.util.FieldUtils.getDeepProvideFieldParam;
import static com.blade.processor.util.FieldUtils.getFetcher;
import static com.blade.processor.util.FieldUtils.getFieldParam;
import static com.blade.processor.util.StringUtils.classNameToParameterName;
import static com.blade.processor.util.StringUtils.formatAndroidParamName;

public class FetcherWriter implements Writer {

    private static final String SUPER_FETCH_FIELD_NAME = "mSuperFetcher";
    private static final String INIT_METHOD_NAME = "init";
    private static final String FETCH_METHOD_NAME = "fetch";

    private Filer filer;

    public FetcherWriter(Filer filer) {
        this.filer = filer;
    }

    @Override
    public void writer(List<ClassEntry> classEntryList) {
        for (ClassEntry classEntry : classEntryList) {
            TypeSpec.Builder typeSpecBuilder = generateTypeSpecBuilder(classEntry);
            addField(typeSpecBuilder, classEntry);
            generateConstructor(typeSpecBuilder, classEntry);
            generateInitMethod(typeSpecBuilder, classEntry);
            generateFetchMethod(typeSpecBuilder, classEntry);

            JavaFile javaFile =
                    JavaFile.builder(classEntry.getPackageName(), typeSpecBuilder.build()).build();
            try {
                javaFile.writeTo(filer);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * 类定义
     * 实现Fetcher泛型接口，同时增加public和final修饰符
     *
     * @param classEntry
     * @return
     */
    private TypeSpec.Builder generateTypeSpecBuilder(ClassEntry classEntry) {
        TypeSpec.Builder builder = TypeSpec.classBuilder((classEntry.isInnerClass() ?
                classEntry.getInnerClassName().replace(".", "$") : classEntry.getSimpleName()) + "Fetcher");
        // 实现Fetcher接口
        TypeName superInterface = ParameterizedTypeName.get(getFetcherInterface(),
                ClassName.get(classEntry.getPackageName(), classEntry.isInnerClass() ?
                        classEntry.getInnerClassName() : classEntry.getSimpleName()));
        builder.addSuperinterface(superInterface);
        builder.addModifiers(Modifier.FINAL, Modifier.PUBLIC);
        return builder;
    }

    /**
     * 定义成员变量
     *
     * @param typeSpecBuilder
     * @param classEntry
     * @return
     */
    private void addField(TypeSpec.Builder typeSpecBuilder, ClassEntry classEntry) {
        for (FieldEntry fieldEntry : classEntry.getFieldEntryList()) {
            TypeName typeName;
            String className;
            // 获得变量类型的泛型类型，例如FieldParam<T> 中的T
            TypeName typeVariable = ClassName.get(fieldEntry.getType());
            // 内部是否支持注入，包括Object和Map
            if (fieldEntry.isDeepProvide()) {
                // 构造变量类型
                typeName = getDeepProvideFieldParam(typeVariable.isPrimitive() ?
                        typeVariable.box() : typeVariable);
                className = Constant.DEEP_PROVIDER_PARAM_CLASS_NAME;
            } else {
                typeName = getFieldParam(typeVariable.isPrimitive() ?
                        typeVariable.box() : typeVariable);
                className = Constant.FIELD_PARAM_CLASS_NAME;
            }
            FieldSpec.Builder fieldSpecBuilder = FieldSpec.builder(typeName,
                    formatAndroidParamName(fieldEntry.getFieldName()),
                    Modifier.PRIVATE).initializer("new $L<>()", className);
            typeSpecBuilder.addField(fieldSpecBuilder.build());
        }
        // 添加superFetcher
        FieldSpec.Builder superFetcherFieldSpecBuilder =
                FieldSpec.builder(getFetcher(ClassName.bestGuess(classEntry.getClassName())), SUPER_FETCH_FIELD_NAME,
                        Modifier.PRIVATE).addAnnotation(ClassUtils.getNullable());
        typeSpecBuilder.addField(superFetcherFieldSpecBuilder.build());
    }

    /**
     * 生成构造方法
     *
     * @param typeSpecBuilder
     * @param classEntry
     * @return
     */
    private void generateConstructor(TypeSpec.Builder typeSpecBuilder,
                                     ClassEntry classEntry) {
        MethodSpec.Builder constructorMethodSpecBuilder =
                MethodUtils.buildConstructorMethod(Modifier.PUBLIC);
        constructorMethodSpecBuilder.addStatement("$L =  ($L<$L>)$T.fetcher($L.class.getSuperclass())",
                SUPER_FETCH_FIELD_NAME, Constant.FETCHER_CLASS_NAME, getInnerClassName(classEntry), getSourceFetchers(),
                getInnerClassName(classEntry));
        typeSpecBuilder.addMethod(constructorMethodSpecBuilder.build());
    }

    /**
     * 生成init方法
     *
     * @param typeSpecBuilder
     * @param classEntry
     */
    private void generateInitMethod(TypeSpec.Builder typeSpecBuilder,
                                    ClassEntry classEntry) {
        String parameterName = classNameToParameterName(classEntry.getSimpleName());
        ParameterSpec.Builder parameterSpecBuilder =
                ParameterSpec.builder(ClassName.bestGuess(classEntry.getClassName()),
                        parameterName);
        MethodSpec.Builder initMethodBuilder = MethodUtils.buildOverrideMethod(INIT_METHOD_NAME,
                Modifier.PUBLIC, void.class, parameterSpecBuilder.build());
        initMethodBuilder.addCode("if ($L != null) {\n", SUPER_FETCH_FIELD_NAME);
        initMethodBuilder.addStatement("\t$L.init($L)", SUPER_FETCH_FIELD_NAME, parameterName);
        initMethodBuilder.addCode("}\n");
        for (FieldEntry fieldEntry : classEntry.getFieldEntryList()) {
            initMethodBuilder.addStatement("$L.init($L.$L, $S)",
                    formatAndroidParamName(fieldEntry.getFieldName()), parameterName,
                    fieldEntry.getFieldName(), fieldEntry.getName());
        }
        typeSpecBuilder.addMethod(initMethodBuilder.build());
    }

    /**
     * 生成fetch方法
     *
     * @param typeSpecBuilder
     * @param classEntry
     */
    private void generateFetchMethod(TypeSpec.Builder typeSpecBuilder,
                                     ClassEntry classEntry) {
        String parameterName = "name";
        ParameterSpec.Builder parameterSpecBuilder = ParameterSpec.builder(String.class,
                parameterName);
        MethodSpec.Builder fetchMethodBuilder = MethodSpec.methodBuilder(FETCH_METHOD_NAME)
                .addModifiers(Modifier.PUBLIC)
                .addAnnotation(AnnotationSpec.builder(Override.class).build())
                .addAnnotation(getNullable())
                .addTypeVariable(TypeVariableName.get("U"))
                .returns(TypeVariableName.get("U"))
                .addParameter(parameterSpecBuilder.build());
        fetchMethodBuilder.addStatement("U u");
        for (FieldEntry fieldEntry : classEntry.getFieldEntryList()) {
            fetchMethodBuilder.addCode("if ((u = $L.getParam($L)) != null) {\n",
                    formatAndroidParamName(fieldEntry.getFieldName()), parameterName);
            fetchMethodBuilder.addStatement("\treturn u");
            fetchMethodBuilder.addCode("}\n");
        }
        fetchMethodBuilder.addCode("if ($L != null) {\n", SUPER_FETCH_FIELD_NAME);
        fetchMethodBuilder.addStatement("\treturn (U) $L.fetch($L)", SUPER_FETCH_FIELD_NAME,
                parameterName);
        fetchMethodBuilder.addCode("}");
        fetchMethodBuilder.addStatement("return null");
        typeSpecBuilder.addMethod(fetchMethodBuilder.build());
    }

    private String getInnerClassName(ClassEntry classEntry) {
        return classEntry.isInnerClass() ?
                classEntry.getInnerClassName() : classEntry.getSimpleName();
    }
}
