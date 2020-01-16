package com.dema.versatile.flashlight.main;

import android.os.Bundle;
import android.widget.TextView;

import com.dema.versatile.flashlight.BuildConfig;
import com.dema.versatile.flashlight.R;
import com.dema.versatile.flashlight.main.base.BaseActivity;

public class AboutActivity extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about);
        TextView tvVersion = findViewById(R.id.tv_version);
        tvVersion.setText(String.format(getString(R.string.version), BuildConfig.VERSION_NAME));

    }
}
