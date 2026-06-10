package com.hades.hKtweaks.utils;

import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class ThemesTest {

    @Test
    public void currentSeedIsPreserved() {
        assertEquals("themeSeedTeal", Themes.normalizeThemeColor("themeSeedTeal"));
    }

    @Test
    public void legacyPairKeepsPrimaryChoice() {
        assertEquals("themeSeedDeepPurple",
                Themes.normalizeThemeColor("deep_purplePrimary;orangeAccent"));
    }

    @Test
    public void legacyAccentDoesNotChangeSeed() {
        assertEquals("themeSeedDefault",
                Themes.normalizeThemeColor("defaultPrimary;redAccent"));
        assertEquals("themeSeedDefault",
                Themes.normalizeThemeColor("defaultPrimary;cyanAccent"));
    }

    @Test
    public void unknownThemeFallsBackToDefaultSeed() {
        assertEquals("themeSeedDefault", Themes.normalizeThemeColor("unknown"));
    }

    @Test
    public void currentThemeModesArePreserved() {
        assertEquals(Themes.THEME_MODE_SYSTEM, Themes.normalizeThemeMode("system"));
        assertEquals(Themes.THEME_MODE_LIGHT, Themes.normalizeThemeMode("light"));
        assertEquals(Themes.THEME_MODE_DARK, Themes.normalizeThemeMode("dark"));
        assertEquals(Themes.THEME_MODE_AMOLED, Themes.normalizeThemeMode("amoled"));
    }

    @Test
    public void legacyAmoledModeIsNormalized() {
        assertEquals(Themes.THEME_MODE_AMOLED, Themes.normalizeThemeMode("black"));
        assertEquals(Themes.THEME_MODE_AMOLED, Themes.normalizeThemeMode("Amoled black"));
    }

    @Test
    public void legacyThemePreferencesAreMigrated() {
        assertEquals(Themes.THEME_MODE_SYSTEM,
                Themes.migrateLegacyThemeMode(false, false, "dark"));
        assertEquals(Themes.THEME_MODE_LIGHT,
                Themes.migrateLegacyThemeMode(true, false, "black"));
        assertEquals(Themes.THEME_MODE_DARK,
                Themes.migrateLegacyThemeMode(true, true, "dark"));
        assertEquals(Themes.THEME_MODE_AMOLED,
                Themes.migrateLegacyThemeMode(true, true, "black"));
    }
}
