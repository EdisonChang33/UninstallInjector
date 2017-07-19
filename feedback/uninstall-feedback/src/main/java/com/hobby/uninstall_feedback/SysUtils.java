package com.hobby.uninstall_feedback;

import android.content.Context;
import android.text.TextUtils;

import java.util.Arrays;
import java.util.List;
import java.util.Scanner;

/**
 * Created by chenyichang on 2017/6/23.
 */
public class SysUtils {

    static final boolean LOG_ENABLE = BuildConfig.DEBUG;

    public static int getFeedbackDaemonPid(String processName) {
        try {

            //通过ps查找弹卸载反馈的进程
            Process p = Runtime.getRuntime().exec("ps");
            p.waitFor();
            Scanner scanner = new Scanner(p.getInputStream());
            List<String> tags;
            int pidRow = -1;
            while (scanner.hasNextLine()) {
                String scannerStr = scanner.nextLine();
                if (scannerStr.contains(processName) || scannerStr.toLowerCase().contains("pid")) {
                    while (scannerStr.contains("  ")) {
                        scannerStr = scannerStr.replaceAll("  ", " ").trim();
                    }
                    String pidStr = null;
                    int pid = -1;
                    if (scannerStr.toLowerCase().contains("pid")) {
                        tags = Arrays.asList(scannerStr.toLowerCase().split(" "));
                        pidRow = tags.indexOf("pid");//pid所在的列号
                    } else if (pidRow != -1) {
                        pidStr = scannerStr.split(" ")[pidRow];
                        if (!TextUtils.isEmpty(pidStr)) {
                            pid = Integer.valueOf(pidStr);
                            return pid;
                        }
                    }
                }
            }
        } catch (Exception e) {

        }
        return -1;
    }

    public static boolean isFeedbackProcessExist(String processName) {
        boolean bret = false;
        try {
            Process p = Runtime.getRuntime().exec("ps");
            p.waitFor();
            Scanner scanner = new Scanner(p.getInputStream());
            while (scanner.hasNextLine()) {
                if (scanner.nextLine().contains(processName)) {
                    bret = true;
                    break;
                }
            }
        } catch (Exception e) {
            if (LOG_ENABLE) {
                e.printStackTrace();
            }
        }

        return bret;
    }


    private static Context sContext;

    public static Context getApplicationContext() {
        if (sContext == null) {
            synchronized (LoneProcess.class) {
                if (sContext == null) {
                    try {
                        Class<?> clazz = Class.forName("android.app.ActivityThread");
                        java.lang.reflect.Method method = clazz.getDeclaredMethod("currentApplication");
                        sContext = (Context) method.invoke(null);
                    } catch (Exception e) {

                    }
                }
            }
        }
        return sContext;
    }
}
