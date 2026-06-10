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
}
