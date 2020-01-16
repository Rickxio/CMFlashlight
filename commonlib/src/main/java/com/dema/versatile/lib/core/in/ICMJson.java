package com.dema.versatile.lib.core.in;

import org.json.JSONObject;

public interface ICMJson {
    JSONObject Serialization();

    void Deserialization(JSONObject jsonObject);
}
