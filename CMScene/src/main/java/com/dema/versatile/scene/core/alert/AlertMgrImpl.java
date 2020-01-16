package com.dema.versatile.scene.core.alert;

import android.content.Context;

import com.dema.versatile.scene.core.CMSceneFactory;
import com.dema.versatile.scene.core.scene.IAlertConfig;
import com.dema.versatile.scene.core.scene.ISceneMgr;
import com.dema.versatile.scene.ui.AlertUiManager;
import com.dema.versatile.scene.ui.simple.CMAlertActivity;
import com.dema.versatile.scene.ui.simple.CMTipsActivity;
import com.dema.versatile.scene.utils.UtilsScene;

/**
 * Created by wangyu on 2019/10/15.
 */
public class AlertMgrImpl implements IAlertMgr {

    private final Context mContext;
    private IAlertConfig mAppAlertConfig;
    private IAlertConfig mDefaultAlertConfig;
    private ISceneMgr mISceneMgr;

    public AlertMgrImpl() {
        mContext = CMSceneFactory.getApplication();
    }

    @Override
    public void showAlert(String scene, String trigger, int count) {
        mISceneMgr = CMSceneFactory.getInstance().createInstance(ISceneMgr.class);
        mAppAlertConfig = mISceneMgr.getAlertUiConfig(scene);
        mDefaultAlertConfig = AlertUiManager.getInstance().getDefaultAlertUiConfig(scene);
        AlertInfoBean mAlertInfoBean = new AlertInfoBean();
        mAlertInfoBean.scene = scene;
        mAlertInfoBean.trigger = trigger;
        mAlertInfoBean.count = count;
        mAlertInfoBean.backgroundRes = getLayoutBackgroundRes();
        mAlertInfoBean.closeIconRes = getCloseIconRes();
        mAlertInfoBean.title = getTitleText();
        mAlertInfoBean.titleColor = getTitleTextColor();
        mAlertInfoBean.content = getContentText();
        mAlertInfoBean.contentColor = getContentTextColor();
        mAlertInfoBean.imageRes = getImageRes();
        mAlertInfoBean.buttonText = getButtonText();
        mAlertInfoBean.buttonBackgroundRes = getButtonBackgroundRes();
        mAlertInfoBean.buttonTextColor = getButtonTextColor();
        mAlertInfoBean.isAnimation = isAnimation();
        mAlertInfoBean.lottieFilePath = getLottieFilePath();
        mAlertInfoBean.lottieImageFolder = getLottieImageFolder();
        mAlertInfoBean.lottieRepeatCount = getLottieRepeatCount();
        if (UtilsScene.isPullScene(scene)) {
            CMAlertActivity.start(mContext, CMAlertActivity.class, mAlertInfoBean);
        } else {
            CMTipsActivity.start(mContext, CMTipsActivity.class, mAlertInfoBean);
        }
    }

    //整个页面的背景  一般是带圆角的
    public int getLayoutBackgroundRes() {
        if (mAppAlertConfig.getBackgroundRes() != null) {
            return mAppAlertConfig.getBackgroundRes();
        }
        return mDefaultAlertConfig.getBackgroundRes();

    }

    //右上角关闭按钮
    public int getCloseIconRes() {
        if (mAppAlertConfig.getCloseIconRes() != null) {
            return mAppAlertConfig.getCloseIconRes();
        }
        return mDefaultAlertConfig.getCloseIconRes();

    }

    //标题
    public String getTitleText() {
        if (mAppAlertConfig.getTitleText() != null) {
            return mAppAlertConfig.getTitleText();
        }
        return mDefaultAlertConfig.getTitleText();

    }

    //标题颜色
    public int getTitleTextColor() {
        if (mAppAlertConfig.getTitleColor() != null) {
            return mAppAlertConfig.getTitleColor();
        }
        return mDefaultAlertConfig.getTitleColor();
    }

    //内容
    public String getContentText() {
        if (mAppAlertConfig.getContentText() != null) {
            return mAppAlertConfig.getContentText();
        }
        return mDefaultAlertConfig.getContentText();
    }

    //内容颜色
    public int getContentTextColor() {
        if (mAppAlertConfig.getContentColor() != null) {
            return mAppAlertConfig.getContentColor();
        }
        return mDefaultAlertConfig.getContentColor();
    }

    //中心图片
    public Integer getImageRes() {
        if (mAppAlertConfig.getImageRes() != null) {
            return mAppAlertConfig.getImageRes();
        }
        return mDefaultAlertConfig.getImageRes();
    }

    public String getButtonText() {
        if (mAppAlertConfig.getButtonText() != null) {
            return mAppAlertConfig.getButtonText();
        }
        return mDefaultAlertConfig.getButtonText();
    }

    public int getButtonBackgroundRes() {
        if (mAppAlertConfig.getButtonBackgroundRes() != null) {
            return mAppAlertConfig.getButtonBackgroundRes();
        }
        return mDefaultAlertConfig.getButtonBackgroundRes();

    }

    public int getButtonTextColor() {
        if (mAppAlertConfig.getButtonTextColor() != null) {
            return mAppAlertConfig.getButtonTextColor();
        }
        return mDefaultAlertConfig.getButtonTextColor();
    }

    public boolean isAnimation() {
        if (mAppAlertConfig.isAnimation() != null) {
            return mAppAlertConfig.isAnimation();
        }
        return mDefaultAlertConfig.isAnimation();
    }

    String getLottieImageFolder() {
        if (mAppAlertConfig.getLottieImageFolder() != null) {
            return mAppAlertConfig.getLottieImageFolder();
        }
        return mDefaultAlertConfig.getLottieImageFolder();
    }

    String getLottieFilePath() {
        if (mAppAlertConfig.getLottieFilePath() != null) {
            return mAppAlertConfig.getLottieFilePath();
        }
        return mDefaultAlertConfig.getLottieFilePath();
    }

    int getLottieRepeatCount() {
        if (mAppAlertConfig.getLottieRepeatCount() != null) {
            return mAppAlertConfig.getLottieRepeatCount();
        }
        return mDefaultAlertConfig.getLottieRepeatCount();
    }
}
