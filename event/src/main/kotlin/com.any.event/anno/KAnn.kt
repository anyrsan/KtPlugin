package com.any.event.anno

/**
 *
 * @author any
 * @time 2019/07/29 19.53
 * @details
 */
@Target(AnnotationTarget.CLASS, AnnotationTarget.FUNCTION)
@Retention(AnnotationRetention.RUNTIME)
annotation class KAnn(val flag: Int = 0, val data: String) {
}