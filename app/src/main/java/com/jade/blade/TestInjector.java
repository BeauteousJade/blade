package com.jade.blade;

import androidx.annotation.NonNull;

import com.jade.blade.annotation.Inject;
import com.jade.blade.support.DataFetcher;
import com.jade.blade.utils.BladeUtils;

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
        a = (int) BladeUtils.INSTANCE.requireNonNull(data.get("test"), "a", "test");
        b = (double) BladeUtils.INSTANCE.requireNonNull(data.get("test"), "a", "test");
        d = (boolean) BladeUtils.INSTANCE.requireNonNull(data.get("test"), "a", "test");
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
}
