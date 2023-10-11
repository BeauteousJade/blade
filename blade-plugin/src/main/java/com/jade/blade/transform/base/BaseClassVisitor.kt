package com.jade.blade.transform.base

import com.jade.blade.transform.FieldVisitorAdapter
import org.objectweb.asm.ClassVisitor
import org.objectweb.asm.FieldVisitor
import org.objectweb.asm.Opcodes

abstract class BaseClassVisitor(
    nextVisitor: ClassVisitor,
    className: String,
) : ClassVisitor(Opcodes.ASM5, nextVisitor) {

    protected val newClassName = className.replace(".", "/")

    override fun visit(
        version: Int,
        access: Int,
        name: String,
        signature: String?,
        superName: String?,
        interfaces: Array<out String>?
    ) {
        // 将每个被Provider注解修饰的类，实现DataProvider接口
        val newInterfaces = ArrayList<String>().apply {
            add(getNewInterfaceName())
            interfaces?.let {
                addAll(it)
            }
        }
        super.visit(version, access, name, signature, superName, newInterfaces.toArray(arrayOf()))
    }

    /**
     * 找到每个被相应注解修饰的变量。
     */
    override fun visitField(
        access: Int,
        fieldName: String?,
        fieldNameDescriptor: String?,
        signature: String?,
        fieldValue: Any?
    ): FieldVisitor {
        val fieldVisitor =
            super.visitField(access, fieldName, fieldNameDescriptor, signature, fieldValue)
        if (fieldName == null || fieldNameDescriptor == null) {
            return fieldVisitor
        }
        return FieldVisitorAdapter(fieldVisitor, getAnnotationDescriptor()) { name, value ->
            applyFieldAnnotation(fieldName, fieldNameDescriptor, name, value)
        }
    }

    protected abstract fun applyFieldAnnotation(
        filedName: String,
        filedDescriptor: String,
        annotationName: String,
        annotationValue: Any
    )


    protected abstract fun getNewInterfaceName(): String

    protected abstract fun getAnnotationDescriptor(): String

}