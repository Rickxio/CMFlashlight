package com.dema.versatile.lib.core.in;

public interface ICMObserver<T extends Object> {
    void addListener(T listener);

    void removeListener(T listener);

    void removeAllListener();

    void notifyListener(ICMNotifyListener<T> listener);

    interface ICMNotifyListener<T> {
        void notify(T t);
    }
}
