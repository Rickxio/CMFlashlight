package com.dema.versatile.scene.ui.base;

import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.dema.versatile.scene.core.CMSceneFactory;
import com.dema.versatile.scene.core.store.ISceneDataStore;

/**
 * Created by wangyu on 2019/8/27.
 */
public abstract class CMSceneActivity extends AppCompatActivity {

    private ISceneDataStore mISceneDataStore;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        init();
        mISceneDataStore = CMSceneFactory.getInstance().createInstance(ISceneDataStore.class);
        mISceneDataStore.setSceneTime(getScene(), System.currentTimeMillis());
    }

    public abstract void init();


    public abstract String getScene();
}
