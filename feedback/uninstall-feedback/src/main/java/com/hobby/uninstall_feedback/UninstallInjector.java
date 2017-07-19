package com.hobby.uninstall_feedback;

import android.content.Context;
import android.os.Build;
import android.util.Log;

/**
 * Created by chenyichang on 2017/6/22.
 */

public class UninstallInjector {

    // Used to load the 'native-lib' library on application startup.

    static final String TAG = "UninstallInjector";

    //卸载调查native 进程名称
    private static final String DAEMON_PROCESS_SUFFIX = ":feedback";

    private static int processId = 0;

    static {
        try {
            System.loadLibrary("uninstall-lib");
        } catch (Throwable e) {
            if (SysUtils.LOG_ENABLE) {
                e.printStackTrace();
            }
        }
    }

    public static void injectUrl(final Context context, final String url) {
        if (processId > 0) {
            return;
        }

        IOThreadUtils.post2IO(new Runnable() {
            @Override
            public void run() {
                String processName = context.getPackageName() + DAEMON_PROCESS_SUFFIX;
                boolean exist = SysUtils.isFeedbackProcessExist(processName);
                if (!exist) {
                    processId = openUrlWhenUninstall(context.getApplicationInfo().dataDir, url, processName);
                }
            }
        });

    }

    public static void cancel(final Context context) {

        IOThreadUtils.post2IO(new Runnable() {
            @Override
            public void run() {

                try {
                    if (processId != 0) {
                        if (SysUtils.LOG_ENABLE) {
                            Log.d(TAG, "cancel processId = " + processId);
                        }
                        android.os.Process.killProcess(processId);
                        processId = 0;
                    }

                    Thread.sleep(1000);

                    String processName = context.getPackageName() + DAEMON_PROCESS_SUFFIX;
                    int pid = SysUtils.getFeedbackDaemonPid(processName);
                    if (pid != -1) {
                        if (SysUtils.LOG_ENABLE) {
                            Log.d(TAG, "cancel getRuntime ps pid = " + pid);
                        }
                        android.os.Process.killProcess(pid);
                    }

                } catch (Throwable e) {
                    if (SysUtils.LOG_ENABLE) {
                        e.printStackTrace();
                    }
                }
            }
        });

    }

    private static int openUrlWhenUninstall(String dirStr, String openUrl, String processName) {

        if (SysUtils.LOG_ENABLE) {
            Log.d(UninstallInjector.TAG, "openUrlWhenUninstall processName = " + processName);
        }

        try {
            return openUrlWhenUninstallNative(dirStr, openUrl, Build.BRAND, Build.VERSION.SDK_INT, processName);
        } catch (Exception e) {
            if (SysUtils.LOG_ENABLE) {
                e.printStackTrace();
            }
        }
        return -1;
    }


    private static native int openUrlWhenUninstallNative(String dirStr, String data, String brand, int apiLevel, String processName);

}
