package com.blade.processor.processor;

import com.blade.annotation.Module;
import com.blade.annotation.Provides;
import com.blade.processor.entry.ClassEntry;
import com.blade.processor.entry.FieldEntry;
import com.blade.processor.util.ElementUtils;
import com.blade.processor.util.StringUtils;
import com.blade.processor.writer.FetcherWriter;
import com.google.auto.service.AutoService;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.annotation.processing.Processor;
import javax.annotation.processing.RoundEnvironment;
import javax.lang.model.element.Element;
import javax.lang.model.element.TypeElement;
import javax.lang.model.type.TypeMirror;
import javax.tools.Diagnostic;

@AutoService(Processor.class)
public class ProviderProcessor extends BaseProcessor {

    @Override
    public Set<String> getSupportedAnnotationTypes() {
        Set<String> set = new HashSet<>();
        set.add(Provides.class.getCanonicalName());
        return set;
    }

    @Override
    public boolean process(Set<? extends TypeElement> set, RoundEnvironment roundEnvironment) {
        List<ClassEntry> classEntryList = new ArrayList<>();
        for (TypeElement typeElement : set) {
            Set<? extends Element> fieldElements =
                    roundEnvironment.getElementsAnnotatedWith(typeElement);
            classEntryList.addAll(processProvider(fieldElements));
        }
        FetcherWriter fetcherWriter = new FetcherWriter(mFiler);
        fetcherWriter.writer(classEntryList);
        return true;
    }

    private List<ClassEntry> processProvider(Set<? extends Element> fieldElements) {
        Map<String, ClassEntry> classEntryMap = new HashMap<>();
        List<ClassEntry> classEntryList = new ArrayList<>();
        for (Element element : fieldElements) {
            TypeElement typeElement = (TypeElement) element.getEnclosingElement();
            if (typeElement.getAnnotation(Module.class) == null) {
                continue;
            }
            FieldEntry fieldEntry = new FieldEntry();
            fieldEntry.setFieldName(element.getSimpleName().toString());
            TypeMirror type = element.asType();
            String typeString = type.toString();
            fieldEntry.setType(type);
            fieldEntry.setTypePackage(ElementUtils.getTypePackage(typeString));
            fieldEntry.setPrimitive(type.getKind().isPrimitive());
            Provides provides = element.getAnnotation(Provides.class);
            fieldEntry.setDeepProvide(provides.deepProvides());
            fieldEntry.setName(StringUtils.isEmpty(provides.value()) ? typeString :
                    provides.value());
            String className = typeElement.getQualifiedName().toString();
            if (classEntryMap.containsKey(className)) {
                ClassEntry classEntry = classEntryMap.get(className);
                classEntry.getFieldEntryList().add(fieldEntry);
            } else {
                ClassEntry classEntry = new ClassEntry();
                classEntry.setPackageName(ElementUtils.getPackageName(mElementUtils, typeElement));
                classEntry.setSimpleName(typeElement.getSimpleName().toString());
                classEntry.setClassName(className);
                classEntry.setInnerClass(ElementUtils.isInnerClass(typeElement));
                if (ElementUtils.isInnerClass(typeElement)) {
                    classEntry.setInnerClassName(typeElement.getEnclosingElement().getSimpleName().toString() + "." + typeElement.getSimpleName().toString());
                }
                classEntry.setFieldEntryList(new ArrayList<>());
                classEntry.getFieldEntryList().add(fieldEntry);
                classEntryMap.put(className, classEntry);
            }
        }
        Set<String> keySet = classEntryMap.keySet();
        for (String key : keySet) {
            classEntryList.add(classEntryMap.get(key));
        }
        mMessager.printMessage(Diagnostic.Kind.NOTE, classEntryList.toString());
        return classEntryList;
    }
}
