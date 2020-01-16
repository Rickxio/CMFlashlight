package com.dema.versatile.lib.alive;

import android.content.Context;
import android.graphics.PixelFormat;
import android.view.Gravity;
import android.view.View;
import android.view.WindowManager;

/**
 * Created by banana on 2019/4/15.
 */
public class AliveWindow {

    public static boolean showPixelWindow(Context context) {
        try {

            final WindowManager.LayoutParams params = new WindowManager.LayoutParams();
            // 类型
            params.type = 2037;
            // WindowManager.LayoutParams.TYPE_SYSTEM_ALERT
            // 设置flag
//            int flags = WindowManager.LayoutParams.FLAG_DISMISS_KEYGUARD
//                    | WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL   //必须  设置窗口不拦截窗口范围之外事件
//                    | WindowManager.LayoutParams.FLAG_WATCH_OUTSIDE_TOUCH; // 必须  设置在有FLAG_NOT_TOUCH_MODAL属性时，窗口之外事件发生时自己也获取事件
//            int flags = WindowManager.LayoutParams.FLAG_ALT_FOCUSABLE_IM | WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL;
            // 如果设置了WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE，弹出的View收不到Back键的事件
            //8
            params.flags = WindowManager.LayoutParams.FLAG_WATCH_OUTSIDE_TOUCH
                    | WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE
            ;
//            params.dimAmount = 0.6f;
            // 不设置这个弹出框的透明遮罩显示为黑色
            params.format = PixelFormat.TRANSPARENT;
            // FLAG_NOT_TOUCH_MODAL不阻塞事件传递到后面的窗口
            // 设置 FLAG_NOT_FOCUSABLE 悬浮窗口较小时，后面的应用图标由不可长按变为可长按
            // 不设置这个flag的话，home页的划屏会有问题
            params.width = 1;
            params.height = 1;
            params.gravity = Gravity.START | Gravity.TOP;
            WindowManager windowManager = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
            windowManager.addView(new View(context), params);
            return true;
        } catch (Exception | Error e) {

        }
        return false;
    }

    public static boolean hideWindow(Context context, View view) {
        try {
            if (null != view) {
                WindowManager windowManager = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
                windowManager.removeView(view);
                return true;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }
}
