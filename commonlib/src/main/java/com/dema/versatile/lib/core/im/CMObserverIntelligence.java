package com.dema.versatile.lib.core.im;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;

import com.dema.versatile.lib.core.in.ICMObserver;

public abstract class CMObserverIntelligence<T extends Object> implements ICMObserver<T> {
    protected List<WeakReference<T>> mListListener = new ArrayList<WeakReference<T>>();

    // private ReferenceQueue<T> mRQ = new ReferenceQueue<T>();
    @Override
    public void addListener(T listener) {
        if (null == listener)
            return;

        synchronized (mListListener) {
            if (!isContains(listener)) {
                WeakReference<T> wr = new WeakReference<T>(listener);
                mListListener.add(wr);
            }
        }
    }

    public void removeListener(T listener) {
        if (null == listener)
            return;

        synchronized (mListListener) {
            for (int nIndex = 0; nIndex < mListListener.size(); nIndex++) {
                WeakReference<T> wr = mListListener.get(nIndex);
                T t = wr.get();
                if (null == t)
                    continue;

                if (t.equals(listener)) {
                    mListListener.remove(nIndex);
                    return;
                }
            }
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
            checkListener();
            List<T> list = new ArrayList<T>();
            for (WeakReference<T> wr : mListListener) {
                T t = wr.get();
                if (null == t)
                    continue;

                list.add(t);
            }

            return list;
        }
    }

    private boolean isContains(T listener) {
        if (null == listener)
            return false;

        synchronized (mListListener) {
            for (WeakReference<T> wr : mListListener) {
                T t = wr.get();
                if (null == t)
                    continue;

                if (t.equals(listener))
                    return true;
            }

            return false;
        }
    }

    private void checkListener() {
        synchronized (mListListener) {
            for (int nIndex = 0; nIndex < mListListener.size(); ) {
                WeakReference<T> wr = mListListener.get(nIndex);
                T t = wr.get();
                if (null == t) {
                    mListListener.remove(nIndex);
                } else {
                    nIndex++;
                }
            }
        }
    }
}
