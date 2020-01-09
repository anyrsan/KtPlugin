package com.any.org.gradleplugin.asm

import com.any.org.gradleplugin.common.Log
import org.objectweb.asm.AnnotationVisitor
import org.objectweb.asm.Opcodes

/**
 *
 * @author any
 * @time 2019/07/29 17.23
 * @details 注解解析
 */
class MyAnnotationVisitor(av: AnnotationVisitor?, private val annoMap: HashMap<String, ArrayList<String>>) :
    AnnotationVisitor(Opcodes.ASM5, av) {


    // 值的话，存起来
    override fun visit(name: String?, value: Any?) {
        super.visit(name, value)
        val values = arrayListOf<String>()
        value?.let {
            values.add(it.toString())
        }
        name?.let {
            annoMap[it] = values
        }
        Log.w("注解  $name  ")
    }

    // 数组的话，会进去继续解析
    override fun visitArray(name: String?): AnnotationVisitor {
        return MyArrayAnnotationVisitor(super.visitArray(name), annoMap, name)
    }

}