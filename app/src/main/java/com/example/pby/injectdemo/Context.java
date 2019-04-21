package com.example.pby.injectdemo;

import com.example.annotation.Provides;

public class Context {
    @Provides(deepProvides = true, value = "contextString1")
    public Demo string1 = new Demo();

    @Provides(value = "contextString2")
    public String string2;

    @Provides(value = "int")
    public int code = 2;
}
