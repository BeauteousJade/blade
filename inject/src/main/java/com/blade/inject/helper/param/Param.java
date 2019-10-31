package com.blade.inject.helper.param;

import androidx.annotation.Nullable;

public interface Param<T> {

    void init(T t, String name);

    @Nullable
    <U> U getParam(String name);
}
