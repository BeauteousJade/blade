package com.blade.processor.entry;

import javax.lang.model.type.TypeMirror;

public class FieldEntry {
    private String fieldName;
    private TypeMirror type;
    private String typeName;
    private String typePackage;
    private String name;
    private boolean supportNull;
    private boolean isPrimitive;
    private boolean deepProvide;

    public String getFieldName() {
        return fieldName;
    }

    public void setFieldName(String filedName) {
        this.fieldName = filedName;
    }

    public TypeMirror getType() {
        return type;
    }

    public void setType(TypeMirror type) {
        this.type = type;
    }

    public String getTypePackage() {
        return typePackage;
    }

    public void setTypePackage(String typePackage) {
        this.typePackage = typePackage;
    }

    public boolean isSupportNull() {
        return supportNull;
    }

    public void setSupportNull(boolean supportNull) {
        this.supportNull = supportNull;
    }

    public boolean isPrimitive() {
        return isPrimitive;
    }

    public void setPrimitive(boolean primitive) {
        isPrimitive = primitive;
    }

    public boolean isDeepProvide() {
        return deepProvide;
    }

    public void setDeepProvide(boolean deepProvide) {
        this.deepProvide = deepProvide;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getTypeName() {
        return typeName;
    }

    public void setTypeName(String typeName) {
        this.typeName = typeName;
    }

    @Override
    public String toString() {
        return "fieldName = " + fieldName + " type = " + type + " typePackage = " + typePackage + " name =" +
                " " + name + "-------";
    }
}
