package com.any.ktplugin

import android.view.View
import java.util.*

/**
 *
 * @author any
 * @time 2019/07/05 09.42
 * @details
 */
open class NDoubleClickListener(val callBack: (view: View) -> Unit) : View.OnClickListener {


   private var lastClickTime = 0L

    companion object {
        const val MIN_CLICK_DELAY_TIME = 1000
    }

    override fun onClick(v: View) {
        val currentTime = Calendar.getInstance().timeInMillis
        if (currentTime - lastClickTime > MIN_CLICK_DELAY_TIME) {
            lastClickTime = currentTime
            callBack(v)
        }
    }



}


  fun View.viewOnClick( nClick: (v: View) -> Unit) {
    setOnClickListener(object : NDoubleClickListener(nClick) {})
}

