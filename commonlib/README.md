CMLib

使用方法：

1. 应用级build.gradle

android {
    compileSdkVersion 28
    defaultConfig {
        minSdkVersion 21
    }

    compileOptions {
        sourceCompatibility JavaVersion.VERSION_1_8
        targetCompatibility JavaVersion.VERSION_1_8
    }
}

dependencies {
    implementation fileTree(dir: 'libs', include: ['*.jar'])
    implementation project(path: ':CMLib')
}


2. 工程级build.gradle

buildscript {
    dependencies {
        classpath 'com.android.tools.build:gradle:3.3.2'
    }
}


3. gradle-wrapper.properties

distributionUrl=https\://services.gradle.org/distributions/gradle-4.10.1-all.zip


4. gradle.properties

加入以下代码
android.enableJetifier=true
android.useAndroidX=true


5. 务必定义几个资源

5.1:res/drawable-xxhdpi/ic_launcher.png
主应用图标，144*144

5.2:res/values/string.xml
<string name="alive_account_type">应用包名+.cm.lib.alive.account</string>
<string name="alive_authority">应用包名+.cm.lib.alive.authority</string>


6. 定义自己的Application类

public class CMApplication extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        initConfig(this);
    }

    private void initConfig(Application application) {
        CrashHandler.init(!BuildConfig.DEBUG);

        CMLibFactory.setApplication(application);
        UtilsJson.addFactory(CMLibFactory.getInstance());
        UtilsEnv.init(application, BuildConfig.FLAVOR);
        UtilsNetwork.init(null);     // 填该项目的域名或IP
        UtilsEncrypt.init(null);     // 填自定义加密KEY
        UtilsLog.init(this,!BuildConfig.DEBUG, BuildConfig.DEBUG, null, null, null);
        ReferrerReceiver.init(application);

        UtilsAlive.init(application);
    }
}
