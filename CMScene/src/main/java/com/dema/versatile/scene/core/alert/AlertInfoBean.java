package com.dema.versatile.scene.core.alert;

import java.io.Serializable;

/**
 * Created by wangyu on 2019/10/30.
 */
public class AlertInfoBean implements Serializable {
    public String scene;
    public String trigger;
    public Integer count;
    public String title;
    public String content;
    public String buttonText;
    public boolean isAnimation;
    public Integer imageRes;
    public String lottieImageFolder;
    public String lottieFilePath;
    public int lottieRepeatCount;
    public Integer backgroundRes;
    public Integer closeIconRes;
    public Integer titleColor;
    public Integer contentColor;
    public Integer buttonBackgroundRes;
    public Integer buttonTextColor;
}
