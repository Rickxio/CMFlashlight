package com.dema.versatile.flashlight.main.function;


import android.animation.Animator;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;

import com.airbnb.lottie.LottieAnimationView;

import com.dema.versatile.flashlight.AdKey;
import com.dema.versatile.flashlight.R;
import com.dema.versatile.flashlight.main.base.BaseActivity;
import com.dema.versatile.mediation.CMMediationFactory;
import com.dema.versatile.mediation.core.in.IMediationMgr;

import static com.dema.versatile.flashlight.main.function.Function.KEY_FUNCTION_RESULT_TEXT;
import static com.dema.versatile.flashlight.main.function.Function.KEY_FUNCTION_TYPE;

public class AnimActivity extends BaseActivity {


    private ImageView mIvBack;
    private TextView mTvTitle;
    private LottieAnimationView mLottieAnimationView;
    private TextView mTvAnimHint;
    private int mFunctionType;
    private String mTextResult;
    private IMediationMgr mMediationMgr;

    public static void start(Context context, @Function.Type int type) {
        start(context, type, null);
    }

    public static void start(Context context, @Function.Type int type, String cleanMemorySize) {
        if (context == null) {
            return;
        }
        Intent intent = new Intent(context, AnimActivity.class);
        if (!Activity.class.isAssignableFrom(context.getClass())) {
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        }
        intent.putExtra(KEY_FUNCTION_TYPE, type);
        intent.putExtra(KEY_FUNCTION_RESULT_TEXT, cleanMemorySize);
        context.startActivity(intent);
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_anim);

        mMediationMgr = CMMediationFactory.getInstance().createInstance(IMediationMgr.class);
        mMediationMgr.requestAdAsync(AdKey.VALUE_STRING_INTERSTITIAL_RESULT, AdKey.VALUE_STRING_AD_REQUEST_SCENE_ANIMATION_START);

