package com.example.processor;

import com.example.annation.Module;
import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.CodeBlock;
import com.squareup.javapoet.JavaFile;
import com.squareup.javapoet.MethodSpec;
import com.squareup.javapoet.TypeSpec;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

import javax.annotation.processing.Filer;
import javax.annotation.processing.Messager;
import javax.lang.model.element.AnnotationMirror;
import javax.lang.model.element.Element;
import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.Modifier;
import javax.lang.model.element.TypeElement;
import javax.lang.model.type.MirroredTypeException;
import javax.lang.model.type.TypeMirror;
import javax.lang.model.util.Elements;

public class JavaFileWriter {

    private Elements elementUtils;
    private Filer filer;
    public Messager mMessager;

    public JavaFileWriter(Elements elementUtils, Filer filer) {
        this.elementUtils = elementUtils;
        this.filer = filer;
    }

    public void writeJavaFile(Map<String, Map<String, Element>> provideMap, Map<TypeElement, Map<String, Element>> injectMap) {
        Set<TypeElement> injectElementMapKeySet = injectMap.keySet();
        for (TypeElement key : injectElementMapKeySet) {
            Map<String, Element> injectElementMap = injectMap.get(key);
            String provideElementMapKey = getClassFromAnnotation(key);
            if (provideElementMapKey != null) {
                Map<String, Element> provideElementMap = provideMap.get(provideElementMapKey);
                TypeSpec.Builder typeSpecBuilder = TypeSpec.classBuilder(key.getSimpleName() + "_Inject")
                        .addModifiers(Modifier.PUBLIC);
                MethodSpec.Builder methodBuilder = MethodSpec.methodBuilder("inject")
                        .addParameter(ClassName.bestGuess(key.getQualifiedName().toString()), "target")
                        .addParameter(ClassName.bestGuess(provideElementMapKey), "source")
                        .addModifiers(Modifier.PUBLIC);
                Set<String> injectElementKeySet = injectElementMap.keySet();
                for (String injectElementKey : injectElementKeySet) {
                    methodBuilder.addStatement(generateCodeBlock(injectElementMap.get(injectElementKey).getSimpleName().toString(), provideElementMap.get(injectElementKey).getSimpleName().toString()));
                }
                TypeSpec typeSpec = typeSpecBuilder.addMethod(methodBuilder.build()).build();
                JavaFile javaFile = JavaFile.builder(elementUtils.getPackageOf(key).getQualifiedName().toString(), typeSpec).build();
                try {
                    javaFile.writeTo(filer);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    private String getClassFromAnnotation(Element key) {
        List<? extends AnnotationMirror> annotationMirrors = key.getAnnotationMirrors();
        for (AnnotationMirror annotationMirror : annotationMirrors) {
            if (Module.class.getName().equals(annotationMirror.getAnnotationType().toString())) {
                Set<? extends ExecutableElement> keySet = annotationMirror.getElementValues().keySet();
                for (ExecutableElement executableElement : keySet) {
                    if (Objects.equals(executableElement.getSimpleName().toString(), "value")) {
                        return annotationMirror.getElementValues().get(executableElement).getValue().toString();
                    }
                }
            }
        }
        return null;
    }

    private String getClassFromAnnotationV2(Element key) {
        try {
            key.getAnnotation(Module.class).value();
        } catch (MirroredTypeException e) {
            TypeMirror typeMirror = e.getTypeMirror();
            return typeMirror.toString();
        }
        return null;
    }

    private CodeBlock generateCodeBlock(String target, String source) {
        return CodeBlock.builder().add("target.$L = source.$L", target, source).build();
    }
}
