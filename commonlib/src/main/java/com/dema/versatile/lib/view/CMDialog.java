package com.dema.versatile.lib.view;

import android.app.Dialog;
import android.view.WindowManager;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.Lifecycle;
import androidx.lifecycle.LifecycleEventObserver;
import androidx.lifecycle.LifecycleOwner;

import com.dema.versatile.lib.R;
import com.dema.versatile.lib.utils.UtilsSize;

public class CMDialog extends Dialog implements LifecycleEventObserver {

    private AppCompatActivity mActivity;
    private Lifecycle mActivityLifecycle;

    public CMDialog(AppCompatActivity context) {
        super(context, R.style.DialogTheme);
        init(context);
    }

    private void init(AppCompatActivity context) {
        mActivity = context;
        mActivityLifecycle = mActivity.getLifecycle();
    }

    @Override
    public void show() {
        try {
            //判断至少是大于create状态才show
            if (mActivityLifecycle.getCurrentState().isAtLeast(Lifecycle.State.CREATED)) {
                super.show();
                mActivityLifecycle.addObserver(this);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    public void show(boolean bIsFillHorizontalScreen, boolean bIsFillVerticalScreen) {
        try {
            //判断至少是大于create状态才show
            if (mActivityLifecycle.getCurrentState().isAtLeast(Lifecycle.State.CREATED)) {
                super.show();
                if (bIsFillHorizontalScreen || bIsFillVerticalScreen) {
                    WindowManager.LayoutParams lp = getWindow().getAttributes();
                    if (bIsFillHorizontalScreen) {
                        lp.width = UtilsSize.getScreenWidth(mActivity);
                    }

                    if (bIsFillVerticalScreen) {
                        lp.height = UtilsSize.getScreenHeight(mActivity);
                    }
                    getWindow().setAttributes(lp);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    @Override
    public void dismiss() {
        try {
            if (mActivityLifecycle.getCurrentState().isAtLeast(Lifecycle.State.CREATED)) {
                super.dismiss();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onStateChanged(@NonNull LifecycleOwner source, @NonNull Lifecycle.Event event) {
        if (Lifecycle.Event.ON_DESTROY.equals(event)) {
            if (isShowing()) {
                try {
                    super.dismiss();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }
}
