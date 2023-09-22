package com.leaps.scaffold.anim

import android.animation.ObjectAnimator
import android.os.Bundle
import android.util.Log
import android.widget.ViewAnimator
import androidx.core.animation.addListener
import com.leaps.scaffold.BaseActivity
import com.leaps.scaffold.databinding.ActivityAnimBinding

class AnimActivity : BaseActivity() {

    private val binding: ActivityAnimBinding by lazy { ActivityAnimBinding.inflate(layoutInflater) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

    }

    override fun onResume() {
        super.onResume()
        binding.animText.post {
            Log.i(
                "hashciaschi",
                "x:${binding.animText.pivotX},y:${binding.animText.pivotY}"
            )
            Log.i(
                "hashciaschi",
                "width:${binding.animText.measuredWidth},height:${binding.animText.measuredHeight}"
            )
            ObjectAnimator.ofFloat(binding.animText, ViewAnimator.SCALE_X, 1F, 2F).apply {
                addListener(onEnd = {
                    Log.i(
                        "hashciaschi",
                        "anim end, width:${binding.animText.width},height:${binding.animText.height}"
                    )
                })
            }.start()
            ObjectAnimator.ofFloat(binding.animText, ViewAnimator.SCALE_Y, 1F, 2F).apply {
                addListener(onEnd = {
                    Log.i(
                        "hashciaschi",
                        "anim end, width:${binding.animText.width},height:${binding.animText.height}"
                    )
                })
            }.start()

        }
    }
}