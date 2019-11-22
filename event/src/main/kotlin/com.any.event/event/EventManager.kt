package com.any.event.event

import com.any.event.log.Log


/**
 *
 * @author any
 * @time 2019/07/29 19.57
 * @details
 */
object EventManager {

    private val eMap = mutableMapOf<String, Long>()
    //执行这个方法加入数据
    fun addEvent(value: String, eventName: String) {
        Log.e("$eventName ...  $value")
    }

    //记录一个起始点时间
    fun addStartTimeEvent(eventName: String) {
        eMap[eventName] = System.currentTimeMillis()
    }

    //根据 事件名 ，算出 此方法的时间
    fun addEndTimeEvent(eventName: String) {
        val startTime = eMap[eventName] ?: 0
        Log.e("【执行完 $eventName 的时间：  ${System.currentTimeMillis() - startTime} ms】")
        //移除当前事件名
        eMap.remove(eventName)
    }


}