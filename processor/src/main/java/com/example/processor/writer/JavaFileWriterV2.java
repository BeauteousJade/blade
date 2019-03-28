package com.example.processor.writer;

import com.example.annation.inter.Provider;
import com.example.processor.Constants;
import com.example.processor.ParameterizedTypeImpl;
import com.example.processor.node.ElementNode;
import com.squareup.javapoet.AnnotationSpec;
import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.CodeBlock;
import com.squareup.javapoet.FieldSpec;
import com.squareup.javapoet.JavaFile;
import com.squareup.javapoet.MethodSpec;
import com.squareup.javapoet.ParameterSpec;
import com.squareup.javapoet.TypeSpec;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import javax.annotation.processing.Filer;
import javax.lang.model.element.Modifier;

public class JavaFileWriterV2 {

    private Filer filer;

    public JavaFileWriterV2(Filer filer) {
        this.filer = filer;
    }

    public void writeProvider(Map<String, ElementNode> elementNodeMap) {
        final Set<String> keySet = elementNodeMap.keySet();
        for (String key : keySet) {
            ElementNode rootNode = elementNodeMap.get(key);
            TypeSpec.Builder typeSpecBuilder = TypeSpec.classBuilder(rootNode.getSimpleName() + Constants.PROVIDER_SUFFIX)
                    .addField(FieldSpec.builder(new ParameterizedTypeImpl(Map.class, new Class[]{String.class, Object.class}), "pathMap", Modifier.PRIVATE).build())
                    .addSuperinterface(Provider.class)
                    .addModifiers(Modifier.PUBLIC);
            // 构造方法
            MethodSpec.Builder constructorMethod = MethodSpec.constructorBuilder()
                    .addModifiers(Modifier.PUBLIC)
                    .addStatement("pathMap = new $T<>()", HashMap.class);
            Map<String, String> pathMap = rootNode.getAllPath();
            MethodSpec.Builder initMethodBuilder = MethodSpec.methodBuilder("init")
                    .addModifiers(Modifier.PUBLIC)
                    .addParameter(ParameterSpec.builder(ClassName.bestGuess(rootNode.getType()), "source").build());
            Set<String> pathKeySet = pathMap.keySet();
            for (String pathKey : pathKeySet) {
                initMethodBuilder.addStatement("pathMap.put($S, source.$L);", pathKey, pathMap.get(pathKey));
            }
            // find方法
            MethodSpec findMethod = MethodSpec.methodBuilder("find")
                    .returns(Object.class)
                    .addModifiers(Modifier.PUBLIC)
                    .addAnnotation(AnnotationSpec.builder(Override.class).build())
                    .addParameter(ParameterSpec.builder(String.class, "id").build())
                    .addStatement("return pathMap.get(id)").build();
            // put方法
            MethodSpec putMethod = MethodSpec.methodBuilder("put")
                    .addModifiers(Modifier.PUBLIC)
                    .addAnnotation(AnnotationSpec.builder(Override.class).build())
                    .addParameter(ParameterSpec.builder(new ParameterizedTypeImpl(Map.class, new Class[]{String.class, Object.class}), "map").build())
                    .addStatement("pathMap.putAll(map)")
                    .build();

            TypeSpec typeSpec = typeSpecBuilder
                    .addMethod(constructorMethod.build())
                    .addMethod(findMethod)
                    .addMethod(putMethod)
                    .addMethod(initMethodBuilder.build())
                    .build();
            JavaFile javaFile = JavaFile.builder(rootNode.getPackageName(), typeSpec).build();
            try {
                javaFile.writeTo(filer);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public void writerInject(Map<String, ElementNode> elementNodeMap) {
        final Set<String> keySet = elementNodeMap.keySet();
        for (String key : keySet) {

            ElementNode rootNode = elementNodeMap.get(key);
            Set<String> injectNodeKeySet = rootNode.getNextMap().keySet();
            TypeSpec.Builder typeSpecBuilder = TypeSpec.classBuilder(rootNode.getSimpleName() + Constants.INJECT_SUFFIX).addModifiers(Modifier.PUBLIC);
            // inject方法
            MethodSpec.Builder injectMethodBuilder = MethodSpec.methodBuilder(Constants.INJECT_METHOD_NAME)
                    .addParameter(ClassName.bestGuess(rootNode.getType()), Constants.TARGET)
                    .addParameter(Provider.class, Constants.SOURCE);
            for (String injectNodeKey : injectNodeKeySet) {
                injectMethodBuilder.addStatement(generateCodeBlock(rootNode.getNextMap().get(injectNodeKey)));
            }

            TypeSpec typeSpec = typeSpecBuilder
                    .addMethod(injectMethodBuilder.build())
                    .build();
            JavaFile javaFile = JavaFile.builder(rootNode.getPackageName(), typeSpec).build();
            try {
                javaFile.writeTo(filer);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private CodeBlock generateCodeBlock(ElementNode injectNode) {
        return CodeBlock.builder().add("$L.$L = ($L)($L.find($S))", Constants.TARGET, injectNode.getSimpleName(), injectNode.getType(), Constants.SOURCE, injectNode.getId()).build();
    }
}