        initParams();
        initViews();
        initAnimAssets();

    }

    private void initViews() {
        mIvBack = findViewById(R.id.iv_back);
        mTvTitle = findViewById(R.id.tv_title);
        mLottieAnimationView = findViewById(R.id.lottie_scan);
        mTvAnimHint = findViewById(R.id.tv_anim_hint);

        mIvBack.setOnClickListener(v -> onBackPressed());
        mTvTitle.setText(Function.getTitleRes(mFunctionType));

        mLottieAnimationView.addAnimatorListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {

            }

            @Override
            public void onAnimationEnd(Animator animation) {
                ResultActivity.start(AnimActivity.this, mFunctionType);
                mMediationMgr.showInterstitialAd(AdKey.VALUE_STRING_INTERSTITIAL_RESULT, AdKey.VALUE_STRING_AD_SHOW_SCENE_ANIMATION_COMPLETE, getApplicationContext());
                finish();
            }

            @Override
            public void onAnimationCancel(Animator animation) {

            }

            @Override
            public void onAnimationRepeat(Animator animation) {

            }
        });
        mLottieAnimationView.addAnimatorUpdateListener(animation -> {

            if (animation == null || mTvAnimHint == null) {
                return;
            }

            float value = animation.getAnimatedFraction();
            if (mFunctionType == Function.TYPE_SAVE_BATTERY) {
//                1-5s显示前2条文案，5.5—8显示中间2条文案，8.5—最后显示最后一条文案
                float v = value * 9.7f;
                if (v < 1) {
                    return;
                }
                String[] array = getResources().getStringArray(R.array.words_battery_optimize);
                if (v <= 2.5f) {
                    mTvAnimHint.setText(array[0]);
                } else if (v <= 5f) {
                    mTvAnimHint.setText(array[1]);
                } else if (v < 5.5f) {
                    mTvAnimHint.setText("");
                } else if (v <= 6.5f) {
                    mTvAnimHint.setText(array[2]);
                } else if (v <= 7.5) {
                    mTvAnimHint.setText(array[3]);
                } else if (v <= 8.5f) {
                    mTvAnimHint.setText("");
                } else {
                    mTvAnimHint.setText(array[4]);
                }

            } else if (mFunctionType == Function.TYPE_COOL) {

                String[] array = getResources().getStringArray(R.array.words_cool);
//                1-5s显示前3条文案，5.5—8.5显示中间3条文案，9.5—11s显示最后一条文案
                float v = value * 11;
                if (v < 1) {
                    return;
                }
                if (v <= 2f) {
                    mTvAnimHint.setText(array[0]);
                } else if (v <= 3f) {
                    mTvAnimHint.setText(array[1]);
                } else if (v <= 4.5) {
                    mTvAnimHint.setText(array[2]);
                } else if (v < 5.5) {
                    mTvAnimHint.setText("");
                } else if (v <= 6.5f) {
                    mTvAnimHint.setText(array[3]);
                } else if (v <= 7.5f) {
                    mTvAnimHint.setText(array[4]);
                } else if (v <= 8.5f) {
                    mTvAnimHint.setText(array[5]);
                } else if (v <= 9.5f) {
                    mTvAnimHint.setText("");
                } else {
                    mTvAnimHint.setText(array[6]);
                }

            } else if (mFunctionType == Function.TYPE_NETWORK) {
//                1—6s显示前边6个文案，7—8s显示最后一条文案
                float v = value * 8;
                if (v < 1f) {
                    return;
                }
                String[] array = getResources().getStringArray(R.array.words_network);
                if (v <= 1.6) {
                    mTvAnimHint.setText(array[0]);
                } else if (v <= 2.5) {
                    mTvAnimHint.setText(array[1]);
                } else if (v <= 3.2) {
                    mTvAnimHint.setText(array[2]);
                } else if (v <= 4.2f) {
                    mTvAnimHint.setText(array[3]);
                } else if (v <= 5.8f) {
                    mTvAnimHint.setText(array[4]);
                }  else if (v <= 6.5f) {
                    mTvAnimHint.setText("");
                } else {
                    mTvAnimHint.setText(array[5]);
                }
            }
        });
    }

    private void initParams() {
        Intent intent = getIntent();
        mFunctionType = intent.getIntExtra(KEY_FUNCTION_TYPE, Function.TYPE_BOOST);
        mTextResult = intent.getStringExtra(KEY_FUNCTION_RESULT_TEXT);
    }

    private void initAnimAssets() {
        switch (mFunctionType) {
            case Function.TYPE_CLEAN:
                mLottieAnimationView.setAnimation("clean/data.json");
                mLottieAnimationView.setImageAssetsFolder("clean/images");
                break;
            case Function.TYPE_COOL:
                mLottieAnimationView.setAnimation("cool/data.json");
                mLottieAnimationView.setImageAssetsFolder("cool/images");
                break;
            case Function.TYPE_SAVE_BATTERY:
                mLottieAnimationView.setAnimation("battery/data.json");
                mLottieAnimationView.setImageAssetsFolder("battery/images");
                break;
            case Function.TYPE_NETWORK:
                mLottieAnimationView.setAnimation("wifi/data.json");
                break;
            case Function.TYPE_BOOST:
            default:
                mLottieAnimationView.setAnimation("boost/data.json");
                mLottieAnimationView.setImageAssetsFolder("boost/images");
                break;
        }
        mLottieAnimationView.playAnimation();
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    protected void onDestroy() {
        if (mLottieAnimationView != null && mLottieAnimationView.isAnimating()) {
            mLottieAnimationView.cancelAnimation();
        }
        mMediationMgr.releaseAd(AdKey.VALUE_STRING_INTERSTITIAL_RESULT);
        super.onDestroy();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        mMediationMgr.showInterstitialAd(AdKey.VALUE_STRING_INTERSTITIAL_RESULT, AdKey.VALUE_STRING_AD_SHOW_SCENE_ANIMATION_CANCEL, getApplicationContext());

    }
}
