package com.blade.annotation.inter;

import java.util.Map;

public interface Provider {
    Object find(String key);

    void init(Object object, Map<String, Object> extraMap);

    void clear();
}
