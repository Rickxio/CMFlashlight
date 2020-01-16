package com.dema.versatile.scene.ui.simple;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.app.Activity;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.core.util.Consumer;

import com.airbnb.lottie.LottieAnimationView;

import java.io.Serializable;

import com.dema.versatile.mediation.CMMediationFactory;
import com.dema.versatile.mediation.core.in.IMediationMgr;
import com.dema.versatile.scene.R;
import com.dema.versatile.scene.SceneConstants;
import com.dema.versatile.scene.core.CMSceneFactory;
import com.dema.versatile.scene.core.alert.AlertInfoBean;
import com.dema.versatile.scene.core.scene.ISceneMgr;
import com.dema.versatile.scene.ui.base.CMAlertBaseActivity;
import com.dema.versatile.scene.utils.UtilsAnimator;

/**
 * 一般固定样式就继承这个activity，如果非要自己定义布局文件，那么继承CMAlertBaseActivity
 */
public class CMAlertActivity extends CMAlertBaseActivity {

    protected View mRootView;
    protected LottieAnimationView mLottieAnimationView;
    protected ImageView mIvClose;
    protected TextView mTvTitle;
    protected TextView mTvContent;
    protected Button mBtAction;
    protected IMediationMgr mIMediationMgr;
    protected FrameLayout mFlAd;
    private boolean mHasClick = false;
    private String mType = SceneConstants.VALUE_STRING_PULL_ALERT_TYPE;

    private AlertInfoBean mAlertInfoBean = new AlertInfoBean();
    private ISceneMgr mISceneMgr;

    public static void start(Context context, Class<? extends CMAlertActivity> cls, AlertInfoBean alertInfoBean) {
        if (context == null || cls == null || alertInfoBean == null || alertInfoBean.scene == null) {
            return;
        }
        Intent starter = new Intent(context, cls);
        if (!(context instanceof Activity)) {
            starter.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        }
        starter.putExtra("bean", alertInfoBean);
        mIsPrintLog = true;
        context.startActivity(starter);
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        parseIntent(getIntent());
        super.onCreate(savedInstanceState);
        if (savedInstanceState != null) {
            finish();
            return;
        }
        setContentView(R.layout.activity_cm_alert);
        init();
    }

    private void init() {
        mISceneMgr = CMSceneFactory.getInstance().createInstance(ISceneMgr.class);
        mIMediationMgr = CMMediationFactory.getInstance().createInstance(IMediationMgr.class);
        findId();
        setContent();
        mIMediationMgr.showAdView(getAdKey(), mFlAd);
        if (mISceneMgr.getCallBack() != null) {
            mISceneMgr.getCallBack().onAlertShow(mAlertInfoBean);
        }
    }

    @Override
    protected void onNewIntent(Intent intent) {
        parseIntent(intent);
        super.onNewIntent(intent);
        init();
    }

    private void findId() {
        mFlAd = findViewById(R.id.fl_ad);
        mRootView = findViewById(R.id.view_root);
        mLottieAnimationView = findViewById(R.id.view_lottie);
        mIvClose = findViewById(R.id.iv_close);
        if (mIvClose != null) {
            mIvClose.setOnClickListener(v -> finish());
        }
        mTvTitle = findViewById(R.id.tv_title);
        mTvContent = findViewById(R.id.tv_content);
        mBtAction = findViewById(R.id.bt_action);
        if (mBtAction != null) {
            mBtAction.setOnClickListener(v -> {
                try {
                    if (!mHasClick) {
                        logAlertClick();
                    }

                    if (mISceneMgr.getCallBack() != null) {
                        mISceneMgr.getCallBack().onAlertClick(mAlertInfoBean);
                    }
                    mHasClick = true;
                    Intent intent = new Intent();
                    intent.setAction(getPackageName() + SceneConstants.ACTION_SPLASH);
                    intent.addCategory(Intent.CATEGORY_DEFAULT);
                    intent.putExtra(SceneConstants.VALUE_STRING_EXTRA_TYPE, getType());
                    intent.putExtra(SceneConstants.VALUE_STRING_EXTRA_SCENE, getScene());
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);

                    PendingIntent pendingIntent = PendingIntent.getActivity(this, 666, intent, PendingIntent.FLAG_UPDATE_CURRENT);
                    pendingIntent.send();
                } catch (Exception e) {
                } finally {
                    finish();
                }
            });
        }

    }

    private void setContent() {
        try {
            mRootView.setBackgroundResource(mAlertInfoBean.backgroundRes);
            mIvClose.setImageResource(mAlertInfoBean.closeIconRes);
            mTvTitle.setText(mAlertInfoBean.title);
            mTvTitle.setTextColor(mAlertInfoBean.titleColor);
            mTvContent.setText(mAlertInfoBean.content);
            mTvContent.setTextColor(mAlertInfoBean.contentColor);
            if (!mAlertInfoBean.isAnimation && mAlertInfoBean.imageRes != null) {
                mLottieAnimationView.setImageResource(mAlertInfoBean.imageRes);
            } else {
                startLottieAnim(mAlertInfoBean.lottieRepeatCount, mAlertInfoBean.lottieImageFolder, mAlertInfoBean.lottieFilePath, null);
            }
            mBtAction.setText(mAlertInfoBean.buttonText);
            mBtAction.setBackgroundResource(mAlertInfoBean.buttonBackgroundRes);
            mBtAction.setTextColor(mAlertInfoBean.buttonTextColor);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void parseIntent(Intent intent) {
        if (intent == null) {
            return;
        }
        try {
            Serializable bean = intent.getSerializableExtra("bean");
            if (bean instanceof AlertInfoBean) {
                mAlertInfoBean = (AlertInfoBean) bean;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public String getScene() {
        return mAlertInfoBean.scene;
    }

    @Override
    public @SceneConstants.Trigger String getTrigger() {
        return mAlertInfoBean.trigger;
    }

    @Override
    public String getType() {
        return mType;
    }

    @Override
    public String getAdKey() {
        return CMSceneFactory.getInstance().createInstance(ISceneMgr.class).getAdKey(getScene());
    }

    @Override
    public int getAlertCount() {
        return mAlertInfoBean.count;
    }


    /**
     * @param imageAssetsFolder lottie assets目录，例如： "scene/images"
     * @param fileName          lottie 文件目录,例如： "scene/data.json"
     * @param repeatCount       重复次数，-1 无限重复 0 不重复  其他对应重复次数
     * @param consumer          动画结束回调
     */
    protected void startLottieAnim(int repeatCount, String imageAssetsFolder, String fileName, Consumer<LottieAnimationView> consumer) {
        if (TextUtils.isEmpty(imageAssetsFolder) || TextUtils.isEmpty(fileName)) {
            return;
        }
        if (mLottieAnimationView.isAnimating()) {
            mLottieAnimationView.cancelAnimation();
        }
        mLottieAnimationView.setImageAssetsFolder(imageAssetsFolder);
        mLottieAnimationView.setAnimation(fileName);
        mLottieAnimationView.setRepeatCount(repeatCount);
        mLottieAnimationView.removeAllAnimatorListeners();
        mLottieAnimationView.addAnimatorListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                if (consumer != null) {
                    consumer.accept(mLottieAnimationView);
                }
            }
        });
        try {
            mLottieAnimationView.playAnimation();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        try {
            UtilsAnimator.releaseLottieNim(mLottieAnimationView);
            mIMediationMgr.releaseAd(getAdKey());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
