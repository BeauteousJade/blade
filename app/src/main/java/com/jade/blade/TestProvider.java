package com.jade.blade;

import androidx.annotation.NonNull;

import com.jade.blade.annotation.Provide;
import com.jade.blade.support.DataProvider;
import com.jade.blade.utils.BladeUtils;

import java.util.HashMap;

public class TestProvider implements DataProvider {
    @Provide
    private byte byteValue = 10;
    @Provide
    private short shortValue = 20;
    @Provide
    private int intValue = 30;
    @Provide
    private long longValue = 40;
    @Provide
    private float floatValue = 40;
    @Provide
    private double doubleValue = 50;
    @Provide
    private boolean booleanValue = true;
    @Provide
    private char charValue = 'a';
    @Provide
    private String string = "a";
    @Provide
    private Object object = null;

    private HashMap<String, Object> providerDataMap;

    public TestProvider() {
        init();
    }

    private void init() {
        providerDataMap = new HashMap<>();
        BladeUtils instance = BladeUtils.INSTANCE;
        instance.checkProvider(providerDataMap, byteValue, "byteValue");
        providerDataMap.put("byteValue", byteValue);
        instance.checkProvider(providerDataMap, shortValue, "shortValue");
        providerDataMap.put("shortValue", shortValue);
    }


    @NonNull
    @Override
    public HashMap<String, Object> provideDataByBlade() {
        return (HashMap<String, Object>) providerDataMap;
    }
}
