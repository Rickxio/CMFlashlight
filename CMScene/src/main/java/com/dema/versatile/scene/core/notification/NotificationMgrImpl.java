package com.dema.versatile.scene.core.notification;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.media.AudioAttributes;
import android.net.Uri;
import android.text.TextUtils;
import android.view.View;
import android.widget.RemoteViews;

import androidx.core.app.NotificationCompat;

import org.json.JSONObject;

import com.dema.versatile.lib.utils.UtilsJson;
import com.dema.versatile.lib.utils.UtilsLog;
import com.dema.versatile.scene.R;
import com.dema.versatile.scene.SceneConstants;
import com.dema.versatile.scene.core.CMSceneFactory;
import com.dema.versatile.scene.core.scene.INotificationConfig;
import com.dema.versatile.scene.core.scene.ISceneMgr;
import com.dema.versatile.scene.ui.NotificationUiManager;
import com.dema.versatile.scene.utils.SceneLog;

/**
 * Created by wangyu on 2019/8/30.
 */
public class NotificationMgrImpl implements INotificationMgr {
    private Context mContext;
    private NotificationManager mNotificationManager;
    private INotificationConfig mAppConfig;
    private INotificationConfig mDefaultConfig;

    public NotificationMgrImpl() {
        mContext = CMSceneFactory.getApplication();
        mNotificationManager = (NotificationManager) mContext.getSystemService(Context.NOTIFICATION_SERVICE);
    }

