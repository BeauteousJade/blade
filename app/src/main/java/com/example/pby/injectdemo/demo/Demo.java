package com.example.pby.injectdemo.demo;

import com.example.annation.Module;
import com.example.annation.Inject;
import com.example.pby.injectdemo.Context;

@Module(Context.class)
public class Demo {
    @Inject("string")
    String string;
}
