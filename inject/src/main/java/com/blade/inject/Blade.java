package com.blade.inject;

import com.blade.inject.injector.Injector;
import com.blade.inject.injector.TargetInjectors;

import java.util.Map;

public class Blade {

    public static <T> void inject(T target, Map<String, Object> extraMap) {
        inject(target, null, extraMap);
    }

    public static <T> void inject(T target, Object source) {
        inject(target, source, null);
    }

    public static <T> void inject(T target, Object source, Map<String, Object> extraMap) {
        Injector<T> injector = TargetInjectors.injector(target);
        if (injector != null) {
            injector.inject(target, source, extraMap);
        }
    }
}
