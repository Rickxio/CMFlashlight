package com.dema.versatile.scene.ui.simple;

import android.graphics.Color;

import com.dema.versatile.scene.R;
import com.dema.versatile.scene.core.scene.IAlertConfig;
import com.dema.versatile.scene.ui.AlertUiManager;
import com.dema.versatile.scene.utils.UtilsScene;

public class OptimizeTipsAlertUiConfig implements IAlertConfig {
    private String mScene;

    public OptimizeTipsAlertUiConfig(String scene) {
        mScene = scene;
    }

    @Override
    public String getTitleText() {
        return UtilsScene.getSceneTitle(mScene);
    }

    @Override
    public String getContentText() {
        return UtilsScene.getSceneContent(mScene);
    }

    @Override
    public String getButtonText() {
        return UtilsScene.getSceneButtonText(mScene);
    }

    @Override
    public Boolean isAnimation() {
        return true;
    }

    @Override
    public Integer getImageRes() {
        return UtilsScene.getSceneImageRes(mScene);
    }

    @Override
    public String getLottieImageFolder() {
        return UtilsScene.getLottieImagePath(mScene);
    }

    @Override
    public String getLottieFilePath() {
        return UtilsScene.getLottieFilePath(mScene);
    }

    @Override
    public Integer getLottieRepeatCount() {
        return 0;
    }

    @Override
    public Integer getBackgroundRes() {
        return R.drawable.bg_opt_tips;
    }

    @Override
    public Integer getCloseIconRes() {
        return AlertUiManager.getInstance().getCloseIconRes();
    }

    @Override
    public Integer getTitleColor() {
        return Color.WHITE;
    }

    @Override
    public Integer getContentColor() {
        return Color.WHITE;
    }

    @Override
    public Integer getButtonBackgroundRes() {
        return AlertUiManager.getInstance().getButtonBackgroundRes();
    }

    @Override
    public Integer getButtonTextColor() {
        return AlertUiManager.getInstance().getButtonTextColor();
    }
}