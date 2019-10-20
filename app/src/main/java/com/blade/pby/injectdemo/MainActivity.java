package com.blade.pby.injectdemo;

import android.os.Bundle;
import android.util.Log;

import androidx.appcompat.app.AppCompatActivity;

import com.blade.annotation.Inject;
import com.blade.inject.Blade;

import java.util.HashMap;
import java.util.Map;


public class MainActivity extends AppCompatActivity {

    @Inject("string")
    public String string;
    @Inject("strings")
    public String[] strings;
    @Inject("booleanValue")
    public boolean booleanValue;
    @Inject("intValue")
    public int intValue;
    @Inject("floatValue")
    public float floatValue;
    @Inject("doubleValue")
    public double doubleValue;
    @Inject("charValue")
    public char charValue;
    @Inject("shortValue")
    public short shortValue;
    @Inject("byteValue")
    public byte byteValue;
    @Inject("longValue")
    public long longValue;
    @Inject("dataMap")
    public Map<String, Object> dataMap;
    @Inject("context2")
    public Context2 context2;


    @Inject("Context2string")
    public String context2String;
    @Inject("Context2strings")
    public String[] context2Strings;
    @Inject("Context2booleanValue")
    public boolean context2BooleanValue;
    @Inject("context2IntValue")
    public int context2IntValue;
    @Inject("context2FloatValue")
    public float context2FloatValue;
    @Inject("context2DoubleValue")
    public double context2DoubleValue;
    @Inject("context2CharValue")
    public char context2CharValue;
    @Inject("context2ShortValue")
    public short context2ShortValue;
    @Inject("context2ByteValue")
    public byte context2ByteValue;
    @Inject("context2LongValue")
    public long context2LongValue;
    @Inject("context2DataMap")
    public Map<String, Object> context2DataMap;

    @Inject("pby1")
    String pby1;
    @Inject("pby2")
    String pby2;
    @Inject("pby3")
    String pby3;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        long time1 = System.currentTimeMillis();
        Context context = new Context();
        HashMap<String, Object> map = new HashMap<>();
        Log.i("pby123", "d1 = " + (System.currentTimeMillis() - time1));
        map.put("pby3", "pby3");
        long time2 = System.currentTimeMillis();
        Blade.inject(this, context, map);
        Log.i("pby123", "d2 = " + (System.currentTimeMillis() - time2));
    }
}
