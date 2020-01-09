package com.any.org.gradleplugin.asm

import com.any.org.gradleplugin.common.Constant
import com.any.gradle.data.ClazzData
import com.any.org.gradleplugin.common.Log
import org.objectweb.asm.AnnotationVisitor
import org.objectweb.asm.ClassVisitor
import org.objectweb.asm.MethodVisitor
import org.objectweb.asm.Opcodes

/**
 *
 * @author any
 * @time 2019/07/29 17.09
 * @details  类解析器
 */
class AnyClassVisitor(private val clazzData: ClazzData, cv: ClassVisitor) : ClassVisitor(Opcodes.ASM5, cv) {
    //注解数据
    private val annoMap by lazy { HashMap<String, ArrayList<String>>() }

    init {
        Log.w("【【【进入 ${clazzData.cName}】】】")
    }

    override fun visitEnd() {
        super.visitEnd()
        Log.w("【【【结束 ${clazzData.cName}】】】")
    }

    override fun visitAnnotation(desc: String?, visible: Boolean): AnnotationVisitor {
        return if (Constant.containsAnnotation(desc)) {
            MyAnnotationVisitor(super.visitAnnotation(desc, visible), annoMap)
        } else {
            super.visitAnnotation(desc, visible)
        }
    }


    override fun visitMethod(
        access: Int,
        name: String?,
        desc: String?,
        signature: String?,
        exceptions: Array<out String>?
    ): MethodVisitor {
        val isInit = annoMap.isNotEmpty()
        clazzData.mInit = if (isInit) Constant.getDataByMap(annoMap) else null
        val mv = cv.visitMethod(access, name, desc, signature, exceptions)
        return AnyMethodVisitor(clazzData, mv, access, name, desc)
    }


}