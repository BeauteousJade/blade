package com.example.pby.injectdemo;

import com.example.annation.Provides;

public class Context {
    @Provides
    public String string1 = "string1";
    @Provides("string")
    public String string2 = "string2";
}
