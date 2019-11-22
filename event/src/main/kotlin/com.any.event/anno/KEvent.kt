package com.any.event.anno

/**
 *
 * @author any
 * @time 2019/07/29 19.51
 * @details
 */
@Target(AnnotationTarget.CLASS, AnnotationTarget.FUNCTION)
@Retention(AnnotationRetention.RUNTIME)
annotation class KEvent(vararg val value: String)