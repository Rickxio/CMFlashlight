package com.dema.versatile.lib.alive;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.PowerManager;
import android.view.Gravity;
import android.view.Window;
import android.view.WindowManager;

import androidx.appcompat.app.AppCompatActivity;

import com.dema.versatile.lib.CMLibFactory;
import com.dema.versatile.lib.R;
import com.dema.versatile.lib.utils.UtilsLog;

public class AlivePixelActivity extends AppCompatActivity {
    private static Activity sAliveActivity = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (null != savedInstanceState && isScreenOn()) {
            finish();
            return;
        }
        setContentView(R.layout.activity_empty);
        UtilsLog.aliveLog("activity", null);
        UtilsLog.send();

        try {
            Window window = getWindow();
            window.setGravity(Gravity.LEFT | Gravity.TOP);
            window.addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
            window.addFlags(WindowManager.LayoutParams.FLAG_DISMISS_KEYGUARD);
            WindowManager.LayoutParams attributes = window.getAttributes();
            attributes.x = 0;
            attributes.y = 0;
            attributes.width = 1;
            attributes.height = 1;
            window.setAttributes(attributes);
        } catch (Exception e) {
            e.printStackTrace();
        }

        sAliveActivity = this;
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        finish();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        sAliveActivity = null;
    }

    public static void startAliveActivity(Context context) {
        if (null == context || isScreenOn() || sAliveActivity != null)
            return;

        try {
            Intent intent = new Intent(context, AlivePixelActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            context.startActivity(intent);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static boolean isScreenOn() {
        PowerManager powerManager = (PowerManager) CMLibFactory.getApplication().getSystemService(Context.POWER_SERVICE);
        if (powerManager == null) {
            return false;
        }
        return powerManager.isInteractive();
    }

    public static void finishAliveActivity() {
        try {
            if (null != sAliveActivity) {
                sAliveActivity.finish();
                sAliveActivity = null;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
