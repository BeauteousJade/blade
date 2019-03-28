package com.example.processor.writer;

import com.example.processor.Constants;
import com.example.processor.node.ElementNode;
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
import javax.lang.model.type.MirroredTypeException;
import javax.lang.model.type.TypeMirror;

public class JavaFileWriter {

    private Filer filer;
    public Messager mMessager;

    public JavaFileWriter(Filer filer) {
        this.filer = filer;
    }

    public void writeJavaFile(Map<String, ElementNode> provideMap, Map<String, ElementNode> injectMap) {

        final Set<String> injectElementKeySet = injectMap.keySet();
        for (String injectElementKey : injectElementKeySet) {
            final ElementNode injectRootNode = injectMap.get(injectElementKey);
            final ElementNode providesRootNode = provideMap.get(getClassFromAnnotation(injectRootNode.getAnnotationMirrorList()));
            final Set<String> injectChildNodeKeySet = injectRootNode.getNextMap().keySet();

            TypeSpec.Builder typeSpecBuilder = TypeSpec.classBuilder(injectRootNode.getSimpleName() + Constants.INJECT_SUFFIX)
                    .addModifiers(Modifier.PUBLIC);
            MethodSpec.Builder methodBuilder = MethodSpec.methodBuilder(Constants.INJECT_METHOD_NAME)
                    .addParameter(ClassName.bestGuess(injectRootNode.getType()), Constants.TARGET)
                    .addParameter(ClassName.bestGuess(providesRootNode.getType()), Constants.SOURCE)
                    .addModifiers(Modifier.PUBLIC);
            for (String injectChildNodeKey : injectChildNodeKeySet) {
                final ElementNode injectChildNode = injectRootNode.getChild(injectChildNodeKey);
                final String sourcePath = providesRootNode.lookUp(injectChildNode.getId());
                if (sourcePath != null) {
                    methodBuilder.addStatement(generateCodeBlock(injectChildNode, sourcePath));
                }
            }
            TypeSpec typeSpec = typeSpecBuilder.addMethod(methodBuilder.build()).build();
            JavaFile javaFile = JavaFile.builder(injectRootNode.getPackageName(), typeSpec).build();
            try {
                javaFile.writeTo(filer);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private String getClassFromAnnotation(List<? extends AnnotationMirror> annotationMirrors) {
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

    private CodeBlock generateCodeBlock(ElementNode injectNode, String sourcePath) {
        return CodeBlock.builder().add("$L.$L = ($L)($L.$L)", Constants.TARGET, injectNode.getSimpleName(), injectNode.getType(), Constants.SOURCE, sourcePath).build();
    }
}
