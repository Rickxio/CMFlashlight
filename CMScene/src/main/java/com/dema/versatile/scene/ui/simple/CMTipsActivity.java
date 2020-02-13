package com.dema.versatile.scene.ui.simple;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
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
public class CMTipsActivity extends CMAlertBaseActivity {

    protected View mRootView;
    protected LottieAnimationView mLottieAnimationView;
    protected ImageView mIvClose;
    protected TextView mTvTitle;
    protected TextView mTvContent;
    protected IMediationMgr mIMediationMgr;
    protected FrameLayout mFlAd;
    private String mType = SceneConstants.VALUE_STRING_TIPS_ALERT_TYPE;
    private AlertInfoBean mAlertInfoBean = new AlertInfoBean();

    public static void start(Context context, Class<? extends CMTipsActivity> cls, AlertInfoBean alertInfoBean) {
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
        setContentView(R.layout.activity_cm_tips);
        mIMediationMgr = CMMediationFactory.getInstance().createInstance(IMediationMgr.class);
        init();
    }

    private void init() {
        ISceneMgr mISceneMgr = CMSceneFactory.getInstance().createInstance(ISceneMgr.class);
        findId();
        setContent();
        if (mIMediationMgr == null) {
            mIMediationMgr = CMMediationFactory.getInstance().createInstance(IMediationMgr.class);
        }
        mIMediationMgr.showAdView(getAdKey(), mFlAd, getApplicationContext());
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
        mTvTitle = findViewById(R.id.tv_title);
        mTvContent = findViewById(R.id.tv_content);
    }

    private void setContent() {
        try {
            mIvClose.setOnClickListener(v -> finish());
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
     */
    protected void startLottieAnim(int repeatCount, String imageAssetsFolder, String fileName, Consumer<LottieAnimationView> consumer) {
        try {
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
