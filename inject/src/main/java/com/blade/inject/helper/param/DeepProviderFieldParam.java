package com.blade.inject.helper.param;

import androidx.annotation.Nullable;

import com.blade.inject.fetcher.FetcherHolder;

public class DeepProviderFieldParam<T> implements Param<T> {

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
        if (name.equals(mName)) {
            return (U) mParam;
        }
        return FetcherHolder.fetch(name, mParam);
    }
}
