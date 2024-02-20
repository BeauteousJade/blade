package com.blade.inject.helper.param;

import androidx.annotation.Nullable;

import java.lang.ref.WeakReference;

public class FieldParam<T> implements Param<T> {

    @Nullable
    private WeakReference<T> mParamRef;
    @Nullable
    private T mParam;
    private String mName;

    @Override
    public void init(T param, String name) {
        boolean isPrimitive = param.getClass().isPrimitive();
        if (isPrimitive || param instanceof String) {
            mParam = param;
            mParamRef = null;
        } else {
            mParam = null;
            mParamRef = new WeakReference<>(param);
        }
        mName = name;
    }

    @SuppressWarnings("unchecked")
    @Nullable
    @Override
    public <U> U getParam(String name) {
        if (mName.equals(name)) {
            if (mParam != null) {
                return (U) mParam;
            } else if (mParamRef != null) {
                final T t = mParamRef.get();
                if (t == null) {
                    return null;
                }
                return (U) t;
            }
        }
        return null;
    }
}
