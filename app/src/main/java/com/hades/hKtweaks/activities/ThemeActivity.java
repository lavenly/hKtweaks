/*
 * Copyright (C) 2026 The hKtweaks Project
 *
 * This file is part of hKtweaks.
 */
package com.hades.hKtweaks.activities;

import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.HorizontalScrollView;
import android.widget.LinearLayout;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatDelegate;

import com.google.android.material.color.MaterialColors;
import com.google.android.material.radiobutton.MaterialRadioButton;
import com.hades.hKtweaks.R;
import com.hades.hKtweaks.services.profile.Widget;
import com.hades.hKtweaks.utils.Themes;
import com.hades.hKtweaks.utils.ViewUtils;
import com.hades.hKtweaks.views.ThemeColorView;

import java.util.List;

public class ThemeActivity extends BaseActivity {

    public static final int RESULT_THEME_CHANGED = RESULT_OK;

    private static final String STATE_CHANGED = "theme_changed";
    private static final int[] THEME_MODE_ROWS = {
            R.id.theme_mode_system,
            R.id.theme_mode_light,
            R.id.theme_mode_dark,
            R.id.theme_mode_amoled
    };
    private static final int[] THEME_MODE_RADIOS = {
            R.id.theme_mode_system_radio,
            R.id.theme_mode_light_radio,
            R.id.theme_mode_dark_radio,
            R.id.theme_mode_amoled_radio
    };

    private boolean mChanged;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mChanged = savedInstanceState != null
                && savedInstanceState.getBoolean(STATE_CHANGED, false);
        if (mChanged) {
            setResult(RESULT_THEME_CHANGED);
        }

        setContentView(R.layout.activity_theme);
        initToolBar();
        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle(R.string.theme_mode);
        }

        View content = findViewById(R.id.theme_content);
        content.setPadding(content.getPaddingLeft(),
                content.getPaddingTop() + Math.round(ViewUtils.getActionBarSize(this)),
                content.getPaddingRight(), content.getPaddingBottom());

        bindPalette();
        bindThemeModes();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        outState.putBoolean(STATE_CHANGED, mChanged);
        super.onSaveInstanceState(outState);
    }

    @Override
    public void finish() {
        if (mChanged) {
            Widget.updateAll(getApplicationContext());
        }
        super.finish();
    }

    private void bindPalette() {
        LinearLayout palette = findViewById(R.id.theme_palette);
        HorizontalScrollView paletteScroll = findViewById(R.id.theme_palette_scroll);
        String selectedColor = Themes.getThemeColor(this);
        boolean dark = Themes.isDarkTheme(this);
        boolean amoled = Themes.isAmoledBlack(this);
        ThemeColorView selectedView = null;

        List<String> colors = Themes.getAvailableThemeColors();
        for (int i = 0; i < colors.size(); i++) {
            String color = colors.get(i);
            Context themedContext = Themes.createThemedContext(
                    this, color, dark, amoled);

            ThemeColorView colorView = new ThemeColorView(this);
            int margin = Math.round(dp(8));
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                    Math.round(dp(72)), Math.round(dp(72)));
            params.setMarginEnd(margin);
            colorView.setLayoutParams(params);
            colorView.setContentDescription(Themes.THEME_COLOR_DYNAMIC.equals(color)
                    ? getString(R.string.theme_color_device)
                    : getString(R.string.theme_color_option, i + 1));
            colorView.setColors(
                    resolveColor(themedContext, R.attr.colorSurfaceContainer),
                    resolveColor(themedContext, R.attr.colorPrimaryContainer),
                    resolveColor(themedContext, R.attr.colorTertiaryContainer),
                    resolveColor(themedContext, R.attr.colorPrimary),
                    resolveColor(themedContext, R.attr.colorOnPrimary));
            colorView.setChecked(color.equals(selectedColor));
            colorView.setOnClickListener(view -> applyThemeColor(color));
            palette.addView(colorView);

            if (color.equals(selectedColor)) {
                selectedView = colorView;
            }
        }

        ThemeColorView viewToReveal = selectedView;
        if (viewToReveal != null) {
            paletteScroll.post(() -> {
                int target = viewToReveal.getLeft()
                        - (paletteScroll.getWidth() - viewToReveal.getWidth()) / 2;
                paletteScroll.scrollTo(Math.max(0, target), 0);
            });
        }
    }

    private void bindThemeModes() {
        int selectedRow = getModeRow(Themes.getThemeMode(this));

        for (int rowId : THEME_MODE_ROWS) {
            View row = findViewById(rowId);
            row.setOnClickListener(view -> {
                selectThemeModeRow(view.getId());
                applyThemeMode(getModeValue(view.getId()));
            });
        }
        selectThemeModeRow(selectedRow);
    }

    private void selectThemeModeRow(int selectedRow) {
        int selectedColor = resolveColor(this, R.attr.colorSecondaryContainer);
        for (int i = 0; i < THEME_MODE_ROWS.length; i++) {
            boolean selected = THEME_MODE_ROWS[i] == selectedRow;
            View row = findViewById(THEME_MODE_ROWS[i]);
            MaterialRadioButton radio = findViewById(THEME_MODE_RADIOS[i]);
            row.setSelected(selected);
            row.setBackgroundColor(selected ? selectedColor : Color.TRANSPARENT);
            radio.setChecked(selected);
        }
    }

    private void applyThemeColor(String color) {
        if (color.equals(Themes.getThemeColor(this))) {
            return;
        }
        Themes.saveThemeColor(color, this);
        applyThemeChange();
    }

    private void applyThemeMode(String mode) {
        if (mode.equals(Themes.getThemeMode(this))) {
            return;
        }
        Themes.saveThemeMode(mode, this);
        markThemeChanged();

        int nightMode = getNightMode(mode);
        if (AppCompatDelegate.getDefaultNightMode() == nightMode) {
            recreate();
        } else {
            AppCompatDelegate.setDefaultNightMode(nightMode);
        }
    }

    private void applyThemeChange() {
        markThemeChanged();
        recreate();
    }

    private void markThemeChanged() {
        mChanged = true;
        setResult(RESULT_THEME_CHANGED);
    }

    private int getNightMode(String mode) {
        switch (mode) {
            case Themes.THEME_MODE_LIGHT:
                return AppCompatDelegate.MODE_NIGHT_NO;
            case Themes.THEME_MODE_DARK:
            case Themes.THEME_MODE_AMOLED:
                return AppCompatDelegate.MODE_NIGHT_YES;
            default:
                return AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM;
        }
    }

    private int getModeRow(String mode) {
        switch (mode) {
            case Themes.THEME_MODE_LIGHT:
                return R.id.theme_mode_light;
            case Themes.THEME_MODE_DARK:
                return R.id.theme_mode_dark;
            case Themes.THEME_MODE_AMOLED:
                return R.id.theme_mode_amoled;
            default:
                return R.id.theme_mode_system;
        }
    }

    private String getModeValue(int rowId) {
        if (rowId == R.id.theme_mode_light) {
            return Themes.THEME_MODE_LIGHT;
        }
        if (rowId == R.id.theme_mode_dark) {
            return Themes.THEME_MODE_DARK;
        }
        if (rowId == R.id.theme_mode_amoled) {
            return Themes.THEME_MODE_AMOLED;
        }
        return Themes.THEME_MODE_SYSTEM;
    }

    private int resolveColor(Context context, int attribute) {
        return MaterialColors.getColor(context, attribute, 0);
    }

    private float dp(float value) {
        return value * getResources().getDisplayMetrics().density;
    }
}
