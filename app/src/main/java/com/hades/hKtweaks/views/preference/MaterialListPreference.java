package com.hades.hKtweaks.views.preference;

import android.content.Context;
import android.util.AttributeSet;

import androidx.preference.ListPreference;

import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.hades.hKtweaks.R;

public class MaterialListPreference extends ListPreference {

    public MaterialListPreference(Context context, AttributeSet attrs) {
        super(context, attrs, R.attr.preferenceStyle);
        setLayoutResource(R.layout.preference_material3);
        setIconSpaceReserved(false);
    }

    @Override
    protected void onClick() {
        CharSequence[] entries = getEntries();
        CharSequence[] entryValues = getEntryValues();
        if (entries == null || entryValues == null) {
            return;
        }

        int selected = findIndexOfValue(getValue());
        new MaterialAlertDialogBuilder(getContext())
                .setTitle(getDialogTitle())
                .setSingleChoiceItems(entries, selected, (dialog, which) -> {
                    String value = entryValues[which].toString();
                    if (callChangeListener(value)) {
                        setValue(value);
                    }
                    dialog.dismiss();
                })
                .setNegativeButton(android.R.string.cancel, null)
                .show();
    }
}
