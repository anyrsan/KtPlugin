package com.any.ktplugin

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import com.any.an.MyAnClass
import com.any.bn.MyClass
import com.any.event.anno.KAnn
import com.any.event.anno.KEvent
import com.any.ktplugin.bean.DataModel
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {

    private val doMain by lazy { DoMain() }

    private val myClass by lazy { MyClass() }

    private val anClass by lazy { MyAnClass() }

    private val dataModel by lazy { DataModel() }

    private val dJava  by lazy { DoMainJava() }

    private val dJ by lazy { JDoMain() }


    @KAnn(data = "这个方法牛", flag = 123)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        //处理
        textview.viewOnClick {
            doMain.doMain()

            myClass.testLog()
        }

        anClass.testAar(aarTest)

        Log.w("msg","DataModel来试下： $dataModel")


        // 处理
        mv.setOnClickListener {
            doMain.todo()
            dJava.test()

            val url = dJ.api
        }




    }

    @KEvent("系统函数输出", "系统输出1", "系统函数2")
    @KAnn(data = "是吧", flag = 111)
    override fun onResume() {
        super.onResume()
    }


}
