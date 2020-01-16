package com.dema.versatile.lib.utils.emulator;

/**
 * Project Name:EasyProtector
 * Package Name:com.lahm.library
 * Created by lahm on 2018/5/14 下午9:38 .
 */
public class EasyProtectorLib {


    public static boolean checkIsRoot() {
        return SecurityCheckUtil.getSingleInstance().isRoot();
    }

    public static boolean checkIsRunningInEmulator() {
        return EmulatorCheckUtil.getSingleInstance().readSysProperty();
    }
}
