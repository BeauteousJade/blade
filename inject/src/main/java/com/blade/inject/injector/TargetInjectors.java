package com.blade.inject.injector;

import java.util.HashMap;
import java.util.Map;

public final class TargetInjectors {

    private static Map<String, Injector> sInjectors;

    public static <T> Injector<T> injector(T target) {
        return getInjector(target);
    }

    @SuppressWarnings("unchecked")
    private static <T> Injector<T> getInjector(Object target) {
        String className = target.getClass().getName();
        if (sInjectors != null && sInjectors.containsKey(className)) {
            return sInjectors.get(className);
        }
        Injector<T> injector = createInjector(className);
        if (injector != null) {
            if (sInjectors == null) {
                sInjectors = new HashMap<>();
            }
            if (!sInjectors.containsKey(className)) {
                sInjectors.put(className, injector);
            }
            return injector;
        }
        throw new IllegalArgumentException();
    }

    @SuppressWarnings("unchecked")
    private static <T> Injector<T> createInjector(String className) {
        try {
            Class<? extends Injector<T>> injectorClass = (Class<? extends Injector<T>>) Class.forName(className + "Injector");
            return injectorClass.newInstance();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
}
