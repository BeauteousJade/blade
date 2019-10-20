package com.blade.inject.injector;

public interface Injector<T> {

    void inject(T target, Object... source);

    void clean(T target);
}
