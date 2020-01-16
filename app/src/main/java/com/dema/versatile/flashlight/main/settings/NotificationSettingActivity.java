package com.dema.versatile.flashlight.main.settings;


import android.os.Bundle;
import android.widget.ImageView;

import androidx.appcompat.widget.Toolbar;

import com.dema.versatile.flashlight.R;
import com.dema.versatile.flashlight.core.CoreFactory;
import com.dema.versatile.flashlight.core.settings.ISettingMgr;
import com.dema.versatile.flashlight.main.base.BaseActivity;
import com.dema.versatile.lib.utils.UtilsLog;

public class NotificationSettingActivity extends BaseActivity {

    private static final String LOG_KEY1 = "Notification_switch";
    private Toolbar mToolbar;
    private ISettingMgr mSettingMgr;
    private ImageView mIvSwitch;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notification_setting);

        mToolbar = findViewById(R.id.toolbar);
        mToolbar.setNavigationOnClickListener(v -> finish());


        mSettingMgr = CoreFactory.getInstance().createInstance(ISettingMgr.class);
        mIvSwitch = findViewById(R.id.iv_switch);
        mIvSwitch.setSelected(mSettingMgr.isSceneOpen());

        findViewById(R.id.rl_switch).setOnClickListener(v -> {

            boolean selected = mIvSwitch.isSelected();
            if (selected) {

                showAlertDialog();
            } else {
                mIvSwitch.setSelected(true);
                mSettingMgr.setSceneState(true);
                UtilsLog.log(LOG_KEY1, "open", null);

            }

        });


    }

    private void showAlertDialog() {

        SceneAlertDialog dialog = new SceneAlertDialog(this);
        dialog.setButtonClickListener(new SceneAlertDialog.OnButtonClickListener() {
            @Override
            public void onCancelClicked() {

            }

            @Override
            public void onOKClicked() {

                mIvSwitch.setSelected(false);
                mSettingMgr.setSceneState(false);
                UtilsLog.log(LOG_KEY1, "close", null);

            }
        });

        dialog.setCancelable(false);
        dialog.show();

    }
}
