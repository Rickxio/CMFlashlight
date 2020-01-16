package com.dema.versatile.logic.rateus;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.dema.versatile.lib.utils.UtilsLog;
import com.dema.versatile.lib.view.CMDialog;
import com.dema.versatile.logic.R;
import com.dema.versatile.scene.utils.UtilsScreen;

/**
 * @author jack
 * Create on 2019/4/3 14:13
 */
public class BaseRateUsDialog extends CMDialog {

    /**
     * 当前展示逻辑，到达完成页记录一次，记录为两次及以上时，
     * 再次回到main页时进行展示，展示过后，以后不再展示
     */
    protected RelativeLayout mRlRoot;
    protected ImageView mIvClose;
    protected ImageView mIvIcon;
    protected TextView mTvTitle;
    protected TextView mTvContent;
    protected StarRatingBar mRatingBar;
    protected TextView mTvConfirm;
    private Activity mActivity;
    private int mNumber = 5;
    private Runnable rate = () -> {
        if (mNumber == 5) {

            onFiveStarReceived();
            UtilsLog.log("rate", "5_star", null);
            gotoGP();
            dismiss();
        }
    };

    /**
     * 选中5颗星
     */
    protected void onFiveStarReceived() {

    }

    private String mStringThanks = "Thanks for your feedback";

    protected BaseRateUsDialog(AppCompatActivity context) {
        super(context);
        mActivity = context;
    }

    public  void safeShow() {
        if (mActivity != null && !mActivity.isFinishing() && mActivity instanceof AppCompatActivity) {
            show();
        }
    }

    public void setStringThanks(String stringThanks) {
        mStringThanks = stringThanks;
    }

    @Override
    public void onStart() {
        super.onStart();
        Window window = getWindow();
        if (window != null) {
            WindowManager.LayoutParams attr = window.getAttributes();
            window.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
            attr.width = (int) (UtilsScreen.getScreenWidth(window.getContext()) * 0.9f);
            attr.height = WindowManager.LayoutParams.WRAP_CONTENT;
            window.setAttributes(attr);
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dialog_rate_us);
        initView();
        initUI();
        mRatingBar.startAnim();

        mRatingBar.setListener(starNum -> {
            mNumber = starNum;
            mRatingBar.postDelayed(rate, 200);
        });

        mIvClose.setOnClickListener(v -> {
            onCloseClicked(mNumber);
            dismiss();
        });

        mTvConfirm.setOnClickListener(v -> {
            if (null != mActivity) {
                try {
                    if (mNumber == 5) {
                        UtilsLog.log("rate", "5_star", null);
                        gotoGP();
                    } else {
                        Toast.makeText(mActivity, mStringThanks, Toast.LENGTH_LONG).show();
                    }
                } catch (Exception e) {
                }
            }

            onRateConfirmed(mNumber);

            dismiss();
        });
    }

    protected void onRateConfirmed(int number) {

    }

    protected void onCloseClicked(int number) {

    }

    protected void initUI() {

    }

    private void initView() {
        mRlRoot = findViewById(R.id.rl_root);
        mIvClose = findViewById(R.id.iv_rate_us_close);
        mIvIcon = findViewById(R.id.iv_rate_us_icon);
        mTvTitle = findViewById(R.id.tv_rate_us_title);
        mTvContent = findViewById(R.id.tv_rate_us_text);
        mRatingBar = findViewById(R.id.rating_bar);
        mTvConfirm = findViewById(R.id.tv_rate_us_confirm);
    }

    private void gotoGP() {
        Context context = mActivity;
        if (context == null) {
            return;
        }
        try {
            Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=" + context.getPackageName()));
            intent.setPackage("com.android.vending");
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            context.startActivity(intent);
        } catch (Exception e) {
            try {
                Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://play.google.com/store/apps/details?id="
                        + context.getPackageName()));
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                context.startActivity(intent);
            } catch (Exception ignored) {
            }
        }
    }


    @Override
    public void dismiss() {
        if (mRatingBar != null) {
            mRatingBar.cancelAnim();
            mRatingBar.removeCallbacks(rate);
        }
        super.dismiss();
        mRatingBar = null;
    }
}
