package com.dema.versatile.flashlight.main.function;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;

import butterknife.BindView;
import butterknife.ButterKnife;
import com.dema.versatile.flashlight.AdKey;
import com.dema.versatile.flashlight.Constants;
import com.dema.versatile.flashlight.R;
import com.dema.versatile.flashlight.core.CoreFactory;
import com.dema.versatile.flashlight.core.info.IPhoneInfoMgr;
import com.dema.versatile.flashlight.core.protect.IProtectMgr;
import com.dema.versatile.flashlight.main.base.BaseActivity;
import com.dema.versatile.mediation.CMMediationFactory;
import com.dema.versatile.mediation.core.in.IMediationMgr;
import com.dema.versatile.scene.core.CMSceneFactory;
import com.dema.versatile.scene.core.store.ISceneDataStore;

public class ResultActivity extends BaseActivity {


    @BindView(R.id.iv_function_icon)
    ImageView ivFunctionIcon;
    @BindView(R.id.tv_result_text)
    TextView tvResultText;
    @BindView(R.id.iv_back)
    ImageView ivBack;
    @BindView(R.id.tv_title)
    TextView tvTitle;
    private int mFunctionType;

    public static void start(Activity context, @Function.Type int type) {
        start(context, type, null);
    }

    public static void start(Activity context, @Function.Type int type, String clearMemorySize) {
        Intent starter = new Intent(context, ResultActivity.class);
        starter.putExtra(Function.KEY_FUNCTION_TYPE, type);
        starter.putExtra(Function.KEY_FUNCTION_RESULT_TEXT, clearMemorySize);
        context.startActivity(starter);
        context.overridePendingTransition(0, 0);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_result);
        ButterKnife.bind(this);

        Intent intent = getIntent();
        mFunctionType = intent.getIntExtra(Function.KEY_FUNCTION_TYPE, Function.TYPE_BOOST);

        ivBack.setOnClickListener(v -> onBackPressed());
        tvTitle.setText(Function.getTitleRes(mFunctionType));
        ivFunctionIcon.setImageResource(Function.getFunctionIconRes(mFunctionType));

        showResultInfo();

    }

    private void showResultInfo() {
        CoreFactory factory = CoreFactory.getInstance();
        IProtectMgr protectMgr = factory.createInstance(IProtectMgr.class);
        IPhoneInfoMgr phoneInfoMgr = factory.createInstance(IPhoneInfoMgr.class);
        ISceneDataStore sceneDataStore = CMSceneFactory.getInstance().createInstance(ISceneDataStore.class);


        if (protectMgr.isUnderProtection(mFunctionType)) {
            tvResultText.setText(R.string.complete_info_over);
        } else {
            sceneDataStore.setSceneTime(getScene(mFunctionType), System.currentTimeMillis());
            protectMgr.updateOptimizeTime(mFunctionType);
            int optimizeValue = phoneInfoMgr.getOptimizeValue(mFunctionType);
            switch (mFunctionType) {
                case Function.TYPE_BOOST:
                    tvResultText.setText(getString(R.string.complete_info_booster, optimizeValue));
                    break;
                case Function.TYPE_CLEAN:
                    tvResultText.setText(getString(R.string.complete_info_storage, optimizeValue));
                    break;
                case Function.TYPE_COOL:
                    tvResultText.setText(getString(R.string.complete_info_cooldown, optimizeValue));
                    break;
                case Function.TYPE_SAVE_BATTERY:
                    tvResultText.setText(getString(R.string.complete_info_save_battery, optimizeValue));
                    break;
                case Function.TYPE_NETWORK:
                    tvResultText.setText(getString(R.string.complete_info_network, optimizeValue));
                    break;
            }

        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        IMediationMgr mediationMgr = CMMediationFactory.getInstance().createInstance(IMediationMgr.class);
        mediationMgr.showInterstitialAd(AdKey.VALUE_STRING_INTERSTITIAL_RESULT, AdKey.VALUE_STRING_AD_SHOW_SCENE_RESULT_BACK, getApplicationContext());
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        IMediationMgr mediationMgr = CMMediationFactory.getInstance().createInstance(IMediationMgr.class);
        mediationMgr.releaseAd(AdKey.VALUE_STRING_INTERSTITIAL_RESULT);
    }

    public String getScene(int optimizeType) {
        switch (optimizeType) {
            case Function.TYPE_SAVE_BATTERY:
                return Constants.VALUE_STRING_PULL_BATTERY;
            case Function.TYPE_COOL:
                return Constants.VALUE_STRING_PULL_COOL;
            case Function.TYPE_CLEAN:
                return Constants.VALUE_STRING_PULL_CLEAN;
            case Function.TYPE_BOOST:
                return Constants.VALUE_STRING_PULL_BOOST;
            case Function.TYPE_NETWORK:
            default:
                return Constants.VALUE_STRING_PULL_NETWORK;
        }
    }

}
