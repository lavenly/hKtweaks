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

import com.google.android.material.textview.MaterialTextView;
import com.google.android.material.slider.Slider;
import android.view.View;

import com.hades.hKtweaks.R;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by willi on 06.05.16.
 */
public class SeekBarView extends RecyclerViewItem {

    public interface OnSeekBarListener {
        void onStop(SeekBarView seekBarView, int position, String value);

        void onMove(SeekBarView seekBarView, int position, String value);
    }

    private MaterialTextView mTitle;
    private MaterialTextView mSummary;
    private MaterialTextView mValue;
    private Slider mSeekBar;

    private CharSequence mTitleText;
    private CharSequence mSummaryText;

    private int mMin;
    private int mMax = 100;
    private int mProgress;
    private String mUnit;
    private final List<String> mItems = new ArrayList<>();
    private int mOffset = 1;
    private boolean mEnabled = true;
    private float mAlpha = 1f;

    private OnSeekBarListener mOnSeekBarListener;

    @Override
    public int getLayoutRes() {
        return R.layout.rv_seekbar_view;
    }

    @Override
    public void onCreateView(final View view) {
        mTitle = view.findViewById(R.id.title);
        mSummary = view.findViewById(R.id.summary);
        mValue = view.findViewById(R.id.value);
        mSeekBar = view.findViewById(R.id.seekbar);

        view.findViewById(R.id.button_minus).setOnClickListener(v -> {
            if(mEnabled) {
                mSeekBar.setValue(Math.max(mSeekBar.getValueFrom(), mSeekBar.getValue() - 1));
                if (mOnSeekBarListener != null && mProgress < mItems.size() && mProgress >= 0) {
                    mOnSeekBarListener.onStop(SeekBarView.this, mProgress, mItems.get(mProgress));
                }
            }
        });
        view.findViewById(R.id.button_plus).setOnClickListener(v -> {
            if (mEnabled) {
                mSeekBar.setValue(Math.min(mSeekBar.getValueTo(), mSeekBar.getValue() + 1));
                if (mOnSeekBarListener != null && mProgress < mItems.size() && mProgress >= 0) {
                    mOnSeekBarListener.onStop(SeekBarView.this, mProgress, mItems.get(mProgress));
                }
            }
        });

        mSeekBar.addOnChangeListener((seekBar, value, fromUser) -> {
                int position = Math.round(value);
                if (position < mItems.size() && position >= 0) {
                    mProgress = position;
                    String text = mItems.get(position);
                    if (mUnit != null) text += mUnit;
                    mValue.setText(text);
                    if (mOnSeekBarListener != null) {
                        mOnSeekBarListener.onMove(
                                SeekBarView.this, mProgress, mItems.get(mProgress));
                    }
                }
        });
        mSeekBar.addOnSliderTouchListener(new Slider.OnSliderTouchListener() {
            @Override
            public void onStartTrackingTouch(Slider seekBar) {
            }

            @Override
            public void onStopTrackingTouch(Slider seekBar) {
                try {
                    if (mOnSeekBarListener != null) {
                        mOnSeekBarListener.onStop(
                                SeekBarView.this, mProgress, mItems.get(mProgress));
                    }
                } catch (Exception ignored) {
                }
            }
        });
        mSeekBar.setFocusable(false);

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

    public void setProgress(int progress) {
        mProgress = progress;
        refresh();
    }

    public void setMin(int min) {
        mMin = min;
        mItems.clear();
        refresh();
    }

    public void setUnit(String unit) {
        mUnit = unit;
        mItems.clear();
        refresh();
    }

    public void setMax(int max) {
        mMax = max;
        mItems.clear();
        refresh();
    }

    public void setItems(List<String> items) {
        mItems.clear();
        mItems.addAll(items);
        refresh();
    }

    public void setOffset(int offset) {
        mOffset = offset;
        mItems.clear();
        refresh();
    }

    public void setEnabled(boolean enable) {
        mEnabled = enable;
        refresh();
    }

    public void setAlpha(float alpha) {
        mAlpha = alpha;
        refresh();
    }

    public int getProgress() {
        return mProgress;
    }

    public void setOnSeekBarListener(OnSeekBarListener onSeekBarListener) {
        mOnSeekBarListener = onSeekBarListener;
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
        if (mSummary != null && mSummaryText != null) {
            mSummary.setText(mSummaryText);
        }
        if (mItems.size() == 0) {
            for (int i = mMin; i <= mMax; i += mOffset) {
                mItems.add(String.valueOf(i));
            }
        }
        if (mSeekBar != null) {
            int maximum = Math.max(1, mItems.size() - 1);
            mSeekBar.setValueFrom(0);
            mSeekBar.setValueTo(maximum);
            mSeekBar.setStepSize(1);
            mSeekBar.setAlpha(mAlpha);
            mSeekBar.setEnabled(mEnabled && mItems.size() > 1);
            if (mValue != null) {
                try {
                    String text = mItems.get(mProgress);
                    mSeekBar.setValue(Math.max(0, Math.min(mProgress, maximum)));
                    if (mUnit != null) text += mUnit;
                    mValue.setText(text);
                } catch (Exception ignored) {
                    mValue.setText(mValue.getResources().getString(R.string.not_in_range));
                }
            }
            if(mEnabled){
                mSeekBar.setAlpha(1f);
            } else {
                mSeekBar.setAlpha(0.4f);
            }
        }
    }
}
