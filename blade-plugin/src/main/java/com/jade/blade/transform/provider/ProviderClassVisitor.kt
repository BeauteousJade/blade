package com.jade.blade.transform.provider

import com.jade.blade.info.ProvideInfo
import com.jade.blade.transform.base.BaseClassVisitor
import com.jade.blade.transform.utils.ClassVisitorUtils
import com.jade.blade.transform.utils.Quintuple
import com.jade.blade.transform.utils.TypeMapper
import org.objectweb.asm.ClassVisitor
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
        private val initFieldByBladeFunInfo = Triple(
            "initFieldByBlade", // name
            "()V", // descriptor
            null// signature
        )

        private val checkProviderFunInfoInBlade = Quintuple(
            "com/jade/blade/utils/BladeUtils", // owner
            "INSTANCE", // objectName
            "checkProvider", "(Ljava/util/Map;Ljava/lang/Object;Ljava/lang/String;)V", null
        )

        private val checkMultipleKeyFunInfoInBlade = Quintuple(
            "com/jade/blade/utils/BladeUtils", // owner
            "INSTANCE", // objectName
            "checkMultipleKey", // methodName
            "(Ljava/util/Map;Ljava/lang/String;)V", // descriptor
            null // signature
        )
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
        // 给类新增providerDataMap变量
        ClassVisitorUtils.addField(
            cv,
            providerDataMapFieldInfo.first,
            providerDataMapFieldInfo.second,
            providerDataMapFieldInfo.third
        )
        addInit()
        addProvideDataByBlade()
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

    private fun addInit() {
        // 给类新增init方法。
        ClassVisitorUtils.addFunc(
            cv,
            initFieldByBladeFunInfo.first,
            initFieldByBladeFunInfo.second,
            initFieldByBladeFunInfo.third,
            ACC_PROTECTED
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
            visitFieldInsn(
                GETSTATIC,
                checkMultipleKeyFunInfoInBlade.first,
                checkMultipleKeyFunInfoInBlade.second,
                "L${checkMultipleKeyFunInfoInBlade.first};"
            )
            visitVarInsn(ASTORE, 1)
            // ---------将每个变量放到Map里面------------
            fieldMap.forEach {
                visitVarInsn(ALOAD, 1)
                visitVarInsn(ALOAD, 0)
                visitFieldInsn(
                    GETFIELD,
                    newClassName,
                    providerDataMapFieldInfo.first,
                    providerDataMapFieldInfo.second
                )
                visitVarInsn(ALOAD, 0)
                visitFieldInsn(GETFIELD, newClassName, it.key, it.value.descriptor)
                TypeMapper.getWrapperInfo(it.value.descriptor)?.let {
                    visitMethodInsn(INVOKESTATIC, it.first, "valueOf", it.second, false)
                }
                visitLdcInsn(it.key)
                visitMethodInsn(
                    INVOKEVIRTUAL,
                    checkProviderFunInfoInBlade.first,
                    checkProviderFunInfoInBlade.third,
                    checkProviderFunInfoInBlade.fourth,
                    false
                );
                visitVarInsn(ALOAD, 0)
                visitFieldInsn(
                    GETFIELD,
                    newClassName,
                    providerDataMapFieldInfo.first,
                    providerDataMapFieldInfo.second
                )
                visitLdcInsn(it.key)
                visitVarInsn(ALOAD, 0)
                visitFieldInsn(GETFIELD, newClassName, it.key, it.value.descriptor)
                TypeMapper.getWrapperInfo(it.value.descriptor)?.let {
                    visitMethodInsn(INVOKESTATIC, it.first, "valueOf", it.second, false)
                }
                visitMethodInsn(
                    INVOKEVIRTUAL,
                    "java/util/HashMap",
                    "put",
                    "(Ljava/lang/Object;Ljava/lang/Object;)Ljava/lang/Object;",
                    false
                )
                visitInsn(POP)
            }
            // ----------------------------------
            visitInsn(RETURN)
            visitMaxs(4, 1)
            visitEnd()
        }
    }
}