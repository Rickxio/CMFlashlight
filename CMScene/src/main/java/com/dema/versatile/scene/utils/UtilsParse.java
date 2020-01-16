package com.dema.versatile.scene.utils;

import android.text.TextUtils;

import org.json.JSONObject;

import java.util.Iterator;
import java.util.Map;

/**
 * Created by wangyu on 2019/8/28.
 */
public class UtilsParse {

    public static <T1 extends Object, T2 extends Object> void JsonUnserialization(JSONObject jsonObject, String strName, Map<T1, T2> mapValue,
                                                                                  Class<?> classT1, Class<?> classT2, Class<?> classInterface2, Class<?> classImplement2) {
        if (null == jsonObject || TextUtils.isEmpty(strName) || null == mapValue || null == classT1 || null == classT2)
            return;

        try {
            JsonUnserialization(jsonObject.getJSONObject(strName), mapValue, classT1, classT2, classInterface2, classImplement2);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static <T1 extends Object, T2 extends Object> void JsonUnserialization(JSONObject jsonObject, Map<T1, T2> mapValue,
                                                                                  Class<?> classT1, Class<?> classT2, Class<?> classInterface2, Class<?> classImplement2) {
        if (null == jsonObject || null == mapValue || null == classT1 || null == classT2)
            return;

        try {
            Iterator<String> iter = jsonObject.keys();
            while (iter.hasNext()) {
                String strKey = iter.next();
                T1 key = null;
                if (classT1 == Integer.class)
                    key = (T1) Integer.valueOf(strKey);
                else if (classT1 == Long.class)
                    key = (T1) Long.valueOf(strKey);
                else if (classT1 == Boolean.class)
                    key = (T1) Boolean.valueOf(strKey);
                else if (classT1 == Double.class)
                    key = (T1) Double.valueOf(strKey);
                else if (classT1 == Float.class)
                    key = (T1) Float.valueOf(strKey);
                else if (classT1 == String.class)
                    key = (T1) strKey;

                T2 value = null;
                if (classT2 == Integer.class)
                    value = (T2) Integer.valueOf(jsonObject.getInt(strKey));
                else if (classT2 == Long.class)
                    value = (T2) Long.valueOf(jsonObject.getLong(strKey));
                else if (classT2 == Boolean.class)
                    value = (T2) Boolean.valueOf(jsonObject.getBoolean(strKey));
                else if (classT2 == Double.class)
                    value = (T2) Double.valueOf(jsonObject.getDouble(strKey));
                else if (classT2 == Float.class)
                    value = (T2) Float.valueOf((float) jsonObject.getDouble(strKey));
                else if (classT2 == String.class)
                    value = (T2) jsonObject.getString(strKey);


                mapValue.put(key, value);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
