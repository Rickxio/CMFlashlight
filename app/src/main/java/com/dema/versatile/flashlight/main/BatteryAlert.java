package com.dema.versatile.flashlight.main;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.widget.FrameLayout;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import org.json.JSONObject;

import com.dema.versatile.flashlight.AdKey;
import com.dema.versatile.flashlight.R;
import com.dema.versatile.flashlight.utils.DateUtil;
import com.dema.versatile.lib.utils.UtilsJson;
import com.dema.versatile.lib.utils.UtilsLog;
import com.dema.versatile.mediation.CMMediationFactory;
import com.dema.versatile.mediation.core.in.IMediationMgr;

/**
 * Created by wangyu on 2019/8/15.
 */
public class BatteryAlert extends AppCompatActivity {

    public static void start(Context context, String source) {
        if (context == null) {
            return;
        }
        Intent starter = new Intent(context, BatteryAlert.class);
        if (!(context instanceof Activity)) {
            starter.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        }
        context.startActivity(starter);
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.alert_battery);
        JSONObject jsonObject = new JSONObject();
        UtilsJson.JsonSerialization(jsonObject, "cur_time", "" + DateUtil.getTimeDetailStr(System.currentTimeMillis()));
        UtilsLog.log("alert", "show", jsonObject);
        UtilsLog.alivePull("alert", "battery");
        findViewById(R.id.iv_close).setOnClickListener(v -> finish());
        findViewById(R.id.bt_save).setOnClickListener(v -> {
            UtilsLog.log("alert", "click", null);
            finish();
        });
        FrameLayout flAd = findViewById(R.id.fl_ad);
        IMediationMgr instance = CMMediationFactory.getInstance().createInstance(IMediationMgr.class);
        instance.showAdView(AdKey.VALUE_STRING_VIEW_ALERT, flAd, getApplicationContext());
    }
}
