package com.blade.processor.writer;

import com.blade.annotation.inter.Provider;
import com.blade.processor.Constants;
import com.blade.processor.ParameterizedTypeImpl;
import com.blade.processor.node.ElementNode;
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

public class JavaFileWriter {

    private Filer filer;

    public JavaFileWriter(Filer filer) {
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
                    .addParameter(ParameterSpec.builder(ClassName.bestGuess(rootNode.getType()), "source").build())
                    .addParameter(ParameterSpec.builder(new ParameterizedTypeImpl(Map.class, new Class[]{String.class, Object.class}), "map").build())
                    .addStatement("pathMap = new $T<>()", HashMap.class)
                    .addCode("if (map != null && !map.isEmpty()) {\n")
                    .addCode("\tpathMap.putAll(map);\n")
                    .addCode("}\n");
            Map<String, String> pathMap = rootNode.getAllPath();
            Set<String> pathKeySet = pathMap.keySet();
            for (String pathKey : pathKeySet) {
                constructorMethod.addStatement("pathMap.put($S, source.$L)", pathKey, pathMap.get(pathKey));
            }
            // find方法
            MethodSpec findMethod = MethodSpec.methodBuilder("find")
                    .returns(Object.class)
                    .addModifiers(Modifier.PUBLIC)
                    .addAnnotation(AnnotationSpec.builder(Override.class).build())
                    .addParameter(ParameterSpec.builder(String.class, "id").build())
                    .addStatement("return pathMap.get(id)").build();

            TypeSpec typeSpec = typeSpecBuilder
                    .addMethod(constructorMethod.build())
                    .addMethod(findMethod)
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
                    .addModifiers(Modifier.PUBLIC)
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
        if (!injectNode.isPrimitive()) {
            return CodeBlock.builder().add("$L.$L = ($L)($L.find($S))", Constants.TARGET, injectNode.getSimpleName(), injectNode.getType(), Constants.SOURCE, injectNode.getId()).build();
        }
        return CodeBlock.builder().add("$L.$L = $L.find($S) == null? 0 : ($L)($L.find($S))", Constants.TARGET,
                injectNode.getSimpleName(), Constants.SOURCE, injectNode.getId(), injectNode.getType(),
                Constants.SOURCE, injectNode.getId()).build();
    }
}
