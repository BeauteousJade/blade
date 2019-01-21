package com.example.inject;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

public class Blade {

    public static void inject(Object target, Object source) {
        String className = target.getClass().getName() + "_Inject";
        try {
            Object object = Class.forName(className).newInstance();
            Class<?> clazz = object.getClass();
            Method method = clazz.getMethod("inject", target.getClass(), source.getClass());
            method.invoke(object, target, source);
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
