package com.jade.blade.annotation

@Retention(AnnotationRetention.BINARY)
@Target(AnnotationTarget.FIELD)
annotation class Provide(val value: String = "", val isProvider: Boolean = false)