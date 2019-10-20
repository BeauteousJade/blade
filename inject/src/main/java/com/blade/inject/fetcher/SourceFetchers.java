package com.blade.inject.fetcher;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.util.HashMap;
import java.util.Map;

public final class SourceFetchers {

    private static Map<Class<?>, Fetcher> sFetchers;
    private static Class<Object> sObjectClazz = Object.class;

    @SuppressWarnings("unchecked")
    @Nullable
    public static <T> Fetcher<T> fetcher(@NonNull Class<T> clazz) {
        if (sFetchers != null && sFetchers.containsKey(clazz)) {
            return sFetchers.get(clazz);
        }
        if (!isObjectClass(clazz)) {
            return fetchFromSourceFetcher(clazz);
        }
        return null;
    }

    private static <T> boolean isObjectClass(Class<T> clazz) {
        return clazz == sObjectClazz;
    }

    @Nullable
    private static <T> Fetcher<T> fetchFromSourceFetcher(Class<T> clazz) {
        Fetcher<T> fetcher = createFetcher(clazz);
        if (sFetchers == null) {
            sFetchers = new HashMap<>();
        }
        sFetchers.put(clazz, fetcher);
        return fetcher;
    }

    @Nullable
    @SuppressWarnings("unchecked")
    private static <T> Fetcher<T> createFetcher(Class<T> clazz) {
        try {
            Class<? extends Fetcher> fetcherClass = (Class<? extends Fetcher>) Class.forName(clazz.getName() +
                    "Fetcher");
            return fetcherClass.newInstance();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
}
