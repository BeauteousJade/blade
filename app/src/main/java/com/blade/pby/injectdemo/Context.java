package com.blade.pby.injectdemo;

import com.blade.annotation.Module;
import com.blade.annotation.Provides;

import java.util.HashMap;
import java.util.Map;

@Module
public class Context {
    @Provides("string")
    public String string = "string";
    @Provides("strings")
    public String[] strings = new String[]{"pby"};
    @Provides("booleanValue")
    public boolean booleanValue = true;
    @Provides("intValue")
    public int intValue = 100;
    @Provides("floatValue")
    public float floatValue = 2.0f;
    @Provides("doubleValue")
    public double doubleValue = 2.1;
    @Provides("charValue")
    public char charValue = 'a';
    @Provides("shortValue")
    public short shortValue = 3;
    @Provides("byteValue")
    public byte byteValue = 1;
    @Provides("longValue")
    public long longValue = 4L;
    @Provides(deepProvides = true, value = "dataMap")
    public Map<String, Object> dataMap = new HashMap<>();
    @Provides(deepProvides = true, value = "context2")
    public Context2 context2 = new Context2();

    {
        dataMap.put("pby1", "pby1");
    }
}
