package com.dema.versatile.lib.core.in;

import org.json.JSONObject;

public interface ICMABTest extends ICMMgr, ICMJson {
    String getHitID();

    String getHitKey();

    int getHitAppVersion();

    int getHitIndex();

    JSONObject getHitContent();
}
