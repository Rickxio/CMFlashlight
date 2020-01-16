package com.dema.versatile.flashlight.main.settings;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.RelativeLayout;

import androidx.appcompat.widget.SwitchCompat;
import androidx.appcompat.widget.Toolbar;

import org.json.JSONObject;

import com.dema.versatile.flashlight.Constants;
import com.dema.versatile.flashlight.R;
import com.dema.versatile.flashlight.main.AboutActivity;
import com.dema.versatile.flashlight.main.base.BaseActivity;
import com.dema.versatile.flashlight.main.function.AnimActivity;
import com.dema.versatile.flashlight.main.function.Function;
import com.dema.versatile.flashlight.utils.UtilsSetting;
import com.dema.versatile.lib.utils.UtilsJson;
import com.dema.versatile.lib.utils.UtilsLog;

import butterknife.OnClick;

public class SettingActivity extends BaseActivity {
    private Toolbar mToolbar = null;
    private SwitchCompat mSCStartup = null;
    private RelativeLayout mRLStartup = null;
    private RelativeLayout mRLPrivacy = null;
    private RelativeLayout mRLTerm = null;
    private static final String KEY = "setting";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting);

        UtilsLog.log(KEY, "create", null);

        mToolbar = findViewById(R.id.toolbar);
        mToolbar.setNavigationOnClickListener(v -> finish());

        mSCStartup = findViewById(R.id.sc_startup);
        mSCStartup.setChecked(UtilsSetting.isLightOnStartup(this));
        mSCStartup.setOnCheckedChangeListener((buttonView, isChecked) -> {
            JSONObject jsonObject = new JSONObject();
            UtilsJson.JsonSerialization(jsonObject, "value", isChecked);
            UtilsLog.log(KEY, "startup", jsonObject);
            UtilsSetting.setLightOnStartup(SettingActivity.this, isChecked);
        });

        mRLStartup = findViewById(R.id.rl_startup);
        mRLStartup.setOnClickListener(v -> mSCStartup.setChecked(!mSCStartup.isChecked()));

        mRLPrivacy = findViewById(R.id.rl_privacy);
        mRLPrivacy.setOnClickListener(v -> {
            UtilsLog.log(KEY,  "privacy", null);
            startSystemWebView(Constants.VALUE_STRING_PRIVACY);
        });

        mRLTerm = findViewById(R.id.rl_term);
        mRLTerm.setOnClickListener(v -> {
            UtilsLog.log(KEY,  "term", null);
            startSystemWebView(Constants.VALUE_STRING_TERM);
        });

        findViewById(R.id.rl_notification).setOnClickListener(v -> {
            Intent intent = new Intent(SettingActivity.this, NotificationSettingActivity.class);
            startActivity(intent);
        });

        findViewById(R.id.rl_rateus).setOnClickListener(v -> new RateUsDialog(SettingActivity.this).safeShow());

        findViewById(R.id.rl_about).setOnClickListener(v -> startActivity(new Intent(this, AboutActivity.class)));

        findViewById(R.id.tv_boost).setOnClickListener(v -> {
            AnimActivity.start(this, Function.TYPE_BOOST);
            UtilsLog.log(KEY, "boost", null);});

        findViewById(R.id.tv_clean).setOnClickListener(v -> {
            AnimActivity.start(this, Function.TYPE_CLEAN);
            UtilsLog.log(KEY, "clean", null);});

        findViewById(R.id.tv_battery).setOnClickListener(v -> {
            AnimActivity.start(this, Function.TYPE_SAVE_BATTERY);
            UtilsLog.log(KEY, "battery", null);});

        findViewById(R.id.tv_cooler).setOnClickListener(v -> {
            AnimActivity.start(this, Function.TYPE_COOL);
            UtilsLog.log(KEY, "cooler", null);});

        findViewById(R.id.tv_network).setOnClickListener(v -> {
            AnimActivity.start(this, Function.TYPE_NETWORK);
            UtilsLog.log(KEY, "network", null);});

    }

    private void startSystemWebView(String url) {
        try {
            Intent intent = new Intent();
            intent.setAction(Intent.ACTION_VIEW);
            intent.setData(Uri.parse(url));
            startActivity(intent);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
