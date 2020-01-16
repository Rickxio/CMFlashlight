package com.dema.versatile.flashlight.main;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.SeekBar;

import com.dema.versatile.flashlight.R;
import com.dema.versatile.flashlight.core.flashlight.im.FlashlightMgrImpl;
import com.dema.versatile.flashlight.core.flashlight.in.IFlashlightMgr;
import com.dema.versatile.flashlight.main.base.BaseActivity;
import com.dema.versatile.flashlight.main.view.VerticalSeekBar;
import com.dema.versatile.lib.utils.UtilsSize;

public class LightActivity extends BaseActivity {

    private static final int[] LIGHT_LEVEL = new int[]{FlashlightMgrImpl.VALUE_INT_1,FlashlightMgrImpl.VALUE_INT_2,
            FlashlightMgrImpl.VALUE_INT_3,FlashlightMgrImpl.VALUE_INT_4,FlashlightMgrImpl.VALUE_INT_5};

    private int mIndex = LIGHT_LEVEL.length - 1;
    private IFlashlightMgr mIFlashlightMgr;
    private VerticalSeekBar verticalSeekBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_light);
        verticalSeekBar = findViewById(R.id.progress);
        verticalSeekBar.setPadding(0,0,0,0);

        mIFlashlightMgr = FlashlightMgrImpl.getInstance();
        mIFlashlightMgr.setSwitchState(true);

        verticalSeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                verticalSeekBar.setThumbOffset(progress >= 50 ? UtilsSize.dpToPx(LightActivity.this,10): 0);
                if (progress <= 20) {
                    mIndex = 1;
                } else if (progress <= 40) {
                    mIndex = 2;
                } else if (progress <= 60) {
                    mIndex = 3;
                } else if (progress <= 80) {
                    mIndex = 4;
                } else if (progress <= 100) {
                    mIndex = 5;
                }
                verticalSeekBar.removeCallbacks(runnable);
               verticalSeekBar.postDelayed(runnable,300);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
            }
        });
        verticalSeekBar.setProgress(100);
        findViewById(R.id.iv_back).setOnClickListener(v -> finish());

    }



    Runnable runnable = new Runnable() {
        @Override
        public void run() {
            Log.e(LightActivity.class.getSimpleName(),"runnable mIndex="+mIndex);
            mIFlashlightMgr.changeRVState(mIndex);
        }
    };

    @Override
    protected void onDestroy() {
        if (verticalSeekBar != null) {
            verticalSeekBar.removeCallbacks(runnable);
        }
        super.onDestroy();
    }
}
