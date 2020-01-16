package com.dema.versatile.scene.ui;

import android.net.Uri;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.dema.versatile.scene.R;
import com.dema.versatile.scene.core.CMSceneFactory;
import com.dema.versatile.scene.core.scene.INotificationConfig;
import com.dema.versatile.scene.utils.UtilsScene;

/**
 * 设置通知的默认ui颜色
 * Created by wangyu on 2019/9/3.
 */
public class NotificationUiManager {
    private static final NotificationUiManager sInstance = new NotificationUiManager();

    private Integer mBackgroundRes;
    private Integer mTitleColor;
    private Integer mContentColor;
    private Integer mButtonBackgroundRes;
    private Integer mButtonTextColor;
    private Integer mMode = INotificationConfig.MODE_SOUND;
    private boolean mIsShowIcon = true;
    private boolean mIsShowButton = true;
    private Uri mSoundUri;
    private long[] mVibrationPattern = new long[]{1000, 500, 2000};

    public static NotificationUiManager getInstance() {
        return sInstance;
    }

    public int getBackgroundRes() {
        if (mBackgroundRes == null) {
            mBackgroundRes = R.drawable.bg_default_alert;
        }
        return mBackgroundRes;
    }

    public NotificationUiManager setBackgroundRes(int backgroundRes) {
        mBackgroundRes = backgroundRes;
        return this;
    }


    public int getTitleColor() {
        if (mTitleColor == null) {
            mTitleColor = CMSceneFactory.getApplication().getResources().getColor(R.color.black1);
        }
        return mTitleColor;
    }

    public NotificationUiManager setTitleColor(int titleColor) {
        mTitleColor = titleColor;
        return this;
    }

    public int getContentColor() {
        if (mContentColor == null) {
            mContentColor = CMSceneFactory.getApplication().getResources().getColor(R.color.white4);
        }
        return mContentColor;
    }

    public NotificationUiManager setContentColor(int contentColor) {
        mContentColor = contentColor;
        return this;
    }

    public int getButtonBackgroundRes() {
        if (mButtonBackgroundRes == null) {
            mButtonBackgroundRes = R.drawable.bg_alert_button_background;
        }
        return mButtonBackgroundRes;
    }

    public NotificationUiManager setButtonBackgroundRes(int buttonBackgroundRes) {
        mButtonBackgroundRes = buttonBackgroundRes;
        return this;
    }

    public int getButtonTextColor() {
        if (mButtonTextColor == null) {
            mButtonTextColor = CMSceneFactory.getApplication().getResources().getColor(R.color.black1);
        }
        return mButtonTextColor;
    }

    public NotificationUiManager setButtonTextColor(int buttonTextColor) {
        mButtonTextColor = buttonTextColor;
        return this;
    }

    public int getMode() {
        return mMode;
    }

    /**
     * @param mode {@link INotificationConfig#MODE_NONE MODE_MUTED MODE_SOUND}
     */
    public void setMode(int mode) {
        mMode = mode;
    }

    public boolean isShowIcon() {
        return mIsShowIcon;
    }

    public void setShowIcon(boolean showIcon) {
        mIsShowIcon = showIcon;
    }

    public boolean isShowButton() {
        return mIsShowButton;
    }

    public void setShowButton(boolean showButton) {
        mIsShowButton = showButton;
    }

    public Uri getSoundUri() {
        return mSoundUri;
    }

    public void setSoundUri(Uri soundUri) {
        mSoundUri = soundUri;
    }

    public long[] getVibrationPattern() {
        return mVibrationPattern;
    }

    public void setVibrationPattern(long[] vibrationPattern) {
        mVibrationPattern = vibrationPattern;
    }

    public INotificationConfig getNotificationUiConfig(String scene) {
        return new CommonNotificationUiConfig(scene);
    }

    public class CommonNotificationUiConfig implements INotificationConfig {
        private String mScene;

        public CommonNotificationUiConfig(String scene) {
            mScene = scene;
        }

        @Override
        public Integer getMode() {
            return mMode;
        }

        @Override
        public Uri getSoundUri() {
            return mSoundUri;
        }

        @Override
        public long[] getVibrationPattern() {
            return mVibrationPattern;
        }

        @Override
        public Boolean isShowIcon() {
            return mIsShowIcon;
        }

        @Override
        public Boolean isShowButton() {
            return mIsShowButton;
        }

        @Override
        public Integer getBackgroundRes() {
            return NotificationUiManager.this.getBackgroundRes();
        }

        @NonNull
        @Override
        public Integer getIconRes() {
            return UtilsScene.getSceneImageRes(mScene);
        }

        @NonNull
        @Override
        public String getTitle() {
            return UtilsScene.getSceneTitle(mScene);
        }

        @Override
        public Integer getTitleColor() {
            return NotificationUiManager.this.getTitleColor();
        }

        @Override
        public String getContent() {
            return UtilsScene.getSceneContent(mScene);
        }

        @Override
        public Integer getContentColor() {
            return NotificationUiManager.this.getContentColor();
        }

        @Override
        public Integer getButtonRes() {
            return getButtonBackgroundRes();
        }

        @Nullable
        @Override
        public String getButtonText() {
            return UtilsScene.getSceneButtonText(mScene);
        }

        @Override
        public Integer getButtonTextColor() {
            return NotificationUiManager.this.getButtonTextColor();
        }
    }
}
