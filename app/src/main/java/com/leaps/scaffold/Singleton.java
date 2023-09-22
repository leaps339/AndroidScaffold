package com.leaps.scaffold;

import android.content.ComponentName;
import android.content.Context;
import android.util.Log;

import com.blankj.utilcode.util.LogUtils;
import com.blankj.utilcode.util.ToastUtils;
import com.leaps.scaffold.memory.MemoryInfoActivity;

public class Singleton {

    private static volatile Singleton INSTANCE = null;
    private static Object lock = new Object();

    private Singleton() {
    }

    public static Singleton getInstance() {
        if (INSTANCE == null) {
            synchronized (lock) {
                if (INSTANCE == null) {
                    INSTANCE = new Singleton();
                }
            }
        }
        return INSTANCE;
    }

    public void testNull(Context context) {
        MainActivity.ItemBean bean = new MainActivity.ItemBean("hahah", new ComponentName(context, MemoryInfoActivity.class));
        toNull(bean);
        Log.i("Singleton", bean.getTitle());
    }

    public void toNull(MainActivity.ItemBean bean) {
        bean.component1();
        bean = null;
        if (bean == null) {
            Log.i("Singleton", "null");
        }
    }

}
