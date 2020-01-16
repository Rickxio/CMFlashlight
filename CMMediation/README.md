CMMediation

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
    implementation('com.facebook.android:audience-network-sdk:5.3.0') {
        exclude group: 'com.android.support'
    }
    implementation('com.google.android.gms:play-services-ads:17.2.0') {
        exclude group: 'com.android.support'
    }
}


2. 工程级build.gradle

buildscript {
    dependencies {
        classpath 'com.android.tools.build:gradle:3.3.2'
    }
}