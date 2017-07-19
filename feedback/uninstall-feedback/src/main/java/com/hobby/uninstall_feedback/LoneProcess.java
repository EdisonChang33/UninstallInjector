package com.hobby.uninstall_feedback;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.util.Log;

/**
 * Created by chenyichang on 2017/6/23.
 */

@Deprecated
public class LoneProcess {

    private static final String TAG = "LoneProcess";

    public static void main(String[] args) {

        Log.d(TAG, "LoneProcess call...");

        final Context context = SysUtils.getApplicationContext();

        if (context == null) {
            return;
        }

        String url = "http://www.baidu.com";
        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.setData(Uri.parse(url));
        context.startActivity(intent);

        new Thread(new Runnable() {
            @Override
            public void run() {
                for (int i = 0; i < 100; i++) {
                    try {
                        Thread.sleep(1000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        }, TAG + "-main").start();

    }

}
