package com.blade.inject;

import com.blade.annotation.inter.Provider;

import java.util.HashMap;
import java.util.Map;

public class EmptyProviderImpl implements Provider {

    private Map<String, Object> pathMap;

    public EmptyProviderImpl(Map<String, ?> extraMap) {
        pathMap = new HashMap<>();
        if (extraMap != null && !extraMap.isEmpty()) {
            pathMap.putAll(extraMap);
        }
    }

    @Override
    public Object find(String key) {
        return pathMap.get(key);
    }
}
