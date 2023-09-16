package com.jade.blade.transform.utils

object TypeMapper {
    private val rawToWrapperMap = HashMap<String, Pair<String, String>>().apply {
        put("B", Pair("java/lang/Byte", "(B)Ljava/lang/Byte;"))
        put("S", Pair("java/lang/Short", "(S)Ljava/lang/Short;"))
        put("I", Pair("java/lang/Integer", "(I)Ljava/lang/Integer;"))
        put("J", Pair("java/lang/Long", "(J)Ljava/lang/Long;"))
        put("F", Pair("java/lang/Float", "(F)Ljava/lang/Float;"))
        put("D", Pair("java/lang/Double", "(D)Ljava/lang/Double;"))
        put("Z", Pair("java/lang/Boolean", "(Z)Ljava/lang/Boolean;"))
        put("C", Pair("java/lang/Character", "(C)Ljava/lang/Character;"))
    }

    private val wrapperToRawMap = HashMap<String, Pair<String, String>>().apply {
        put("B", Pair("java/lang/Byte", "byteValue"))
        put("S", Pair("java/lang/Short", "shortValue"))
        put("I", Pair("java/lang/Integer", "intValue"))
        put("J", Pair("java/lang/Long", "longValue"))
        put("F", Pair("java/lang/Float", "floatValue"))
        put("D", Pair("java/lang/Double", "doubleValue"))
        put("Z", Pair("java/lang/Boolean", "booleanValue"))
        put("C", Pair("java/lang/Character", "charValue"))
    }

    /**
     * 通过描述，获取包装类型相关的信息。
     */
    fun getWrapperInfo(rawDescriptor: String): Pair<String, String>? {
        return rawToWrapperMap[rawDescriptor]
    }

    /**
     * 通过描述，获取原生类型相关的信息。
     */
    fun getRawInfoBy(rawDescriptor: String): Pair<String, String>? {
        return wrapperToRawMap[rawDescriptor]
    }
}