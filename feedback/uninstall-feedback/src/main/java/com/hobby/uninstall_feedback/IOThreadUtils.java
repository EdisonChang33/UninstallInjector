package com.hobby.uninstall_feedback;

import android.os.Handler;
import android.os.HandlerThread;
import android.os.Looper;

/**
 * Created by chenyichang on 2017/6/23.
 */

public class IOThreadUtils {

    private static Handler sIOHandler = null;
    private static final Object sIOHandlerLock = new Object();

    private static Handler getIOThreadHandler() {
        synchronized (sIOHandlerLock) {
            if (sIOHandler == null) {
                HandlerThread t = new HandlerThread("io-handler-thread");
                t.start();
                sIOHandler = new Handler(t.getLooper());
            }
            return sIOHandler;
        }
    }

    public static void post2IO(Runnable r) {
        getIOThreadHandler().post(r);
    }

    public static void postDelayed2IO(Runnable r, long delayMillis) {
        getIOThreadHandler().postDelayed(r, delayMillis);
    }

    public static void cancelIOTask(Runnable r) {
        getIOThreadHandler().removeCallbacks(r);
    }

    public static boolean runningOnIO() {
        return getIOThreadHandler().getLooper() == Looper.myLooper();
    }
}
