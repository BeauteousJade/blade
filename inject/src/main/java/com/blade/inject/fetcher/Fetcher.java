package com.blade.inject.fetcher;

public interface Fetcher<T> {

    void init(T t);

    <U> U fetch(String name);
}
