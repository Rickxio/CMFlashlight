package com.dema.versatile.mediation.view;

import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.widget.FrameLayout;

public class AdMaskLayout extends FrameLayout {
    private boolean mIsNeedDispatch = false;

    public AdMaskLayout(Context context) {
        this(context, null);
    }

    public AdMaskLayout(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public AdMaskLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        return mIsNeedDispatch || super.dispatchTouchEvent(ev);
    }

    public void setInterceptTouchEvent(boolean bIsNeedDispatch) {
        mIsNeedDispatch = bIsNeedDispatch;
    }
}