    @Override
    public boolean showNotification(String scene) {
        if (TextUtils.isEmpty(scene)) {
            return false;
        }
        ISceneMgr iSceneMgr = CMSceneFactory.getInstance().createInstance(ISceneMgr.class);
        mAppConfig = iSceneMgr.getNotificationConfig(scene);
        mDefaultConfig = NotificationUiManager.getInstance().getNotificationUiConfig(scene);
        int mode = getMode();
        if (mode == INotificationConfig.MODE_NONE) {
            SceneLog.logFail("show notification fail,mode is none", null, scene, null);
            return false;
        }
        boolean isMuted = mode == INotificationConfig.MODE_MUTED;
        try {
            updateOrCreateChannel(getChannelId(scene), getChannelName(scene), isMuted);
            Intent intent = new Intent();
            intent.setAction(mContext.getPackageName() + SceneConstants.ACTION_SPLASH);
            intent.addCategory(Intent.CATEGORY_DEFAULT);
            intent.putExtra(SceneConstants.VALUE_STRING_EXTRA_TYPE, SceneConstants.VALUE_STRING_NOTIFICATION_TYPE);
            intent.putExtra(SceneConstants.VALUE_STRING_EXTRA_SCENE, scene);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
            PendingIntent pendingIntent = PendingIntent.getActivity(mContext, getRequestCode(scene),
                    intent, PendingIntent.FLAG_UPDATE_CURRENT);

            NotificationCompat.Builder builder = new NotificationCompat.Builder(mContext, getChannelId(scene))
                    .setContent(getRemoteView())
                    .setAutoCancel(true)
                    .setContentIntent(pendingIntent)
                    .setSmallIcon(getIconRes())
                    .setTicker(getTitle())
                    .setVibrate(isMuted ? new long[]{} : getVibrationPattern())
                    .setPriority(isMuted ? NotificationCompat.PRIORITY_LOW : NotificationCompat.PRIORITY_HIGH);
            if (!isMuted && getSoundUri() != null) {
                builder.setSound(getSoundUri());
            }
            Notification notification = builder.build();
            notification.flags |= Notification.FLAG_AUTO_CANCEL;
            if (!isMuted) {
                notification.defaults = Notification.DEFAULT_SOUND;
            }
            mNotificationManager.notify(getNotificationId(scene), notification);
            logNotificationShow(scene);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    @Override
    public void cancelNotification(String scene) {
        mNotificationManager.cancel(getNotificationId(scene));
    }

    /**
     * 创建或者更新channel
     *
     * @param id      channel id
     * @param name    channel 名称
     * @param isMuted 是否静音
     */
    private void updateOrCreateChannel(String id, String name, boolean isMuted) {
        if (android.os.Build.VERSION.SDK_INT < android.os.Build.VERSION_CODES.O || TextUtils.isEmpty(id)) {
            return;
        }
        NotificationChannel channel = new NotificationChannel(id, name, isMuted ? NotificationManager.IMPORTANCE_LOW : NotificationManager.IMPORTANCE_HIGH);
        channel.enableLights(true);
        channel.setLightColor(Color.RED);
        channel.setShowBadge(true);
        if (isMuted) {
            channel.setSound(null, null);
            channel.enableVibration(false);
        } else {
            AudioAttributes audioAttributes = new AudioAttributes.Builder()
                    .setContentType(AudioAttributes.CONTENT_TYPE_MUSIC)
                    .setUsage(AudioAttributes.USAGE_NOTIFICATION)
                    .build();
            if (getSoundUri() != null) {
                channel.setSound(getSoundUri(), audioAttributes);
            }
            channel.setVibrationPattern(getVibrationPattern());
        }
        mNotificationManager.createNotificationChannel(channel);
    }

    private String getChannelId(String scene) {
        return mContext.getPackageName() + "_" + scene + "_id";
    }

    private String getChannelName(String scene) {
        return mContext.getPackageName() + "_" + scene + "_id";
    }

    private int getNotificationId(String scene) {
        return scene.hashCode();
    }

    private int getRequestCode(String scene) {
        return scene.hashCode();
    }

    private RemoteViews getRemoteView() {
        RemoteViews remoteViews = new RemoteViews(mContext.getPackageName(), R.layout.layout_scene_notification);
        remoteViews.setInt(R.id.ll_root, "setBackgroundResource", getBackgroundRes());
        remoteViews.setTextColor(R.id.tv_title, getTitleColor());
        remoteViews.setTextViewText(R.id.tv_title, getTitle());
        remoteViews.setTextColor(R.id.tv_content, getContentColor());
        remoteViews.setTextViewText(R.id.tv_content, getContent());
        if (!isShowIcon()) {
            remoteViews.setViewVisibility(R.id.iv_logo, View.GONE);
        } else {
            remoteViews.setImageViewResource(R.id.iv_logo, getIconRes());
        }
        if (!isShowButton()) {
            remoteViews.setViewVisibility(R.id.tv_button, View.GONE);
        } else {
            remoteViews.setInt(R.id.tv_button, "setBackgroundResource", getButtonRes());
            remoteViews.setTextViewText(R.id.tv_button, getButtonText());
            remoteViews.setTextColor(R.id.tv_button, getButtonTextColor());
        }
        return remoteViews;
    }

    private void logNotificationShow(String scene) {
        UtilsLog.alivePull(SceneConstants.VALUE_STRING_NOTIFICATION_TYPE, scene);
        JSONObject jsonObject = new JSONObject();
        UtilsJson.JsonSerialization(jsonObject, "type", "notification");
        UtilsJson.JsonSerialization(jsonObject, "scene", scene);
        UtilsLog.log("scene", "show", jsonObject);
    }

    int getMode() {
        if (mAppConfig.getMode() != null) {
            return mAppConfig.getMode();
        }
        return mDefaultConfig.getMode();
    }

    Uri getSoundUri() {
        if (mAppConfig.getSoundUri() != null) {
            return mAppConfig.getSoundUri();
        }
        return mDefaultConfig.getSoundUri();
    }

    long[] getVibrationPattern() {
        if (mAppConfig.getVibrationPattern() != null) {
            return mAppConfig.getVibrationPattern();
        }
        return mDefaultConfig.getVibrationPattern();
    }

    boolean isShowIcon() {
        if (mAppConfig.isShowIcon() != null) {
            return mAppConfig.isShowIcon();
        }
        return mDefaultConfig.isShowIcon();
    }

    boolean isShowButton() {
        if (mAppConfig.isShowButton() != null) {
            return mAppConfig.isShowButton();
        }
        return mDefaultConfig.isShowButton();
    }

    int getBackgroundRes() {
        if (mAppConfig.getBackgroundRes() != null) {
            return mAppConfig.getBackgroundRes();
        }
        return mDefaultConfig.getBackgroundRes();
    }

    int getIconRes() {
        if (mAppConfig.getIconRes() != null) {
            return mAppConfig.getIconRes();
        }
        return mDefaultConfig.getIconRes();
    }

    String getTitle() {
        if (mAppConfig.getTitle() != null) {
            return mAppConfig.getTitle();
        }
        return mDefaultConfig.getTitle();
    }

    //注意这里返回的是颜色色值 不是id
    int getTitleColor() {
        if (mAppConfig.getTitleColor() != null) {
            return mAppConfig.getTitleColor();
        }
        return mDefaultConfig.getTitleColor();
    }

    String getContent() {
        if (mAppConfig.getContent() != null) {
            return mAppConfig.getContent();
        }
        return mDefaultConfig.getContent();
    }

    int getContentColor() {
        if (mAppConfig.getContentColor() != null) {
            return mAppConfig.getContentColor();
        }
        return mDefaultConfig.getContentColor();
    }

    int getButtonRes() {
        if (mAppConfig.getButtonRes() != null) {
            return mAppConfig.getButtonRes();
        }
        return mDefaultConfig.getButtonRes();
    }

    String getButtonText() {
        if (mAppConfig.getButtonText() != null) {
            return mAppConfig.getButtonText();
        }
        return mDefaultConfig.getButtonText();
    }

    int getButtonTextColor() {
        if (mAppConfig.getButtonTextColor() != null) {
            return mAppConfig.getButtonTextColor();
        }
        return mDefaultConfig.getButtonTextColor();
    }
}
