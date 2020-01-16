package com.dema.versatile.mediation.core.im;

import com.dema.versatile.mediation.core.in.IAdItem;

public class AdBean {
    public IAdItem mIAdItem;
    public Object mObjectAd;
    public String mAdKey = null;
    public String mAdRequestID = null;

    public AdBean(IAdItem iAdItem, Object objectAd, String strAdKey, String strAdRequestID) {
        mIAdItem = iAdItem;
        mObjectAd = objectAd;
        mAdKey = strAdKey;
        mAdRequestID = strAdRequestID;
    }
}
