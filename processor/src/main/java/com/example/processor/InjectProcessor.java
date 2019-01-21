package com.example.processor;

import com.example.annation.Inject;
import com.example.annation.Provides;
import com.example.processor.node.ElementNode;
import com.example.processor.util.ElementUtils;
import com.example.processor.writer.JavaFileWriter;
import com.google.auto.service.AutoService;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import javax.annotation.processing.AbstractProcessor;
import javax.annotation.processing.Filer;
import javax.annotation.processing.Messager;
import javax.annotation.processing.ProcessingEnvironment;
import javax.annotation.processing.Processor;
import javax.annotation.processing.RoundEnvironment;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.Element;
import javax.lang.model.element.TypeElement;
import javax.lang.model.util.Elements;
import javax.tools.Diagnostic;

@AutoService(Processor.class)
public class InjectProcessor extends AbstractProcessor {

    private Messager mMessager;
    private Elements mElementUtils;
    private Filer mFiler;

    @Override
    public synchronized void init(ProcessingEnvironment processingEnvironment) {
        super.init(processingEnvironment);
        mMessager = processingEnvironment.getMessager();
        mElementUtils = processingEnvironment.getElementUtils();
        mFiler = processingEnvironment.getFiler();
        mMessager.printMessage(Diagnostic.Kind.NOTE, "init");
    }

    @Override
    public Set<String> getSupportedAnnotationTypes() {
        Set<String> set = new HashSet<>();
        set.add(Provides.class.getCanonicalName());
        set.add(Inject.class.getCanonicalName());
        return set;
    }

    @Override
    public SourceVersion getSupportedSourceVersion() {
        return SourceVersion.latestSupported();
    }

    @Override
    public boolean process(Set<? extends TypeElement> set, RoundEnvironment roundEnvironment) {
        // 处理@Provides
        Map<String, ElementNode> provideMap = processProvide(roundEnvironment.getElementsAnnotatedWith(Provides.class));
        // 处理 @Inject
        Map<String, ElementNode> injectMap = processInject(roundEnvironment.getElementsAnnotatedWith(Inject.class));
        // 生成Java文件
        JavaFileWriter javaFileUtil = new JavaFileWriter(mFiler);
        javaFileUtil.mMessager = mMessager;
        javaFileUtil.writeJavaFile(provideMap, injectMap);
        return true;
    }

    private Map<String, ElementNode> processProvide(Set<? extends Element> elements) {

        final Map<String, ElementNode> provideMap = processAnnotation(elements, new Callback() {
            @Override
            public ElementNode generateChildNode(Element element) {
                final Provides provides = element.getAnnotation(Provides.class);
                final String elementId = provides.value().equals("") ? element.asType().toString() : provides.value();
                ElementNode childNode = new ElementNode(elementId, element.getSimpleName().toString(), element.asType().toString(), ElementUtils.getPackageName(mElementUtils, element));
                childNode.addAnnotation(Provides.class, provides);
                return childNode;
            }
        });
        final Set<String> rooNodeKeySet = provideMap.keySet();
        for (String key : rooNodeKeySet) {
            final ElementNode rootNode = provideMap.get(key);
            final Map<String, ElementNode> nextMap = rootNode.getNextMap();
            final Set<String> childNodeKeySet = nextMap.keySet();
            for (String childNodeKey : childNodeKeySet) {
                ElementNode childNode = nextMap.get(childNodeKey);
                Provides provides = childNode.getAnnotation(Provides.class);
                if (provides.deepProvides()) {
                    if (provideMap.get(childNode.getType()) != null) {
                        childNode.setNextMap(provideMap.get(childNode.getType()).getNextMap());
                    }
                }
            }
        }
        return provideMap;
    }

    private Map<String, ElementNode> processInject(Set<? extends Element> elements) {
        return processAnnotation(elements, new Callback() {
            @Override
            public ElementNode generateChildNode(Element element) {
                final Inject inject = element.getAnnotation(Inject.class);
                final String elementId = inject.value().equals("") ? element.asType().toString() : inject.value();
                return new ElementNode(elementId, element.getSimpleName().toString(), element.asType().toString(), ElementUtils.getPackageName(mElementUtils, element));
            }
        });
    }


    private Map<String, ElementNode> processAnnotation(Set<? extends Element> elements, Callback callback) {
        final Map<String, ElementNode> map = new HashMap<>();
        for (Element element : elements) {
            final TypeElement typeElement = (TypeElement) element.getEnclosingElement();
            final String rootNodeKey = typeElement.asType().toString();
            final ElementNode childNode = callback.generateChildNode(element);
            ElementNode elementRootNode = map.get(rootNodeKey);
            if (elementRootNode == null) {
                elementRootNode = new ElementNode(rootNodeKey, typeElement.getSimpleName().toString(), rootNodeKey, ElementUtils.getPackageName(mElementUtils, typeElement));
                elementRootNode.setAnnotationMirrorList(typeElement.getAnnotationMirrors());
                map.put(rootNodeKey, elementRootNode);
            }
            elementRootNode.addChild(childNode);
        }
        return map;
    }


    private void printMap(Map<String, ElementNode> map) {
        final Set<String> rootNodeKeySet = map.keySet();
        for (String key : rootNodeKeySet) {
            mMessager.printMessage(Diagnostic.Kind.NOTE, "key = " + key + "--" + map.get(key).toString());
        }
    }


    private interface Callback {
        ElementNode generateChildNode(Element element);
    }
}