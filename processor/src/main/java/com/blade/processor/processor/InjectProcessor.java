package com.blade.processor.processor;

import com.blade.annotation.Inject;
import com.blade.processor.Constant;
import com.blade.processor.entry.ClassEntry;
import com.blade.processor.entry.FieldEntry;
import com.blade.processor.util.ElementUtils;
import com.blade.processor.util.StringUtils;
import com.blade.processor.writer.InjectorWriter;
import com.google.auto.service.AutoService;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.annotation.processing.Processor;
import javax.annotation.processing.RoundEnvironment;
import javax.lang.model.element.AnnotationMirror;
import javax.lang.model.element.Element;
import javax.lang.model.element.TypeElement;
import javax.lang.model.type.TypeMirror;

@AutoService(Processor.class)
public class InjectProcessor extends BaseProcessor {

    @Override
    public Set<String> getSupportedAnnotationTypes() {
        Set<String> set = new HashSet<>();
        set.add(Inject.class.getCanonicalName());
        return set;
    }

    @Override
    public boolean process(Set<? extends TypeElement> set, RoundEnvironment roundEnvironment) {
        List<ClassEntry> classEntryList = new ArrayList<>();
        for (TypeElement typeElement : set) {
            Set<? extends Element> fieldElements = roundEnvironment.getElementsAnnotatedWith(typeElement);
            classEntryList.addAll(processInject(fieldElements));
        }
        InjectorWriter injectorWriter = new InjectorWriter(mFiler);
        injectorWriter.writer(classEntryList);
        return true;
    }

    private List<ClassEntry> processInject(Set<? extends Element> fieldElements) {
        Map<String, ClassEntry> classEntryMap = new HashMap<>();
        List<ClassEntry> classEntryList = new ArrayList<>();
        for (Element element : fieldElements) {
            TypeElement typeElement = (TypeElement) element.getEnclosingElement();
            FieldEntry fieldEntry = new FieldEntry();
            fieldEntry.setFieldName(element.getSimpleName().toString());
            TypeMirror type = element.asType();
            fieldEntry.setType(type);
            Inject provides = element.getAnnotation(Inject.class);
            fieldEntry.setName(StringUtils.isEmpty(provides.value()) ? type.toString() : provides.value());
            fieldEntry.setSupportNull(containsNullableAnnotation(element));
            fieldEntry.setPrimitive(type.getKind().isPrimitive());
            fieldEntry.setTypeName(ElementUtils.getSimpleType(type.toString()));
            String className = typeElement.getQualifiedName().toString();
            if (classEntryMap.containsKey(className)) {
                ClassEntry classEntry = classEntryMap.get(className);
                classEntry.getFieldEntryList().add(fieldEntry);
            } else {
                ClassEntry classEntry = new ClassEntry();
                classEntry.setPackageName(ElementUtils.getPackageName(mElementUtils, typeElement));
                classEntry.setSimpleName(typeElement.getSimpleName().toString());
                classEntry.setInnerClass(ElementUtils.isInnerClass(typeElement));
                if (ElementUtils.isInnerClass(typeElement)) {
                    classEntry.setInnerClassName(typeElement.getEnclosingElement().getSimpleName().toString() + "." + typeElement.getSimpleName().toString());
                }
                classEntry.setClassName(className);
                classEntry.setFieldEntryList(new ArrayList<>());
                classEntry.getFieldEntryList().add(fieldEntry);
                classEntryMap.put(className, classEntry);
            }
        }
        Set<String> keySet = classEntryMap.keySet();
        for (String key : keySet) {
            classEntryList.add(classEntryMap.get(key));
        }
        return classEntryList;
    }

    private boolean containsNullableAnnotation(Element element) {
        List<? extends AnnotationMirror> annotationMirrors = element.getAnnotationMirrors();
        for (AnnotationMirror annotationMirror : annotationMirrors) {
            if (Constant.NULLABLE_QUALIFIED_NAME.equals(annotationMirror.getAnnotationType().toString())) {
                return true;
            }
        }
        return false;
    }
}
