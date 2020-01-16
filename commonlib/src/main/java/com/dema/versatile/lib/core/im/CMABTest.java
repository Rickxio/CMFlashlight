package com.dema.versatile.lib.core.im;

import android.text.TextUtils;

import org.json.JSONException;
import org.json.JSONObject;

import com.dema.versatile.lib.CMLibFactory;
import com.dema.versatile.lib.core.in.ICMABTest;
import com.dema.versatile.lib.utils.UtilsApp;
import com.dema.versatile.lib.utils.UtilsJson;

public class CMABTest implements ICMABTest {
    private String mKey = null;
    private int mAppVersion = -1;
    private int mIndex = -1;
    private JSONObject mJSONObjectContent = null;

    public CMABTest() {
        _init();
    }

    private void _init() {
    }

    @Override
    public JSONObject Serialization() {
        return null;
    }

    @Override
    public void Deserialization(JSONObject jsonObject) {
        if (null == jsonObject)
            return;

        try {
            mAppVersion = -1;
            mAppVersion = UtilsJson.JsonUnserialization(jsonObject, "app_version", mAppVersion);
            if (mAppVersion != UtilsApp.getMyAppVersionCode(CMLibFactory.getApplication()))
                return;

            mKey = UtilsJson.JsonUnserialization(jsonObject, "key", "");
            mIndex = UtilsJson.JsonUnserialization(jsonObject, "index", mIndex);
            if (jsonObject.has("content")) {
                mJSONObjectContent = jsonObject.getJSONObject("content");
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    @Override
    public String getHitID() {
        if (TextUtils.isEmpty(mKey))
            return null;

        return mKey + "_" + mAppVersion + "_" + mIndex;
    }

    @Override
    public String getHitKey() {
        return mKey;
    }

    @Override
    public int getHitAppVersion() {
        return mAppVersion;
    }

    @Override
    public int getHitIndex() {
        return mIndex;
    }

    @Override
    public JSONObject getHitContent() {
        return mJSONObjectContent;
    }
}
