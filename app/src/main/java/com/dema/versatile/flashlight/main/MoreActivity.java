package com.dema.versatile.flashlight.main;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;

import butterknife.ButterKnife;
import butterknife.OnClick;
import com.dema.versatile.flashlight.R;
import com.dema.versatile.flashlight.main.base.BaseActivity;
import com.dema.versatile.flashlight.main.function.AnimActivity;
import com.dema.versatile.flashlight.main.function.Function;
import com.dema.versatile.flashlight.main.settings.SettingActivity;
import com.dema.versatile.lib.utils.UtilsLog;

public class MoreActivity extends BaseActivity {

    private static final String KEY = "more";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_more);
        ButterKnife.bind(this);

        UtilsLog.log(KEY, "create", null);

    }

    @OnClick({R.id.iv_back_more, R.id.tv_boost, R.id.tv_clean, R.id.tv_battery,
            R.id.tv_cooler, R.id.tv_network, R.id.tv_setting})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.iv_back_more:
                finish();
                break;
            case R.id.tv_boost:
                AnimActivity.start(this, Function.TYPE_BOOST);
                UtilsLog.log(KEY, "boost", null);
                break;
            case R.id.tv_clean:
                AnimActivity.start(this, Function.TYPE_CLEAN);
                UtilsLog.log(KEY, "clean", null);
                break;
            case R.id.tv_battery:
                AnimActivity.start(this, Function.TYPE_SAVE_BATTERY);
                UtilsLog.log(KEY, "battery", null);
                break;
            case R.id.tv_cooler:
                AnimActivity.start(this, Function.TYPE_COOL);
                UtilsLog.log(KEY, "cool", null);
                break;
            case R.id.tv_network:
                AnimActivity.start(this, Function.TYPE_NETWORK);
                UtilsLog.log(KEY, "network", null);
                break;
            case R.id.tv_setting:
                Intent intent = new Intent(this, SettingActivity.class);
                startActivity(intent);
                break;
//            case R.id.tv_light:
//                mScStartup.setChecked(!mScStartup.isChecked());
//                break;
//            case R.id.tv_terms:
//                UtilsLog.log(KEY, "term", null);
//                startSystemWebView(Constants.VALUE_STRING_TERM);
//                break;
//            case R.id.tv_privacy:
//                UtilsLog.log(KEY, "privacy", null);
//                startSystemWebView(Constants.VALUE_STRING_PRIVACY);
//                break;
            default:
                break;
        }
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
