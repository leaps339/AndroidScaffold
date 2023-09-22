package com.leaps.scaffold;

public class AlternatePrintingNumbers {
    private static final int MAX_NUM = 100;
    private static volatile int currentNum = 1;
    private static final Object lock = new Object();

    public static void executor() {
        Thread thread1 = new Thread(new Runnable() {
            @Override
            public void run() {
                synchronized (lock) {
                    while (currentNum <= MAX_NUM) {
                        if (currentNum % 2 == 1) {
                            System.out.println("Thread 1: " + currentNum++);
                            lock.notify();
                        } else {
                            try {
                                lock.wait();
                            } catch (InterruptedException e) {
                                e.printStackTrace();
                            }
                        }
                    }
                }
            }
        });

        Thread thread2 = new Thread(new Runnable() {
            @Override
            public void run() {
                synchronized (lock) {
                    while (currentNum <= MAX_NUM) {
                        if (currentNum % 2 == 0) {
                            System.out.println("Thread 2: " + currentNum++);
                            lock.notify();
                        } else {
                            try {
                                lock.wait();
                            } catch (InterruptedException e) {
                                e.printStackTrace();
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
