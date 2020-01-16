package com.dema.versatile.scene.ui.base;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.text.TextUtils;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import org.json.JSONObject;

import com.dema.versatile.lib.utils.UtilsSize;
import com.dema.versatile.mediation.CMMediationFactory;
import com.dema.versatile.mediation.core.in.IMediationMgr;
import com.dema.versatile.scene.SceneConstants;
import com.dema.versatile.scene.utils.SceneLog;

/**
 * 这里只做必要的逻辑 例如log打印
 */
public abstract class CMAlertBaseActivity extends AppCompatActivity {
    protected static boolean mIsPrintLog = false;
    private IMediationMgr mIMediationMgr;
    private boolean mIsAdLoaded = false;
    private JSONObject mJsonObject;
    private BroadcastReceiver mReceiver;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (savedInstanceState != null) {
            return;
        }
        mIMediationMgr = CMMediationFactory.getInstance().createInstance(IMediationMgr.class);
        mIsAdLoaded = mIMediationMgr.isAdLoaded(getAdKey());
        logAlertShow();
        registerHomeReceiver();
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        if (mIMediationMgr == null) {
            mIMediationMgr = CMMediationFactory.getInstance().createInstance(IMediationMgr.class);
        }
        mIsAdLoaded = mIMediationMgr.isAdLoaded(getAdKey());
        logAlertShow();
    }

    @Override
    protected void onStart() {
        super.onStart();
        getWindow().getAttributes().width = UtilsSize.getScreenWidth(this) - UtilsSize.dpToPx(this, 60);
    }

    protected void logAlertShow() {
        if (!mIsPrintLog) {
            return;
        }
        mIsPrintLog = false;
        mJsonObject = SceneLog.logAlertShow(getType(), getScene(), getTrigger(), mIsAdLoaded, getAlertCount());
    }

    protected void logAlertClick() {
        SceneLog.logAlertClick(mJsonObject);
    }

    public abstract String getScene();

    public abstract @SceneConstants.Trigger String getTrigger();

    public abstract String getType();

    public abstract String getAdKey();

    public abstract int getAlertCount();

    private void registerHomeReceiver() {
        try {
            mReceiver = new BroadcastReceiver() {
                @Override
                public void onReceive(Context context, Intent intent) {
                    if (intent == null || TextUtils.isEmpty(intent.getAction()))
                        return;

                    if (TextUtils.equals(Intent.ACTION_CLOSE_SYSTEM_DIALOGS, intent.getAction())) {
                        String reason = intent.getStringExtra("reason");
                        if (TextUtils.equals(reason, "homekey")
                            /*|| TextUtils.equals(reason, "recentapps")*/) {
                            finish();
                        }
                    }
                }
            };
            IntentFilter filter = new IntentFilter();
            filter.addAction(Intent.ACTION_CLOSE_SYSTEM_DIALOGS);
            registerReceiver(mReceiver, filter);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        try {
            if (mReceiver != null) {
                unregisterReceiver(mReceiver);
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            mReceiver = null;
        }
    }
}
