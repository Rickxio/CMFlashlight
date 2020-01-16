package com.dema.versatile.scene.utils;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.drawable.Drawable;
import android.text.TextUtils;

import java.util.Random;

import com.dema.versatile.scene.core.CMSceneFactory;

/**
 * Created by wangyu on 2019/9/3.
 */
public class UtilsScene {

    public static String getSceneTitle(String scene) {
        return getRandomText(scene + "_title");
    }

    public static String getSceneContent(String scene) {
        if (scene == null) {
            return null;
        }
        String randomText = getRandomText(scene + "_content");
        if (randomText != null) {
            switch (scene) {
                case "tips_battery":
                    return String.format(randomText, new Random().nextInt(20) + 10);
                case "tips_boost":
                case "tips_network":
                    return String.format(randomText, new Random().nextInt(20) + 20);
                case "tips_clean":
                    return String.format(randomText, new Random().nextInt(150) + 50);
                case "tips_cool":
                    return String.format(randomText, new Random().nextInt(5) + 3);
            }
        }
        return randomText;
    }

    public static String getSceneButtonText(String scene) {
        return getRandomText(scene + "_button");
    }


    private static String getRandomText(String arrayName) {
        try {
            Context context = CMSceneFactory.getApplication();
            int id = context.getResources().getIdentifier(arrayName, "array", context.getPackageName());
            String[] stringArray = context.getResources().getStringArray(id);
            Random random = new Random();
            return stringArray[random.nextInt(stringArray.length)];
        } catch (Resources.NotFoundException e) {
            e.printStackTrace();
        }
        return null;
    }

    private static Drawable getDrawable(String name) {
        try {
            Context context = CMSceneFactory.getApplication();
            int id = context.getResources().getIdentifier(name, "drawable", context.getPackageName());
            return context.getResources().getDrawable(id);
        } catch (Resources.NotFoundException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static int getSceneImageRes(String scene) {
        if (TextUtils.isEmpty(scene)) {
            return 0;
        }
        Context context = CMSceneFactory.getApplication();
        if ("tips_sleep".equals(scene) || "tips_health".equals(scene)) {
            return context.getResources().getIdentifier("ic_" + scene + "_" + (new Random().nextInt(5) + 1), "drawable", context.getPackageName());
        } else {
            return context.getResources().getIdentifier("ic_" + scene, "drawable", context.getPackageName());
        }
    }

//    public static Drawable getSceneImageDrawable(String scene) {
//        if (TextUtils.isEmpty(scene)) {
//            return null;
//        }
//        if ("tips_sleep".equals(scene) || "tips_health".equals(scene)) {
//            return getDrawable("ic_" + scene + "_" + (new Random().nextInt(6) + 1));
//        } else {
//            return getDrawable("ic_" + scene);
//        }
//    }

    public static String getLottieImagePath(String scene) {
        return scene + "/images";
    }

    public static String getLottieFilePath(String scene) {
        return scene + "/data.json";
    }

    public static boolean isOptimizingScene(String scene) {
        if (TextUtils.isEmpty(scene)) {
            return false;
        }
        return scene.contains("boost") || scene.contains("battery") || scene.contains("cool")
                || scene.contains("clean") || scene.contains("network");
    }

    public static boolean isPullScene(String scene) {
        if (TextUtils.isEmpty(scene)) {
            return false;
        }
        return scene.startsWith("pull");
    }
}
