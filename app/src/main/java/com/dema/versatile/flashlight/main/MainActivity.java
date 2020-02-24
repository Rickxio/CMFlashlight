package com.dema.versatile.flashlight.main;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.SeekBar;

import com.airbnb.lottie.LottieAnimationView;
import com.dema.versatile.flashlight.AdKey;
import com.dema.versatile.flashlight.Constants;
import com.dema.versatile.flashlight.ExitActivity;
import com.dema.versatile.flashlight.R;
import com.dema.versatile.flashlight.core.CoreFactory;
import com.dema.versatile.flashlight.core.alarm.IAppAlarmMgr;
import com.dema.versatile.flashlight.core.flashlight.im.FlashlightMgrImpl;
import com.dema.versatile.flashlight.core.flashlight.in.IFlashlightMgr;
import com.dema.versatile.flashlight.core.settings.ISettingMgr;
import com.dema.versatile.flashlight.main.base.BaseActivity;
import com.dema.versatile.flashlight.main.function.AnimActivity;
import com.dema.versatile.flashlight.main.function.Function;
import com.dema.versatile.flashlight.main.settings.RateUsDialog;
import com.dema.versatile.flashlight.main.settings.SettingActivity;
import com.dema.versatile.flashlight.main.view.CompassView;
import com.dema.versatile.flashlight.main.view.StrobeRecyclerView;
import com.dema.versatile.flashlight.main.view.VerticalSeekBar;
import com.dema.versatile.flashlight.utils.UtilsAnimator;
import com.dema.versatile.flashlight.utils.UtilsSetting;
import com.dema.versatile.lib.utils.UtilsJson;
import com.dema.versatile.lib.utils.UtilsLog;
import com.dema.versatile.lib.utils.UtilsSize;
import com.dema.versatile.lib.utils.UtilsTime;
import com.dema.versatile.mediation.CMMediationFactory;
import com.dema.versatile.mediation.core.in.IMediationConfig;
import com.dema.versatile.mediation.core.in.IMediationMgr;
import com.dema.versatile.mediation.core.in.IMediationMgrListener;
import com.dema.versatile.mediation.utils.UtilsAd;
import com.dema.versatile.scene.SceneConstants;
import com.dema.versatile.scene.core.CMSceneFactory;
import com.dema.versatile.scene.core.alert.AlertMgrImpl;
import com.dema.versatile.scene.core.alert.IAlertMgr;

import org.json.JSONObject;

public class MainActivity extends BaseActivity {
    public static final String EXTRA_SCENE = "scene";
    private StrobeRecyclerView mRvSelect;
    private ImageView mBtColor;
    private ImageView mBtSetting;
    private ImageView mIvSwitch;
    private CompassView mCompassView;
    private ImageView mIvSos;
    private ImageView ivLight;

    private IFlashlightMgr mIFlashlightMgr;
    private IMediationMgr mIMediationMgr = null;
    private IMediationMgrListener mIMediationMgrListener = null;
    private StrobeAdapter mStrobeAdapter;
    private RelativeLayout mRelCompass;
    private FrameLayout mFlAd;
    private int mCurrIndex = 7 * 3000 + 1;
    private LottieAnimationView mViewLottie;
    private boolean mHasBoost = false;

    private static final int[] LIGHT_LEVEL = new int[]{FlashlightMgrImpl.VALUE_INT_1,FlashlightMgrImpl.VALUE_INT_2,
            FlashlightMgrImpl.VALUE_INT_3,FlashlightMgrImpl.VALUE_INT_4,FlashlightMgrImpl.VALUE_INT_5};

    private int mIndex = LIGHT_LEVEL.length - 1;
    private VerticalSeekBar verticalSeekBar;
    private RelativeLayout fight_main;
    private RelativeLayout main_flash;
    private String lclickSwitch = "close";

