package com.blade.pby.injectdemo;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;

import com.blade.annotation.Inject;
import com.blade.annotation.Module;
import com.blade.annotation.Provides;
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
        View view = findViewById(R.id.button);
        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, MainActivity.class);
                startActivity(intent);
            }
        });


        long time1 = System.currentTimeMillis();
        Context context = new Context();
        context.mActivity = this;
        HashMap<String, Object> map = new HashMap<>();
        Log.i("pby123", "d1 = " + (System.currentTimeMillis() - time1));
        map.put("pby3", "pby3");
        long time2 = System.currentTimeMillis();
        Target2 target = new Target2();
        doInject(target, target.getClass(), context, map);
        Log.i("pby123", "d2 = " + (System.currentTimeMillis() - time2));
    }

    private void doInject(Target target, Class<?> clazz, Context context, HashMap<String, Object> map) {
        if (clazz == Target.class) {
            Blade.inject(target, clazz.getName(), context, map);
        } else {
            doInject(target, target.getClass().getSuperclass(), context, map);
            Blade.inject(target, clazz.getName(), context, map);
        }
    }

    public static class Target {
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

        @Inject("activity")
        Activity mActivity;
    }

    public class Target2 extends Target {
        @Inject("pby3")
        String pby3_2;
    }

    @Module
    public static class Context {
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

        @Provides("activity")
        public Activity mActivity;

        {
            dataMap.put("pby1", "pby1");
        }
    }

}
