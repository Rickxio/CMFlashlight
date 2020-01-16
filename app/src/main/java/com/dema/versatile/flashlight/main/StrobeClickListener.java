package com.dema.versatile.flashlight.main;

import com.dema.versatile.flashlight.core.flashlight.in.IStrobeItem;

public abstract class StrobeClickListener {
    public void onItemClickListener(int index, int position, IStrobeItem item){}

    public void onItemClickListener(int position, IStrobeItem item){}
}
