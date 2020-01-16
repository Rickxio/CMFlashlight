package com.dema.versatile.flashlight.core.compass.in;

public interface ISensorListener {
    /***
     * 返回角度，水平，垂直的数值
     * @param degree
     * @param pitchAngle
     * @param rollAngle
     */
    void onAngelChanged(float degree, float pitchAngle, float rollAngle);
}
