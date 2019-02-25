package com.example.inject;

import com.example.annation.Module;
import com.example.annation.Provides;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

public class Blade {

    public static void inject(Object target, Object source) {
        try {
            final String className = target.getClass().getName() + "_Inject";
            final Object object = Class.forName(className).newInstance();
            final Class<?> clazz = object.getClass();
            final Module module = target.getClass().getAnnotation(Module.class);
            if (module != null) {
                final Class<?> moduleClazz = module.value();
                final Object sourceTmp = findSource(moduleClazz, source.getClass(), source);
                if (sourceTmp != null) {
                    final Method method = clazz.getMethod("inject", target.getClass(), sourceTmp.getClass());
                    method.invoke(object, target, sourceTmp);
                }
            }
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

    /**
     * 从根Context中找到与在{@link Module}注解里面配置相匹配的Context对象
     *
     * @param moduleClazz
     * @param sourceClazz
     * @param source
     * @return
     */
    private static Object findSource(Class<?> moduleClazz, Class<?> sourceClazz, Object source) {
        if (moduleClazz == sourceClazz) {
            return source;
        }
        final Field[] fields = sourceClazz.getDeclaredFields();
        for (Field field : fields) {
            final Provides provides = field.getAnnotation(Provides.class);
            if (provides != null && provides.deepProvides()) {
                try {
                    field.setAccessible(true);
                    final Object obj = field.get(source);
                    if (obj != null) {
                        if (obj.getClass() == moduleClazz) {
                            return obj;
                        }
                        return findSource(moduleClazz, obj.getClass(), obj);
                    }
                } catch (IllegalAccessException e) {
                    e.printStackTrace();
                }
            }
        }
        return null;
    }
}
