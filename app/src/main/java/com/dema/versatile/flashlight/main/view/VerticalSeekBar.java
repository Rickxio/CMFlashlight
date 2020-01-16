package com.dema.versatile.flashlight.main.view;

import android.content.Context;
import android.graphics.Canvas;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;

import androidx.appcompat.widget.AppCompatSeekBar;

public class VerticalSeekBar extends AppCompatSeekBar {
 public VerticalSeekBar(Context context) {
  this (context, null);
 }
 public VerticalSeekBar(Context context, AttributeSet attrs) {
  this (context, attrs, 0);
 }
 public VerticalSeekBar(Context context, AttributeSet attrs, int defStyle) {
  super(context, attrs, defStyle);
 }

 protected void onSizeChanged(int w, int h, int oldw, int oldh) {
  super.onSizeChanged(h, w, oldh, oldw);
 }

 @Override
 protected synchronized void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
  super.onMeasure(heightMeasureSpec, widthMeasureSpec);
  setMeasuredDimension(getMeasuredHeight(), getMeasuredWidth());
 }

 @Override
 protected void onDraw(Canvas c) {
  // 将SeekBar转转90度
  c.rotate(-90);
  // 将旋转后的视图移动回来
  c.translate(-getHeight(), 0);
  Log.e(getClass().getSimpleName(), "getHeight(): " + getHeight());
  super.onDraw(c);
 }

 @Override
 public boolean onTouchEvent(MotionEvent event) {
  if (!isEnabled()) {
   return false;
  }
  switch (event.getAction() & MotionEvent.ACTION_MASK) {
   case MotionEvent.ACTION_DOWN:
   case MotionEvent.ACTION_MOVE:
   case MotionEvent.ACTION_UP:
    int i = 0;
    // 获取滑动的距离
    i = getMax() - (int) (getMax() * event.getY() / getHeight());
    // 设置进度
    setProgress(i);
    Log.e(getClass().getSimpleName(), "Progress:" +  getProgress());
    // 每次拖动SeekBar都会调用
    onSizeChanged(getWidth(), getHeight(), 0, 0);
    Log.e(getClass().getSimpleName(), "getWidth():" + getWidth());
    Log.e(getClass().getSimpleName(), "getHeight():" + getHeight());
    break;
   case MotionEvent.ACTION_CANCEL:
    break;
  }
  return true;
 }

 @Override
 public synchronized void setProgress(int progress) {
  super.setProgress(progress);
  onSizeChanged(getWidth(), getHeight(), 0, 0);
 }

}