package com.dema.versatile.flashlight.main.base;

import android.content.Intent;
import android.os.Bundle;

import com.dema.versatile.flashlight.SplashActivity;
import com.dema.versatile.lib.tool.SessionActivity;

public abstract class BaseActivity extends SessionActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (null != savedInstanceState) {
            Intent intent = new Intent(this, SplashActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
            finish();
            return;
        }
    }
}
