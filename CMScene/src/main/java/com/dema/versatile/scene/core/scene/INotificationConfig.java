package com.dema.versatile.scene.core.scene;

import android.net.Uri;

import androidx.annotation.DrawableRes;

/**
 * Created by wangyu on 2019/8/30.
 */
public interface INotificationConfig {
    int MODE_NONE = 0;
    int MODE_MUTED = 1;
    int MODE_SOUND = 2;

    Integer getMode();

    Uri getSoundUri();

    long[] getVibrationPattern();

    Boolean isShowIcon();

    Boolean isShowButton();

    @DrawableRes
    Integer getBackgroundRes();

    @DrawableRes
    Integer getIconRes();

    String getTitle();

    Integer getTitleColor();

    String getContent();

    Integer getContentColor();

    @DrawableRes
    Integer getButtonRes();

    /**
     * 如果不需要button就返回null
     *
     * @return
     */
    String getButtonText();

    Integer getButtonTextColor();
}
