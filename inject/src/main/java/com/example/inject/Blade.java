package com.example.inject;

import com.example.annotation.inter.Provider;

import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;
import java.util.Map;

public class Blade {

    private static final Map<String, Object> OBJECT_MAP = new HashMap<>();

    public static void inject(Object target, Map<String, ?> extraMap) {
        inject(target, new EmptyProviderImpl(extraMap), extraMap);
    }

    public static void inject(Object target, Object source) {
        inject(target, source, null);
    }

    public static void inject(Object target, Object source, Map<String, ?> extraMap) {
        try {
            final String targetClassName = target.getClass().getName() + "_Inject";
            final String sourceClassName = source.getClass().getName();
            Object targetObject = OBJECT_MAP.get(targetClassName);
            if (targetObject == null) {
                targetObject = Class.forName(targetClassName).newInstance();
                OBJECT_MAP.put(targetClassName, targetObject);
            }
            Provider sourceObject = (Provider) OBJECT_MAP.get(sourceClassName);
            if (sourceObject == null) {
                sourceObject = source instanceof Provider ? (Provider) source : (Provider) Class.forName(sourceClassName + "ProviderImpl")
                        .getConstructor(source.getClass(), Map.class)
                        .newInstance(source, extraMap);
                OBJECT_MAP.put(sourceClassName, sourceObject);
            }
            targetObject.getClass()
                    .getMethod("inject", target.getClass(), Provider.class)
                    .invoke(targetObject, target, sourceObject);
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (InstantiationException e) {
            e.printStackTrace();
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        }
    }
}
