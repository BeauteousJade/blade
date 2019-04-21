package com.example.processor;

import com.example.annotation.Inject;
import com.example.annotation.Provides;
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
        JavaFileWriter fileWriterV2 = new JavaFileWriter(mFiler);
        // 处理@Provides
        Map<String, ElementNode> provideMap = processProvide(roundEnvironment.getElementsAnnotatedWith(Provides.class));
        new JavaFileWriter(mFiler).writeProvider(provideMap);
        // 处理@Inject
        Map<String, ElementNode> injectMap = processInject(roundEnvironment.getElementsAnnotatedWith(Inject.class));
        fileWriterV2.writerInject(injectMap);
        return true;
    }

    private Map<String, ElementNode> processProvide(Set<? extends Element> elements) {

        final Map<String, ElementNode> provideMap = processAnnotation(elements, new Callback() {
            @Override
            public ElementNode generateChildNode(Element element) {
                final Provides provides = element.getAnnotation(Provides.class);
                final String elementId = provides.value().equals("") ? element.asType().toString() : provides.value();
                ElementNode childNode = new ElementNode(elementId, element.getSimpleName().toString(),
                        element.asType().toString(), ElementUtils.getPackageName(mElementUtils, element), element.asType().getKind().isPrimitive());
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
                return new ElementNode(elementId, element.getSimpleName().toString(),
                        element.asType().toString(), ElementUtils.getPackageName(mElementUtils, element), element.asType().getKind().isPrimitive());
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
                elementRootNode = new ElementNode(rootNodeKey, getRootNodeName(typeElement),
                        rootNodeKey, ElementUtils.getPackageName(mElementUtils, typeElement), element.asType().getKind().isPrimitive());
                map.put(rootNodeKey, elementRootNode);
            }
            elementRootNode.addChild(childNode);
        }
        return map;
    }

    /**
     * 区别内部类和非内部类
     * 如果是内部类，那么返回的是外部类名$内部类名
     * 如果是外部类，返回的是外部类名
     *
     * @return
     */
    private String getRootNodeName(TypeElement typeElement) {
        final Element element = typeElement.getEnclosingElement();
        if (!element.getKind().isClass() && !element.getKind().isInterface()) {
            return typeElement.getSimpleName().toString();
        } else {
            String name = (typeElement).getQualifiedName().toString();
            final int lastSecondIndex = name.lastIndexOf(".", name.lastIndexOf(".") - 1) + 1;
            return name.substring(lastSecondIndex).replace(".", "$");
        }
    }

    private interface Callback {
        ElementNode generateChildNode(Element element);
    }
}