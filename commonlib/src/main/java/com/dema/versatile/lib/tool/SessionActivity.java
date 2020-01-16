package com.dema.versatile.lib.tool;

import org.json.JSONObject;

import androidx.appcompat.app.AppCompatActivity;
import com.dema.versatile.lib.utils.UtilsJson;
import com.dema.versatile.lib.utils.UtilsLog;

public abstract class SessionActivity extends AppCompatActivity {
    private long mTime = 0;

    @Override
    protected void onStart() {
        super.onStart();
        mTime = System.currentTimeMillis();
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        mTime = System.currentTimeMillis();
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (0 == mTime)
            return;

        long lTime = System.currentTimeMillis() - mTime;
        if (lTime <= 0)
            return;

        mTime = 0;
        JSONObject jsonObject = new JSONObject();
        UtilsJson.JsonSerialization(jsonObject, UtilsLog.VALUE_STRING_LOG_SESSION_CONTENT_NAME, getClass().getName());
        UtilsJson.JsonSerialization(jsonObject, UtilsLog.VALUE_STRING_LOG_SESSION_CONTENT_TIME, lTime);
        UtilsLog.aliveLog(UtilsLog.VALUE_STRING_LOG_KEY2_SESSION, jsonObject);
        UtilsLog.send();
    }
}
