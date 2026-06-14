/*
 * Copyright (C) 2018 Willi Ye <williye97@gmail.com>
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
package com.hades.hKtweaks.utils;

import android.content.Context;
import android.content.res.Configuration;
import android.content.res.Resources;

import androidx.annotation.ColorRes;
import androidx.annotation.StyleRes;

import com.hades.hKtweaks.R;

import java.util.Arrays;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Resolves the app theme from one Material color seed.
 */
public final class Themes {

    public static final class Theme {
        private final String mColor;
        private final @StyleRes int mStyle;

        private Theme(String color, @StyleRes int style) {
            mColor = color;
            mStyle = style;
        }

        public int getStyle() {
            return mStyle;
        }

        @Override
        public String toString() {
            return mColor;
        }
    }

    private static final class ThemeSet {
        private final @ColorRes int mColor;
        private final @StyleRes int mLightStyle;
        private final @StyleRes int mDarkStyle;
        private final @StyleRes int mAmoledStyle;

        private ThemeSet(@ColorRes int color, @StyleRes int lightStyle, @StyleRes int darkStyle,
                         @StyleRes int amoledStyle) {
            mColor = color;
            mLightStyle = lightStyle;
            mDarkStyle = darkStyle;
            mAmoledStyle = amoledStyle;
        }
    }

    private static final String THEME_PREF_KEY = "application_theme";
    private static final String THEME_MODE_PREF_KEY = "theme_mode";
    private static final String DARK_THEME_PREF_KEY = "darktheme";
    private static final String DARK_THEME_MODE_PREF_KEY = "darkthememode";
    private static final String DEFAULT_THEME_COLOR = "themeSeedDefault";

    public static final String THEME_MODE_SYSTEM = "system";
    public static final String THEME_MODE_LIGHT = "light";
    public static final String THEME_MODE_DARK = "dark";
    public static final String THEME_MODE_AMOLED = "amoled";

    public static final List<String> THEME_COLORS = Collections.unmodifiableList(Arrays.asList(
            "themeSeedDefault",
            "themeSeedRed",
            "themeSeedPink",
            "themeSeedPurple",
            "themeSeedBlue",
            "themeSeedGreen",
            "themeSeedOrange",
            "themeSeedBrown",
            "themeSeedGrey",
            "themeSeedBlueGrey",
            "themeSeedTeal",
            "themeSeedDeepPurple",
            "themeSeedLime",
            "themeSeedIndigo",
            "themeSeedCyan",
            "themeSeedDeepOrange"
    ));

    private static final Map<String, ThemeSet> THEMES = new LinkedHashMap<>();
    private static final Map<String, String> LEGACY_COLORS = new LinkedHashMap<>();

