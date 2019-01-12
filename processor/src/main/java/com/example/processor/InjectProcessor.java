package com.example.processor;

import com.example.annation.Inject;
import com.example.annation.Provides;
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
        Map<String, Map<String, Element>> provideMap = new HashMap<>();
        Map<TypeElement, Map<String, Element>> injectMap = new HashMap<>();
        // 处理@Provides
        processProvide(roundEnvironment.getElementsAnnotatedWith(Provides.class), provideMap);
        // 处理 @Inject
        processInject(roundEnvironment.getElementsAnnotatedWith(Inject.class), injectMap);
        // 生成Java文件
        JavaFileWriter javaFileUtil = new JavaFileWriter(mElementUtils, mFiler);
        javaFileUtil.mMessager = mMessager;
        javaFileUtil.writeJavaFile(provideMap, injectMap);
        return true;
    }

    private void processProvide(Set<? extends Element> elements, Map<String, Map<String, Element>> provideMap) {
        for (Element element : elements) {
            TypeElement typeElement = (TypeElement) element.getEnclosingElement();
            String elementMapKey = typeElement.getQualifiedName().toString();
            Map<String, Element> elementMap = provideMap.get(elementMapKey);
            Provides provides = element.getAnnotation(Provides.class);
            String elementKey = provides.value();
            if (elementKey.equals("")) {
                elementKey = element.asType().toString();
            }
            if (elementMap != null) {
                elementMap.put(elementKey, element);
            } else {
                elementMap = new HashMap<>();
                elementMap.put(elementKey, element);
                provideMap.put(elementMapKey, elementMap);
            }
        }
    }

    private void processInject(Set<? extends Element> elements, Map<TypeElement, Map<String, Element>> injectMap) {
        for (Element element : elements) {
            TypeElement typeElement = (TypeElement) element.getEnclosingElement();
            Map<String, Element> elementMap = injectMap.get(typeElement);
            Inject inject = element.getAnnotation(Inject.class);
            String elementKey = inject.value();
            if (elementKey.equals("")) {
                elementKey = element.asType().toString();
            }
            if (elementMap != null) {
                elementMap.put(elementKey, element);
            } else {
                elementMap = new HashMap<>();
                elementMap.put(elementKey, element);
                injectMap.put(typeElement, elementMap);
            }
        }
    }
}