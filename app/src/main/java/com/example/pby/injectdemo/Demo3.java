package com.example.pby.injectdemo;

import com.example.annation.Provides;
import com.example.pby.injectdemo.demo.Demo;

public class Demo3 {
    @Provides(value = "demo3String")
    String string = "demo";
    @Provides(value = "strings")
    String[] strings = new String[]{"string1", "string2"};

    @Provides
    String[] string1;

    @Provides(deepProvides = true)
    Demo demo;
}
