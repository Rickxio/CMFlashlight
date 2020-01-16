package com.dema.versatile.scene.core.scene;

/**
 * Created by wangyu on 2019/9/3.
 */
public interface IAlertConfig {
    String getTitleText();

    String getContentText();

    String getButtonText();

    Boolean isAnimation();

    Integer getImageRes();

    String getLottieImageFolder();

    String getLottieFilePath();

    Integer getLottieRepeatCount();

    Integer getBackgroundRes();

    Integer getCloseIconRes();

    Integer getTitleColor();

    Integer getContentColor();

    Integer getButtonBackgroundRes();

    Integer getButtonTextColor();

}
