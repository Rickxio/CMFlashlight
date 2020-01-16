package com.dema.versatile.mediation.core.in;

import com.dema.versatile.lib.core.in.ICMJson;
import com.dema.versatile.lib.core.in.ICMObj;

public interface IAdItem extends ICMObj, ICMJson {
    String getAdID();

    String getAdType();

    String getAdPlatform();

    String getAdBannerSize();

    boolean isNeedMask();
}
