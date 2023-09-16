package com.jade.blade.support

interface DataProvider {

    fun provideDataByBlade(): HashMap<String, Any>
}