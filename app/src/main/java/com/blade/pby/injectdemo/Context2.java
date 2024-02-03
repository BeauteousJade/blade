package com.blade.pby.injectdemo;

import com.blade.annotation.Module;
import com.blade.annotation.Provides;

import java.util.HashMap;
import java.util.Map;

@Module
public class Context2 {
    @Provides("Context2string")
    public String context2String = "string";
    @Provides("Context2strings")
    public String[] context2Strings = new String[]{"pby"};
    @Provides("Context2booleanValue")
    public boolean context2BooleanValue = true;
    @Provides("context2IntValue")
    public int context2IntValue = 1100;
    @Provides("context2FloatValue")
    public float context2FloatValue = 1002.0f;
    @Provides("context2DoubleValue")
    public double context2DoubleValue = 1002.1;
    @Provides("context2CharValue")
    public char context2CharValue = 'b';
    @Provides("context2ShortValue")
    public short context2ShortValue = 1003;
    @Provides("context2ByteValue")
    public byte context2ByteValue = 11;
    @Provides("context2LongValue")
    public long context2LongValue = 1004L;
    @Provides(deepProvides = true, value = "context2DataMap")
    public Map<String, Object> context2DataMap = new HashMap<>();

    {
        context2DataMap.put("pby2", "pby2");
    }
}
