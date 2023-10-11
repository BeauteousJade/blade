package com.jade.blade.transform.utils

import groovyjarjarasm.asm.Opcodes.ACC_PRIVATE
import org.objectweb.asm.ClassVisitor
import org.objectweb.asm.MethodVisitor
import org.objectweb.asm.Opcodes.ACC_FINAL
import org.objectweb.asm.Opcodes.RETURN

object ClassVisitorUtils {

    /**
     * 给类添加一个变量。
     */
    fun addField(
        cv: ClassVisitor,
        name: String,
        descriptor: String,
        signature: String,
        value: String? = null,
        access: Int = ACC_PRIVATE or ACC_FINAL,
    ) {
        cv.visitField(access, name, descriptor, signature, value).apply {
            visitEnd()
        }
    }

    /**
     * 给类添加一个方法。
     */
    fun addFunc(
        cv: ClassVisitor,
        name: String,
        descriptor: String = "()V",
        signature: String? = null,
        access: Int = ACC_PRIVATE or ACC_FINAL,
        callback: MethodVisitor.() -> Unit = {
            visitCode()
            visitInsn(RETURN)
            visitMaxs(0, 1)
            visitEnd()
        }
    ) {
        cv.visitMethod(access, name, descriptor, signature, null).apply {
            callback.invoke(this)
        }
    }
}