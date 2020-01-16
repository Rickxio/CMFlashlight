package com.dema.versatile.scene.utils;

import androidx.annotation.Nullable;

import java.util.Collection;

/**
 * Created by wangyu on 2019/8/23.
 */
public class UtilsCollection {

    public static boolean isEmpty(@Nullable Collection<?> var0) {
        return var0 == null || var0.isEmpty();
    }
}
