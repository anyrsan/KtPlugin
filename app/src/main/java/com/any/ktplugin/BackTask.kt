@file:Suppress("unused")

package com.any.testgradle

import android.os.Handler
import android.os.Looper
import java.lang.ref.WeakReference
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors
import java.util.concurrent.Future
import java.util.concurrent.ThreadFactory
import java.util.concurrent.atomic.AtomicLong

/**
 *
 * @author any
 * @time 2019/07/25 11.03
 * @details
 */

// 处理UI 提切换线程
fun <T> T.runOnUiThread(f: T.() -> Unit) {
    if (Looper.getMainLooper() == Looper.myLooper()) f() else ContextHandler.handler.post { f() }
}

//提供 主线的 handler 对象
private object ContextHandler {
    val handler = Handler(Looper.getMainLooper())
}

//上下文弱引用
class AsyncContextTask<T>(val weak: WeakReference<T>)

// 函数传回值
fun <T> AsyncContextTask<T>.onComplete(f: (t: T?) -> Unit) {
    val ref = weak.get()
    if (Looper.getMainLooper() == Looper.myLooper()) f(ref) else ContextHandler.handler.post { f(ref) }
}



// 函数调用
fun <T> T.doAsyncTask(task: AsyncContextTask<T>.() -> Unit): Future<Unit> {
    val context = AsyncContextTask(WeakReference(this))
    return BackgroundExecutor.submit {
        return@submit try {
            context.task()
        } catch (e: Throwable) {
            println(e)
            e.printStackTrace()
        }
    }
}


//fun <T, R> AsyncContextTask<T>.onCompleteR(f: (t: T?) -> R) {
//    val ref = weak.get()
//    if (Looper.getMainLooper() == Looper.myLooper()) f(ref) else ContextHandler.handler.post { f(ref) }
//}

// 函数调用
//fun <T, R> T.doAsyncResultTask(task: AsyncContextTask<T>.() -> R): Future<R> {
//    val context = AsyncContextTask(WeakReference(this))
//    return BackgroundExecutor.submit {
//        try {
//            context.task()
//        } catch (e: Exception) {
//            e.printStackTrace()
//            throw e
//        }
//    }
//}


internal object BackgroundExecutor {
    private val executor: ExecutorService =
        Executors.newScheduledThreadPool(2 * Runtime.getRuntime().availableProcessors(), MyThreadFactory("BTE"))

    fun <T> submit(task: () -> T): Future<T> = executor.submit(task)
}

internal class MyThreadFactory(private val prefix: String) : ThreadFactory {
    private val atom = AtomicLong()
    override fun newThread(r: Runnable): Thread {
        val value = atom.incrementAndGet()
        val name = StringBuffer().append(prefix).append("-").append(value).toString()
        val thread = Thread(r, name)
        thread.isDaemon = true
        return thread
    }
}

