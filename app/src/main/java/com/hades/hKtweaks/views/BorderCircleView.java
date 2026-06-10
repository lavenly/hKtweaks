/*
 * Copyright (C) 2015-2016 Willi Ye <williye97@gmail.com>
 *
 * This file is part of Kernel Adiutor.
 *
 * Kernel Adiutor is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * Kernel Adiutor is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with Kernel Adiutor.  If not, see <http://www.gnu.org/licenses/>.
 *
 */
package com.hades.hKtweaks.views;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.drawable.Drawable;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.drawable.DrawableCompat;
import android.util.AttributeSet;
import android.widget.FrameLayout;

import com.hades.hKtweaks.R;
import com.hades.hKtweaks.utils.ViewUtils;

public class BorderCircleView extends FrameLayout {

    private final Drawable mCheck;
    private boolean mChecked;
    private final Paint mPaint;
    private final Paint mPaintBorder;

    public BorderCircleView(Context context) {
        this(context, null);
    }

    public BorderCircleView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public BorderCircleView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        if (isClickable()) {
            setForeground(ViewUtils.getSelectableBackground(context));
        }
        mCheck = ContextCompat.getDrawable(context, R.drawable.ic_done);
        DrawableCompat.setTint(mCheck, Color.WHITE);

        mPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mPaintBorder = new Paint(Paint.ANTI_ALIAS_FLAG);

        TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.BorderCircleView, defStyleAttr, 0);

        int primaryColor = ViewUtils.getColorPrimaryColor(getContext());
        int outlineColor = ViewUtils.getColorOutlineColor(getContext());
        mPaint.setColor(a.getColor(R.styleable.BorderCircleView_circlecolor, primaryColor));
        mPaintBorder.setColor(a.getColor(R.styleable.BorderCircleView_bordercolor, outlineColor));

        a.recycle();

        mPaintBorder.setStrokeWidth((int) getResources().getDimension(R.dimen.circleview_border));
        mPaintBorder.setStyle(Paint.Style.STROKE);

        setWillNotDraw(false);
    }

    public void setCircleColor(int color) {
        mPaint.setColor(color);
        invalidate();
    }

    public void setBorderColor(int color) {
        mPaintBorder.setColor(color);
        invalidate();
    }

    public void setCheckColor(int color) {
        DrawableCompat.setTint(mCheck, color);
        invalidate();
    }

    public void setChecked(boolean checked) {
        mChecked = checked;
        invalidate();
    }

    @Override
    public void draw(Canvas canvas) {
        super.draw(canvas);

        float left = getPaddingLeft();
        float top = getPaddingTop();
        float right = getWidth() - getPaddingRight();
        float bottom = getHeight() - getPaddingBottom();
        float centerX = (left + right) / 2f;
        float centerY = (top + bottom) / 2f;
        float contentInset = getResources().getDimension(R.dimen.circleview_inset);
        float borderInset = mPaintBorder.getStrokeWidth() / 2f;
        float radius = Math.max(0f,
                Math.min(right - left, bottom - top) / 2f - contentInset - borderInset);

        canvas.drawCircle(centerX, centerY, radius, mPaint);
        canvas.drawCircle(centerX, centerY, radius, mPaintBorder);

        if (mChecked) {
            mCheck.setBounds(Math.round(centerX - radius), Math.round(centerY - radius),
                    Math.round(centerX + radius), Math.round(centerY + radius));
            mCheck.draw(canvas);
        }
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);

        float desiredWidth = getResources().getDimension(R.dimen.circleview_width);
        float desiredHeight = getResources().getDimension(R.dimen.circleview_height);

        int widthMode = MeasureSpec.getMode(widthMeasureSpec);
        int widthSize = MeasureSpec.getSize(widthMeasureSpec);
        int heightMode = MeasureSpec.getMode(heightMeasureSpec);
        int heightSize = MeasureSpec.getSize(heightMeasureSpec);

        float width;
        float height;

        if (widthMode == MeasureSpec.EXACTLY) width = widthSize;
        else if (widthMode == MeasureSpec.AT_MOST) width = Math.min(desiredWidth, widthSize);
        else width = desiredWidth;

        if (heightMode == MeasureSpec.EXACTLY) height = heightSize;
        else if (heightMode == MeasureSpec.AT_MOST) height = Math.min(desiredHeight, heightSize);
        else height = desiredHeight;

        setMeasuredDimension((int) width, (int) height);
    }
}
