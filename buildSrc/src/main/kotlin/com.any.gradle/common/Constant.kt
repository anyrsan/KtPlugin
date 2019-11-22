package com.any.gradle.common

import com.any.gradle.data.ClazzData
import proguard.util.ConstantMatcher

/**
 *
 * @author any
 * @time 2019/07/29 17.42
 * @details 常量数据类
 */
object Constant {

    // 初始化  类注解
    const val INIT_EVENT = "<init>()V"

    fun containsEvent(desc: String?): Boolean {
        return ConvertMap.getInstance().evMap.containsKey(desc)
    }


    fun getEventName(clazzData: ClazzData, funName: String?): String {
        val sb = StringBuffer("[")
        sb.append(clazzData.cPackage)
        sb.append(".")
        sb.append(clazzData.cName)
        sb.append(".")
        sb.append(funName)
        sb.append("]")
        return sb.toString()
    }

    /**
     *     com.any.ktplugin.anno.KAnn
     *      Lcom/any/ktplugin/anno/KAnn;
     * 获取是否支持当前注解
     */
    fun containsAnnotation(desc: String?): Boolean {
        val newDesc = parseAnnoName(desc)
        return ConvertMap.getInstance().aMap.containsKey(newDesc)
    }


    private fun parseAnnoName(desc: String?): String? {
        return desc?.let {
            it.substring(1, it.length - 1).replace("/", ".")
        }
    }


    /**
     * 排除的类要全类名，主要是存在相同名字的类，但可以通过包名区别
     * 检查是否满足条件
     * 类名   com.any.bn.MyClass
     */
    fun containsClass(clazzData: ClazzData): Boolean {
        if (ConvertMap.getInstance().aMap.containsKey(clazzData.pathName)) {
            return false
        }
        if (ConvertMap.getInstance().mMap.containsKey(clazzData.pathName)) {
            return true
        }
        return ConvertMap.getInstance().pMap.containsKey(clazzData.cPackage) && !ConvertMap.getInstance().cMap.containsKey(
            clazzData.pathName
        )
    }


    /**
     * map 转为 String
     */
    fun getDataByMap(map: Map<String, List<String>>): String {
        val sb = StringBuffer()
        sb.append("[")
        map.keys.forEach {
            sb.append(it).append("={")
            val values = map[it]
            values?.forEach { value ->
                sb.append(value).append(",")
            }
            if (values != null) {
                sb.deleteCharAt(sb.length - 1)
            }
            sb.append("}")
            sb.append(",")
        }
        if (map.isNotEmpty()) {
            sb.deleteCharAt(sb.length - 1)
        }
        sb.append("]")
        return sb.toString()
    }


}