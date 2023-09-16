package com.jade.blade.annotation

@Retention(AnnotationRetention.BINARY)
@Target(AnnotationTarget.FIELD)
annotation class Inject(val value: String = "", val isNullable: Boolean = false)