    private void startSceneActivity() {
        String scene = getIntent().getStringExtra(EXTRA_SCENE);
        if (!TextUtils.isEmpty(scene)) {
            switch (scene) {
                // 加速（拉活场景）
                case Constants.VALUE_STRING_PULL_BOOST:
                    AnimActivity.start(this, Function.TYPE_BOOST);
                    break;
                // 电池优化（拉活场景）
                case Constants.VALUE_STRING_PULL_BATTERY:
                    AnimActivity.start(this, Function.TYPE_SAVE_BATTERY);
                    break;
                // 降温（拉活场景）
                case Constants.VALUE_STRING_PULL_COOL:
                    AnimActivity.start(this, Function.TYPE_COOL);
                    break;
                // 清理（拉活场景）
                case Constants.VALUE_STRING_PULL_CLEAN:
                    AnimActivity.start(this, Function.TYPE_CLEAN);
                    break;
                // 网络优化（拉活场景）
                case Constants.VALUE_STRING_PULL_NETWORK:
                    AnimActivity.start(this, Function.TYPE_NETWORK);
                    break;
                default:
                    break;
            }
        }
    }

    Runnable runnable = new Runnable() {
        @Override
        public void run() {
            Log.e(LightActivity.class.getSimpleName(),"runnable mIndex="+mIndex);
            mIFlashlightMgr.changeRVState(mIndex);
        }
    };

