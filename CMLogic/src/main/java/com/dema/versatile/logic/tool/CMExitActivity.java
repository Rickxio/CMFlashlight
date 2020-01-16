package com.dema.versatile.logic.tool;

import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.Lifecycle;
import com.dema.versatile.lib.CMLibFactory;
import com.dema.versatile.lib.core.in.ICMTimer;
import com.dema.versatile.lib.core.in.ICMTimerListener;

/**
 * 此类主要用途
 * 1.应用退出应用弹出插屏广告，广告关闭后展示ExitActivity。调用时注意先展示ExitActivity在展示插屏广告
 */
public abstract class CMExitActivity extends AppCompatActivity {
    private ICMTimer mICMTimer1 = null;
    private ICMTimer mICMTimer2 = null;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mICMTimer1 = CMLibFactory.getInstance().createInstance(ICMTimer.class);
        mICMTimer2 = CMLibFactory.getInstance().createInstance(ICMTimer.class);
    }

    @Override
    protected void onResume() {
        super.onResume();
        mICMTimer1.start(500, 0, new ICMTimerListener() {
            @Override
            public void onComplete(long lRepeatTime) {
                if (getLifecycle().getCurrentState().isAtLeast(Lifecycle.State.RESUMED)) {
                    mICMTimer2.start(1000, 0, new ICMTimerListener() {
                        @Override
                        public void onComplete(long lRepeatTime) {
                            finish();
                        }
                    });
                }
            }
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (null != mICMTimer1) {
            mICMTimer1.stop();
        }

        if (null != mICMTimer2) {
            mICMTimer2.stop();
        }
    }

    @Override
    public void onBackPressed() {
    }
}
