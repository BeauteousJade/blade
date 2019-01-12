package com.example.pby.injectdemo;

import com.example.annation.Module;
import com.example.annation.Inject;

@Module(Context.class)
public class Demo {
    @Inject
    String string3;
}
