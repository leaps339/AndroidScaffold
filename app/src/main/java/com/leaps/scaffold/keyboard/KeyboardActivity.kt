package com.leaps.scaffold.keyboard

import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import android.util.Log
import com.blankj.utilcode.util.ToastUtils
import com.leaps.scaffold.BaseActivity
import com.leaps.scaffold.R
import com.leaps.scaffold.databinding.ActivityKeyboardBinding

class KeyboardActivity : BaseActivity() {

    private val keyboardHelper: KeyboardPopupWindowHelper by lazy { KeyboardPopupWindowHelper(this) }

    private val binding: ActivityKeyboardBinding by lazy {
        ActivityKeyboardBinding.inflate(
            layoutInflater
        )
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (!Settings.canDrawOverlays(this)) {
                getOverlayPermission()
            }
        }

        keyboardHelper.setOnKeyboardStatusChangeListener(object : OnKeyboardStatusChangeListener {
            override fun keyboardOpen(height: Float) {
                Log.i("MainActivity", "keyboardOpen:$height")
            }

            override fun keyboardClose() {
                Log.i("MainActivity", "keyboardClose")
            }

        })

        binding.button.setOnClickListener {
//            keyboardHelper.show(binding.text)
            ToastUtils.showShort("点击了")
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        keyboardHelper.release()
    }

    private fun getOverlayPermission() {
        val intent = Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION);
        intent.setData(Uri.parse("package:" + getPackageName()));
        startActivityForResult(intent, 0);
    }

}