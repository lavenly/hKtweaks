package com.hades.hKtweaks.views.preference;

import android.content.Context;
import android.util.AttributeSet;

import androidx.preference.PreferenceViewHolder;
import androidx.preference.TwoStatePreference;

import com.google.android.material.materialswitch.MaterialSwitch;
import com.hades.hKtweaks.R;

public class MaterialSwitchPreference extends TwoStatePreference {

    public MaterialSwitchPreference(Context context, AttributeSet attrs) {
        super(context, attrs, R.attr.preferenceStyle);
        setLayoutResource(R.layout.preference_material3);
        setWidgetLayoutResource(R.layout.preference_widget_material_switch);
        setIconSpaceReserved(false);
    }

    public MaterialSwitchPreference(Context context) {
        this(context, null);
    }

    @Override
    public void onBindViewHolder(PreferenceViewHolder holder) {
        super.onBindViewHolder(holder);
        MaterialSwitch materialSwitch = (MaterialSwitch) holder.findViewById(R.id.material_switch);
        if (materialSwitch != null) {
            materialSwitch.setChecked(isChecked());
            materialSwitch.setEnabled(isEnabled());
        }
        syncSummaryView(holder);
    }
}
