package com.blade.processor.writer;

import com.blade.annotation.inter.Injectable;
import com.blade.annotation.inter.Provider;
import com.blade.processor.Constants;
import com.blade.processor.ParameterizedTypeImpl;
import com.blade.processor.node.ElementNode;
import com.blade.processor.util.MethodUtils;
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
            MethodSpec constructorMethod = MethodUtils.buildConstructorMethod(Modifier.PUBLIC)
                    .addStatement("pathMap = new $T<>()", HashMap.class)
                    .build();

            // find方法
            MethodSpec findMethod = MethodUtils.buildOverrideMethod("find", Modifier.PUBLIC, Object.class,
                    ParameterSpec.builder(String.class, "id").build())
                    .addStatement("return pathMap.get(id)")
                    .build();

            Map<String, String> pathMap = rootNode.getAllPath();
            Set<String> pathKeySet = pathMap.keySet();
            // init方法
            MethodSpec.Builder initMethodBuild = MethodUtils.buildOverrideMethod("init", Modifier.PUBLIC, void.class,
                    ParameterSpec.builder(Object.class, "object").build(),
                    ParameterSpec.builder(new ParameterizedTypeImpl(Map.class, new Class[]{String.class, Object.class}), "map").build())
                    .addStatement("clear()")
                    .addCode("if (map != null && !map.isEmpty()) {\n")
                    .addCode("\tpathMap.putAll(map);\n")
                    .addCode("}\n")
                    .addStatement("$L source = ($L)object", rootNode.getType(), rootNode.getType());
            for (String pathKey : pathKeySet) {
                initMethodBuild.addStatement("pathMap.put($S, source.$L)", pathKey, pathMap.get(pathKey));
            }
            // clear方法
            MethodSpec initMethod = MethodUtils.buildOverrideMethod("clear", Modifier.PUBLIC, void.class)
                    .addStatement("pathMap.clear()")
                    .build();
            TypeSpec typeSpec = typeSpecBuilder
                    .addMethod(constructorMethod)
                    .addMethod(findMethod)
                    .addMethod(initMethod)
                    .addMethod(initMethodBuild.build())
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
            TypeSpec.Builder typeSpecBuilder = TypeSpec.classBuilder(rootNode.getSimpleName() + Constants.INJECT_SUFFIX)
                    .addSuperinterface(Injectable.class)
                    .addModifiers(Modifier.PUBLIC);
            // inject方法
            MethodSpec.Builder injectMethodBuilder = MethodUtils.buildOverrideMethod(Constants.INJECT_METHOD_NAME, Modifier.PUBLIC, void.class,
                    ParameterSpec.builder(Object.class, "object").build(),
                    ParameterSpec.builder(Provider.class, Constants.SOURCE).build())
                    .addStatement("$L $L = ($L) object", rootNode.getType(), Constants.TARGET, rootNode.getType());
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
        if (injectNode.getType().toLowerCase().contains("boolean")) {
            return CodeBlock.builder().add("$L.$L = $L.find($S) == null ? false : ($L)($L.find($S))", Constants.TARGET,
                    injectNode.getSimpleName(), Constants.SOURCE, injectNode.getId(), injectNode.getType(),
                    Constants.SOURCE, injectNode.getId()).build();
        }
        return CodeBlock.builder().add("$L.$L = $L.find($S) == null ? 0 : ($L)($L.find($S))", Constants.TARGET,
                injectNode.getSimpleName(), Constants.SOURCE, injectNode.getId(), injectNode.getType(),
                Constants.SOURCE, injectNode.getId()).build();
    }
}
