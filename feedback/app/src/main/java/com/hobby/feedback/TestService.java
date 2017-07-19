package com.hobby.feedback;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.IBinder;
import android.support.annotation.Nullable;

import com.hobby.uninstall_feedback.UninstallInjector;

/**
 * Created by chenyichang on 2017/6/23.
 */

public class TestService extends Service {

    @Override
    public void onCreate() {
        super.onCreate();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {


        if (intent != null) {


            int type = intent.getIntExtra("type", 0);

            if (type == 0) {
                UninstallInjector.cancel(TestService.this);
            } else if (type == 1) {
                UninstallInjector.injectUrl(TestService.this, intent.getStringExtra("url"));
            }
        }


        return super.onStartCommand(intent, flags, startId);
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    public static void start(Context context, String url) {

        Intent intent = new Intent(context, TestService.class);
        intent.putExtra("url", url);
        intent.putExtra("type", 1);
        context.startService(intent);
    }

    public static void cancel(Context context) {
        Intent intent = new Intent(context, TestService.class);
        intent.putExtra("type", 0);
        context.startService(intent);
    }
}
