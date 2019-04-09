package com.example.inject;

import com.example.annation.inter.Provider;

import java.lang.reflect.InvocationTargetException;
import java.util.Map;

public class Blade {

    public static void inject(Object target, Object source) {
        inject(target, source, null);
    }

    private static void inject(Object target, Map<String, ?> extraMap) {
        inject(target, new EmptyProviderImpl(), extraMap);
    }

    public static void inject(Object target, Object source, Map<String, ?> extraMap) {
        try {
            Object targetObject = Class.forName(target.getClass().getName() + "_Inject").newInstance();
            Provider sourceObject = (Provider) Class.forName(source.getClass().getName() + "ProviderImpl")
                    .getConstructor(source.getClass(), Map.class)
                    .newInstance(source, extraMap);
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
