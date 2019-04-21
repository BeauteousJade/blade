package com.example.pby.injectdemo;

import com.example.annotation.Provides;

public class Demo {
    @Provides(deepProvides = true, value = "demoString")
    Demo3 string3 = new Demo3();
}
