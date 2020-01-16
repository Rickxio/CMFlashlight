package com.dema.versatile.flashlight;

import android.os.Bundle;

import androidx.annotation.Nullable;
import com.dema.versatile.lib.utils.UtilsLog;
import com.dema.versatile.logic.tool.CMExitActivity;

public class ExitActivity extends CMExitActivity {
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_exit);
        UtilsLog.log("exit", "create", null);
    }
}
