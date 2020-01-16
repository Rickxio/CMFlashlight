package com.dema.versatile.flashlight.core.flashlight.im;

import com.dema.versatile.flashlight.core.flashlight.in.IStrobeItem;

public class StrobeItemImpl implements IStrobeItem {

    private String mText;
    private boolean mIsSelect =false;
    private int mBgRes;
    private int mColor;

    @Override
    public boolean isSelect() {
        return mIsSelect;
    }

    @Override
    public void setSelect(boolean mIsSelect) {
        this.mIsSelect = mIsSelect;
    }

    @Override
    public String getText() {
        return mText;
    }

    @Override
    public void setText(String mText) {
        this.mText = mText;
    }

    @Override
    public void setBackground(int res) {
        this.mBgRes = res;
    }

    @Override
    public int getBackground() {
        return mBgRes;
    }

    @Override
    public void setColor(int res) {
        mColor = res;
    }

    @Override
    public int getColor() {
        return mColor;
    }
}
