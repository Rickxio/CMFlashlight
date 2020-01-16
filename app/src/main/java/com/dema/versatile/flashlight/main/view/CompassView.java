package com.dema.versatile.flashlight.main.view;

import android.content.Context;
import android.os.Handler;
import android.util.AttributeSet;
import android.view.animation.AccelerateInterpolator;
import android.widget.FrameLayout;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.FragmentActivity;
import androidx.lifecycle.Lifecycle;
import androidx.lifecycle.OnLifecycleEvent;
import com.dema.versatile.flashlight.R;
import com.dema.versatile.flashlight.core.compass.im.SensorObserverImpl;
import com.dema.versatile.flashlight.core.compass.in.ISensorListener;

public class CompassView extends FrameLayout implements ISensorListener {
    private final float VALUE_FLOAT_MAX_ROTATE_DEGREE = 1.0f;
    private final int VALUE_INT_HANDLER_INTERVAL = 10;

    private ConstraintLayout mConstraintLayout;
    private ImageView mIvRing;
    private ImageView mIvWatch;

    private Context mContext;
    private AccelerateInterpolator mInterpolator;
    private Handler mHandler;
    private boolean mStopDrawing = true;
    private float mDirection;
    private float mTargetDirection;
    private SensorObserverImpl mISensorObserver;
    protected Runnable mCompassViewUpdater = new Runnable() {
        @Override
        public void run() {
            if (mStopDrawing) {
                return;
            }
            if (mDirection != mTargetDirection) {
                // calculate the short routine
                float to = mTargetDirection;
                if (to - mDirection > 180) {
                    to -= 360;
                } else if (to - mDirection < -180) {
                    to += 360;
                }
                // limit the max speed to MAX_ROTATE_DEGREE
                float distance = to - mDirection;
                if (Math.abs(distance) > VALUE_FLOAT_MAX_ROTATE_DEGREE) {
                    distance = distance > 0 ? VALUE_FLOAT_MAX_ROTATE_DEGREE : (-1.0f * VALUE_FLOAT_MAX_ROTATE_DEGREE);
                }

                // need to slow down if the distance is short
                mDirection = normalizeDegree(mDirection
                        + ((to - mDirection) * mInterpolator.getInterpolation(Math
                        .abs(distance) > VALUE_FLOAT_MAX_ROTATE_DEGREE ? 0.4f : 0.3f)));
                updateDirection(mDirection);
            }
            mHandler.postDelayed(mCompassViewUpdater, VALUE_INT_HANDLER_INTERVAL);
        }
    };


    public CompassView(@NonNull Context context) {
        super(context);
        initView(context);
    }

    public CompassView(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        initView(context);
    }

    private void initView(Context context) {
        mContext = context;
        inflate(context, R.layout.view_compass, this);
        mConstraintLayout = findViewById(R.id.constraint_layout);
        mIvRing = findViewById(R.id.iv_ring);
        mIvWatch = findViewById(R.id.iv_watch);
        mInterpolator = new AccelerateInterpolator();
        mHandler = new Handler();
        mISensorObserver = SensorObserverImpl.getInstance();
        if (mISensorObserver.init()){
            mISensorObserver.setListener(this);
            ((FragmentActivity)context).getLifecycle().addObserver(mISensorObserver);
        }
    }

    public void updateDirection(float direction) {
        mDirection = direction;
        mIvWatch.setRotation(direction);
    }


    @OnLifecycleEvent(value = Lifecycle.Event.ON_DESTROY)
    public void cancelAnimation() {
    }

    private float normalizeDegree(float degree) {
        return (degree + 720) % 360;
    }

    public void onResume(){
        mStopDrawing = false;
        mHandler.postDelayed(mCompassViewUpdater, VALUE_INT_HANDLER_INTERVAL);
    }

    public void onPause(){
        mStopDrawing = true;
    }

    @Override
    public void onAngelChanged(float degree, float pitchAngle, float rollAngle) {
        float direction = degree * -1.0f;
        mTargetDirection = normalizeDegree(direction);
    }
}
