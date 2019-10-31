package com.blade.inject.fetcher;

import androidx.annotation.Nullable;

import java.util.HashMap;
import java.util.Map;

public final class FetcherHolder {


    @SuppressWarnings("unchecked")
    @Nullable
    public static <T> T fetch(String name, Object... sources) {
        if (sources == null || sources.length == 0) {
            return null;
        }
        for (Object source : sources) {
            if (source instanceof Map && ((Map) source).containsKey(name)) {
                return (T) ((Map) source).get(name);
            } else if (!(source instanceof Map)) {
                Fetcher fetcher = getFetcher(source.getClass());
                if (fetcher != null) {
                    fetcher.init(source);
                    T t = (T) fetcher.fetch(name);
                    if (t != null) {
                        return t;
                    }
                }
            }
        }
        return null;
    }

    @SuppressWarnings("unchecked")
    @Nullable
    private static <T> Fetcher<T> getFetcher(Class<T> clazz) {
        return SourceFetchers.fetcher(clazz);
    }
}