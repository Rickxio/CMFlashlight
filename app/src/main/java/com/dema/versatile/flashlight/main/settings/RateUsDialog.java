package com.dema.versatile.flashlight.main.settings;

import android.content.Context;
import android.graphics.Color;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import com.dema.versatile.flashlight.R;
import com.dema.versatile.flashlight.core.log.RateLog;
import com.dema.versatile.logic.rateus.BaseRateUsDialog;

/**
 * Create on 2019/10/11 15:47
 *
 * @author XuChuanting
 */
public class RateUsDialog extends BaseRateUsDialog {


    public RateUsDialog(AppCompatActivity context) {
        super(context);
    }

    @Override
    protected void initUI() {
        super.initUI();
        setCancelable(false);
        Context context = getContext();

        //背景
        mRlRoot.setBackground(ContextCompat.getDrawable(context,R.drawable.bg_rateus_dialog));

        //关闭按钮
        mIvClose.setImageResource(R.drawable.icon_close);
        //图标
        mIvIcon.setImageResource(R.mipmap.ic_launcher);

        //标题
        mTvTitle.setTextColor(0xffffffff);
        mTvTitle.setText(R.string.app_name);

        //副标题
        mTvContent.setTextColor(0x96FFFFFF);
        mTvContent.setText(R.string.text_grade);

        //评分条
        mRatingBar.setLightColor(0xFFFFE800);
        mRatingBar.setStarBg(ContextCompat.getDrawable(context,R.drawable.sel_star));

        setStringThanks(context.getString(R.string.thanks_feedback));

        //rateus按钮
        mTvConfirm.setBackground(ContextCompat.getDrawable(context,R.drawable.bg_btn_rate_us));
        mTvConfirm.setTextColor(Color.WHITE);
        String strRateUs = context.getString(R.string.rate_us).toUpperCase();
        mTvConfirm.setText(strRateUs);

    }

    @Override
    protected void onFiveStarReceived() {
        RateLog.fiveStarReport();
    }


    @Override
    protected void onRateConfirmed(int number) {
        RateLog.submitReport(number);
    }


    @Override
    protected void onCloseClicked(int number) {
        RateLog.closeReport(number);
    }

    @Override
    public void safeShow() {
        super.safeShow();
        RateLog.showReport();
    }
}
