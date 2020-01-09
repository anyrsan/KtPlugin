package com.any.gradle.data

/**
 *
 * @author any
 * @time 2019/07/29 16.19
 * @details   类名，包名  mInit是针对类注解，如果类上有注解，则在初始化方法中添加相应的操作
 */
data class ClazzData(
    val pathName: String,
    val cName: String,
    val cPackage: String,
    var mInit: String?
)