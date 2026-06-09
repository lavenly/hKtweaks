package com.grx.soundcontrol;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.android.material.slider.Slider;
import com.google.android.material.textview.MaterialTextView;
import com.hades.hKtweaks.R;
import com.hades.hKtweaks.utils.kernel.sound.MoroSound;

public class GrxEqualizerBandController extends LinearLayout {

    public int mBandId;

    EqBandValueChange mCallBack = null;

    String mCurrentValue, mOldValue;


    public interface EqBandValueChange{
        void EqValueChanged(int id, String value);
    }

    public GrxEqualizerBandController(Context context) {
       this(context, null, 0);
    }

    public GrxEqualizerBandController(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public GrxEqualizerBandController(Context context, AttributeSet attrs, int defStyleAttr) {
         super(context, attrs, defStyleAttr);
         initView();
    }

    public void setCallBack(EqBandValueChange listener){
        mCallBack=listener;
    }

    private Slider mSlider;
    public MaterialTextView mValueTextView;

    private void initView(){
        String tag = (String) getTag();
        mBandId = Integer.valueOf( (String) getTag() );

       inflate(getContext(),R.layout.grx_equalizer_band,this);
    }

    @Override
    protected void onFinishInflate(){
        super.onFinishInflate();
        mSlider = findViewById(R.id.eqseekbar);
        mSlider.addOnChangeListener((slider, value, fromUser) -> {
            String newValue = String.valueOf(Math.round(value));
            mValueTextView.setText(getContext().getString(R.string.db_value, newValue));
            if (fromUser) {
                mCurrentValue = newValue;
                MoroSound.setEqValues(newValue, mBandId, getContext());
            }
        });
        mSlider.addOnSliderTouchListener(new Slider.OnSliderTouchListener() {
            @Override
            public void onStartTrackingTouch(Slider slider) {
                mOldValue = mCurrentValue;
            }

            @Override
            public void onStopTrackingTouch(Slider slider) {
                mCurrentValue = String.valueOf(Math.round(slider.getValue()));
                if (!mCurrentValue.equals(mOldValue) && mCallBack != null) {
                    mCallBack.EqValueChanged(mBandId, mCurrentValue);
                }
                mOldValue = mCurrentValue;
            }
        });
        mValueTextView = findViewById(R.id.value);

        TextView bandview = findViewById(R.id.band);
        String[] bands = getResources().getStringArray(R.array.equalizerbands);
        bandview.setText(String.valueOf(  bands[mBandId]    ));
        updateSeekBar();
    }

    public void updateSeekBar(){
        mCurrentValue = MoroSound.getEqValue(mBandId);
        if(mCurrentValue==null || mCurrentValue.isEmpty()) mCurrentValue="0";
        mOldValue=mCurrentValue;
        setValue(mCurrentValue);
    }

    public void setValue(String value) {
        float sliderValue;
        try {
            sliderValue = Float.parseFloat(value);
        } catch (NumberFormatException ignored) {
            sliderValue = 0;
        }
        sliderValue = Math.max(mSlider.getValueFrom(), Math.min(sliderValue, mSlider.getValueTo()));
        mSlider.setValue(sliderValue);
        mCurrentValue = String.valueOf(Math.round(sliderValue));
        mValueTextView.setText(getContext().getString(R.string.db_value, mCurrentValue));
    }

    public void setSliderEnabled(boolean enabled) {
        mSlider.setEnabled(enabled);
    }
}
