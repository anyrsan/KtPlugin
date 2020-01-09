package com.any.gradle.data

/**
 *
 * @author any
 * @time 2019/07/29 15.15
 * @details
 */

open class CustomData {
    var debug: Boolean? = null
    var excludeClass: Array<String>? = null
    var includeClass: Array<String>? = null
    var packageName: Array<String>? = null
    var annotationName: Array<String>? = null
    var eventList:Array<String>?=null


    override fun toString(): String {
        return "[debug=$debug,includeClass=$includeClass,excludeClass=$excludeClass,packageName=$packageName,annotationName=$annotationName,eventList=$eventList]"
    }
}