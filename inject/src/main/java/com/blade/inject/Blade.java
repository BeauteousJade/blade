package com.blade.inject;

import com.blade.annotation.inter.Injectable;
import com.blade.annotation.inter.Provider;

import java.util.HashMap;
import java.util.Map;

public class Blade {

    private static final Map<String, Object> OBJECT_MAP = new HashMap<>();

    public static void inject(Object target, Map<String, Object> extraMap) {
        inject(target, new EmptyProviderImpl(), extraMap);
    }

    public static void inject(Object target, Object source) {
        inject(target, source, null);
    }

    public static void inject(Object target, Object source, Map<String, Object> extraMap) {
        try {
            final String targetClassName = target.getClass().getName() + "_Inject";
            final String sourceClassName = source.getClass().getName();
            Injectable targetObject = (Injectable) OBJECT_MAP.get(targetClassName);
            if (targetObject == null) {
                targetObject = (Injectable) Class.forName(targetClassName).newInstance();
                OBJECT_MAP.put(targetClassName, targetObject);
            }
            Provider sourceObject = (Provider) OBJECT_MAP.get(sourceClassName);
            if (sourceObject == null) {
                sourceObject = source instanceof Provider ? (Provider) source : (Provider) Class.forName(sourceClassName + "ProviderImpl").newInstance();
                OBJECT_MAP.put(sourceClassName, sourceObject);
            }
            sourceObject.init(source, extraMap);
            targetObject.inject(target, sourceObject);
            sourceObject.clear();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (InstantiationException e) {
            e.printStackTrace();
        }
    }
}
