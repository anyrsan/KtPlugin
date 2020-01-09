package com.any.org.gradleplugin.asm

import org.objectweb.asm.AnnotationVisitor
import org.objectweb.asm.Opcodes

/**
 *
 * @author any
 * @time 2019/07/29 17.23
 * @details 注解数组解析
 */
class MyArrayAnnotationVisitor(
    av: AnnotationVisitor,
    private val annoMap: HashMap<String, ArrayList<String>>,
    private val paramName: String?
) :
    AnnotationVisitor(Opcodes.ASM5, av) {

    private val values by lazy { ArrayList<String>() }

    override fun visit(name: String?, value: Any?) {
        super.visit(name, value)
        value?.let {
            values.add(it.toString())
        }
        paramName?.let {
            annoMap[it] = values
        }
    }
}