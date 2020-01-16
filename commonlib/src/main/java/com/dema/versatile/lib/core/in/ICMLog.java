package com.dema.versatile.lib.core.in;

import org.json.JSONObject;

public interface ICMLog extends ICMMgr {
    void log(String strKey1, String strKey2, JSONObject jsonObjectContent);

    void crash(Throwable throwable);

    void send();
}
