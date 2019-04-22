package com.blade.inject;

import com.blade.annotation.inter.Provider;

import java.util.HashMap;
import java.util.Map;

public class EmptyProviderImpl implements Provider {

    private Map<String, Object> pathMap;

    public EmptyProviderImpl() {
        pathMap = new HashMap<>();
    }

    @Override
    public Object find(String key) {
        return pathMap.get(key);
    }

    @Override
    public void init(Object object, Map<String, Object> extraMap) {
        clear();
        if (extraMap != null && !extraMap.isEmpty()) {
            pathMap.putAll(extraMap);
        }
    }

    @Override
    public void clear() {
        pathMap.clear();
    }
}
