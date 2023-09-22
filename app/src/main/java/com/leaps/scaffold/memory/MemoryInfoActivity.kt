package com.leaps.scaffold.memory

import android.app.ActivityManager
import android.content.Context
import android.os.Bundle
import android.util.Log
import com.leaps.scaffold.BaseActivity
import com.leaps.scaffold.databinding.ActivityMemoryInfoBinding

class MemoryInfoActivity : BaseActivity() {

    private val binding: ActivityMemoryInfoBinding by lazy {
        ActivityMemoryInfoBinding.inflate(
            layoutInflater
        )
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        printMemoryInfo()
        var str = "abc"
        for (i in 0..9999) {
            str += "def$i"
        }
        printMemoryInfo()
        Runtime.getRuntime().gc()
        printMemoryInfo()
    }

    /**
     * 程序运行时，内存是慢慢的从操作系统那里挖来的，基本上是用多少挖多少（最多能挖maxMemory），
     * 但实际上JVM肯定是会稍微多挖一点的，这多出来的就是freeMemory()。
     */
    private fun printMemoryInfo() {
        val freeMemory = Runtime.getRuntime().freeMemory() / 1024 / 1024
        val maxMemory = Runtime.getRuntime().maxMemory() / 1024 / 1024
        val totalMemory = Runtime.getRuntime().totalMemory() / 1024 / 1024
        Log.i(
            "(RUNTIME)",
            "当前进程中 jvm 中的可用内存" + freeMemory + "MB"
        )
        Log.i("(RUNTIME) ", "当前进程能从操作系统那挖来的最大内存" + maxMemory + "MB");
        Log.i(
            "(RUNTIME) ",
            "当前进程 JVM 占用的总内存: " + totalMemory + "MB"
        )

        Log.i(
            "(RUNTIME) ",
            "实际可用内存: " + (maxMemory - totalMemory + freeMemory) + "MB"
        )

        val ams = getSystemService(Context.ACTIVITY_SERVICE) as ActivityManager
        val memoryInfo = ActivityManager.MemoryInfo()
        ams.getMemoryInfo(memoryInfo)
        Log.i(
            "(RUNTIME) ",
            "AMS Memory Info: ${memoryInfo.String()}"
        )

        Log.i(
            "(RUNTIME) ",
            "AMS Memory Class: ${ams.memoryClass}"
        )
    }

    fun ActivityManager.MemoryInfo.String(): String {
        return "availMem:${availMem / 1024 / 1024}MB,totalMem:${totalMem / 1024 / 1024}MB,threshold:${threshold / 1024 / 1024}MB,lowMemory:$lowMemory"
    }

}