    static {
        addTheme("themeSeedDefault", R.color.themeSeedDefault, R.style.Theme_Seed_Default,
                R.style.Theme_Seed_Default_Dark, R.style.Theme_Seed_Default_Dark_Amoled);
        addTheme("themeSeedRed", R.color.themeSeedRed, R.style.Theme_Seed_Red,
                R.style.Theme_Seed_Red_Dark, R.style.Theme_Seed_Red_Dark_Amoled);
        addTheme("themeSeedPink", R.color.themeSeedPink, R.style.Theme_Seed_Pink,
                R.style.Theme_Seed_Pink_Dark, R.style.Theme_Seed_Pink_Dark_Amoled);
        addTheme("themeSeedPurple", R.color.themeSeedPurple, R.style.Theme_Seed_Purple,
                R.style.Theme_Seed_Purple_Dark, R.style.Theme_Seed_Purple_Dark_Amoled);
        addTheme("themeSeedBlue", R.color.themeSeedBlue, R.style.Theme_Seed_Blue,
                R.style.Theme_Seed_Blue_Dark, R.style.Theme_Seed_Blue_Dark_Amoled);
        addTheme("themeSeedGreen", R.color.themeSeedGreen, R.style.Theme_Seed_Green,
                R.style.Theme_Seed_Green_Dark, R.style.Theme_Seed_Green_Dark_Amoled);
        addTheme("themeSeedOrange", R.color.themeSeedOrange, R.style.Theme_Seed_Orange,
                R.style.Theme_Seed_Orange_Dark, R.style.Theme_Seed_Orange_Dark_Amoled);
        addTheme("themeSeedBrown", R.color.themeSeedBrown, R.style.Theme_Seed_Brown,
                R.style.Theme_Seed_Brown_Dark, R.style.Theme_Seed_Brown_Dark_Amoled);
        addTheme("themeSeedGrey", R.color.themeSeedGrey, R.style.Theme_Seed_Grey,
                R.style.Theme_Seed_Grey_Dark, R.style.Theme_Seed_Grey_Dark_Amoled);
        addTheme("themeSeedBlueGrey", R.color.themeSeedBlueGrey, R.style.Theme_Seed_Blue_Grey,
                R.style.Theme_Seed_Blue_Grey_Dark, R.style.Theme_Seed_Blue_Grey_Dark_Amoled);
        addTheme("themeSeedTeal", R.color.themeSeedTeal, R.style.Theme_Seed_Teal,
                R.style.Theme_Seed_Teal_Dark, R.style.Theme_Seed_Teal_Dark_Amoled);
        addTheme("themeSeedDeepPurple", R.color.themeSeedDeepPurple,
                R.style.Theme_Seed_Deep_Purple,
                R.style.Theme_Seed_Deep_Purple_Dark,
                R.style.Theme_Seed_Deep_Purple_Dark_Amoled);
        addTheme("themeSeedLime", R.color.themeSeedLime, R.style.Theme_Seed_Lime,
                R.style.Theme_Seed_Lime_Dark, R.style.Theme_Seed_Lime_Dark_Amoled);
        addTheme("themeSeedIndigo", R.color.themeSeedIndigo, R.style.Theme_Seed_Indigo,
                R.style.Theme_Seed_Indigo_Dark, R.style.Theme_Seed_Indigo_Dark_Amoled);
        addTheme("themeSeedCyan", R.color.themeSeedCyan, R.style.Theme_Seed_Cyan,
                R.style.Theme_Seed_Cyan_Dark, R.style.Theme_Seed_Cyan_Dark_Amoled);
        addTheme("themeSeedDeepOrange", R.color.themeSeedDeepOrange,
                R.style.Theme_Seed_Deep_Orange,
                R.style.Theme_Seed_Deep_Orange_Dark,
                R.style.Theme_Seed_Deep_Orange_Dark_Amoled);

        addLegacyColor("defaultPrimary", "themeSeedDefault");
        addLegacyColor("redPrimary", "themeSeedRed");
        addLegacyColor("pinkPrimary", "themeSeedPink");
        addLegacyColor("purplePrimary", "themeSeedPurple");
        addLegacyColor("bluePrimary", "themeSeedBlue");
        addLegacyColor("greenPrimary", "themeSeedGreen");
        addLegacyColor("orangePrimary", "themeSeedOrange");
        addLegacyColor("brownPrimary", "themeSeedBrown");
        addLegacyColor("greyPrimary", "themeSeedGrey");
        addLegacyColor("blue_greyPrimary", "themeSeedBlueGrey");
        addLegacyColor("tealPrimary", "themeSeedTeal");
        addLegacyColor("deep_purplePrimary", "themeSeedDeepPurple");
        addLegacyColor("limePrimary", "themeSeedLime");
        addLegacyColor("indigoPrimary", "themeSeedIndigo");
        addLegacyColor("cyanPrimary", "themeSeedCyan");
        addLegacyColor("deep_orangePrimary", "themeSeedDeepOrange");
    }

    private Themes() {
    }

    public static boolean isDarkTheme(Context context) {
        String themeMode = getThemeMode(context);
        if (THEME_MODE_DARK.equals(themeMode) || THEME_MODE_AMOLED.equals(themeMode)) {
            return true;
        }
        if (THEME_MODE_LIGHT.equals(themeMode)) {
            return false;
        }
        int currentNightMode = Resources.getSystem().getConfiguration().uiMode
                & Configuration.UI_MODE_NIGHT_MASK;
        return currentNightMode == Configuration.UI_MODE_NIGHT_YES;
    }

    public static boolean isAmoledBlack(Context context) {
        return THEME_MODE_AMOLED.equals(getThemeMode(context));
    }

