package com.any.ktplugin

import android.util.Log
import org.junit.Test

import org.junit.Assert.*
import java.io.File

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * See [testing documentation](http://d.android.com/tools/testing).
 */
class ExampleUnitTest {
    @Test
    fun addition_isCorrect() {
        assertEquals(4, 2 + 2)
    }


    @Test
    fun testR() {
        val rtm = File("wa")

        val b = rtm.name.let {
            it != "R" && it.startsWith("R$") && it != "BuildConfig"
        }

        println("b... $b")
    }


    @Test
    fun testSubString() {
        val string = "com/any/bn/MyClass.class"

        val newS = string.replace("/", ".").replace(".class", "")
        println(newS)

        val cL = newS.substringAfterLast(".")
        println(cL)

        val cP = newS.substringBeforeLast(".")
        println(cP)

    }
}