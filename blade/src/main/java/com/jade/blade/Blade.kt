package com.jade.blade

import com.jade.blade.support.DataFetcher
import com.jade.blade.support.DataProvider

class Blade {

    companion object {

        fun inject(target: Any, source: Any) {
            if (target is DataFetcher && source is DataProvider) {
                target.setupDataByBlade(source.provideDataByBlade())
            }
        }
    }
}