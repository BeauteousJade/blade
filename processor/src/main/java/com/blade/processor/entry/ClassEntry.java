package com.blade.processor.entry;

import java.util.List;

public class ClassEntry {

    // 简易名称,真正意义上的类名
    private String simpleName;
    // 全限名称，包括包名+类名，包名+外部类名+类名
    private String className;
    // 内部类名，包括外部类名+内部类名
    // 如果不是内部类，此字段为null
    private String innerClassName;
    private boolean isInnerClass;
    private String packageName;
    private List<FieldEntry> fieldEntryList;

    public String getSimpleName() {
        return simpleName;
    }

    public void setSimpleName(String simpleName) {
        this.simpleName = simpleName;
    }

    public String getClassName() {
        return className;
    }

    public void setClassName(String className) {
        this.className = className;
    }

    public String getPackageName() {
        return packageName;
    }

    public void setPackageName(String packageName) {
        this.packageName = packageName;
    }

    public String getInnerClassName() {
        return innerClassName;
    }

    public void setInnerClassName(String innerClassName) {
        this.innerClassName = innerClassName;
    }

    public boolean isInnerClass() {
        return isInnerClass;
    }

    public void setInnerClass(boolean innerClass) {
        isInnerClass = innerClass;
    }

    public List<FieldEntry> getFieldEntryList() {
        return fieldEntryList;
    }

    public void setFieldEntryList(List<FieldEntry> fieldEntryList) {
        this.fieldEntryList = fieldEntryList;
    }

    @Override
    public String toString() {
        return "simpleName = " + simpleName + " className = " + className + " packageName = " + packageName + " " +
                "fieldList = " + fieldEntryList + "||||||";
    }
}
