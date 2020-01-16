package com.dema.versatile.flashlight.main.settings;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import com.dema.versatile.flashlight.R;
import com.dema.versatile.lib.view.CMDialog;

/**
 * Create on 2019/10/11 14:22
 *
 * @author XuChuanting
 */
public class SceneAlertDialog extends CMDialog {


    private OnButtonClickListener mButtonClickListener;

    public SceneAlertDialog(AppCompatActivity context) {
        super(context);
    }

    public void setButtonClickListener(OnButtonClickListener buttonClickListener) {
        mButtonClickListener = buttonClickListener;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dialog_scene_close);

        findViewById(R.id.tv_cancel).setOnClickListener(v -> {
            if (mButtonClickListener != null) {
                mButtonClickListener.onCancelClicked();
            }
            dismiss();
        });


        findViewById(R.id.tv_ok).setOnClickListener(v -> {
            if (mButtonClickListener != null) {
                mButtonClickListener.onOKClicked();
            }
            dismiss();
        });

    }


    public interface OnButtonClickListener {
        void onCancelClicked();

        void onOKClicked();
    }
}
