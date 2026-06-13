package com.hades.hKtweaks.views;

import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.ViewParent;
import android.widget.FrameLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.viewpager2.widget.ViewPager2;

public class NestedScrollableHost extends FrameLayout {

    private final int mTouchSlop;
    private float mInitialX;
    private float mInitialY;

    public NestedScrollableHost(@NonNull Context context) {
        this(context, null);
    }

    public NestedScrollableHost(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        mTouchSlop = ViewConfiguration.get(context).getScaledTouchSlop();
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent event) {
        handleInterceptTouchEvent(event);
        return super.onInterceptTouchEvent(event);
    }

    private void handleInterceptTouchEvent(MotionEvent event) {
        ViewPager2 parentViewPager = findParentViewPager();
        if (parentViewPager == null) {
            return;
        }

        int orientation = parentViewPager.getOrientation();
        if (!canChildScroll(orientation, -1f) && !canChildScroll(orientation, 1f)) {
            return;
        }

        if (event.getAction() == MotionEvent.ACTION_DOWN) {
            mInitialX = event.getX();
            mInitialY = event.getY();
            getParent().requestDisallowInterceptTouchEvent(true);
        } else if (event.getAction() == MotionEvent.ACTION_MOVE) {
            float dx = event.getX() - mInitialX;
            float dy = event.getY() - mInitialY;
            boolean horizontal = orientation == ViewPager2.ORIENTATION_HORIZONTAL;
            float scaledDx = Math.abs(dx) * (horizontal ? 0.5f : 1f);
            float scaledDy = Math.abs(dy) * (horizontal ? 1f : 0.5f);

            if (scaledDx > mTouchSlop || scaledDy > mTouchSlop) {
                if (horizontal == (scaledDy > scaledDx)) {
                    getParent().requestDisallowInterceptTouchEvent(false);
                } else if (canChildScroll(orientation, horizontal ? dx : dy)) {
                    getParent().requestDisallowInterceptTouchEvent(true);
                } else {
                    getParent().requestDisallowInterceptTouchEvent(false);
                }
            }
        }
    }

    @Nullable
    private ViewPager2 findParentViewPager() {
        ViewParent parent = getParent();
        while (parent instanceof View) {
            if (parent instanceof ViewPager2) {
                return (ViewPager2) parent;
            }
            parent = parent.getParent();
        }
        return null;
    }

    private boolean canChildScroll(int orientation, float delta) {
        View child = getChildAt(0);
        if (child == null) {
            return false;
        }
        int direction = -((int) Math.signum(delta));
        if (orientation == ViewPager2.ORIENTATION_HORIZONTAL) {
            return child.canScrollHorizontally(direction);
        }
        return child.canScrollVertically(direction);
    }
}
