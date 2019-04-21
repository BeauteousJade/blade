package com.blade.pby.injectdemo;

import com.blade.annotation.Provides;

public class Demo {
    @Provides(deepProvides = true, value = "demoString")
    Demo3 string3 = new Demo3();
}
