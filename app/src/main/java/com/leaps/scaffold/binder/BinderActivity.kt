package com.leaps.scaffold.binder

import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.os.Bundle
import android.os.Handler
import android.os.IBinder
import android.os.Looper
import android.os.Message
import android.os.Messenger
import android.util.Log
import androidx.core.os.bundleOf
import com.blankj.utilcode.util.ToastUtils
import com.leaps.scaffold.BaseActivity
import com.leaps.scaffold.databinding.ActivityBinderBinding
import com.leaps.scaffold.utils.binding

class BinderActivity : BaseActivity() {

    private val binding: ActivityBinderBinding by binding()

    private var mServiceMessenger: Messenger? = null
    private val TAG = "LeapsService"
    private var mClientMessenger: Messenger = Messenger(object : Handler(Looper.getMainLooper()) {
        override fun handleMessage(msg: Message) {
            val count = msg.data?.getInt("count")
            ToastUtils.showShort(count.toString())
            Log.i(TAG, "Received from service: $count")
        }
    })

    private val mServiceConnection = object : ServiceConnection {
        override fun onServiceConnected(name: ComponentName?, service: IBinder?) {
            mServiceMessenger = Messenger(service)
        }

        override fun onServiceDisconnected(name: ComponentName?) {
            mServiceMessenger = null
        }

    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding.button.setOnClickListener {
            mServiceMessenger?.send(Message.obtain().apply {
                what = 1
                data = bundleOf("num" to 3)
                replyTo = mClientMessenger
            })
        }

        connectService()
    }

    private fun connectService() {
        Intent().apply {
            action = "com.leaps.scaffold.messenger.service"
            `package` = "com.leaps.scaffold"
        }.let {
            applicationContext.bindService(it, mServiceConnection, Context.BIND_AUTO_CREATE)
        }
    }

}