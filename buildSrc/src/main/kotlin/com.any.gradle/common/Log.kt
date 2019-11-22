package com.any.gradle.common

import com.android.build.gradle.internal.LoggerWrapper
import com.any.gradle.MyCustomPlugin

/**
 *
 * @author any
 * @time 2019/07/29 15.17
 * @details
 */
object Log {

    private val logger by lazy { LoggerWrapper.getLogger(MyCustomPlugin::class.java) }

    private var isDebug = true

    private val MESSAGE = """
        ###################################################################################################################################################
        ###   <msg>                                                                                                                                            
        ###################################################################################################################################################
    """.trimIndent()

    fun setDebug(debug: Boolean?) {
        isDebug = debug ?: true
    }

    fun w(msg: String) {
        if (isDebug)
            logger.warning(MESSAGE.replace("<msg>", msg))
    }


}