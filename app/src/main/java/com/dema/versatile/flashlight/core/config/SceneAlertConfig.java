package com.dema.versatile.flashlight.core.config;


import com.dema.versatile.scene.core.scene.IAlertConfig;

/**
 * Create  by
 *
 * @author User:Administrator
 * @date Data:2019/10/30
 */

public class SceneAlertConfig implements IAlertConfig {

    private String scene;

    public SceneAlertConfig(String scene) {
        this.scene = scene;
    }

    @Override
    public String getTitleText() {
        return null;
    }

    @Override
    public String getContentText() {
        return null;
    }

    @Override
    public String getButtonText() {
        return null;
    }

    @Override
    public Boolean isAnimation() {
        return true;
    }

    @Override
    public Integer getImageRes() {
        return null;
    }

    @Override
    public String getLottieImageFolder() {
        if (scene == null) {
            return null;
        }
        switch (scene) {
            case "pull_boost":
                return "scene/boost/images";
//            case "pull_battery":
//                return "scene/battery/images";
            case "pull_cool":
                return "scene/cool/images";
//            case "pull_clean":
//                return "scene/clean/images";
//            case "pull_network":
//                return "scene/network/images";
            default:
                break;
        }
        return null;
    }

    @Override
    public String getLottieFilePath() {
        if (scene == null) {
            return null;
        }
        switch (scene) {
            case "pull_boost":
                return "scene/boost/data.json";
            case "pull_battery":
                return "scene/battery/data.json";
            case "pull_cool":
                return "scene/cool/data.json";
            case "pull_clean":
                return "scene/clean/data.json";
            case "pull_network":
                return "scene/network/data.json";
            default:
                break;
        }
        return null;
    }

    @Override
    public Integer getLottieRepeatCount() {
        return -1;
    }

    @Override
    public Integer getBackgroundRes() {
        return null;
    }

    @Override
    public Integer getCloseIconRes() {
        return null;
    }

    @Override
    public Integer getTitleColor() {
        return null;
    }

    @Override
    public Integer getContentColor() {
        return null;
    }

    @Override
    public Integer getButtonBackgroundRes() {
        return null;
    }

    @Override
    public Integer getButtonTextColor() {
        return null;
    }
}
