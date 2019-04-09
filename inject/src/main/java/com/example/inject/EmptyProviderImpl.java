package com.example.inject;

import com.example.annation.inter.Provider;

public class EmptyProviderImpl implements Provider {
    @Override
    public Object find(String key) {
        return null;
    }
}
