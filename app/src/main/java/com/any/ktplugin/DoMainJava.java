package com.any.ktplugin;

import android.util.Log;
import com.any.event.anno.KEvent;

/**
 * @author any
 * @time 2019/07/31 14.52
 * @details
 */
public class DoMainJava {


    @KEvent("这个应该是不会被处理的")
    public int doMain(){
        int a =1;
        int b =3;
        int c = a+b;
        return c;
    }


    public void test(){
        int r = doMain();
        Log.e("msg","..."  +r);

    }


}
