package com.hades.hKtweaks.views.recyclerview;

import com.google.android.material.textview.MaterialTextView;
import com.google.android.material.progressindicator.LinearProgressIndicator;
import android.view.View;

import com.hades.hKtweaks.R;

/**
 * Created by MoroGoku on 27/09/2017.
 */

public class ProgressBarView extends RecyclerViewItem {

    private LinearProgressIndicator mProgressBar;
    private MaterialTextView mTitle;
    private MaterialTextView mTotal;
    private MaterialTextView mUsed;
    private MaterialTextView mUsedLabel;
    private MaterialTextView mFree;
    private MaterialTextView mFreeLabel;
    private MaterialTextView mPercent;

    private String mTitleText;
    private String mTotalText;
    private String mUsedText;
    private String mFreeText;
    private String mPercentText;

    private long mProgress;
    private int mMax = 100;
    private int mPadding = 0;
    private int mRadius = 10;
    private int mColorBackground;
    private int mColorProgress;
    private String mUnit;
    private boolean mShowUsed = true;
    private boolean mShowFree = true;
    private boolean mShowPercent = true;

    @Override
    public int getLayoutRes() {
        return R.layout.rv_progressbar_view;
    }

    @Override
    public void onCreateView(final View view) {

        mProgressBar = view.findViewById(R.id.progressbar);
        mTitle = view.findViewById(R.id.title);
        mTotal = view.findViewById(R.id.total);
        mUsed = view.findViewById(R.id.used);
        mUsedLabel = view.findViewById(R.id.used_label);
        mFree = view.findViewById(R.id.free);
        mFreeLabel = view.findViewById(R.id.free_label);
        mPercent = view.findViewById(R.id.percent);

        super.onCreateView(view);
    }

    public void setTitle(String text) {
        mTitleText = text;
        refresh();
    }

    public void setTotal(String text) {
        mTotalText = text;
        refresh();
    }

    public void setUsed(String text) {
        mUsedText = text;
        refresh();
    }

    public void setFree(String text) {
        mFreeText = text;
        refresh();
    }

    public void setProgress(long progress) {
        mProgress = progress;
        mPercentText = mProgress + " %";
        refresh();
    }

    public void setMax(int max) {
        mMax = max;
        refresh();
    }

    public void setUnit(String text) {
        mUnit = text;
        refresh();
    }

    public void setItems(long total, long progress) {
        try {
            mProgress = (progress * 100) / total;
            mTotalText = String.valueOf(total);
            mUsedText = String.valueOf(progress);
            mFreeText = String.valueOf(total - progress);
            mPercentText = mProgress + " %";
            refresh();
        }catch (Exception ignored) {
        }
    }

    public void showUsedLabel(boolean bool) {
        mShowUsed = bool;
        refresh();
    }

    public void showFreeLabel(boolean bool) {
        mShowFree = bool;
        refresh();
    }

    public void showPercent(boolean bool){
        mShowPercent = bool;
        refresh();
    }

    public void setPadding(int padding) {
        mPadding = padding;
        refresh();
    }

    public void setRadius(int radius) {
        mRadius = radius;
        refresh();
    }

    public void setProgressBackgroundColor(int colorBackground) {
        mColorBackground = colorBackground;
        refresh();
    }

    public void setProgressColor(int colorProgress) {
        mColorProgress = colorProgress;
        refresh();
    }

    @Override
    protected void refresh() {
        super.refresh();
        if (mTitle != null){
            if (mTitleText != null) {
                mTitle.setText(mTitleText);
            }
        }
        if (mTotal != null && mTotalText != null) {
            String text = mTotalText;
            if (mUnit != null) text += " " + mUnit;
            mTotal.setText(text);
        }
        if (mUsed != null && mUsedText != null) {
            String text = mUsedText;
            if (mUnit != null) text += " " + mUnit;
            if (mShowUsed){
                text += " ";
                mUsedLabel.setVisibility(View.VISIBLE);
            }else {
                mUsedLabel.setVisibility(View.GONE);
            }
            mUsed.setText(text);
        }
        if (mFree != null && mFreeText != null) {
            String text = mFreeText;
            if (mUnit != null) text += " " + mUnit;
            if (mShowFree){
                text += " ";
                mFreeLabel.setVisibility(View.VISIBLE);
            }else {
                mFreeLabel.setVisibility(View.GONE);
            }
            mFree.setText(text);
        }
        if (mProgressBar != null) {
            mProgressBar.setMax(mMax);
            mProgressBar.setProgressCompat((int) mProgress, true);
            mProgressBar.setPadding(mPadding, 0, mPadding, 0);
            mProgressBar.setTrackCornerRadius(mRadius);
            if (mColorProgress != 0) mProgressBar.setIndicatorColor(mColorProgress);
            if (mColorBackground != 0) mProgressBar.setTrackColor(mColorBackground);
            if (mPercent != null) {
                if(mPercentText != null){
                    mPercent.setText(mPercentText);
                    if(mShowPercent){
                        mPercent.setVisibility(View.VISIBLE);
                    }else {
                        mPercent.setVisibility(View.GONE);
                    }
                }
            }
        }
    }
}
