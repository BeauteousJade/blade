package com.blade.inject.helper.param;

import androidx.annotation.Nullable;

import java.lang.ref.WeakReference;

public class FieldParam<T> implements Param<T> {

    private WeakReference<T> mParam;
    private String mName;

    @Override
    public void init(T param, String name) {
        mParam = new WeakReference<>(param);
        mName = name;
    }

    @SuppressWarnings("unchecked")
    @Nullable
    @Override
    public <U> U getParam(String name) {
        if (mName.equals(name)) {
            final T t = mParam.get();
            if (t == null) {
                return null;
            }
            return (U) t;
        }
        return null;
    }
}