    Runnable runnable1 = new Runnable() {
        @Override
        public void run() {
            verticalSeekBar.setProgress(100);
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        startSceneActivity();

        UtilsLog.log("main", "create", null);

        requestAd();

        init_item();

        startup_light();


        registered_rate();

        mBtColor.setOnClickListener(v -> {
            UtilsLog.log("main", "color", null);
            Intent intent = new Intent(MainActivity.this, ColorActivity.class);
            startActivity(intent);
        });

        mBtSetting.setOnClickListener(v -> {
            UtilsLog.log("main", "setting", null);
            Intent intent = new Intent(MainActivity.this, SettingActivity.class);
            startActivity(intent);
        });

        mIvSos.setOnClickListener(v -> {
            mIFlashlightMgr.changeRVState(FlashlightMgrImpl.VALUE_INT_SOS);
            boolean clickSwitch = mIFlashlightMgr.clickSwitch();
            if(clickSwitch){
                lclickSwitch = "open";
            } else {
                lclickSwitch = "close";
            }
            JSONObject object = new JSONObject();
            UtilsJson.JsonSerialization(object,"status",lclickSwitch);
            UtilsLog.log("main", "sos", object);
            updateSosUI(clickSwitch);
        });

        ivLight.setOnClickListener(v -> {
//            ivLight.setImageResource(R.drawable.ic_flight_open);
//            startActivity(new Intent(this,LightActivity.class));
            main_flash.setVisibility(View.VISIBLE);
            fight_main.setVisibility(View.GONE);
            UtilsLog.log("main", "Light", null);
            mIFlashlightMgr.setSwitchState(true);
            mIvSwitch.setImageResource(R.drawable.ic_close);
            mIvSos.setImageResource(R.drawable.ic_sos);
            mIFlashlightMgr.changeRVState(FlashlightMgrImpl.VALUE_INT_10);
            verticalSeekBar.postDelayed(runnable1,0);

        });

        verticalSeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                verticalSeekBar.setThumbOffset(progress >= 50 ? UtilsSize.dpToPx(MainActivity.this,10): 0);
                if (progress <= 10) {
                    mIndex = 1;
                } else if (progress <= 20) {
                    mIndex = 2;
                } else if (progress <= 30) {
                    mIndex = 3;
                } else if (progress <= 40) {
                    mIndex = 4;
                } else if (progress <= 50) {
                    mIndex = 5;
                } else if (progress <= 60) {
                    mIndex = 6;
                } else if (progress <= 70) {
                    mIndex = 7;
                } else if (progress <= 80) {
                    mIndex = 8;
                } else if (progress <= 90) {
                    mIndex = 9;
                } else if (progress <= 100) {
                    mIndex = 10;
                }
                verticalSeekBar.removeCallbacks(runnable);
                verticalSeekBar.postDelayed(runnable,300);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
            }
        });

        findViewById(R.id.iv_back).setOnClickListener(v -> {
            if (verticalSeekBar != null) {
                verticalSeekBar.removeCallbacks(runnable);
                verticalSeekBar.removeCallbacks(runnable1);
            }
            main_flash.setVisibility(View.GONE);
            fight_main.setVisibility(View.VISIBLE);

            boolean clickSwitch = mIFlashlightMgr.clickSwitch();
        });


        IAppAlarmMgr alarmMgr = CoreFactory.getInstance().createInstance(IAppAlarmMgr.class);
        mViewLottie = findViewById(R.id.lottie_view);
        mViewLottie.setOnClickListener(v -> {
            BatteryActivity.start(this);
            mHasBoost = true;
        });
        if (System.currentTimeMillis() - alarmMgr.getLastBatteryTime() > UtilsTime.VALUE_LONG_TIME_ONE_MINUTE * 15) {
            UtilsAnimator.startLottieAnim(mViewLottie, "icon_red");
        } else {
            UtilsAnimator.startLottieAnim(mViewLottie, "icon");
        }




        mIMediationMgr = CMMediationFactory.getInstance().createInstance(IMediationMgr.class);
        mIMediationMgrListener = new IMediationMgrListener() {
            @Override
            public void onAdLoaded(IMediationConfig iMediationConfig) {
                if (null == iMediationConfig)
                    return;

                if (AdKey.VALUE_STRING_VIEW_MAIN.equals(iMediationConfig.getAdKey())) {
                    mIMediationMgr.showAdView(AdKey.VALUE_STRING_VIEW_MAIN, mFlAd, getApplicationContext());
                }
            }
        };
        mIMediationMgr.addListener(mIMediationMgrListener);

        if (mIMediationMgr.isAdLoaded(AdKey.VALUE_STRING_VIEW_MAIN)) {
            mIMediationMgr.showAdView(AdKey.VALUE_STRING_VIEW_MAIN, mFlAd, getApplicationContext());
        }
    }

    /**
     * 注册手电开关监听逻辑
     */
    private void registered_rate() {
        ISettingMgr settingMgr = CoreFactory.getInstance().createInstance(ISettingMgr.class);
        mIvSwitch.setOnClickListener(v -> {
            mIFlashlightMgr.setLevel(0);
            boolean switchState = mIFlashlightMgr.clickSwitch();
            if(switchState){
                lclickSwitch = "open";
            } else {
                lclickSwitch = "close";
            }
            JSONObject object = new JSONObject();
            UtilsJson.JsonSerialization(object,"status",lclickSwitch);
            UtilsLog.log("main", "Switch", object);
            updateSwitchUI(switchState);
            if (!switchState) {
                int count = settingMgr.addCloseCount();
                if (count == 2) {
                    new RateUsDialog(MainActivity.this).safeShow();
                }
            }
        });
    }

    /**
     * 判断是否打开了启动APP就亮灯
     */
    public void startup_light() {
        boolean isOpen = UtilsSetting.isLightOnStartup(this);
        mIFlashlightMgr.setSwitchState(isOpen);
        if (isOpen) {
            mIFlashlightMgr.openLight();
        } else {
            mIFlashlightMgr.closeLight();
        }
        updateSwitchUI(mIFlashlightMgr.getSwitchState());
    }

    public void init_item() {
        mBtColor = findViewById(R.id.iv_color);
        mIvSos = findViewById(R.id.iv_sos);
        mBtSetting = findViewById(R.id.iv_setting);
        mIvSwitch = findViewById(R.id.iv_switch);
//        mCompassView = findViewById(R.id.compass_layout);
        mFlAd = findViewById(R.id.fl_ad);
//        mRelCompass = findViewById(R.id.rel_compass);
        ivLight = findViewById(R.id.iv_light);

        fight_main = findViewById(R.id.fight_main);
        main_flash = findViewById(R.id.main_flash);

        mIFlashlightMgr = FlashlightMgrImpl.getInstance();
        mIFlashlightMgr.register();

        verticalSeekBar = findViewById(R.id.progress);

        //测试按钮
 /*       Button crashButton = new Button(this);
        crashButton.setText("Crash!");
        crashButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
               for(int i=0 ; i<6 ; i++){
                    final  int n = i;
                    Thread t_i = new Thread("i:"+i){
                        @Override
                        public void run() {
                            UtilsAd.logE("rick-superlog", "第"+n+"次请求广告");
//                            UtilsLog.log("test_log", "第"+n+"次打印：线程："+Thread.currentThread().getId()+"，"+Thread.currentThread().getName(), null);
                            UtilsLog.send();

                        }
                    };
                   t_i.start();
                }
            }
        });

        addContentView(crashButton, new ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT));*/

    }

    private void requestAd() {
        UtilsAd.logE("rick-requestAd", Log.getStackTraceString(new Exception()));
        IMediationMgr mediationMgr = CMMediationFactory.getInstance().createInstance(IMediationMgr.class);
        mediationMgr.requestAdAsync(AdKey.VALUE_STRING_VIEW_MAIN, AdKey.VALUE_STRING_AD_REQUEST_SCENE_MAIN_CREATE);
        mediationMgr.requestAdAsync(AdKey.VALUE_STRING_INTERSTITIAL_RESULT, AdKey.VALUE_STRING_AD_REQUEST_SCENE_MAIN_CREATE);
        mediationMgr.requestAdAsync(AdKey.VALUE_STRING_INTERSTITIAL_EXIT, AdKey.VALUE_STRING_AD_REQUEST_SCENE_MAIN_CREATE);
//        mediationMgr.requestAdAsync(AdKey.VALUE_STRING_VIEW_ALERT, AdKey.VALUE_STRING_AD_REQUEST_SCENE_MAIN_CREATE);
    }

    private void updateSwitchUI(boolean state) {
        if (state) {
            mIvSwitch.setImageResource(R.drawable.ic_open);
        } else {
            mIvSwitch.setImageResource(R.drawable.ic_close);
            mIvSos.setImageResource(R.drawable.ic_sos);
        }
    }

    private void updateSosUI(boolean state) {
        if (state) {
            mIvSos.setImageResource(R.drawable.ic_sos_open);
            mIvSwitch.setImageResource(R.drawable.ic_open);
        } else {
            mIvSos.setImageResource(R.drawable.ic_sos);
            mIvSwitch.setImageResource(R.drawable.ic_close);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        try {
//            mCompassView.onResume();
            if (mHasBoost) {
                UtilsAnimator.startLottieAnim(mViewLottie, "icon");
                mHasBoost = false;
            }

            boolean clickSwitch = mIFlashlightMgr.getSwitchState();
            mIvSwitch.setImageResource(clickSwitch ? R.drawable.ic_open : R.drawable.ic_close);

        }catch (Exception e){
            e.printStackTrace();
        }
    }

    @Override
    protected void onPause() {
        super.onPause();

    }

    @Override
    public void onBackPressed() {
        Intent intent = new Intent(this, ExitActivity.class);
        startActivity(intent);
        mIMediationMgr.showInterstitialAd(AdKey.VALUE_STRING_INTERSTITIAL_EXIT, AdKey.VALUE_STRING_AD_SHOW_SCENE_MAIN_EXIT, getApplicationContext());
        super.onBackPressed();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mIFlashlightMgr != null) {
            mIFlashlightMgr.closeLight();
            mIFlashlightMgr.destroy();
            mIFlashlightMgr.cleanTimeTask();
            mIFlashlightMgr.unregister();
        }

        if (null != mIMediationMgr) {
            mIMediationMgr.removeListener(mIMediationMgrListener);
            mIMediationMgr.releaseAd(AdKey.VALUE_STRING_INTERSTITIAL_EXIT);
            mIMediationMgr.releaseAd(AdKey.VALUE_STRING_VIEW_MAIN);
        }
    }
}
