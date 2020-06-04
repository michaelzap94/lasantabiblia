package com.michaelzap94.santabiblia.utilities;

import android.content.Context;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

import androidx.drawerlayout.widget.DrawerLayout;

public class DrawerLayoutHorizontalSupport extends DrawerLayout {
    private int mInterceptTouchEventChildId;
    private boolean verifyScrollChild = false;

    public void setInterceptTouchEventChildId(int id) {
        this.mInterceptTouchEventChildId = id;
    }

    public void setverifyScrollChild(boolean verifyScrollChild) {
        this.verifyScrollChild = verifyScrollChild;
    }

    public DrawerLayoutHorizontalSupport(Context context) {
        super(context);
    }

    public DrawerLayoutHorizontalSupport(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public DrawerLayoutHorizontalSupport(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    public boolean onInterceptTouchEvent(MotionEvent ev) {
        if (this.verifyScrollChild && this.mInterceptTouchEventChildId > 0) {
            View scroll = findViewById(this.mInterceptTouchEventChildId);
            if (scroll != null) {
                Rect rect = new Rect();
                scroll.getHitRect(rect);
                if (rect.contains((int) ev.getX(), (int) ev.getY())) {
                    return false;
                }
            }
        }
        return super.onInterceptTouchEvent(ev);
    }
}
