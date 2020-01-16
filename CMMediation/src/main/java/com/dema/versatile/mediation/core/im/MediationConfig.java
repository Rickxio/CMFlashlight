package com.dema.versatile.mediation.core.im;

import android.text.TextUtils;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import com.dema.versatile.lib.utils.UtilsJson;
import com.dema.versatile.mediation.core.in.IAdItem;
import com.dema.versatile.mediation.core.in.IMediationConfig;

public class MediationConfig implements IMediationConfig {
    private String mKey = null;
    private int mCacheCount = 1;
    private List<String> mListRequestScene = null;
    private List<String> mListShowScene = null;
    private List<IAdItem> mListAdItem = null;

    public MediationConfig() {
        _init();
    }

    private void _init() {
        mListRequestScene = new ArrayList<>();
        mListShowScene = new ArrayList<>();
        mListAdItem = new ArrayList<>();
    }

    @Override
    public boolean equals(Object object) {
        if (null == object || getClass() != object.getClass())
            return false;

        if (TextUtils.isEmpty(mKey))
            return false;

        if (this == object)
            return true;

        IMediationConfig iMediationConfig = (IMediationConfig) object;
        return mKey.equals(iMediationConfig.getAdKey());
    }

    @Override
    public int hashCode() {
        if (TextUtils.isEmpty(mKey))
            return 0;

        return mKey.hashCode();
    }

    @Override
    public JSONObject Serialization() {
        return null;
    }

    @Override
    public void Deserialization(JSONObject jsonObject) {
        if (null == jsonObject)
            return;

        mCacheCount = UtilsJson.JsonUnserialization(jsonObject, "cache_count", mCacheCount);
        mListRequestScene = new ArrayList<>();
        UtilsJson.JsonUnserialization(jsonObject, "request_scene", mListRequestScene, String.class, null, null);
        mListShowScene = new ArrayList<>();
        UtilsJson.JsonUnserialization(jsonObject, "show_scene", mListShowScene, String.class, null, null);
        mListAdItem = new ArrayList<>();
        UtilsJson.JsonUnserialization(jsonObject, "ad_list", mListAdItem, IAdItem.class, IAdItem.class, AdItem.class);
    }

    @Override
    public String getAdKey() {
        return mKey;
    }

    @Override
    public int getCacheCount() {
        return mCacheCount;
    }

    @Override
    public boolean isSupportRequestScene(String strScene) {
        return !mListRequestScene.isEmpty() && !TextUtils.isEmpty(strScene) && mListRequestScene.contains(strScene);
    }

    @Override
    public boolean isSupportShowScene(String strScene) {
        return !mListShowScene.isEmpty() && !TextUtils.isEmpty(strScene) && mListShowScene.contains(strScene);
    }

    @Override
    public IAdItem getAdItem(int nIndex) {
        if (null == mListAdItem || mListAdItem.isEmpty() || nIndex < 0 || nIndex >= mListAdItem.size())
            return null;

        return mListAdItem.get(nIndex);
    }

    @Override
    public int getAdItemCount() {
        if (null == mListAdItem)
            return 0;

        return mListAdItem.size();
    }

    public void setAdKey(String strKey) {
        mKey = strKey;
    }
}
