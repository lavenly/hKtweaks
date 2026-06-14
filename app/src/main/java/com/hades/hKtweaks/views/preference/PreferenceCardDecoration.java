/*
 * Copyright (C) 2026 The hKtweaks Project
 *
 * This file is part of hKtweaks.
 */
package com.hades.hKtweaks.views.preference;

import android.content.Context;
import android.content.res.ColorStateList;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.preference.Preference;
import androidx.preference.PreferenceCategory;
import androidx.preference.PreferenceGroupAdapter;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.R;
import com.google.android.material.color.MaterialColors;
import com.google.android.material.shape.MaterialShapeDrawable;
import com.google.android.material.shape.ShapeAppearanceModel;

public class PreferenceCardDecoration extends RecyclerView.ItemDecoration {

    private final float mCornerSize;
    private final float mDividerInset;
    private final MaterialShapeDrawable mBackground = new MaterialShapeDrawable();
    private final Paint mDividerPaint = new Paint(Paint.ANTI_ALIAS_FLAG);

    public PreferenceCardDecoration(Context context) {
        float density = context.getResources().getDisplayMetrics().density;
        mCornerSize = 28f * density;
        mDividerInset = 56f * density;

        mBackground.setFillColor(ColorStateList.valueOf(
                MaterialColors.getColor(context, R.attr.colorSurfaceContainer, 0)));
        mDividerPaint.setColor(
                MaterialColors.getColor(context, R.attr.colorOutlineVariant, 0));
        mDividerPaint.setStrokeWidth(density);
    }

    @Override
    public void onDraw(@NonNull Canvas canvas, @NonNull RecyclerView parent,
                       @NonNull RecyclerView.State state) {
        RecyclerView.Adapter<?> adapter = parent.getAdapter();
        if (!(adapter instanceof PreferenceGroupAdapter)) {
            return;
        }

        PreferenceGroupAdapter preferenceAdapter = (PreferenceGroupAdapter) adapter;
        int itemCount = preferenceAdapter.getItemCount();
        for (int i = 0; i < parent.getChildCount(); i++) {
            View child = parent.getChildAt(i);
            int position = parent.getChildAdapterPosition(child);
            if (position == RecyclerView.NO_POSITION || position >= itemCount) {
                continue;
            }

            Preference preference = preferenceAdapter.getItem(position);
            if (preference instanceof PreferenceCategory) {
                continue;
            }

            boolean first = position == 0
                    || preferenceAdapter.getItem(position - 1) instanceof PreferenceCategory;
            boolean last = position == itemCount - 1
                    || preferenceAdapter.getItem(position + 1) instanceof PreferenceCategory;

            ShapeAppearanceModel shape = ShapeAppearanceModel.builder()
                    .setTopLeftCornerSize(first ? mCornerSize : 0f)
                    .setTopRightCornerSize(first ? mCornerSize : 0f)
                    .setBottomLeftCornerSize(last ? mCornerSize : 0f)
                    .setBottomRightCornerSize(last ? mCornerSize : 0f)
                    .build();
            mBackground.setShapeAppearanceModel(shape);
            mBackground.setBounds(child.getLeft(), child.getTop(),
                    child.getRight(), child.getBottom());
            mBackground.draw(canvas);

            if (!last) {
                float dividerY = child.getBottom() - mDividerPaint.getStrokeWidth() / 2f;
                canvas.drawLine(child.getLeft() + mDividerInset, dividerY,
                        child.getRight(), dividerY, mDividerPaint);
            }
        }
    }
}
