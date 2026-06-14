/*
 * Copyright (C) 2026 The hKtweaks Project
 *
 * This file is part of hKtweaks.
 */
package com.hades.hKtweaks.views;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.util.AttributeSet;
import android.view.View;

import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.drawable.DrawableCompat;

import com.hades.hKtweaks.R;
import com.hades.hKtweaks.utils.ViewUtils;

public class ThemeColorView extends View {

    private final Paint mPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
    private final Paint mRingPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
    private final Path mColorCircle = new Path();
    private final Drawable mCheck;
    private final GradientDrawable mContainer = new GradientDrawable();

    private int mPrimaryContainer;
    private int mTertiaryContainer;
    private int mPrimary;
    private boolean mChecked;

    public ThemeColorView(Context context) {
        this(context, null);
    }

    public ThemeColorView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public ThemeColorView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        Drawable check = ContextCompat.getDrawable(context, R.drawable.ic_done);
        mCheck = check != null ? check.mutate() : null;
        mRingPaint.setStyle(Paint.Style.STROKE);
        mRingPaint.setStrokeWidth(dp(2));

        mContainer.setCornerRadius(dp(20));
        setBackground(mContainer);
        setForeground(ViewUtils.getSelectableBackground(context));
        setClipToOutline(true);
        setClickable(true);
        setFocusable(true);
    }

    public void setColors(int container, int primaryContainer, int tertiaryContainer,
                          int primary, int onPrimary) {
        mContainer.setColor(container);
        mPrimaryContainer = primaryContainer;
        mTertiaryContainer = tertiaryContainer;
        mPrimary = primary;
        mRingPaint.setColor(primary);
        if (mCheck != null) {
            DrawableCompat.setTint(mCheck, onPrimary);
        }
        invalidate();
    }

    public void setChecked(boolean checked) {
        if (mChecked == checked) {
            return;
        }
        mChecked = checked;
        setSelected(checked);
        invalidate();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        float centerX = getWidth() / 2f;
        float centerY = getHeight() / 2f;
        float paletteRadius = dp(24);

        mColorCircle.reset();
        mColorCircle.addCircle(centerX, centerY, paletteRadius, Path.Direction.CW);
        canvas.save();
        canvas.clipPath(mColorCircle);
        mPaint.setColor(mPrimaryContainer);
        canvas.drawRect(centerX - paletteRadius, centerY - paletteRadius,
                centerX + paletteRadius, centerY, mPaint);
        mPaint.setColor(mTertiaryContainer);
        canvas.drawRect(centerX - paletteRadius, centerY,
                centerX + paletteRadius, centerY + paletteRadius, mPaint);
        canvas.restore();

        if (mChecked) {
            canvas.drawCircle(centerX, centerY, dp(28), mRingPaint);
        }

        mPaint.setColor(mPrimary);
        float centerRadius = dp(mChecked ? 12 : 10);
        canvas.drawCircle(centerX, centerY, centerRadius, mPaint);

        if (mChecked && mCheck != null) {
            int checkRadius = Math.round(dp(8));
            int x = Math.round(centerX);
            int y = Math.round(centerY);
            mCheck.setBounds(x - checkRadius, y - checkRadius,
                    x + checkRadius, y + checkRadius);
            mCheck.draw(canvas);
        }
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int desiredSize = Math.round(dp(72));
        setMeasuredDimension(resolveSize(desiredSize, widthMeasureSpec),
                resolveSize(desiredSize, heightMeasureSpec));
    }

    @Override
    public boolean performClick() {
        animate().cancel();
        setScaleX(0.94f);
        setScaleY(0.94f);
        animate().scaleX(1f).scaleY(1f).setDuration(180).start();
        return super.performClick();
    }

    private float dp(float value) {
        return value * getResources().getDisplayMetrics().density;
    }
}
