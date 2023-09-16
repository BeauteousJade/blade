package com.jade.blade

import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import com.jade.blade.annotation.Inject
import com.jade.blade.annotation.Injector

@Injector
class MainActivity : AppCompatActivity() {

    @Inject("byteValue1", isNullable = true)
    private var byteValue: Byte = 0

    @Inject("shortValue1", isNullable = true)
    private var shortValue: Short = 0

    @Inject(isNullable = true)
    private var intValue = 0

    @Inject
    private var longValue: Long = 0

    @Inject
    private var floatValue = 0f

    @Inject
    private var doubleValue = 0.0

    @Inject
    private var booleanValue = false

    @Inject
    private var charValue = '0'

    @Inject(isNullable = true)
    private var string = ""


    @Inject
    private val object1 = Object()

    @Inject
    private val object2 = Object()

    @Inject
    private val object3 = Object()

    @Inject
    private val object4 = Object()

    @Inject
    private val object5 = Object()

    @Inject
    private val object6 = Object()

    @Inject
    private val object7 = Object()

    @Inject
    private val object8 = Object()

    @Inject
    private val object9 = Object()

    @Inject
    private val object10 = Object()

    @Inject
    private val object11 = Object()

    @Inject
    private val object12 = Object()

    @Inject
    private val object13 = Object()

    @Inject
    private val object14 = Object()

    @Inject
    private val object15 = Object()

    @Inject
    private val object16 = Object()

    @Inject
    private val object17 = Object()

    @Inject
    private val object18 = Object()

    @Inject
    private val object19 = Object()

    @Inject
    private val object20 = Object()

    @Inject
    private val object21 = Object()

    @Inject
    private val object22 = Object()

    @Inject
    private val object23 = Object()

    @Inject
    private val object24 = Object()

    @Inject
    private val object25 = Object()

    @Inject
    private val object26 = Object()

    @Inject
    private val object27 = Object()

    @Inject
    private val object28 = Object()

    @Inject
    private val object29 = Object()

    @Inject
    private val object30 = Object()

    @Inject
    private val object31 = Object()

    @Inject
    private val object32 = Object()

    @Inject
    private val object33 = Object()

    @Inject
    private val object34 = Object()

    @Inject
    private val object35 = Object()

    @Inject
    private val object36 = Object()

    @Inject
    private val object37 = Object()

    @Inject
    private val object38 = Object()

    @Inject
    private val object39 = Object()

    @Inject
    private val object40 = Object()

    @Inject
    private val object41 = Object()

    @Inject
    private val object42 = Object()

    @Inject
    private val object43 = Object()

    @Inject
    private val object44 = Object()

    @Inject
    private val object45 = Object()

    @Inject
    private val object46 = Object()

    @Inject
    private val object47 = Object()

    @Inject
    private val object48 = Object()

    @Inject
    private val object49 = Object()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        val time = System.currentTimeMillis()
        Blade.inject(this, ProviderContext())
        Log.i("pby123", "test time:${System.currentTimeMillis() - time}")
    }
}