package com.dema.versatile.mediation.core.im;

import android.text.TextUtils;

import org.json.JSONObject;

import com.dema.versatile.lib.CMLibFactory;
import com.dema.versatile.lib.core.in.ICMTimer;
import com.dema.versatile.lib.core.in.ICMTimerListener;
import com.dema.versatile.lib.utils.UtilsJson;
import com.dema.versatile.mediation.BuildConfig;
import com.dema.versatile.mediation.CMMediationFactory;
import com.dema.versatile.mediation.core.in.IAdItem;
import com.dema.versatile.mediation.core.in.IMediationConfig;
import com.dema.versatile.mediation.utils.UtilsAdmob;
import com.dema.versatile.mediation.utils.UtilsAdx;
import com.dema.versatile.mediation.utils.UtilsFacebook;
import com.dema.versatile.mediation.utils.UtilsMopub;

public class AdItem implements IAdItem {
    private String mID = null;
    private String mType = IMediationConfig.VALUE_STRING_TYPE_UNKNOWN;
    private String mPlatform = IMediationConfig.VALUE_STRING_PLATFORM_UNKNOWN;
    private String mBannerSize = null;
    private double mMaskRate = 0;

    @Override
    public JSONObject Serialization() {
        return null;
    }

    @Override
    public void Deserialization(JSONObject jsonObject) {
        if (null == jsonObject)
            return;

        mID = UtilsJson.JsonUnserialization(jsonObject, "id", "");
        mType = UtilsJson.JsonUnserialization(jsonObject, "type", mType);
        mPlatform = UtilsJson.JsonUnserialization(jsonObject, "platform", mPlatform);
        mBannerSize = UtilsJson.JsonUnserialization(jsonObject, "banner_size", "");
        mMaskRate = UtilsJson.JsonUnserialization(jsonObject, "mask_rate", mMaskRate);

        if (!TextUtils.isEmpty(mID) && !TextUtils.isEmpty(mPlatform) &&
                mPlatform.equals(IMediationConfig.VALUE_STRING_PLATFORM_MOPUB)) {
            ICMTimer iCMTimer = CMLibFactory.getInstance().createInstance(ICMTimer.class);
            iCMTimer.start(0, 0, new ICMTimerListener() {
                @Override
                public void onComplete(long lRepeatTime) {
                    UtilsMopub.init(CMMediationFactory.getApplication(), mID);
                }
            });
        }
    }

    @Override
    public String getAdID() {
        if (BuildConfig.DEBUG) {
            switch (mPlatform) {
                case IMediationConfig.VALUE_STRING_PLATFORM_FACEBOOK:
                    return UtilsFacebook.getTestAdID(mType);
                case IMediationConfig.VALUE_STRING_PLATFORM_ADMOB:
                    return UtilsAdmob.getTestAdID(mType);
                case IMediationConfig.VALUE_STRING_PLATFORM_ADX:
                    return UtilsAdx.getTestAdID(mType);
                case IMediationConfig.VALUE_STRING_PLATFORM_MOPUB:
                    return UtilsMopub.getTestAdID(mType, mBannerSize);
                case IMediationConfig.VALUE_STRING_PLATFORM_INMOBI:
                    return mID;
            }
        }

        return mID;
    }

    @Override
    public String getAdType() {
        return mType;
    }

    @Override
    public String getAdPlatform() {
        return mPlatform;
    }

    @Override
    public String getAdBannerSize() {
        return mBannerSize;
    }

    @Override
    public boolean isNeedMask() {
        if (mMaskRate <= 0)
            return false;

        return Math.random() <= mMaskRate;
    }
}
