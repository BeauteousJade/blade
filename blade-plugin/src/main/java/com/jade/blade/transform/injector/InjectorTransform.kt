package com.jade.blade.transform.injector

import com.android.build.api.instrumentation.AsmClassVisitorFactory
import com.android.build.api.instrumentation.ClassContext
import com.android.build.api.instrumentation.ClassData
import com.android.build.api.instrumentation.InstrumentationParameters
import org.objectweb.asm.ClassVisitor

abstract class InjectorTransform : AsmClassVisitorFactory<InstrumentationParameters.None> {

    final override fun createClassVisitor(
        classContext: ClassContext, nextClassVisitor: ClassVisitor
    ): ClassVisitor {
        return InjectorClassVisitor(nextClassVisitor, classContext.currentClassData.className)
    }

    final override fun isInstrumentable(classData: ClassData): Boolean {
        return classData.classAnnotations.contains("com.jade.blade.annotation.Injector")
    }
}