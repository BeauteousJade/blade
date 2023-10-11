package com.jade.blade.transform

import org.objectweb.asm.AnnotationVisitor
import org.objectweb.asm.FieldVisitor
import org.objectweb.asm.Opcodes

class FieldVisitorAdapter(
    originVisitor: FieldVisitor,
    private val annotationDescriptor: String,
    private val callback: (name: String, value: Any) -> Unit
) : FieldVisitor(Opcodes.ASM5, originVisitor) {


    override fun visitAnnotation(descriptor: String?, visible: Boolean): AnnotationVisitor {
        val annotationVisitor = super.visitAnnotation(descriptor, visible)
        if (annotationDescriptor != descriptor) {
            return annotationVisitor
        }
        callback.invoke("value", "")
        return object : AnnotationVisitor(Opcodes.ASM5, annotationVisitor) {
            override fun visit(name: String?, value: Any?) {
                if (name != null && value != null) {
                    callback.invoke(name, value)
                }
            }
        }
    }
}