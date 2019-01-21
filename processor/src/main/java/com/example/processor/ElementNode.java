package com.example.processor;

import com.example.processor.util.StringUtils;

import java.lang.annotation.Annotation;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

import javax.lang.model.element.AnnotationMirror;

public class ElementNode {

    private String id;
    private String simpleName;
    private String type;
    private String packageName;
    // 当前Element的annotationMirror集合，只有被Inject注解标记的根节点不为空，其他情况下均为空
    private List<? extends AnnotationMirror> annotationMirrorList;
    private Map<Class<? extends Annotation>, Annotation> annotationMap;
    private Map<String, ElementNode> nextMap;

    public ElementNode(String id, String simpleName, String type, String packageName) {
        this.id = id;
        this.simpleName = simpleName;
        this.type = type;
        this.packageName = packageName;
    }

    public String getPackageName() {
        return packageName;
    }

    public void setPackageName(String packageName) {
        this.packageName = packageName;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getSimpleName() {
        return simpleName;
    }

    public void setSimpleName(String simpleName) {
        this.simpleName = simpleName;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public void setNextMap(Map<String, ElementNode> nextMap) {
        this.nextMap = nextMap;
    }

    public Map<String, ElementNode> getNextMap() {
        return nextMap;
    }

    public List<? extends AnnotationMirror> getAnnotationMirrorList() {
        return annotationMirrorList;
    }

    public void setAnnotationMirrorList(List<? extends AnnotationMirror> annotationMirrorList) {
        this.annotationMirrorList = annotationMirrorList;
    }

    public void setAnnotationMap(Map<Class<? extends Annotation>, Annotation> annotationMap) {
        this.annotationMap = annotationMap;
    }

    public Map<Class<? extends Annotation>, Annotation> getAnnotationMap() {
        return annotationMap;
    }

    public void addAnnotation(Class<? extends Annotation> key, Annotation annotation) {
        if (annotationMap == null) {
            annotationMap = new HashMap<>();
        }
        annotationMap.put(key, annotation);
    }

    @SuppressWarnings("unchecked")
    public <T extends Annotation> T getAnnotation(Class<T> key) {
        if (annotationMap != null) {
            return (T) annotationMap.get(key);
        }
        return null;
    }

    public void addChild(ElementNode elementNode) {
        if (nextMap == null) {
            nextMap = new HashMap<>();
        }
        if (elementNode != null) {
            nextMap.put(elementNode.id, elementNode);
        }
    }

    public void addChild(String id, ElementNode elementNode) {
        if (nextMap == null) {
            nextMap = new HashMap<>();
        }
        if (elementNode != null) {
            nextMap.put(id, elementNode);
        }
    }

    public void removeChild(ElementNode elementNode) {
        if (nextMap != null) {
            nextMap.remove(elementNode.id);
        }
    }

    public ElementNode getChild(String id) {
        if (nextMap != null) {
            return nextMap.get(id);
        }
        return null;
    }

    public String lookUp(String id) {
        if (Objects.equals(id, this.id)) {
            return this.simpleName;
        }
        return lookUp(this, id, "");
    }

    private String lookUp(ElementNode node, String id, String tagName) {
        final Map<String, ElementNode> nodeMap = node.getNextMap();
        if (nodeMap == null || nodeMap.size() == 0) {
            return null;
        }
        final Set<String> keySet = nodeMap.keySet();
        for (String key : keySet) {
            final ElementNode elementNode = nodeMap.get(key);
            final String separation = StringUtils.isEmpty(tagName) ? "" : ".";
            if (Objects.equals(id, elementNode.id)) {
                return tagName + separation + elementNode.getSimpleName();
            } else {
                final String sourcePath = lookUp(elementNode, id, tagName + separation + elementNode.getSimpleName());
                if (sourcePath != null) {
                    return sourcePath;
                }
            }
        }
        return null;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null) {
            return false;
        }
        if (this.getClass() != o.getClass()) {
            return false;
        }
        return Objects.equals(id, ((ElementNode) o).id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @Override
    public String toString() {
        return "[id = " + id + " type = " + type + " simpleName = " + simpleName + " nextMap = " + nextMap+ "]";
    }
}
