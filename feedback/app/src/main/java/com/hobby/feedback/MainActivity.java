package com.hobby.feedback;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

import com.hobby.uninstall_feedback.UninstallInjector;

public class MainActivity extends AppCompatActivity {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        findViewById(R.id.start).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                UninstallInjector.injectUrl(getApplicationContext(), "http://www.baidu.com");
            }
        });

        findViewById(R.id.cancel).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                UninstallInjector.cancel(getApplicationContext());
            }
        });

        findViewById(R.id.daemon_start).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                TestService.start(MainActivity.this, "http://www.baidu.com");
            }
        });

        findViewById(R.id.daemon_cancel).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                TestService.cancel(MainActivity.this);
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
    }
}
