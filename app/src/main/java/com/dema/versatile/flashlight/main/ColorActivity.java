package com.dema.versatile.flashlight.main;

import android.content.res.ColorStateList;
import android.graphics.Color;
import android.os.Bundle;
import android.widget.FrameLayout;
import android.widget.ImageView;

import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.dema.versatile.flashlight.R;
import com.dema.versatile.flashlight.main.base.BaseActivity;
import com.dema.versatile.flashlight.utils.UtilsScreen;
import com.dema.versatile.lib.CMLibFactory;
import com.dema.versatile.lib.core.in.ICMTimer;
import com.dema.versatile.lib.core.in.ICMTimerListener;
import com.dema.versatile.lib.utils.UtilsLog;

public class ColorActivity extends BaseActivity {
    private FrameLayout mFLRoot = null;

    private int mBacklightBrightness = 255;

    private ICMTimer mICMTimer = null;
    private int mColorIndex = 0;
    private int[] mColor = new int[]{
            Color.parseColor("#FF0000"),
            Color.parseColor("#FC00FF"),
            Color.parseColor("#6600FF"),
            Color.parseColor("#007EFF"),
            Color.parseColor("#00FFEA"),
            Color.parseColor("#0CFF00"),
            Color.parseColor("#EAFF00"),
            Color.parseColor("#FF7200")
    };



    private static final long VALUE_LONG_COLOR_TIME = 300;

    private ImageView mTvOpen;
    private boolean mIsOpen = true;
    private int mCurrentPosition = mColor.length;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_color);

        UtilsLog.log("color", "create", null);

        Toolbar mToolbar = findViewById(R.id.toolbar);
        mToolbar.setNavigationOnClickListener(v -> finish());

        mFLRoot = findViewById(R.id.fl_root);
        mFLRoot.setOnClickListener(v -> finish());
        mTvOpen = findViewById(R.id.iv_close);
        mTvOpen.setOnClickListener(v -> {
            if (mICMTimer != null) {
                mICMTimer.stop();
            }
            mIsOpen = !mIsOpen;
            mTvOpen.setImageTintList(ColorStateList.valueOf(mIsOpen ? 0xFFFFFFFF : 0xFF666666));
            openRandom(mCurrentPosition);
        });


        RecyclerView recyclerView = findViewById(R.id.recycler_view);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this,LinearLayoutManager.VERTICAL,false);
        recyclerView.setLayoutManager(linearLayoutManager);
        ColorAdapter colorAdapter = new ColorAdapter();
        recyclerView.setAdapter(colorAdapter);
        colorAdapter.setOnItemClickListener(position -> {
            mIsOpen = true;
            mTvOpen.setImageTintList(ColorStateList.valueOf( 0xFFFFFFFF));
            mCurrentPosition = position;
            openRandom(mCurrentPosition);
        });

        mBacklightBrightness = UtilsScreen.getScreenBrightness(this);
        mICMTimer = CMLibFactory.getInstance().createInstance(ICMTimer.class);
    }

    @Override
    protected void onStart() {
        super.onStart();

        openRandom(mCurrentPosition);
    }

    private void openRandom(int position){
        try {
            if (mIsOpen) {
                UtilsScreen.setScreenBrightness(this, 255);
                if (position == mColor.length) {
                    mICMTimer.start(0, (int) VALUE_LONG_COLOR_TIME, new ICMTimerListener() {
                        @Override
                        public void onComplete(long lRepeatTime) {
                            mColorIndex = (int) (Math.random() * (mColor.length - 1));
                            mFLRoot.setBackgroundColor(mColor[mColorIndex]);
                        }
                    });
                }else{
                    mICMTimer.stop();
                    mFLRoot.setBackgroundColor(mColor[position]);
                }

            }else{
                UtilsScreen.setScreenBrightness(this, 50);
                mFLRoot.setBackgroundColor(Color.BLACK);
            }
        }catch (Exception e){
            e.printStackTrace();
        }
    }



    @Override
    protected void onStop() {
        super.onStop();
        UtilsScreen.setScreenBrightness(this, mBacklightBrightness);
        if (null != mICMTimer) {
            mICMTimer.stop();
        }
    }
}
