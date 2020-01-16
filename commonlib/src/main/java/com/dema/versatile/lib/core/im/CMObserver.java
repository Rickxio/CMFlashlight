package com.dema.versatile.lib.core.im;

import java.util.ArrayList;
import java.util.List;

import com.dema.versatile.lib.core.in.ICMObserver;

public abstract class CMObserver<T extends Object> implements ICMObserver<T> {
    protected List<T> mListListener = new ArrayList<T>();

    @Override
    public void addListener(T listener) {
        if (null == listener)
            return;

        synchronized (mListListener) {
            if (!mListListener.contains(listener))
                mListListener.add(listener);
        }
    }

    @Override
    public void removeListener(T listener) {
        if (null == listener)
            return;

        synchronized (mListListener) {
            mListListener.remove(listener);
        }
    }

    @Override
    public void removeAllListener() {
        synchronized (mListListener) {
            mListListener.clear();
        }
    }

    @Override
    public void notifyListener(ICMNotifyListener<T> listener) {
        if (null == listener)
            return;

        for (T t : getListenerList()) {
            listener.notify(t);
        }
    }

    protected List<T> getListenerList() {
        synchronized (mListListener) {
            return new ArrayList<T>(mListListener);
        }
    }
}
