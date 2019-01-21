package com.example.pby.injectdemo.demo;

import com.example.annation.Provides;

public class Demo {
    @Provides("demoDemoString")
    public Object demoDemoString = "string";
}