    public static String getThemeMode(Context context) {
        if (!Prefs.contains(THEME_MODE_PREF_KEY, context)) {
            String migratedMode = migrateLegacyThemeMode(
                    Prefs.contains(DARK_THEME_PREF_KEY, context),
                    Prefs.getBoolean(DARK_THEME_PREF_KEY, false, context),
                    Prefs.getString(DARK_THEME_MODE_PREF_KEY, "dark", context));
            Prefs.saveString(THEME_MODE_PREF_KEY, migratedMode, context);
            return migratedMode;
        }

        String savedMode = Prefs.getString(THEME_MODE_PREF_KEY, THEME_MODE_SYSTEM, context);
        String normalizedMode = normalizeThemeMode(savedMode);
        if (!normalizedMode.equals(savedMode)) {
            Prefs.saveString(THEME_MODE_PREF_KEY, normalizedMode, context);
        }
        return normalizedMode;
    }

    public static void saveThemeMode(String themeMode, Context context) {
        Prefs.saveString(THEME_MODE_PREF_KEY, normalizeThemeMode(themeMode), context);
    }

    static String normalizeThemeMode(String themeMode) {
        if (THEME_MODE_SYSTEM.equals(themeMode)
                || THEME_MODE_LIGHT.equals(themeMode)
                || THEME_MODE_DARK.equals(themeMode)
                || THEME_MODE_AMOLED.equals(themeMode)) {
            return themeMode;
        }
        if ("black".equalsIgnoreCase(themeMode)
                || "Amoled black".equalsIgnoreCase(themeMode)) {
            return THEME_MODE_AMOLED;
        }
        return THEME_MODE_SYSTEM;
    }

    static String migrateLegacyThemeMode(boolean hasLegacyDarkPreference, boolean darkTheme,
                                         String darkThemeMode) {
        if (!hasLegacyDarkPreference) {
            return THEME_MODE_SYSTEM;
        }
        if (!darkTheme) {
            return THEME_MODE_LIGHT;
        }
        return "black".equalsIgnoreCase(darkThemeMode)
                || "Amoled black".equalsIgnoreCase(darkThemeMode)
                ? THEME_MODE_AMOLED : THEME_MODE_DARK;
    }

    public static Theme getTheme(Context context, boolean darkTheme, boolean amoledDarkTheme) {
        return getTheme(getThemeColor(context), darkTheme, amoledDarkTheme);
    }

    public static Theme getTheme(String color, boolean darkTheme, boolean amoledDarkTheme) {
        ThemeSet themeSet = THEMES.get(color);
        if (themeSet == null) {
            color = DEFAULT_THEME_COLOR;
            themeSet = THEMES.get(DEFAULT_THEME_COLOR);
        }

        int style = darkTheme
                ? amoledDarkTheme ? themeSet.mAmoledStyle : themeSet.mDarkStyle
                : themeSet.mLightStyle;
        return new Theme(color, style);
    }

    public static void saveThemeColor(String color, Context context) {
        Prefs.saveString(THEME_PREF_KEY,
                THEMES.containsKey(color) ? color : DEFAULT_THEME_COLOR, context);
    }

    public static String getThemeColor(Context context) {
        String savedTheme = Prefs.getString(THEME_PREF_KEY, DEFAULT_THEME_COLOR, context);
        String color = normalizeThemeColor(savedTheme);
        if (!color.equals(savedTheme)) {
            Prefs.saveString(THEME_PREF_KEY, color, context);
        }
        return color;
    }

    @ColorRes
    public static int getColor(String color) {
        ThemeSet themeSet = THEMES.get(color);
        return themeSet != null ? themeSet.mColor : R.color.themeSeedDefault;
    }

    static String normalizeThemeColor(String savedTheme) {
        if (THEMES.containsKey(savedTheme)) {
            return savedTheme;
        }

        int separator = savedTheme.indexOf(';');
        String legacyPrimary = separator >= 0 ? savedTheme.substring(0, separator) : savedTheme;
        String migratedColor = LEGACY_COLORS.get(legacyPrimary);
        return migratedColor != null ? migratedColor : DEFAULT_THEME_COLOR;
    }

    private static void addTheme(String color, @ColorRes int colorResource,
                                 @StyleRes int lightStyle, @StyleRes int darkStyle,
                                 @StyleRes int amoledStyle) {
        THEMES.put(color, new ThemeSet(colorResource, lightStyle, darkStyle, amoledStyle));
    }

    private static void addLegacyColor(String legacyColor, String color) {
        LEGACY_COLORS.put(legacyColor, color);
    }
}
