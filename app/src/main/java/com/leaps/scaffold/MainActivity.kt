package com.leaps.scaffold

import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.os.Bundle
import android.os.Looper
import android.os.Message
import android.util.Log
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.blankj.utilcode.util.ClickUtils
import com.leaps.scaffold.anim.AnimActivity
import com.leaps.scaffold.binder.BinderActivity
import com.leaps.scaffold.databinding.ActivityMainBinding
import com.leaps.scaffold.keyboard.KeyboardActivity
import com.leaps.scaffold.memory.MemoryInfoActivity
import com.leaps.scaffold.svga.SVGAActivity
import com.leaps.scaffold.utils.FpsMonitor
import com.leaps.scaffold.utils.LooperPrinter
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import java.io.RandomAccessFile
import kotlin.concurrent.thread


class MainActivity : AppCompatActivity() {

    private val binding: ActivityMainBinding by lazy { ActivityMainBinding.inflate(layoutInflater) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        binding.widgetsList.layoutManager = LinearLayoutManager(this)
        binding.widgetsList.adapter = WidgetsAdapter(createWidgetsComponent())
        binding.root.post {
            Log.i(
                "shaisvhasviav",
                "refreshRate:${ViewCompat.getDisplay(binding.root)?.refreshRate}"
            )
        }
//        FpsMonitor.startMonitor {
//            Log.i("shaisvhasviav", "FPS:$it")
//            Log.i("shaisvhasviav", "FPS:$it")
//        }
//        Looper.getMainLooper().setMessageLogging(LooperPrinter())
        AlternatePrintingNumbers.executor()
        Singleton.getInstance().testNull(this)

        GlobalScope.launch(CoroutineExceptionHandler { context, throwable ->
            Log.i("shaisvhasviav", throwable.message.toString())
        }) {
            Log.i("shaisvhasviav", "1")
            launch {
                Log.i("shaisvhasviav", "2")
                throw IllegalArgumentException("hahaha")
            }
            Log.i("shaisvhasviav", "3")
        }
        Singleton2.getInstance().test()
    }

    override fun onResume() {
        super.onResume()
        binding.toolbar.post {
            Log.i("avaisvasv", "${binding.toolbar.measuredWidth},${binding.toolbar.width}")
        }
    }

    private fun createWidgetsComponent(): List<ItemBean> {
        val beans = mutableListOf<ItemBean>()
        beans.add(ItemBean("内存占用", ComponentName(this, MemoryInfoActivity::class.java)))
        beans.add(ItemBean("SVGA控件", ComponentName(this, SVGAActivity::class.java)))
        beans.add(ItemBean("动画演示", ComponentName(this, AnimActivity::class.java)))
        beans.add(ItemBean("软键盘监听", ComponentName(this, KeyboardActivity::class.java)))
        beans.add(ItemBean("跨进程通信", ComponentName(this, BinderActivity::class.java)))
        return beans
    }

    class WidgetsAdapter(private val beans: List<ItemBean>) :
        RecyclerView.Adapter<WidgetViewHolder>() {
        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): WidgetViewHolder {
            val view = LayoutInflater.from(parent.context)
                .inflate(R.layout.recycle_main_item_widget, parent, false)
            return WidgetViewHolder(view)
        }

        override fun getItemCount(): Int {
            return beans.size
        }

        override fun onBindViewHolder(viewHolder: WidgetViewHolder, position: Int) {
            viewHolder.bindView(beans[position])
        }
    }

    class WidgetViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        fun bindView(bean: ItemBean) {
            itemView.findViewById<TextView>(R.id.title).text = bean.title
            itemView.setOnClickListener {
                it.context.startActivity(Intent().apply { component = bean.component })
            }
        }
    }

    data class ItemBean(val title: String, val component: ComponentName)
}