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
package com.hades.hKtweaks.views.recyclerview;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ValueAnimator;

import androidx.dynamicanimation.animation.DynamicAnimation;
import androidx.dynamicanimation.animation.SpringAnimation;

import com.google.android.material.imageview.ShapeableImageView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.hades.hKtweaks.R;
import com.hades.hKtweaks.utils.ExpressiveMotion;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by willi on 04.06.16.
 */
public class DropDownView extends RecyclerViewItem {

    private TextView mTitle;
    private TextView mSummary;
    private ShapeableImageView mArrow;
    private LinearLayout mParent;

    private CharSequence mTitleText;
    private CharSequence mSummaryText;
    private List<String> mItems;
    private int mSelection = -1;
    private boolean mExpanded;

    private final List<View> mDoneViews = new ArrayList<>();

    private float mItemHeight;
    private ValueAnimator mAnimator;
    private SpringAnimation mArrowAnimator;

    private OnDropDownListener mOnDropDownListener;

    public interface OnDropDownListener {
        void onSelect(DropDownView dropDownView, int position, String value);
    }

    @Override
    public int getLayoutRes() {
        return R.layout.rv_drop_down_view;
    }

    @Override
    public void onCreateView(View view) {
        mTitle = view.findViewById(R.id.title);
        mSummary = view.findViewById(R.id.summary);
        mArrow = view.findViewById(R.id.arrow_image);
        mParent = view.findViewById(R.id.parent_layout);

        mItemHeight = view.getResources().getDimension(R.dimen.rv_drop_down_item_height);

        mArrow.setRotationX(mExpanded ? 0 : 180);
        setHeight(mExpanded && mItems != null ? Math.round(mItemHeight * mItems.size()) : 0);

        view.findViewById(R.id.title_parent).setOnClickListener(v -> {
            if (mExpanded) {
                collapse();
            } else {
                expand();
            }
        });

        super.onCreateView(view);
    }

    public void setTitle(CharSequence title) {
        mTitleText = title;
        refresh();
    }

    public void setSummary(CharSequence summary) {
        mSummaryText = summary;
        refresh();
    }

    public void setItems(List<String> items) {
        mItems = items;
        refresh();
    }

    public void setSelection(int selection) {
        mSelection = selection;
        refresh();
    }

    public void setOnDropDownListener(OnDropDownListener onDropDownListener) {
        mOnDropDownListener = onDropDownListener;
    }

    @Override
    protected void refresh() {
        super.refresh();

        if (mTitle != null) {
            if (mTitleText != null) {
                mTitle.setText(mTitleText);
                mTitle.setVisibility(View.VISIBLE);
            } else {
                mTitle.setVisibility(View.GONE);
            }
        }
        if (mSummary != null) {
            if (mSummaryText != null) {
                mSummary.setText(mSummaryText);
                mSummary.setVisibility(View.VISIBLE);
            } else {
                mSummary.setVisibility(View.GONE);
            }
        }

        if (mParent != null && mItems != null) {
            mParent.removeAllViews();
            mDoneViews.clear();
            for (int i = 0; i < mItems.size(); i++) {
                View item = LayoutInflater.from(mParent.getContext()).inflate(R.layout.rv_drop_down_item_view,
                        mParent, false);
                ((TextView) item.findViewById(R.id.title)).setText(mItems.get(i));
                mDoneViews.add(item.findViewById(R.id.done_image));
                item.findViewById(R.id.done_image).setVisibility(View.GONE);

                final int position = i;
                item.setOnClickListener(v -> {
                    mSelection = position;
                    for (int i1 = 0; i1 < mDoneViews.size(); i1++) {
                        mDoneViews.get(i1).setVisibility(position == i1 ? View.VISIBLE : View.INVISIBLE);
                    }
                    if (mOnDropDownListener != null) {
                        mOnDropDownListener.onSelect(DropDownView.this, position, mItems.get(position));
                    }
                });
                mParent.addView(item);
            }
            if (mSelection >= 0 && mSelection < mDoneViews.size()) {
                mDoneViews.get(mSelection).setVisibility(View.VISIBLE);
            }
        }
    }

    private void expand() {
        animateExpansion(true);
    }

    private void collapse() {
        animateExpansion(false);
    }

    private void animateExpansion(boolean expanded) {
        mExpanded = expanded;
        if (mArrow == null) {
            return;
        }

        if (mArrowAnimator != null) {
            mArrowAnimator.cancel();
        }
        mArrowAnimator = ExpressiveMotion.spring(
                mArrow,
                DynamicAnimation.ROTATION_X,
                expanded ? 0 : 180,
                com.google.android.material.R.attr.motionSpringDefaultSpatial,
                R.style.Motion_hKtweaks_Material3Expressive_Default_Spatial);
        mArrowAnimator.addEndListener((animation, canceled, value, velocity) -> {
            if (mArrowAnimator == animation) {
                mArrowAnimator = null;
            }
        });
        mArrowAnimator.start();

        if (mAnimator != null) {
            mAnimator.cancel();
        }
        if (mItems == null) {
            return;
        }

        float targetHeight = expanded ? mItemHeight * mItems.size() : 0;
        ValueAnimator animator = ValueAnimator.ofFloat(mParent.getHeight(), targetHeight);
        animator.addUpdateListener(animation
                -> setHeight(Math.round((float) animation.getAnimatedValue())));
        animator.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                if (mAnimator == animation) {
                    mAnimator = null;
                }
            }
        });
        ExpressiveMotion.applyEmphasized(
                animator,
                mArrow.getContext(),
                com.google.android.material.R.attr.motionDurationMedium4,
                400);
        mAnimator = animator;
        animator.start();
    }

    private void setHeight(int height) {
        if (mParent != null) {
            ViewGroup.LayoutParams params = mParent.getLayoutParams();
            params.height = height;
            mParent.requestLayout();
            viewChanged();
        }
    }

}
