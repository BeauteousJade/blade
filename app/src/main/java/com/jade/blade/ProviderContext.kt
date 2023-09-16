package com.jade.blade

import com.jade.blade.annotation.Provide
import com.jade.blade.annotation.Provider

@Provider
class ProviderContext : BaseProviderTest {

    @Provide(isProvider = true)
    private val context2 = ProviderContext2()

    @Provide("shortValue1")
    private val shortValue: Short = 20

    @Provide("shortValue2")
    private val shortVal: Short = 20

    @Provide
    private val doubleValue = 50.0

    @Provide
    private val booleanValue = true

    @Provide
    private val charValue = 'a'

    @Provide("str")
    private val string = "a"

    @Provide
    private val object1 = Object()

    @Provide
    private val object2 = Object()

    @Provide
    private val object3 = Object()

    @Provide
    private val object4 = Object()

    @Provide
    private val object5 = Object()

    @Provide
    private val object6 = Object()

    @Provide
    private val object7 = Object()

    @Provide
    private val object8 = Object()

    @Provide
    private val object9 = Object()

    @Provide
    private val object10 = Object()

    @Provide
    private val object11 = Object()

    @Provide
    private val object12 = Object()

    @Provide
    private val object13 = Object()

    @Provide
    private val object14 = Object()

    @Provide
    private val object15 = Object()

    @Provide
    private val object16 = Object()

    @Provide
    private val object17 = Object()

    @Provide
    private val object18 = Object()

    @Provide
    private val object19 = Object()

    @Provide
    private val object20 = Object()

    @Provide
    private val object21 = Object()

    @Provide
    private val object22 = Object()

    @Provide
    private val object23 = Object()

    @Provide
    private val object24 = Object()

    @Provide
    private val object25 = Object()

    @Provide
    private val object26 = Object()

    @Provide
    private val object27 = Object()

    @Provide
    private val object28 = Object()

    @Provide
    private val object29 = Object()

    @Provide
    private val object30 = Object()

    @Provide
    private val object31 = Object()

    @Provide
    private val object32 = Object()

    @Provide
    private val object33 = Object()

    @Provide
    private val object34 = Object()

    @Provide
    private val object35 = Object()

    @Provide
    private val object36 = Object()

    @Provide
    private val object37 = Object()

    @Provide
    private val object38 = Object()

    @Provide
    private val object39 = Object()

    @Provide
    private val object40 = Object()

    @Provide
    private val object41 = Object()

    @Provide
    private val object42 = Object()

    @Provide
    private val object43 = Object()

    @Provide
    private val object44 = Object()

    @Provide
    private val object45 = Object()

    @Provide
    private val object46 = Object()

    @Provide
    private val object47 = Object()

    @Provide
    private val object48 = Object()

    @Provide
    private val object49 = Object()

    constructor() {
    }

    constructor(test: String) : super(test) {
    }

    @Provider
    private class ProviderContext2 {
        @Provide("byteValue1")
        private val byteValue: Byte = 10

        @Provide
        private val intValue = 30

        @Provide
        private val longValue: Long = 40

        @Provide
        private val floatValue = 40f
    }
}