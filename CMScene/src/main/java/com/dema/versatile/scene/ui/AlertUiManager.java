package com.dema.versatile.scene.ui;

import com.dema.versatile.scene.R;
import com.dema.versatile.scene.core.CMSceneFactory;
import com.dema.versatile.scene.core.scene.IAlertConfig;
import com.dema.versatile.scene.ui.simple.CommonAlertUiConfig;
import com.dema.versatile.scene.ui.simple.OptimizeTipsAlertUiConfig;
import com.dema.versatile.scene.utils.UtilsScene;

/**
 * 设置alert的默认ui颜色
 * Created by wangyu on 2019/9/3.
 */
public class AlertUiManager {
    private static final AlertUiManager sInstance = new AlertUiManager();

    private Integer mBackgroundRes;
    private Integer mCloseIconRes;
    private Integer mTitleColor;
    private Integer mContentColor;
    private Integer mButtonBackgroundRes;
    private Integer mButtonTextColor;

    public static AlertUiManager getInstance() {
        return sInstance;
    }

    public int getBackgroundRes() {
        if (mBackgroundRes == null) {
            mBackgroundRes = R.drawable.bg_default_alert;
        }
        return mBackgroundRes;
    }

    public AlertUiManager setBackgroundRes(int backgroundRes) {
        mBackgroundRes = backgroundRes;
        return this;
    }

    public int getCloseIconRes() {
        if (mCloseIconRes == null) {
            mCloseIconRes = R.drawable.ic_alert_default_close;
        }
        return mCloseIconRes;
    }

    public AlertUiManager setCloseIconRes(int closeIconRes) {
        mCloseIconRes = closeIconRes;
        return this;
    }

    public int getTitleColor() {
        if (mTitleColor == null) {
            mTitleColor = CMSceneFactory.getApplication().getResources().getColor(R.color.white4);
        }
        return mTitleColor;
    }

    public AlertUiManager setTitleColor(int titleColor) {
        mTitleColor = titleColor;
        return this;
    }

    public int getContentColor() {
        if (mContentColor == null) {
            mContentColor = 0xff888888;
        }
        return mContentColor;
    }

    public AlertUiManager setContentColor(int contentColor) {
        mContentColor = contentColor;
        return this;
    }

    public int getButtonBackgroundRes() {
        if (mButtonBackgroundRes == null) {
            mButtonBackgroundRes = R.drawable.bg_alert_button_background;
        }
        return mButtonBackgroundRes;
    }

    public AlertUiManager setButtonBackgroundRes(int buttonBackgroundRes) {
        mButtonBackgroundRes = buttonBackgroundRes;
        return this;
    }

    public int getButtonTextColor() {
        if (mButtonTextColor == null) {
            mButtonTextColor = CMSceneFactory.getApplication().getResources().getColor(R.color.white1);
        }
        return mButtonTextColor;
    }

    public AlertUiManager setButtonTextColor(int buttonTextColor) {
        mButtonTextColor = buttonTextColor;
        return this;
    }

    public IAlertConfig getDefaultAlertUiConfig(String scene) {
        if (!UtilsScene.isPullScene(scene) && UtilsScene.isOptimizingScene(scene)) {
            return new OptimizeTipsAlertUiConfig(scene);
        }
        return new CommonAlertUiConfig(scene);
    }

}
