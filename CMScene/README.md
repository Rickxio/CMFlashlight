## 1.在清单文件的splash activity下加入
```
            <intent-filter>
                <action android:name="${applicationId}.action.splash"/>
                <category android:name="android.intent.category.DEFAULT" />
            </intent-filter>
```
## 2.(可选)设置alert统一的颜色样式，可在各自的alert里面重写对应的getXXX方法进行覆盖
```
        AlertUiManager.getInstance()
                .setBackgroundRes(R.drawable.bg_dialog_common)
                .setCloseIconRes(R.drawable.ic_dialog_close)
                .setTitleColor(Color.RED)
                .setContentColor(Color.BLUE)
                .setButtonBackgroundRes(R.drawable.ripple_button_background)
                .setButtonTextColor(Color.GREEN);
```
## 3.初始化Scene库
```
            CMSceneFactory.setApplication(this);
          ISceneMgr iSceneMgr = CMSceneFactory.getInstance().createInstance(ISceneMgr.class);
                iSceneMgr.init(new ISceneCallback() {
                    @Override
                    public String getAlertViewAdKey(String scene) {
                        //根据scene返回对应的广告key
                        return AdKey.AD_KEY_BANNER_ALERT;
                    }
                    @Override
                    public Long getWakeupTime() {
                        //如果app不支持设置起床睡眠时间就返回null，返回的数值只需保证时分秒正确，不需要考虑日期，scene库获取的时候会做转化，变成当前的时间
                        //需和睡眠时间同步设置，起床和睡眠时间只设置一个是不会生效的
                        return UtilsDate.getTimeMillisForToday(0, 0, 0);
                    }
                    @Override
                    public Long getSleepTime() {
                        //如果app不支持设置起床睡眠时间就返回null，返回的数值只需保证时分秒正确，不需要考虑日期，scene库获取的时候会做转化，变成当前的时间
                        //需和起床时间同步设置，起床和睡眠时间只设置一个是不会生效的
                        return UtilsDate.getTimeMillisForToday(22, 0, 0);
                    }
                    @Override
                    public void preLoadAd() {
                        //这里可以预加载广告，会在请求alert广告的时候一起回调
                    }
                    @Override
                    public boolean isSupportScene(String scene) {
                        //定制app支持那些scene
                        return true;
                    }
                    @Override
                    public INotificationConfig getNotificationConfig(String scene) {
                        //根据不同的scene返回不同的config
                        return null;
                    }
                    @Override
                    public IAlertConfig getAlertUiConfig(String scene) {
                        //没有特殊定制就返回null,这里可以根据scene定制不同场景对应不同的config，也可以把scene传到自定义的config
                        //里面，在里面进行判断
                        if ("pull_drink".equals(scene)) {
                            return new HealthAlertConfig(scene);
                        }
                        return null;
                    }
                    @Override
                    public boolean onShowAlert(String scene, @SceneConstants.Trigger String trigger, int count) {
                        //这里如果是上层自己做alert，那么返回true。如果是用库里默认的alert，就返回false
                        return false;
                    }
                });
```               
## 4.说明一下如何自定义通知和alert
  
    1.通过AlertUiManager可设置每个app默认的alert风格
    
    2.通过getAlertUiConfig根据场景去返回不同的config或者根据场景判断返回不同的参数，这里说明一下，即使是自定义的config也不需要
        每个方法都返回参数，里面的各个方法，如果想继续保持默认，就返回null即可。
        
    3.创建activity继承CMAlertActivity或者CMTipsActivity,直接复写相应的get方法，更原始一点就是继承CMAlertBaseActivity,这个可以完全自定义布局
    
    在三种方式都设置了的情况下，优先级为 3>2>1>scene库默认ui        

## 5.与Alert对应的Scene页面必须继承CMSceneActivity
        
# scene列表参考

scene：只有在解锁状态下才能触发，场景包含：拉活场景（pull前缀）和提示场景（tips前缀），拉活场景有引导启动按钮，提示场景没有引导按钮。

### 健康类：

    pull_step：计步（拉活场景）

    pull_drink：饮水（拉活场景）

    pull_breath：深呼吸（拉活场景）

    pull_plank：平板支撑（拉活场景）

    pull_stretch：拉伸（拉活场景）

    pull_neck：脖子操（拉活场景）

    pull_fruit：水果（拉活场景）

    pull_report：健康报告（拉活场景）

    tips_sleep：睡眠提示（提示场景，睡眠时间，没有引导按钮）

    tips_health：健康提示（提示场景，非睡觉时间，没有引导按钮）

### 优化类：

    pull_boost：加速（拉活场景）

    pull_battery：电池优化（拉活场景）

    pull_cool：降温（拉活场景）

    pull_clean：清理（拉活场景）

    pull_network：网络优化（拉活场景）

    tips_boost：加速（提示场景，自动加速已完成）

    tips_battery：电池优化（提示场景，自动电池优化已完成）

    tips_cool：降温（提示场景，自动降温已完成）

    tips_clean：清理（提示场景，自动清理已完成）

    tips_network：网络优化（提示场景，自动网络优化已完成）
            
            
