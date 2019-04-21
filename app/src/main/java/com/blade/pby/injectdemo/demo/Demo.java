package com.blade.pby.injectdemo.demo;

import com.blade.annotation.Provides;

public class Demo {
    @Provides("demoDemoString")
    public Object demoDemoString = "string";
}
