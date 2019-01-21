package com.example.pby.injectdemo;

import com.example.annation.Provides;

public class Demo {
    @Provides(deepProvides = true, value = "demoString")
    Demo3 string3;
}
