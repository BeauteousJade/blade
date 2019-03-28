package com.example.annation.inter;

import java.util.Map;

public interface Provider {

    void put(Map<String, Object> map);

    Object find(String key);
}
