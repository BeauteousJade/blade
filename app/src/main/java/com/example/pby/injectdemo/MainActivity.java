package com.example.pby.injectdemo;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;

import com.example.annation.Module;
import com.example.annation.Inject;
import com.example.inject.Injector;


@Module(Context.class)
public class MainActivity extends AppCompatActivity {

    @Inject
    String string1;
    @Inject("string")
    String string2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Injector.inject(this, new Context());
        Log.i("pby123", "string1 = " + string1 + " string2 = " + string2);
    }
}
