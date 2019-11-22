package com.any.gradle.common

/**
 *
 * @author any
 * @time 2019/07/29 19.26
 * @details
 */
class ConvertMap private constructor() {

    //注解
    val aMap by lazy { mutableMapOf<String, String>() }
    // 包名
    val pMap by lazy { mutableMapOf<String, String>() }
    // 排除的类名   com.any.bn.MyClass
    val cMap by lazy { mutableMapOf<String, String>() }
    // 指定的类名
    val mMap by lazy { mutableMapOf<String, String>() }
    // 指定的注解事件,添加监听
    val evMap by lazy { mutableMapOf<String, String>() }


    companion object {
        private val cmp by lazy { ConvertMap() }
        fun getInstance(): ConvertMap {
            return cmp
        }
    }

    fun initMap(
        annoArray: Array<String>?,
        packageName: Array<String>?,
        includeClass: Array<String>?,
        excludeClass: Array<String>?,
        eventList: Array<String>?
    ) {
        annoArray?.forEach {
            aMap[it] = it
        }

        packageName?.forEach {
            pMap[it] = it
        }

        excludeClass?.forEach {
            cMap[it] = it
        }

        includeClass?.forEach {
            mMap[it] = it
        }

        eventList?.forEach {
            evMap[it] = it
        }

    }


}

