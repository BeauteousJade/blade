package com.blade.inject.helper.param;

import androidx.annotation.Nullable;

public class FieldParam<T> implements Param<T> {

    private T mParam;
    private String mName;

    @Override
    public void init(T param, String name) {
        mParam = param;
        mName = name;
    }

    @SuppressWarnings("unchecked")
    @Nullable
    @Override
    public <U> U getParam(String name) {
        if (mName.equals(name)) {
            return (U) mParam;
        }
        return null;
    }
}
