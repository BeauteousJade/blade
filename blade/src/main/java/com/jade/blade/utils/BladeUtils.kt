package com.jade.blade.utils

import com.jade.blade.support.DataProvider

object BladeUtils {

    fun requireNonNull(obj: Any?, fieldName: String, key: String): Any {
        if (obj == null) {
            throw NullPointerException("$fieldName is null, key:$key")
        }
        return obj
    }

    fun checkMultipleKey(map: Map<String, *>, key: String) {
        require(!map.containsKey(key)) { "multiple key:$key" }
    }


    fun checkProvider(map: MutableMap<String, Any>, `object`: Any, key: String) {
        if (`object` is DataProvider) {
            val hashMap = `object`.provideDataByBlade()
            val keySet: Set<String> = hashMap.keys
            for (key in keySet) {
                checkMultipleKey(map, key)
            }
            map.putAll(hashMap)
        }
        checkMultipleKey(map, key)
    }

}