package com.example.pby.injectdemo;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import com.example.annation.Inject;
import com.example.annation.Module;
import com.example.inject.Blade;


@Module(Context.class)
public class MainActivity extends AppCompatActivity {

    @Inject("demo3String")
    String string1;
    @Inject("strings")
    String[] strings;
    @Inject
    String[] strings1;
    @Inject("demoDemoString")
    String demoDemoString;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Blade.inject(this, new Context());
        Log.i("pby123", "string1 = " + string1 + " string2 = " + strings);
    }
}
