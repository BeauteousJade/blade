package com.jade.blade.transform.provider

import com.android.build.api.instrumentation.AsmClassVisitorFactory
import com.android.build.api.instrumentation.ClassContext
import com.android.build.api.instrumentation.ClassData
import com.android.build.api.instrumentation.InstrumentationParameters
import org.objectweb.asm.ClassVisitor

/**
 * Transform 不允许抽取基类，会编译报错。
 */
abstract class ProviderTransform : AsmClassVisitorFactory<InstrumentationParameters.None> {


    final override fun createClassVisitor(
        classContext: ClassContext, nextClassVisitor: ClassVisitor
    ): ClassVisitor {
        return ProviderClassVisitor(nextClassVisitor, classContext.currentClassData.className)
    }

    final override fun isInstrumentable(classData: ClassData): Boolean {
        return classData.classAnnotations.contains("com.jade.blade.annotation.Provider")
    }

}