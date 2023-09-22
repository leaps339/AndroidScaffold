package com.leaps.scaffold

import java.util.concurrent.locks.ReentrantLock
import kotlin.concurrent.thread

@Volatile
var i = 0
val lock = ReentrantLock()
val MAX = 100

fun main() {
    val lock = ReentrantLock()
    val condition = lock.newCondition()

    val thread1 = Thread {
        lock.lock()
        try {
            for (i in 1..10 step 2) {
                println(i)
                condition.signal()
                condition.await()
            }
            condition.signal() // 释放另一个线程的 await
        } finally {
            lock.unlock()
        }
    }

    val thread2 = Thread {
        lock.lock()
        try {
            for (i in 2..10 step 2) {
                println(i)
                condition.signal()
                condition.await()
            }
            condition.signal() // 释放另一个线程的 await
        } finally {
            lock.unlock()
        }
    }

    thread1.start()
    thread2.start()
//    thread1.join()
//    thread2.join()
}