package com.example.inject;

import com.example.annation.inter.Provider;

import java.util.HashMap;
import java.util.Map;

public class EmptyProviderImpl implements Provider {

    private Map<String, Object> provideMap;

    public EmptyProviderImpl() {
        provideMap = new HashMap<>();
    }

    @Override
    public void put(Map<String, Object> map) {
        provideMap.putAll(map);
    }

    public void init() {

    }


    @Override
    public Object find(String key) {
        return null;
    }
}
