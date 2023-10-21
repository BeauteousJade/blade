package com.jade.blade.transform.injector

import com.jade.blade.info.InjectInfo
import com.jade.blade.transform.base.BaseClassVisitor
import com.jade.blade.transform.utils.ClassVisitorUtils
import com.jade.blade.transform.utils.Quintuple
import com.jade.blade.transform.utils.TypeMapper
import org.objectweb.asm.ClassVisitor
import org.objectweb.asm.Label
import org.objectweb.asm.Opcodes.*


class InjectorClassVisitor(
    nextVisitor: ClassVisitor,
    className: String,
) : BaseClassVisitor(nextVisitor, className) {

    private val fieldMap = HashMap<String, InjectInfo>()

    companion object {

        private val setupDataByBladeFunInfo = Triple(
            "setupDataByBlade", // name
            "(Ljava/util/Map;)V", // descriptor
            "(Ljava/util/Map<Ljava/lang/String;*>;)V" // signature
        )

        private val requireNonNullFunInfoInBlade = Quintuple(
            "com/jade/blade/utils/BladeUtils", // owner
            "INSTANCE", // objectName
            "requireNonNull", // methodName
            "(Ljava/lang/Object;Ljava/lang/String;Ljava/lang/String;)Ljava/lang/Object;", // descriptor
            null // signature
        )
    }


    override fun applyFieldAnnotation(
        filedName: String, filedDescriptor: String, annotationName: String, annotationValue: Any
    ) {
        fieldMap.computeIfAbsent(filedName) {
            InjectInfo(filedDescriptor, filedName, false)
        }.apply {
            if (annotationName == "value") {
                key = annotationValue.toString().takeIf {
                    it.isNotEmpty()
                } ?: key
            } else if (annotationName == "isNullable") {
                isNullable = (annotationValue as? Boolean) ?: false
            }
        }
    }


    override fun getNewInterfaceName() = "com/jade/blade/support/DataFetcher"

    override fun getAnnotationDescriptor() = "Lcom/jade/blade/annotation/Inject;"


    override fun visitEnd() {
        super.visitEnd()
        addSetupDataByBlade()
    }

    private fun addSetupDataByBlade() {
        ClassVisitorUtils.addFunc(
            cv,
            setupDataByBladeFunInfo.first,
            setupDataByBladeFunInfo.second,
            setupDataByBladeFunInfo.third,
            ACC_PUBLIC or ACC_FINAL
        ) {
            visitAnnotableParameterCount(1, false)
            visitParameterAnnotation(0, "Landroidx/annotation/NonNull;", false).apply {
                visitEnd()
            }
            visitCode()
            val newMap = fieldMap.keys.groupBy {
                if (fieldMap[it]!!.isNullable) 1 else 0
            }
            val commonRunnable = { info: InjectInfo ->
                visitVarInsn(ALOAD, 1)
                visitLdcInsn(info.key)
                visitMethodInsn(
                    INVOKEINTERFACE,
                    "java/util/Map",
                    "get",
                    "(Ljava/lang/Object;)Ljava/lang/Object;",
                    true
                )
            }
            val castRunnable = { info: InjectInfo ->
                TypeMapper.getRawInfoBy(info.descriptor)?.let {
                    visitTypeInsn(CHECKCAST, it.first)
                    visitMethodInsn(
                        INVOKEVIRTUAL, it.first, it.second, "()${info.descriptor}", false
                    )
                } ?: run {
                    visitTypeInsn(
                        CHECKCAST,
                        // Object类型， 将前面的L和后面的;删除
                        info.descriptor.substring(1, info.descriptor.length - 1)
                    )
                }
            }

            // 不能为空，当为空的时候，抛出异常。
            newMap[0]?.forEach { key ->
                val value = fieldMap[key]!!
                visitVarInsn(ALOAD, 0)
                visitVarInsn(ALOAD, 0)
                visitFieldInsn(
                    GETSTATIC,
                    requireNonNullFunInfoInBlade.first,
                    requireNonNullFunInfoInBlade.second,
                    "L${requireNonNullFunInfoInBlade.first};"
                )
                commonRunnable(value)
                visitLdcInsn(key)
                visitLdcInsn(value.key)
                visitMethodInsn(
                    INVOKEVIRTUAL,
                    requireNonNullFunInfoInBlade.first,
                    requireNonNullFunInfoInBlade.third,
                    requireNonNullFunInfoInBlade.fourth,
                    false
                )
                castRunnable(value)
                visitFieldInsn(PUTFIELD, newClassName, key, value.descriptor)
            }
            var index = 2
            newMap[1]?.forEach { key ->
                val value = fieldMap[key]!!
                commonRunnable(value)
                visitVarInsn(ASTORE, index)
                visitVarInsn(ALOAD, 2)
                val label = Label()
                visitJumpInsn(IFNULL, label)
                visitVarInsn(ALOAD, 0)
                visitVarInsn(ALOAD, index)
                castRunnable(value)
                visitFieldInsn(PUTFIELD, newClassName, key, value.descriptor)
                visitLabel(label)
                index++
            }
            visitInsn(RETURN)
            visitMaxs(3, 2)
            visitEnd()
        }
    }

}