package com.any.ktplugin

import android.util.Log
import com.any.event.anno.KEvent

/**
 *
 * @author any
 * @time 2019/07/29 16.59
 * @details
 */
class DoMain {


    @KEvent("有返回值的，也加下注解，看会不会有异常")
    fun doMain(): Int {
        val a = 1
        val b = 2
        val c = a + b
        return c
    }

    fun todo() {
        val re = doMain()
        Log.e("msg", "re... $re")
    }

}