package com.dema.versatile.lib.tool;

import android.app.Activity;
import android.app.Application;
import android.os.Bundle;

import java.util.LinkedList;
import java.util.List;
import java.util.ListIterator;

import com.dema.versatile.lib.alive.AlivePixelActivity;

/**
 * Created by wangyu on 2019/7/8.
 */
public class AppStatusTool implements  Application.ActivityLifecycleCallbacks {
    private static final AppStatusTool sInstance = new AppStatusTool();
    //不保存activity对象，避免不知道什么情况下发生的内存泄露
    private List<Activity> mActivityList = new LinkedList<>();
    private int mCountOfStartedActivities;
    private boolean mInForeground;

    public static AppStatusTool getInstance() {
        return sInstance;
    }

    public void init(Application context) {
        context.registerActivityLifecycleCallbacks(this);
    }

    public boolean isForeground() {
        return mInForeground;
    }

//    public boolean containsActivity(Class<? extends Activity> activityCls) {
//        if (activityCls != null) {
//            return mActivityList.contains(activityCls);
//        }
//        return false;
//    }

    public void finishOtherActivity(Activity current) {
        ListIterator<Activity> iterator = mActivityList.listIterator();
        while (iterator.hasNext()) {
            Activity activity = iterator.next();
            try {
                if (activity.getClass() != current.getClass()) {
                    activity.finish();
                    iterator.remove();
                }
            } catch (Exception e) {

            }
        }
    }

    public void finishAllActivity() {
        for (Activity activity : mActivityList) {
            try {
                activity.finish();
            } catch (Exception e) {

            }
        }
        try {
            mActivityList.clear();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onActivityCreated(Activity activity, Bundle savedInstanceState) {
        if (activity.getClass() == AlivePixelActivity.class) {
            return;
        }
        if (!mActivityList.contains(activity)) {
            mActivityList.add(activity);
        }
    }

    @Override
    public void onActivityStarted(Activity activity) {
        if (activity.getClass() == AlivePixelActivity.class) {
            return;
        }
        mCountOfStartedActivities++;
        if (mCountOfStartedActivities >= 1) {
            mInForeground = true;
        }
    }

    @Override
    public void onActivityResumed(Activity activity) {

    }

    @Override
    public void onActivityPaused(Activity activity) {

    }

    @Override
    public void onActivityStopped(Activity activity) {
        if (activity.getClass() == AlivePixelActivity.class) {
            return;
        }
        mCountOfStartedActivities--;
        if (mCountOfStartedActivities <= 0) {
            mInForeground = false;
        }
    }

    @Override
    public void onActivitySaveInstanceState(Activity activity, Bundle outState) {

    }

    @Override
    public void onActivityDestroyed(Activity activity) {
        if (activity.getClass() == AlivePixelActivity.class) {
            return;
        }
        mActivityList.remove(activity);
    }

}
