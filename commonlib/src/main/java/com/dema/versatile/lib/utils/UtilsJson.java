package com.dema.versatile.lib.utils;

import java.io.ByteArrayOutputStream;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.json.JSONArray;
import org.json.JSONObject;

import android.content.Context;
import android.text.TextUtils;

import com.dema.versatile.lib.CMLibFactory;
import com.dema.versatile.lib.core.in.ICMFactory;
import com.dema.versatile.lib.core.in.ICMJson;

public class UtilsJson {
    private static Set<ICMFactory> sSetCMFactory = null;

    static {
        sSetCMFactory = new HashSet<ICMFactory>();
        sSetCMFactory.add(CMLibFactory.getInstance());
    }

    public static void addFactory(ICMFactory iCMFactory) {
        if (null == iCMFactory)
            return;

        if (sSetCMFactory.contains(iCMFactory))
            return;

        sSetCMFactory.add(iCMFactory);
    }

    private static ICMFactory findFactoryByClassInterface(Class<?> classInterface) {
        if (null == classInterface)
            return null;

        for (ICMFactory iCMFactory : sSetCMFactory) {
            if (null == iCMFactory)
                continue;

            if (iCMFactory.isClassInterfaceExist(classInterface))
                return iCMFactory;
        }

        return null;
    }

    public static boolean saveJsonToFile(Context context, String strFileName, JSONObject jsonObject) {
        if (null == context || TextUtils.isEmpty(strFileName) || null == jsonObject)
            return false;

        try {
            FileOutputStream fos = context.openFileOutput(strFileName, Context.MODE_PRIVATE);
            byte[] buffer = jsonObject.toString().getBytes();
            fos.write(buffer);
            fos.flush();
            fos.close();
            return true;
        } catch (Exception e) {
            e.printStackTrace();
        }

        return false;
    }

    public static boolean saveJsonToFileWithEncrypt(Context context, String strFileName, JSONObject jsonObject) {
        if (null == context || TextUtils.isEmpty(strFileName) || null == jsonObject)
            return false;

        try {
            FileOutputStream fos = context.openFileOutput(strFileName, Context.MODE_PRIVATE);
            byte[] buffer = jsonObject.toString().getBytes();
            buffer = UtilsEncrypt.encryptByBlowFish(buffer);
            fos.write(buffer);
            fos.flush();
            fos.close();
            return true;
        } catch (Exception e) {
            e.printStackTrace();
        }

        return false;
    }

