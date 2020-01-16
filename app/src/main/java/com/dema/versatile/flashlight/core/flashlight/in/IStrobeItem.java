package com.dema.versatile.flashlight.core.flashlight.in;

public interface IStrobeItem {

    boolean isSelect();

    void setSelect(boolean mIsSelect);

    String getText();

    void setText(String mText);

    void setBackground(int res);

    int getBackground();

    void setColor(int res);

    int getColor();

}
