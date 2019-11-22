package com.any.event.log

import android.util.Log
/**
 *
 * @author any
 * @time 2019/07/31 10.35
 * @details
 */
object Log {

    private val TAG = "msg"
    private val debug = true

    fun e(msg: String) {
        if (debug) {
            Log.e(TAG, msg)
        }
    }
}