    public static JSONObject loadJsonFromFile(Context context, String strFileName) {
        if (null == context || TextUtils.isEmpty(strFileName))
            return null;

        try {
            FileInputStream fIn = context.openFileInput(strFileName);
            if (null == fIn)
                return null;

            int nReadSize = -1;
            byte[] buffer = new byte[UtilsEnv.VALUE_INT_BUFFER_SIZE];
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            while ((nReadSize = fIn.read(buffer)) != -1)
                baos.write(buffer, 0, nReadSize);

            fIn.close();
            baos.close();

            return new JSONObject(baos.toString());
        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }

    public static JSONObject loadJsonFromFileWithDecrypt(Context context, String strFileName) {
        if (null == context || TextUtils.isEmpty(strFileName))
            return null;

        try {
            FileInputStream fIn = context.openFileInput(strFileName);
            if (null == fIn)
                return null;

            int nReadSize = -1;
            byte[] buffer = new byte[UtilsEnv.VALUE_INT_BUFFER_SIZE];
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            while ((nReadSize = fIn.read(buffer)) != -1)
                baos.write(buffer, 0, nReadSize);

            fIn.close();
            baos.close();

            String strJson = new String(UtilsEncrypt.decryptByBlowFish(baos.toByteArray()));
            return new JSONObject(strJson);
        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }

    public static <T extends Object> void JsonSerialization(JSONObject jsonObject, String strName, T tValue) {
        if (null == jsonObject || TextUtils.isEmpty(strName) || null == tValue)
            return;

        try {
            if (ICMJson.class.isAssignableFrom(tValue.getClass())) {
                ICMJson iCMJson = (ICMJson) tValue;
                jsonObject.put(strName, iCMJson.Serialization());
            } else
                jsonObject.put(strName, tValue);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static <T extends Object> void JsonSerialization(JSONObject jsonObject, String strName, List<T> listValue) {
        if (null == jsonObject || TextUtils.isEmpty(strName) || null == listValue)
            return;

        try {
            JSONArray jsonArray = new JSONArray();
            JsonSerialization(jsonArray, listValue);
            jsonObject.put(strName, jsonArray);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static <T extends Object> void JsonSerialization(JSONArray jsonArray, List<T> listValue) {
        if (null == jsonArray || null == listValue)
            return;

        try {
            for (T t : listValue) {
                if (ICMJson.class.isAssignableFrom(t.getClass())) {
                    ICMJson iCMJson = (ICMJson) t;
                    jsonArray.put(iCMJson.Serialization());
                } else {
                    jsonArray.put(t);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static <T extends Object> void JsonSerialization(JSONObject jsonObject, String strName, Set<T> setValue) {
        if (null == jsonObject || TextUtils.isEmpty(strName) || null == setValue)
            return;

        try {
            JSONArray jsonArray = new JSONArray();
            JsonSerialization(jsonArray, setValue);
            jsonObject.put(strName, jsonArray);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static <T extends Object> void JsonSerialization(JSONArray jsonArray, Set<T> setValue) {
        if (null == jsonArray || null == setValue)
            return;

        try {
            for (T t : setValue) {
                if (ICMJson.class.isAssignableFrom(t.getClass())) {
                    ICMJson iCMJson = (ICMJson) t;
                    jsonArray.put(iCMJson.Serialization());
                } else {
                    jsonArray.put(t);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static <T1 extends Object, T2 extends Object> void JsonSerialization(JSONObject jsonObject, String strName, Map<T1, T2> mapValue) {
        if (null == jsonObject || TextUtils.isEmpty(strName) || null == mapValue)
            return;

        try {
            JSONObject jObject = new JSONObject();
            JsonSerialization(jObject, mapValue);
            jsonObject.put(strName, jObject);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static <T1 extends Object, T2 extends Object> void JsonSerialization(JSONObject jsonObject, Map<T1, T2> mapValue) {
        if (null == jsonObject || null == mapValue)
            return;

        try {
            Set<Map.Entry<T1, T2>> set = mapValue.entrySet();
            Iterator<Map.Entry<T1, T2>> iter = set.iterator();
            while (iter.hasNext()) {
                Map.Entry<T1, T2> entry = iter.next();
                JsonSerialization(jsonObject, String.valueOf(entry.getKey()), entry.getValue());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static <T extends Object> T JsonUnserialization(JSONObject jsonObject, String strName, T tValue) {
        if (null == jsonObject || TextUtils.isEmpty(strName) || null == tValue)
            return null;

        try {
            if (tValue.getClass() == Integer.class)
                tValue = (T) Integer.valueOf(jsonObject.getInt(strName));
            else if (tValue.getClass() == Long.class)
                tValue = (T) Long.valueOf(jsonObject.getLong(strName));
            else if (tValue.getClass() == Boolean.class)
                tValue = (T) Boolean.valueOf(jsonObject.getBoolean(strName));
            else if (tValue.getClass() == Double.class)
                tValue = (T) Double.valueOf(jsonObject.getDouble(strName));
            else if (tValue.getClass() == Float.class)
                tValue = (T) Float.valueOf((float) jsonObject.getDouble(strName));
            else if (tValue.getClass() == String.class)
                tValue = (T) jsonObject.getString(strName);
            else if (ICMJson.class.isAssignableFrom(tValue.getClass())) {
                ICMJson iCMJson = (ICMJson) tValue;
                iCMJson.Deserialization(jsonObject.getJSONObject(strName));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return tValue;
    }

    public static <T extends Object> T JsonUnserialization(JSONObject jsonObject, String strName, Class<?> classInterface, Class<?> classImplement) {
        if (null == jsonObject || TextUtils.isEmpty(strName) || null == classInterface || null == classImplement)
            return null;

        try {
            return JsonUnserialization(jsonObject.getJSONObject(strName), classInterface, classImplement);
        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }

    public static <T extends Object> T JsonUnserialization(JSONObject jsonObject, Class<?> classInterface, Class<?> classImplement) {
        if (null == jsonObject || null == classInterface || null == classImplement)
            return null;

        T tValue = null;
        try {
            if (ICMJson.class.isAssignableFrom(classInterface)) {
                tValue = (T) findFactoryByClassInterface(classInterface).createInstance(classInterface, classImplement);
                ICMJson iCMJson = (ICMJson) tValue;
                iCMJson.Deserialization(jsonObject);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return tValue;
    }

    public static <T extends Object> void JsonUnserialization(JSONObject jsonObject, String strName, List<T> listValue,
                                                              Class<?> classT, Class<?> classInterface, Class<?> classImplement) {
        if (null == jsonObject || TextUtils.isEmpty(strName) || null == listValue || null == classT)
            return;

        try {
            JsonUnserialization(jsonObject.getJSONArray(strName), listValue, classT, classInterface, classImplement);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static <T extends Object> void JsonUnserialization(JSONArray jsonArray, List<T> listValue,
                                                              Class<?> classT, Class<?> classInterface, Class<?> classImplement) {
        if (null == jsonArray || null == listValue || null == classT)
            return;

        try {
            listValue.clear();
            for (int nIndex = 0; nIndex < jsonArray.length(); ++nIndex) {
                T tValue = null;
                if (classT == Integer.class)
                    tValue = (T) Integer.valueOf(jsonArray.getInt(nIndex));
                else if (classT == Long.class)
                    tValue = (T) Long.valueOf(jsonArray.getLong(nIndex));
                else if (classT == Boolean.class)
                    tValue = (T) Boolean.valueOf(jsonArray.getBoolean(nIndex));
                else if (classT == Double.class)
                    tValue = (T) Double.valueOf(jsonArray.getDouble(nIndex));
                else if (classT == Float.class)
                    tValue = (T) Float.valueOf((float) jsonArray.getDouble(nIndex));
                else if (classT == String.class)
                    tValue = (T) jsonArray.getString(nIndex);
                else if (ICMJson.class.isAssignableFrom(classT)) {
                    tValue = (T) findFactoryByClassInterface(classInterface).createInstance(classInterface, classImplement);
                    ICMJson iCMJson = (ICMJson) tValue;
                    iCMJson.Deserialization(jsonArray.getJSONObject(nIndex));
                }

                if (null != tValue)
                    listValue.add(tValue);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static <T extends Object> void JsonUnserialization(JSONObject jsonObject, String strName, Set<T> setValue,
                                                              Class<?> classT, Class<?> classInterface, Class<?> classImplement) {
        if (null == jsonObject || TextUtils.isEmpty(strName) || null == setValue || null == classT)
            return;

        try {
            JsonUnserialization(jsonObject.getJSONArray(strName), setValue, classT, classInterface, classImplement);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static <T extends Object> void JsonUnserialization(JSONArray jsonArray, Set<T> setValue,
                                                              Class<?> classT, Class<?> classInterface, Class<?> classImplement) {
        if (null == jsonArray || null == setValue || null == classT)
            return;

        try {
            setValue.clear();
            for (int nIndex = 0; nIndex < jsonArray.length(); ++nIndex) {
                T tValue = null;
                if (classT == Integer.class)
                    tValue = (T) Integer.valueOf(jsonArray.getInt(nIndex));
                else if (classT == Long.class)
                    tValue = (T) Long.valueOf(jsonArray.getLong(nIndex));
                else if (classT == Boolean.class)
                    tValue = (T) Boolean.valueOf(jsonArray.getBoolean(nIndex));
                else if (classT == Double.class)
                    tValue = (T) Double.valueOf(jsonArray.getDouble(nIndex));
                else if (classT == Float.class)
                    tValue = (T) Float.valueOf((float) jsonArray.getDouble(nIndex));
                else if (classT == String.class)
                    tValue = (T) jsonArray.getString(nIndex);
                else if (ICMJson.class.isAssignableFrom(classT)) {
                    tValue = (T) findFactoryByClassInterface(classInterface).createInstance(classInterface, classImplement);
                    ICMJson iCMJson = (ICMJson) tValue;
                    iCMJson.Deserialization(jsonArray.getJSONObject(nIndex));
                }

                if (null != tValue)
                    setValue.add(tValue);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

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
            mapValue.clear();
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
                else if (ICMJson.class.isAssignableFrom(classT2)) {
                    value = (T2) findFactoryByClassInterface(classInterface2).createInstance(classInterface2, classImplement2);
                    ICMJson iCMJson = (ICMJson) value;
                    iCMJson.Deserialization(jsonObject.getJSONObject(strKey));
                }

                mapValue.put(key, value);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
