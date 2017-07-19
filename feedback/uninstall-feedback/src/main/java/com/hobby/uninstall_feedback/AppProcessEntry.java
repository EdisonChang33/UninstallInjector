package com.hobby.uninstall_feedback;

import android.content.Context;
import android.os.Process;
import android.text.TextUtils;
import android.util.Log;

import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.lang.reflect.Method;


/**
 * Created by chenyichang on 2017/6/23.
 */

public class AppProcessEntry {

    //native callback
    public static void setProcessName(String name) {
        try {
            if (SysUtils.LOG_ENABLE) {
                Log.d(UninstallInjector.TAG, "setProcessName = " + name);
            }

            Class<Process> clazz = android.os.Process.class;
            Method method = clazz.getMethod("setArgV0", String.class);
            method.invoke(null, name);

        } catch (Exception e) {
            if (SysUtils.LOG_ENABLE) {
                e.printStackTrace();
            }
        }
    }

}
