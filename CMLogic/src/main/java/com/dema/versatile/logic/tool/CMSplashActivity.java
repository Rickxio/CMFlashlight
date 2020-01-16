package com.dema.versatile.logic.tool;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.dema.versatile.lib.CMLibFactory;
import com.dema.versatile.lib.core.in.ICMTimer;
import com.dema.versatile.lib.core.in.ICMTimerListener;
import com.dema.versatile.lib.tool.AppStatusTool;
import com.dema.versatile.lib.utils.UtilsLog;

/**
 * 此类主要用途
 * 1.配合CMApplication封装Config逻辑
 */
public abstract class CMSplashActivity extends AppCompatActivity {
    public static final String VALUE_STRING_EXTRA_TYPE = "intent_extra_type";
    public static final String VALUE_STRING_EXTRA_SCENE = "intent_extra_scene";

    public static final String VALUE_STRING_ICON_TYPE = "icon";

    private ICMTimer mICMTimer = null;
    private String mScene = null;
    private String mType;

    // 返回界面等待时长 单位：毫秒
    protected abstract long getWaitTime();

    // 广告预加载逻辑
    protected abstract void requestAd();

    // 下一步逻辑
    protected abstract void nextStep();

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        AppStatusTool.getInstance().finishOtherActivity(this);
        // 解析intent 上报alive start打点统计
        Intent intent = getIntent();
        mType = VALUE_STRING_ICON_TYPE;
        if (intent != null && !TextUtils.isEmpty(intent.getStringExtra(VALUE_STRING_EXTRA_TYPE))) {
            mType = intent.getStringExtra(VALUE_STRING_EXTRA_TYPE);
        }
        if (intent != null && !TextUtils.isEmpty(intent.getStringExtra(VALUE_STRING_EXTRA_SCENE))) {
            mScene = intent.getStringExtra(VALUE_STRING_EXTRA_SCENE);
        }
        UtilsLog.aliveStart(mType, mScene);

        // 预加载广告
        requestAd();

        // 开启定时器
        mICMTimer = CMLibFactory.getInstance().createInstance(ICMTimer.class);
        mICMTimer.start(getWaitTime(), 0, new ICMTimerListener() {
            @Override
            public void onComplete(long lRepeatTime) {
                nextStep();
            }
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (null != mICMTimer) {
            mICMTimer.stop();
        }
    }

    public String getScene() {
        return mScene;
    }

    public String getType() {
        return mType;
    }

    @Override
    public void onBackPressed() {
    }
}
