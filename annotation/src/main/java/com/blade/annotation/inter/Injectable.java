package com.blade.annotation.inter;

public interface Injectable {
    void inject(Object object, Provider provider);
}
