package com.blade.processor.writer;

import com.blade.processor.util.MethodUtils;
import com.blade.processor.entry.ClassEntry;
import com.blade.processor.entry.FieldEntry;
import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.JavaFile;
import com.squareup.javapoet.MethodSpec;
import com.squareup.javapoet.ParameterSpec;
import com.squareup.javapoet.ParameterizedTypeName;
import com.squareup.javapoet.TypeName;
import com.squareup.javapoet.TypeSpec;

import java.io.IOException;
import java.util.List;

import javax.annotation.processing.Filer;
import javax.lang.model.element.Modifier;
import javax.lang.model.type.TypeKind;

import static com.blade.processor.util.ClassUtils.getFetchHolder;
import static com.blade.processor.util.ClassUtils.getInjectorInterface;
import static com.blade.processor.util.ClassUtils.getUtils;
import static com.blade.processor.util.StringUtils.classNameToParameterName;

public class InjectorWriter implements Writer {

    private static final String INJECT_METHOD_NAME = "inject";

    private Filer filer;

    public InjectorWriter(Filer filer) {
        this.filer = filer;
    }

    @Override
    public void writer(List<ClassEntry> classEntryList) {
        for (ClassEntry classEntry : classEntryList) {
            TypeSpec.Builder classBuild = generateTypeSpecBuilder(classEntry);
            generateInjectMethod(classBuild, classEntry);
            generateCleanMethod(classBuild, classEntry);
            JavaFile javaFile =
                    JavaFile.builder(classEntry.getPackageName(), classBuild.build()).build();
            try {
                javaFile.writeTo(filer);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * 类定义
     * 实现Injector泛型接口，同时增加public和final修饰符
     *
     * @param classEntry
     * @return
     */
    private TypeSpec.Builder generateTypeSpecBuilder(ClassEntry classEntry) {
        TypeSpec.Builder builder = TypeSpec.classBuilder((classEntry.isInnerClass() ?
                classEntry.getInnerClassName().replace(".", "$") : classEntry.getSimpleName()) + "Injector");
        // 实现Injector接口
        TypeName superInterface = ParameterizedTypeName.get(getInjectorInterface(),
                ClassName.bestGuess(classEntry.getClassName()));
        builder.addSuperinterface(superInterface);
        builder.addModifiers(Modifier.FINAL, Modifier.PUBLIC);
        return builder;
    }

    /**
     * 生成inject方法
     *
     * @param builder
     * @param classEntry
     */
    private void generateInjectMethod(TypeSpec.Builder builder, ClassEntry classEntry) {
        String targetName = classNameToParameterName(classEntry.getSimpleName());
        String sourcesName = "sources";
        ParameterSpec.Builder targetBuilder =
                ParameterSpec.builder(ClassName.bestGuess(classEntry.getClassName()), targetName);
        ParameterSpec.Builder objectBuilder = ParameterSpec.builder(Object[].class, "sources");
        MethodSpec.Builder methodBuilder = MethodUtils.buildOverrideMethod(INJECT_METHOD_NAME,
                Modifier.PUBLIC, void.class, targetBuilder.build(), objectBuilder.build()).varargs(true);
        for (FieldEntry fieldEntry : classEntry.getFieldEntryList()) {
            if (fieldEntry.isPrimitive()) {
                methodBuilder.addStatement("$L.$L = ($L)$T.getPrimitive($T.fetch($S, $L), $L)", targetName,
                        fieldEntry.getFieldName(), fieldEntry.getTypeName(), getUtils(), getFetchHolder(),
                        fieldEntry.getName(),
                        sourcesName, getPrimitiveDefaultValue(fieldEntry.getType().getKind()));
            } else {
                methodBuilder.addStatement("$L.$L = $T.checkNoNull($T.fetch($S, $L), $L)", targetName,
                        fieldEntry.getFieldName(), getUtils(), getFetchHolder(), fieldEntry.getName(),
                        sourcesName, fieldEntry.isSupportNull());
            }
        }
        builder.addMethod(methodBuilder.build());
    }

    /**
     * 生成clean方法
     *
     * @param builder
     * @param classEntry
     */
    private void generateCleanMethod(TypeSpec.Builder builder, ClassEntry classEntry) {
        String targetName = classNameToParameterName(classEntry.getSimpleName());
        ParameterSpec.Builder targetBuilder =
                ParameterSpec.builder(ClassName.bestGuess(classEntry.getClassName()), targetName);
        MethodSpec.Builder methodBuilder = MethodUtils.buildOverrideMethod("clean",
                Modifier.PUBLIC, void.class, targetBuilder.build());
        for (FieldEntry fieldEntry : classEntry.getFieldEntryList()) {
            String statementString = "$L.$L = " + (fieldEntry.isPrimitive() ?
                    getPrimitiveDefaultValue(fieldEntry.getType().getKind()) : "null");
            methodBuilder.addStatement(statementString, targetName, fieldEntry.getFieldName());
        }
        builder.addMethod(methodBuilder.build());
    }

    private String getPrimitiveDefaultValue(TypeKind typeKind) {
        switch (typeKind) {
            case INT:
            case LONG:
            case FLOAT:
            case DOUBLE:
            case SHORT:
            case BYTE:
            case CHAR:
                return "0";
            default:
                return "false";
        }
    }
}
