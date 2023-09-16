package com.jade.blade;

import androidx.annotation.NonNull;

import com.jade.blade.annotation.Inject;
import com.jade.blade.support.DataFetcher;

import java.util.Map;

public class TestInjector implements DataFetcher {

    @Inject("test")
    private int a;

    @Inject("1234")
    private double b;

    @Inject("12345")
    private String c;

    private boolean d;

    private char e;

    @Override
    public void setupDataByBlade(@NonNull Map<String, ?> data) {
        a = (int) requireNonNull(data.get("test"), "a", "test");
        b = (double) requireNonNull(data.get("test"), "a", "test");
        d = (boolean) requireNonNull(data.get("test"), "a", "test");
        Object o = data.get("1234");
        if (o != null) {
            b = (double) o;
        }
        Object o2 = data.get("1234");
        if (o != null) {
            b = (double) o2;
        }
        Object o1 = data.get("1234");
        if (o != null) {
            b = (double) o1;
        }
    }

    private Object requireNonNull(Object obj, String fieldName, String key) {
        if (obj == null) {
            throw new NullPointerException(fieldName + " is null, key:" + key);
        }
        return obj;
    }
}
