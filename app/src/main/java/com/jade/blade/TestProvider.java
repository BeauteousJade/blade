package com.jade.blade;

import androidx.annotation.NonNull;

import com.jade.blade.annotation.Provide;
import com.jade.blade.support.DataProvider;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

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

    private Map<String, Object> providerDataMap;

    public TestProvider() {
        init();
    }

    private void init() {
        providerDataMap = new HashMap<>();
        checkMultipleKey("byteValue");
        providerDataMap.put("byteValue", byteValue);
        checkProvider(byteValue);
        checkMultipleKey("byteValue");
        providerDataMap.put("shortValue", shortValue);
        checkProvider(shortValue);
        providerDataMap.put("intValue", intValue);
        providerDataMap.put("longValue", longValue);
        providerDataMap.put("floatValue", floatValue);
        providerDataMap.put("doubleValue", doubleValue);
        providerDataMap.put("booleanValue", booleanValue);
        providerDataMap.put("charValue", charValue);
        providerDataMap.put("string", string);
        checkProvider(string);
    }

    private void checkProvider(Object object) {
        if (object instanceof DataProvider) {
            HashMap<String, Object> hashMap = ((DataProvider) object).provideDataByBlade();
            Set<String> keySet = hashMap.keySet();
            for (String key : keySet) {
                checkMultipleKey(key);
            }
            providerDataMap.putAll(hashMap);
        }
    }


    private void checkMultipleKey(String key) {
        if (providerDataMap.containsKey(key)) {
            throw new IllegalArgumentException("multiple key:" + key);
        }
    }

    @NonNull
    @Override
    public HashMap<String, Object> provideDataByBlade() {
        return (HashMap<String, Object>) providerDataMap;
    }
}
