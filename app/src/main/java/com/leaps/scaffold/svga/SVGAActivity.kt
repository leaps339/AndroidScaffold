package com.leaps.scaffold.svga

import android.os.Bundle
import com.gmlive.svgaplayer.util.load
import com.gmlive.svgaplayer.util.loadAsset
import com.leaps.scaffold.BaseActivity
import com.leaps.scaffold.R
import com.leaps.scaffold.databinding.ActivitySvgaBinding

class SVGAActivity : BaseActivity() {

    private val binding: ActivitySvgaBinding by lazy { ActivitySvgaBinding.inflate(layoutInflater) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        binding.svgaAssest.loadAsset("heartbeat.svga")
        binding.svgaRaw.load(R.raw.alarm)
        binding.svgaNetwork.load("https://github.com/yyued/SVGA-Samples/blob/master/posche.svga?raw=true")
    }

}