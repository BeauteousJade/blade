package com.example.inject;

import com.example.annation.inter.Provider;

import java.lang.reflect.InvocationTargetException;
import java.util.Map;

public class Blade {

    public static void inject(Object target, Object source) {
        inject(target, source, null);
    }

    public static void inject(Object target, Object source, Map<String, ?> extraMap) {
        try {
            final Object targetObject = Class.forName(target.getClass().getName() + "_Inject").newInstance();
            final Provider sourceObject = (Provider) Class.forName(source.getClass().getName() + "ProviderImpl").newInstance();
            if (extraMap != null && !extraMap.isEmpty()) {
                sourceObject.getClass().getMethod("put", Map.class).invoke(sourceObject, extraMap);
            }
            sourceObject.getClass().getMethod("init", source.getClass()).invoke(sourceObject, source);
            targetObject.getClass().getMethod("inject", target.getClass(), Provider.class).invoke(targetObject, target, sourceObject);
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
