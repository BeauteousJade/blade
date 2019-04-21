package com.blade.pby.injectdemo;

import com.blade.annotation.Provides;
import com.blade.pby.injectdemo.demo.Demo;

public class Demo3 {
    @Provides(value = "demo3String")
    String string = "demo";
    @Provides(value = "strings")
    String[] strings = new String[]{"string1", "string2"};

    @Provides
    String[] string1 = new String[]{"123"};

    @Provides(deepProvides = true)
    Demo demo = new Demo();
}
