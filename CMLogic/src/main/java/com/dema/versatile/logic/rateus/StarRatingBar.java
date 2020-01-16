package com.dema.versatile.logic.rateus;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.StateListDrawable;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.View;
import android.view.animation.OvershootInterpolator;
import android.widget.LinearLayout;

import androidx.annotation.ColorInt;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;

import java.util.ArrayList;
import java.util.List;

import com.dema.versatile.lib.utils.UtilsSize;
import com.dema.versatile.logic.R;

public class StarRatingBar extends LinearLayout {
    private Context mContext;
    private int mStarCount;
    private int mSelectedCount = 5;
    private int mStarSpacing;
    private int mStarSize;
    private Drawable mStarSelector;
    private View[] mStarViews;
    private boolean mFlagEnable;
    private AnimatorSet mAnimWave;
    private ValueAnimator mAnimLights;
    private Listener mListener;
    private Paint mPaintLights;
    private float mLastStarCenterX, mCenterY;
    private float mRadiusOuter, mRadiusInner;
    private boolean mFlagShowLights;
    private int mLightsAlpha;
    private int mCountAnimWave;

    public StarRatingBar(Context context) {
        this(context, null);
    }

    public StarRatingBar(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public StarRatingBar(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        TypedArray ta = context.obtainStyledAttributes(attrs, R.styleable.StarRatingBar);
        mStarCount = ta.getInteger(R.styleable.StarRatingBar_starCount, 5);
        mStarSize = ta.getDimensionPixelSize(R.styleable.StarRatingBar_starSize, UtilsSize.dpToPx(context, 30f));
        mStarSpacing = ta.getDimensionPixelSize(R.styleable.StarRatingBar_starSpacing, 0);

        mStarSelector = ta.getDrawable(R.styleable.StarRatingBar_starSelector);
        if (mStarSelector == null) {
            StateListDrawable drawable = new StateListDrawable();
            drawable.addState(new int[]{android.R.attr.state_selected}, ContextCompat.getDrawable(context, android.R.drawable.star_on));
            drawable.addState(new int[]{-android.R.attr.state_selected}, ContextCompat.getDrawable(context, android.R.drawable.star_off));
            mStarSelector = drawable;
        }
        ta.recycle();
        init(context);
        setWillNotDraw(false);
    }

    public void setStarBg(Drawable drawable) {
        if (drawable == null) {
            return;
        }
        mStarSelector = drawable;
        initChildViews();
    }

    private void init(Context context) {
        setGravity(Gravity.CENTER);
        setClipToPadding(false);
        mContext = context;
        initChildViews();
        mPaintLights = new Paint(Paint.ANTI_ALIAS_FLAG);
        mPaintLights.setColor(Color.YELLOW);
        mPaintLights.setStyle(Paint.Style.STROKE);
        mPaintLights.setStrokeWidth(UtilsSize.dpToPx(context, 2f));
    }

    public void setLightColor(@ColorInt int lightColor) {
        mPaintLights.setColor(lightColor);
    }

    private void initChildViews() {
        removeAllViews();
        mStarViews = new View[mStarCount];
        for (int i = 0; i < mStarCount; i++) {
            View view = new View(mContext);
            LayoutParams params = new LayoutParams(mStarSize, mStarSize);
            if (i < mStarCount - 1) {
                params.setMarginEnd(mStarSpacing);
            }
            Drawable.ConstantState constantState = mStarSelector.getConstantState();
            if (constantState != null) {
                Drawable drawable = constantState.newDrawable();
                view.setBackground(drawable);
            }
            if (i + 1 <= mSelectedCount) {
                view.setSelected(true);
            } else {
                view.setSelected(false);
            }
            view.setOnClickListener(new OnStarItemClickListener(i));
            mStarViews[i] = view;
            addView(view, params);
        }
    }

    public void startAnim() {
        postDelayed(this::startGuideAnim, 500);
    }

    public void cancelAnim() {
        if (null != mAnimWave && mAnimWave.isRunning()) {
            mAnimWave.removeAllListeners();
            mAnimWave.cancel();
            mAnimWave = null;
        }
        if (null != mAnimLights && mAnimLights.isRunning()) {
            mAnimLights.removeAllUpdateListeners();
            mAnimLights.cancel();
            mAnimLights = null;
        }
    }

    public void setListener(Listener listener) {
        mListener = listener;
    }

    public int getSelectedCount() {
        return mSelectedCount;
    }

    public void setSelectedCount(int selectedCount) {
        mSelectedCount = selectedCount;
        resetSelectedStatus();
    }

    private void startGuideAnim() {
        if (mStarViews == null) {
            return;
        }
        cancelAnim();
        if (mLastStarCenterX <= 0) {
            if (LAYOUT_DIRECTION_RTL == getLayoutDirection()) {
                mLastStarCenterX = getPaddingEnd() + mStarSize * 0.5f;
            } else {
                mLastStarCenterX = getPaddingStart();
                for (int i = 0; i < mStarCount; i++) {
                    View view = getChildAt(i);
                    LayoutParams params = (LayoutParams) view.getLayoutParams();
                    mLastStarCenterX += mStarSize + params.getMarginEnd();
                }
                mLastStarCenterX -= mStarSize * 0.5f;
            }
        }
        mCountAnimWave = 0;
        List<Animator> animators = new ArrayList<>();
        for (int i = 0, count = mStarViews.length; i < count; i++) {
            ObjectAnimator fraction1 = ObjectAnimator.ofFloat(mStarViews[i], "alpha", 0f, 1f);
            fraction1.setStartDelay(100);
            ObjectAnimator translationY1 = ObjectAnimator.ofFloat(mStarViews[i],
                    "translationY", 0, -0.5f * mStarSize, 0);
            translationY1.setInterpolator(new OvershootInterpolator());
            AnimatorSet animatorSet = new AnimatorSet();
            animatorSet.playTogether(fraction1, translationY1);
            animatorSet.setDuration(400);
            animatorSet.setStartDelay(i * 50);
            animators.add(animatorSet);
        }
        mAnimWave = new AnimatorSet();
        mAnimWave.playTogether(animators);
        mAnimWave.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                mCountAnimWave++;
                if (2 == mCountAnimWave) {
                    mFlagEnable = true;
                    mFlagShowLights = true;
                    mLightsAlpha = 0;
                    invalidate();
                    mAnimLights = ValueAnimator.ofInt(0, 255).setDuration(600);
                    mAnimLights.setRepeatCount(ValueAnimator.INFINITE);
                    mAnimLights.setRepeatMode(ValueAnimator.REVERSE);
                    mAnimLights.addUpdateListener(animation1 -> {
                        mLightsAlpha = (int) animation1.getAnimatedValue();
                        invalidate();
                    });
                    mAnimLights.start();
                } else {
                    mAnimWave.start();
                }
            }
        });
        mAnimWave.start();
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        Drawable drawable = mStarSelector;
        int iWidth;
        if (w <= 0 && drawable != null) {
            iWidth = drawable.getIntrinsicWidth();
        } else {
            iWidth = (int) ((w - getPaddingStart() - getPaddingEnd() - (mStarCount - 1) * mStarSpacing) * 1.0f / mStarCount);
        }
        if (mStarSize > 0 && mStarSize > iWidth) {
            mStarSize = iWidth;
            initChildViews();
        }
        mCenterY = (getPaddingTop() + h - getPaddingBottom()) * 0.5f;
        mRadiusOuter = h * 0.5f;
        mRadiusInner = mStarSize * 0.5f + UtilsSize.dpToPx(mContext, 2f);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        if (mFlagShowLights) {
            mPaintLights.setAlpha(mLightsAlpha);
            for (int i = 130; i >= 50; i -= 20) {
                double degree = -i * Math.PI / 180;
                float px1 = (float) (mLastStarCenterX + mRadiusOuter * Math.cos(degree));
                float py1 = (float) (mCenterY + mRadiusOuter * Math.sin(degree));
                float px2 = (float) (mLastStarCenterX + mRadiusInner * Math.cos(degree));
                float py2 = (float) (mCenterY + mRadiusInner * Math.sin(degree));
                canvas.drawLine(px1, py1, px2, py2, mPaintLights);
            }
        }
    }

    private void resetSelectedStatus() {
        if (mStarViews != null) {
            for (int i = 0; i < mStarCount; i++) {
                boolean selected = i <= mSelectedCount - 1;
                mStarViews[i].setSelected(selected);
            }
        }
    }

    public interface Listener {
        void onRatingCallback(int starNum);
    }

    private class OnStarItemClickListener implements OnClickListener {
        int index;

        OnStarItemClickListener(int index) {
            this.index = index;
        }

        @Override
        public void onClick(View v) {
            if (!mFlagEnable) {
                return;
            }
            mFlagShowLights = false;
            mLightsAlpha = 0;
            invalidate();

            if (mSelectedCount == index + 1) {
                return;
            }
            mSelectedCount = index + 1;
            resetSelectedStatus();
            if (mListener != null) {
                mListener.onRatingCallback(mSelectedCount);
            }
        }
    }


}
