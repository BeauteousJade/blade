package com.jade.blade.transform.provider

import com.jade.blade.info.ProvideInfo
import com.jade.blade.transform.base.BaseClassVisitor
import com.jade.blade.transform.utils.ClassVisitorUtils
import com.jade.blade.transform.utils.TypeMapper
import org.objectweb.asm.ClassVisitor
import org.objectweb.asm.Label
import org.objectweb.asm.MethodVisitor
import org.objectweb.asm.Opcodes.*
import org.objectweb.asm.commons.AdviceAdapter


class ProviderClassVisitor(
    nextVisitor: ClassVisitor,
    className: String,
) : BaseClassVisitor(nextVisitor, className) {

    private val fieldMap = HashMap<String, ProvideInfo>()

    companion object {
        private val providerDataMapFieldInfo = Triple(
            "providerDataMap",
            "Ljava/util/Map;",
            "Ljava/util/Map<Ljava/lang/String;Ljava/lang/Object;>;"
        )
        private val provideDataByBladeFunInfo = Triple(
            "provideDataByBlade", // name
            "()Ljava/util/HashMap;", // descriptor
            "()Ljava/util/HashMap<Ljava/lang/String;Ljava/lang/Object;>;" // signature
        )
        private val initFunInfo = Triple(
            "init", // name
            "()V", // descriptor
            null// signature
        )
        private val checkMultipleKeyInfo = Triple(
            "checkMultipleKey", "(Ljava/lang/String;)V", null
        )

        private val checkProviderInfo = Triple("checkProvider", "(Ljava/lang/Object;)V", null)
    }

    override fun applyFieldAnnotation(
        filedName: String, filedDescriptor: String, annotationName: String, annotationValue: Any
    ) {
        fieldMap.computeIfAbsent(filedName) {
            ProvideInfo(filedDescriptor, filedName, false)
        }.apply {
            if (annotationName == "value") {
                key = annotationValue.toString().takeIf {
                    it.isNotEmpty()
                } ?: key
            } else if (annotationName == "isProvider") {
                isProvider = (annotationValue as? Boolean) ?: false
            }
        }
    }

    override fun getNewInterfaceName() = "com/jade/blade/support/DataProvider"

    override fun getAnnotationDescriptor() = "Lcom/jade/blade/annotation/Provide;"

    override fun visitMethod(
        access: Int,
        name: String?,
        descriptor: String?,
        signature: String?,
        exceptions: Array<out String>?
    ): MethodVisitor {
        val methodVisitor = super.visitMethod(access, name, descriptor, signature, exceptions)
        return object : AdviceAdapter(ASM5, methodVisitor, access, name, descriptor) {


            override fun onMethodExit(opcode: Int) {
                if (name == "<init>") {
                    // 调用init 方法
                    mv.visitVarInsn(ALOAD, 0)
                    mv.visitMethodInsn(INVOKESPECIAL, newClassName, "init", "()V", false);
                }
            }
        }
    }


    override fun visitEnd() {
        super.visitEnd()
        println(fieldMap)
        // 给类新增providerDataMap变量
        ClassVisitorUtils.addField(
            cv,
            providerDataMapFieldInfo.first,
            providerDataMapFieldInfo.second,
            providerDataMapFieldInfo.third
        )
        addCheckMultipleKey()
        addInit()
        addProvideDataByBlade()
        addCheckProvider()
    }

    private fun addProvideDataByBlade() {
        // 给类新增provideDataByBlade方法
        ClassVisitorUtils.addFunc(
            cv,
            provideDataByBladeFunInfo.first,
            provideDataByBladeFunInfo.second,
            provideDataByBladeFunInfo.third,
            ACC_PUBLIC or ACC_FINAL
        ) {
            visitAnnotation("Landroidx/annotation/NonNull;", false).apply {
                visitEnd()
            }
            visitCode()
            visitVarInsn(ALOAD, 0)
            visitFieldInsn(
                GETFIELD,
                newClassName,
                providerDataMapFieldInfo.first,
                providerDataMapFieldInfo.second
            )
            visitTypeInsn(CHECKCAST, "java/util/HashMap")
            visitInsn(ARETURN)
            visitMaxs(1, 1)
            visitEnd()
        }
    }

    private fun addCheckMultipleKey() {
        // 给checkMultipleKey新增方法，是否key是否重复定义。
        ClassVisitorUtils.addFunc(
            cv, checkMultipleKeyInfo.first, checkMultipleKeyInfo.second, checkMultipleKeyInfo.third
        ) {
            visitCode()
            visitVarInsn(ALOAD, 0)
            visitFieldInsn(
                GETFIELD,
                newClassName,
                providerDataMapFieldInfo.first,
                providerDataMapFieldInfo.second
            )
            visitVarInsn(ALOAD, 1)
            visitMethodInsn(
                INVOKEINTERFACE, "java/util/Map", "containsKey", "(Ljava/lang/Object;)Z", true
            )
            val label0 = Label()
            visitJumpInsn(IFEQ, label0)
            visitTypeInsn(NEW, "java/lang/IllegalArgumentException")
            visitInsn(DUP)
            visitTypeInsn(NEW, "java/lang/StringBuilder")
            visitInsn(DUP)
            visitMethodInsn(INVOKESPECIAL, "java/lang/StringBuilder", "<init>", "()V", false)
            visitLdcInsn("multiple key:")
            visitMethodInsn(
                INVOKEVIRTUAL,
                "java/lang/StringBuilder",
                "append",
                "(Ljava/lang/String;)Ljava/lang/StringBuilder;",
                false
            );
            visitVarInsn(ALOAD, 1)
            visitMethodInsn(
                INVOKEVIRTUAL,
                "java/lang/StringBuilder",
                "append",
                "(Ljava/lang/String;)Ljava/lang/StringBuilder;",
                false
            );
            visitMethodInsn(
                INVOKEVIRTUAL, "java/lang/StringBuilder", "toString", "()Ljava/lang/String;", false
            );
            visitMethodInsn(
                INVOKESPECIAL,
                "java/lang/IllegalArgumentException",
                "<init>",
                "(Ljava/lang/String;)V",
                false
            )
            visitInsn(ATHROW)
            visitLabel(label0)
            visitInsn(RETURN)
            visitMaxs(4, 2)
            visitEnd()
        }
    }

    private fun addInit() {
        // 给类新增init方法。
        ClassVisitorUtils.addFunc(
            cv, initFunInfo.first, initFunInfo.second, initFunInfo.third
        ) {
            visitCode()
            // ---- 初始化providerDataMap变量------
            visitVarInsn(ALOAD, 0)
            visitTypeInsn(NEW, "java/util/HashMap")
            visitInsn(DUP)
            visitMethodInsn(INVOKESPECIAL, "java/util/HashMap", "<init>", "()V", false)
            visitFieldInsn(
                PUTFIELD,
                newClassName,
                providerDataMapFieldInfo.first,
                providerDataMapFieldInfo.second
            )
            // ----------------------------------
            // ---------将每个变量放到Map里面------------
            fieldMap.forEach {
                visitVarInsn(ALOAD, 0)
                visitLdcInsn(it.value.key)
                visitMethodInsn(
                    INVOKESPECIAL,
                    newClassName,
                    checkMultipleKeyInfo.first,
                    checkMultipleKeyInfo.second,
                    false
                )
                visitVarInsn(ALOAD, 0)
                visitFieldInsn(
                    GETFIELD,
                    newClassName,
                    providerDataMapFieldInfo.first,
                    providerDataMapFieldInfo.second
                )
                visitLdcInsn(it.value.key)
                visitVarInsn(ALOAD, 0)
                visitFieldInsn(GETFIELD, newClassName, it.key, it.value.descriptor)
                TypeMapper.getWrapperInfo(it.value.descriptor)?.let {
                    visitMethodInsn(INVOKESTATIC, it.first, "valueOf", it.second, false)
                }
                visitMethodInsn(
                    INVOKEINTERFACE,
                    "java/util/Map",
                    "put",
                    "(Ljava/lang/Object;Ljava/lang/Object;)Ljava/lang/Object;",
                    true
                )
                visitInsn(POP)
                if (it.value.isProvider) {
                    visitVarInsn(ALOAD, 0)
                    visitVarInsn(ALOAD, 0)
                    visitFieldInsn(GETFIELD, newClassName, it.key, it.value.descriptor)
                    TypeMapper.getWrapperInfo(it.value.descriptor)?.let {
                        visitMethodInsn(INVOKESTATIC, it.first, "valueOf", it.second, false)
                    }
                    visitMethodInsn(
                        INVOKESPECIAL,
                        newClassName,
                        checkProviderInfo.first,
                        checkProviderInfo.second,
                        false
                    )
                }
            }
            // ----------------------------------
            visitInsn(RETURN)
            visitMaxs(4, 1)
            visitEnd()
        }
    }

    private fun addCheckProvider() {
        val interfaceName = getNewInterfaceName()
        ClassVisitorUtils.addFunc(cv, checkProviderInfo.first, checkProviderInfo.second, checkProviderInfo.third) {
            visitCode()
            visitVarInsn(ALOAD, 1)
            visitTypeInsn(INSTANCEOF, interfaceName)
            val label0 = Label()
            visitJumpInsn(IFEQ, label0)
            visitVarInsn(ALOAD, 1)
            visitTypeInsn(CHECKCAST, interfaceName)
            visitMethodInsn(
                INVOKEINTERFACE,
                interfaceName,
                provideDataByBladeFunInfo.first,
                provideDataByBladeFunInfo.second,
                true
            )
            visitVarInsn(ASTORE, 2)
            visitVarInsn(ALOAD, 2)
            visitMethodInsn(
                INVOKEVIRTUAL, "java/util/HashMap", "keySet", "()Ljava/util/Set;", false
            )
            visitVarInsn(ASTORE, 3)
            visitVarInsn(ALOAD, 3)
            visitMethodInsn(
                INVOKEINTERFACE, "java/util/Set", "iterator", "()Ljava/util/Iterator;", true
            )
            visitVarInsn(ASTORE, 4)
            val label1 = Label()
            visitLabel(label1)
            visitVarInsn(ALOAD, 4)
            visitMethodInsn(
                INVOKEINTERFACE, "java/util/Iterator", "hasNext", "()Z", true
            )
            val label2 = Label()
            visitJumpInsn(IFEQ, label2)
            visitVarInsn(ALOAD, 4)
            visitMethodInsn(
                INVOKEINTERFACE, "java/util/Iterator", "next", "()Ljava/lang/Object;", true
            )
            visitTypeInsn(CHECKCAST, "java/lang/String")
            visitVarInsn(ASTORE, 5)
            visitVarInsn(ALOAD, 0)
            visitVarInsn(ALOAD, 5)
            visitMethodInsn(
                INVOKESPECIAL,
                newClassName,
                checkMultipleKeyInfo.first,
                checkMultipleKeyInfo.second,
                false
            )
            visitJumpInsn(GOTO, label1)
            visitLabel(label2)
            visitVarInsn(ALOAD, 0)
            visitFieldInsn(
                GETFIELD, newClassName, providerDataMapFieldInfo.first, providerDataMapFieldInfo.second
            )
            visitVarInsn(ALOAD, 2)
            visitMethodInsn(
                INVOKEINTERFACE, "java/util/Map", "putAll", "(Ljava/util/Map;)V", true
            )
            visitLabel(label0)
            visitInsn(RETURN)
            visitMaxs(2, 6)
            visitEnd()
        }

    }
}