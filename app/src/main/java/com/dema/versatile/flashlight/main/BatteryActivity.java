package com.dema.versatile.flashlight.main;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ValueAnimator;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.animation.LinearInterpolator;
import android.widget.TextView;

import androidx.core.util.Consumer;

import com.airbnb.lottie.LottieAnimationView;

import java.util.Locale;

import com.dema.versatile.flashlight.AdKey;
import com.dema.versatile.flashlight.R;
import com.dema.versatile.flashlight.core.CoreFactory;
import com.dema.versatile.flashlight.core.alarm.IAppAlarmMgr;
import com.dema.versatile.flashlight.main.base.BaseActivity;
import com.dema.versatile.flashlight.utils.UtilsAnimator;
import com.dema.versatile.flashlight.utils.UtilsSys;
import com.dema.versatile.mediation.CMMediationFactory;
import com.dema.versatile.mediation.core.in.IMediationMgr;

/**
 * Created by wangyu on 2019/8/15.
 */
public class BatteryActivity extends BaseActivity {

    public static final int VALUE_INT_RESULT_CODE = 6;
    private LottieAnimationView mLottieAnimationView;
    private IMediationMgr mIMediationMgr;
    private TextView mTvRate;
    private ValueAnimator mAnimTextView;
    private boolean mHasShowAd = false;
    private boolean mHasComplete = false;
    private ValueAnimator mValueAnimator;
    private TextView mTvDec;

    public static void start(Activity context) {
        if (context == null) {
            return;
        }
        Intent starter = new Intent(context, BatteryActivity.class);
        context.startActivity(starter);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_battery);
        mLottieAnimationView = findViewById(R.id.lottie_view);
        mTvRate = findViewById(R.id.tv_anim);
        mTvDec = findViewById(R.id.tv_dec);
        findViewById(R.id.iv_back).setOnClickListener(v -> {
            setResult(VALUE_INT_RESULT_CODE);
            finish();
        });
        mIMediationMgr = CMMediationFactory.getInstance().createInstance(IMediationMgr.class);
        mIMediationMgr.requestAdAsync(AdKey.VALUE_STRING_INTERSTITIAL_RESULT, "battery");
        initTvAnim();
        startLottieAnim("battery", animator -> {
            mHasShowAd = mIMediationMgr.showInterstitialAd(AdKey.VALUE_STRING_INTERSTITIAL_RESULT, "complete", getApplicationContext());
            if (!mHasShowAd) {
                startLottieAnim("complete", null);
                mHasComplete = true;
            }
        });
        CoreFactory.getInstance().createInstance(IAppAlarmMgr.class).recordBatteryTime();
//        IDrinkAlarmMgr mIAlarmMgr = CMLogicFactory.getInstance().createInstance(IDrinkAlarmMgr.class);
//        mIAlarmMgr.recordSceneShow(IDrinkAlarmMgr.SCENE_DRINK);
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (mHasShowAd && !mHasComplete) {
            startLottieAnim("complete", null);
            mHasComplete = true;
            mTvDec.setText(getText(R.string.battery_complete));
        }
    }

    private void initTvAnim() {
        int startRate = (int) (UtilsSys.getMemRate(this) * 100);
        mTvRate.setText(String.format(Locale.ENGLISH, "%d%%", startRate));
        mAnimTextView = ValueAnimator.ofFloat(0, 1, 1, 1, 1, 0);
        mAnimTextView.setInterpolator(new LinearInterpolator());
        mAnimTextView.setDuration(4000);
        mAnimTextView.addUpdateListener(animation -> {
            float value = (float) animation.getAnimatedValue();
            mTvRate.setScaleX(value);
            mTvRate.setScaleY(value);
        });
        mAnimTextView.start();
        UtilsSys.killProcess(this, () -> {
            try {
                int endValue = (int) (UtilsSys.getMemRate(this) * 100);
//                Log.i("wangyu", "start:" + startRate + ",end:" + endValue);
                if (endValue >= startRate) {
                    return;
                }
                mValueAnimator = ValueAnimator.ofInt(startRate, endValue);
                mValueAnimator.setDuration(2000);
                mValueAnimator.setInterpolator(new LinearInterpolator());
                mValueAnimator.addUpdateListener(animation -> {
                    int animatedValue = (int) animation.getAnimatedValue();
                    mTvRate.setText(String.format(Locale.ENGLISH, "%d%%", animatedValue));
                });
                mValueAnimator.setStartDelay(1000);
                mValueAnimator.start();
            } catch (Exception e) {

            }

        });
    }

    private void startLottieAnim(String folderName, Consumer<Animator> consumer) {
        if (mLottieAnimationView.isAnimating()) {
            mLottieAnimationView.cancelAnimation();
        }
        mLottieAnimationView.setImageAssetsFolder(folderName + "/images");
        mLottieAnimationView.setAnimation(folderName + "/data.json");
        mLottieAnimationView.setRepeatCount(0);
        mLottieAnimationView.removeAllAnimatorListeners();
        mLottieAnimationView.addAnimatorListener(new AnimatorListenerAdapter() {

            @Override
            public void onAnimationEnd(Animator animation) {
                if (consumer != null) {
                    consumer.accept(animation);
                }
            }
        });
        mLottieAnimationView.playAnimation();
    }

    @Override
    public void onBackPressed() {
        mIMediationMgr.showInterstitialAd(AdKey.VALUE_STRING_INTERSTITIAL_RESULT, "quit", getApplicationContext());
        super.onBackPressed();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        UtilsAnimator.releaseLottieNim(mLottieAnimationView);
        if (mIMediationMgr != null) {
            mIMediationMgr.releaseAd(AdKey.VALUE_STRING_INTERSTITIAL_RESULT);
        }
        UtilsAnimator.releaseValueAnimator(mAnimTextView);
        UtilsAnimator.releaseValueAnimator(mValueAnimator);
    }

}
