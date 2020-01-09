package com.any.org.gradleplugin.asm

import com.any.org.gradleplugin.common.Constant
import com.any.gradle.data.ClazzData
import org.objectweb.asm.AnnotationVisitor
import org.objectweb.asm.MethodVisitor
import org.objectweb.asm.Opcodes
import org.objectweb.asm.commons.AdviceAdapter

/**
 *
 * @author any
 * @time 2019/07/29 17.35
 * @details  方法解析器
 */
class AnyMethodVisitor(
    private val clazzData: ClazzData,
    mv: MethodVisitor,
    access: Int,
    funName: String?,
    desc: String?
) : AdviceAdapter(
    Opcodes.ASM5, mv, access, funName, desc
) {
    //获取事件名称
    private var eventName: String = Constant.getEventName(clazzData, funName)
    //注解数据
    private val annoMap by lazy { HashMap<String, ArrayList<String>>() }
    // 处理点击事件 ,不一定是点击事件
    private val clickEvent =
        if (Constant.INIT_EVENT != funName + desc) Constant.containsEvent(funName + desc) else false
    // 处理初始化事件
    private val initEvent = if (clazzData.mInit != null) Constant.containsEvent(funName + desc) else false

    // 注解
    override fun visitAnnotation(desc: String?, visible: Boolean): AnnotationVisitor {
        // 要处理判断
        return if (Constant.containsAnnotation(desc)) {
            MyAnnotationVisitor(super.visitAnnotation(desc, visible), annoMap)
        } else {
            super.visitAnnotation(desc, visible)
        }
    }


    //方法体
    override fun visitCode() {
        super.visitCode()
//     插入代码
        val tempValue = when {
            annoMap.isNotEmpty() -> Constant.getDataByMap(annoMap)
            initEvent -> clazzData.mInit
            else -> null
        } ?: return
        //动态注入
        mv.visitFieldInsn(
            GETSTATIC,
            "com/any/event/event/EventManager",
            "INSTANCE",
            "Lcom/any/event/event/EventManager;"
        )
        mv.visitLdcInsn(tempValue)
        mv.visitLdcInsn(eventName)
        mv.visitMethodInsn(
            INVOKEVIRTUAL,
            "com/any/event/event/EventManager",
            "addEvent",
            "(Ljava/lang/String;Ljava/lang/String;)V",
            false
        )
    }


    //方法进入
    override fun onMethodEnter() {
        super.onMethodEnter()
        if (annoMap.isNotEmpty() || clickEvent) {
            //动态注入
            mv.visitFieldInsn(
                GETSTATIC,
                "com/any/event/event/EventManager",
                "INSTANCE",
                "Lcom/any/event/event/EventManager;"
            )
            mv.visitLdcInsn(eventName)
            mv.visitMethodInsn(
                INVOKEVIRTUAL,
                "com/any/event/event/EventManager",
                "addStartTimeEvent",
                "(Ljava/lang/String;)V",
                false
            )
        }
    }


    // 方法离开
    override fun onMethodExit(opcode: Int) {
        super.onMethodExit(opcode)
        if (annoMap.isNotEmpty() || clickEvent) {
            //动态注入
            mv.visitFieldInsn(
                GETSTATIC,
                "com/any/event/event/EventManager",
                "INSTANCE",
                "Lcom/any/event/event/EventManager;"
            )
            mv.visitLdcInsn(eventName)
            mv.visitMethodInsn(
                INVOKEVIRTUAL,
                "com/any/event/event/EventManager",
                "addEndTimeEvent",
                "(Ljava/lang/String;)V",
                false
            )
        }
    }


//    private val sb by lazy { StringBuffer() }
//
//    override fun visitInsn(opcode: Int) {
//        super.visitInsn(opcode)
//        sb.append("(")
//            .append(opcode)
//            .append(")")
//    }
//
//
//    override fun visitIntInsn(opcode: Int, operand: Int) {
//        super.visitIntInsn(opcode, operand)
//        sb.append("(")
//            .append(opcode)
//            .append(",")
//            .append(operand)
//            .append(")")
//    }
//
//
//    override fun visitLdcInsn(cst: Any?) {
//        super.visitLdcInsn(cst)
//        sb.append("(")
//            .append(cst)
//            .append(")")
//    }
//
//
//    override fun visitVarInsn(opcode: Int, `var`: Int) {
//        super.visitVarInsn(opcode, `var`)
//        sb.append("(")
//            .append(opcode)
//            .append(",")
//            .append(`var`)
//            .append(")")
//    }
//
//    override fun visitLabel(label: Label?) {
//        super.visitLabel(label)
//        sb.append("(")
//            .append(label)
//            .append(")")
//    }
//
//    override fun visitLineNumber(line: Int, start: Label?) {
//        super.visitLineNumber(line, start)
//        sb.append("(")
//            .append(line)
//            .append(",")
//            .append(start)
//            .append(")")
//    }
//
//    override fun visitLocalVariable(
//        name: String?,
//        desc: String?,
//        signature: String?,
//        start: Label?,
//        end: Label?,
//        index: Int
//    ) {
//        super.visitLocalVariable(name, desc, signature, start, end, index)
//        sb.append("(")
//            .append(name)
//            .append(",")
//            .append(desc)
//            .append(",")
//            .append(signature)
//            .append(",")
//            .append(start)
//            .append(",")
//            .append(end)
//            .append(",")
//            .append(index)
//            .append(")")
//    }
}