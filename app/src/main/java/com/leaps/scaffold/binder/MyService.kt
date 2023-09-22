package com.leaps.scaffold.binder

import android.app.Service
import android.content.Intent
import android.os.Handler
import android.os.IBinder
import android.os.Looper
import android.os.Message
import android.os.Messenger
import android.os.RemoteException
import android.util.Log
import androidx.core.os.bundleOf

class MyService : Service() {

    private val TAG = "LeapsService"
    private var count = 0

    private val mMessenger: Messenger = Messenger(object : Handler(Looper.getMainLooper()) {
        override fun handleMessage(msg: Message) {
            when (msg.what) {
                1 -> {
                    val num = msg.data?.getInt("num") ?: 0
                    count += num
                    Log.i(TAG, "Received from client: $num")
                    val replyMsg = Message.obtain()
                    val replyBundle = bundleOf()
                    replyBundle.putInt("count", count)
                    replyMsg.data = replyBundle
                    try {
                        msg.replyTo.send(replyMsg)
                    } catch (e: RemoteException) {
                        e.printStackTrace()
                    }
                }
            }
        }
    })

    override fun onCreate() {
        super.onCreate()
        Log.i(TAG, "onCreate")
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        Log.i(TAG, "onStartCommand")
        return super.onStartCommand(intent, flags, startId)
    }

    override fun onBind(intent: Intent?): IBinder? {
        Log.i(TAG, "onBind")
        return mMessenger.binder
    }

    override fun onUnbind(intent: Intent?): Boolean {
        Log.i(TAG, "onUnbind")
        return super.onUnbind(intent)
    }

    override fun onDestroy() {
        super.onDestroy()
        Log.i(TAG, "onDestroy")
    }

}