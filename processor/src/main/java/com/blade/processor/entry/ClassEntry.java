package com.blade.processor.entry;

import java.util.List;

public class ClassEntry {

    private String simpleName;
    private String className;
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

    public List<FieldEntry> getFieldEntryList() {
        return fieldEntryList;
    }

    public void setFieldEntryList(List<FieldEntry> fieldEntryList) {
        this.fieldEntryList = fieldEntryList;
    }

    @Override
    public String toString() {
        return "simpleName = " + simpleName + " className = " + className + " packageName = " + packageName + " fieldList = " + fieldEntryList + "||||||";
    }
}
