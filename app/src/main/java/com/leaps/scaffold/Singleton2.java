package com.leaps.scaffold;

import android.util.Log;

public class Singleton2 {

    private int curNum = 0;
    private final int maxNum = 100;
    private final Object lock = new Object();

    private static class SingleInstance {
        public final static Singleton2 mINSTANCE = new Singleton2();
    }

    private Singleton2() {
    }

    public static Singleton2 getInstance() {
        return SingleInstance.mINSTANCE;
    }

    public void test() {
        Thread thread1 = new Thread(new Runnable() {
            @Override
            public void run() {
                while (curNum < maxNum) {
                    synchronized (lock) {
                        if (curNum % 2 == 0) {
                            Log.i("Singleton2", "thread1:" + curNum);
                            curNum++;
                            lock.notify();
                            try {
                                lock.wait();
                            } catch (InterruptedException e) {
                                break;
                            }
                        }
                    }
                }
            }
        });
        Thread thread2 = new Thread(new Runnable() {
            @Override
            public void run() {
                while (curNum < maxNum) {
                    synchronized (lock) {
                        if (curNum % 2 == 1) {
                            Log.i("Singleton2", "thread2:" + curNum);
                            curNum++;
                            lock.notify();
                            try {
                                lock.wait();
                            } catch (InterruptedException e) {
                                break;
                            }
                        }
                    }
                }
            }
        });
        thread1.start();
        thread2.start();
    }
}
