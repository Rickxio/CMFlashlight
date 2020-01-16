package com.dema.versatile.lib.core.in;

public interface ICMFactory {
    <T> T createInstance(Class<T> classInterface);

    <T> T createInstance(Class<T> classInterface, Class<?> classImplement);

    boolean isClassInterfaceExist(Class<?> classInterface);
}
