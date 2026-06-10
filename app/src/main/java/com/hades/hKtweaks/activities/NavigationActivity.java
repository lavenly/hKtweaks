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
package com.hades.hKtweaks.activities;

import android.content.Intent;
import android.content.pm.ShortcutInfo;
import android.content.pm.ShortcutManager;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.Icon;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Parcel;
import android.os.Parcelable;
import android.os.SystemClock;
import android.view.Menu;
import android.view.MenuItem;
import android.view.SubMenu;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.core.content.ContextCompat;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.viewpager2.adapter.FragmentStateAdapter;
import androidx.viewpager2.widget.ViewPager2;

import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.navigation.NavigationView;
import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;

import com.hades.hKtweaks.R;
import com.hades.hKtweaks.fragments.BaseFragment;
import com.hades.hKtweaks.fragments.kernel.BatteryFragment;
import com.hades.hKtweaks.fragments.kernel.BoefflaWakelockFragment;
import com.hades.hKtweaks.fragments.kernel.BusCamFragment;
import com.hades.hKtweaks.fragments.kernel.BusDispFragment;
import com.hades.hKtweaks.fragments.kernel.BusIntFragment;
import com.hades.hKtweaks.fragments.kernel.BusMifFragment;
import com.hades.hKtweaks.fragments.kernel.CPUVoltageCl1Fragment;
import com.hades.hKtweaks.fragments.kernel.CPUFragment;
import com.hades.hKtweaks.fragments.kernel.CPUHotplugFragment;
import com.hades.hKtweaks.fragments.kernel.CPUVoltageCl0Fragment;
import com.hades.hKtweaks.fragments.kernel.EntropyFragment;
import com.hades.hKtweaks.fragments.kernel.GPUFragment;
import com.hades.hKtweaks.fragments.kernel.DvfsFragment;
import com.hades.hKtweaks.fragments.kernel.HmpFragment;
import com.hades.hKtweaks.fragments.kernel.IOFragment;
import com.hades.hKtweaks.fragments.kernel.KSMFragment;
import com.hades.hKtweaks.fragments.kernel.LEDFragment;
import com.hades.hKtweaks.fragments.kernel.LMKFragment;
import com.hades.hKtweaks.fragments.kernel.WakelockFragment;
import com.hades.hKtweaks.fragments.kernel.MiscFragment;
import com.hades.hKtweaks.fragments.kernel.ScreenFragment;
import com.hades.hKtweaks.fragments.kernel.SoundFragment;
import com.hades.hKtweaks.fragments.kernel.SpectrumFragment;
import com.hades.hKtweaks.fragments.kernel.ThermalFragment;
import com.hades.hKtweaks.fragments.kernel.VMFragment;
import com.hades.hKtweaks.fragments.kernel.WakeFragment;
import com.hades.hKtweaks.fragments.other.AboutFragment;
import com.hades.hKtweaks.fragments.other.DonationFragment;
import com.hades.hKtweaks.fragments.other.SettingsFragment;
import com.hades.hKtweaks.fragments.statistics.DeviceFragment;
import com.hades.hKtweaks.fragments.statistics.InputsFragment;
import com.hades.hKtweaks.fragments.statistics.MemoryFragment;
import com.hades.hKtweaks.fragments.statistics.OverallFragment;
import com.hades.hKtweaks.fragments.tools.BackupFragment;
import com.hades.hKtweaks.fragments.tools.BuildpropFragment;
import com.hades.hKtweaks.fragments.tools.InitdFragment;
import com.hades.hKtweaks.fragments.tools.OnBootFragment;
import com.hades.hKtweaks.fragments.tools.ProfileFragment;
import com.hades.hKtweaks.fragments.tools.RecoveryFragment;
import com.hades.hKtweaks.fragments.tools.customcontrols.CustomControlsFragment;
import com.hades.hKtweaks.fragments.tools.downloads.DownloadsFragment;
import com.hades.hKtweaks.services.monitor.Monitor;
import com.hades.hKtweaks.utils.AppSettings;
import com.hades.hKtweaks.utils.Device;
import com.hades.hKtweaks.utils.Utils;
import com.hades.hKtweaks.utils.kernel.battery.Battery;
import com.hades.hKtweaks.utils.kernel.bus.VoltageCam;
import com.hades.hKtweaks.utils.kernel.bus.VoltageDisp;
import com.hades.hKtweaks.utils.kernel.bus.VoltageInt;
import com.hades.hKtweaks.utils.kernel.bus.VoltageMif;
import com.hades.hKtweaks.utils.kernel.cpuhotplug.Hotplug;
import com.hades.hKtweaks.utils.kernel.cpuvoltage.VoltageCl0;
import com.hades.hKtweaks.utils.kernel.cpuvoltage.VoltageCl1;
import com.hades.hKtweaks.utils.kernel.entropy.Entropy;
import com.hades.hKtweaks.utils.kernel.gpu.GPU;
import com.hades.hKtweaks.utils.kernel.hmp.Hmp;
import com.hades.hKtweaks.utils.kernel.dvfs.Dvfs;
import com.hades.hKtweaks.utils.kernel.io.IO;
import com.hades.hKtweaks.utils.kernel.ksm.KSM;
import com.hades.hKtweaks.utils.kernel.led.LED;
import com.hades.hKtweaks.utils.kernel.lmk.LMK;
import com.hades.hKtweaks.utils.kernel.screen.Screen;
import com.hades.hKtweaks.utils.kernel.sound.Sound;
import com.hades.hKtweaks.utils.kernel.spectrum.Spectrum;
import com.hades.hKtweaks.utils.kernel.thermal.Thermal;
import com.hades.hKtweaks.utils.kernel.wake.Wake;
import com.hades.hKtweaks.utils.kernel.boefflawakelock.BoefflaWakelock;
import com.hades.hKtweaks.utils.kernel.wakelock.Wakelock;
import com.hades.hKtweaks.utils.root.RootUtils;
import com.hades.hKtweaks.utils.tools.Backup;
import com.hades.hKtweaks.utils.tools.SupportedDownloads;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.PriorityQueue;

public class NavigationActivity extends BaseActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    private static final String PACKAGE = NavigationActivity.class.getCanonicalName();
    private static final long STARTUP_ROOT_COMMAND_TIMEOUT_MS = 10_000;
    public static final String INTENT_SECTION = PACKAGE + ".INTENT.SECTION";

    private ArrayList<NavigationFragment> mFragments = new ArrayList<>();
    private final Map<Integer, Class<? extends Fragment>> mActualFragments = new LinkedHashMap<>();
    private final ArrayList<Integer> mTabIds = new ArrayList<>();
    private final Map<Integer, Fragment> mPagerFragments = new LinkedHashMap<>();

    private DrawerLayout mDrawer;
    private NavigationView mNavigationView;
    private TabLayout mNavigationTabs;
    private ViewPager2 mNavigationPager;
    private NavigationPagerAdapter mPagerAdapter;
    private View mNavigationContent;
    private View mNavigationLoading;
    private long mLastTimeBackbuttonPressed;
    private boolean mUseTopTabs;

    private int mSelection;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mUseTopTabs = AppSettings.isTopTabsNavigation(this);
        setContentView(mUseTopTabs
                ? R.layout.activity_navigation
                : R.layout.activity_navigation_drawer);
        mNavigationContent = findViewById(R.id.navigation_content);
        mNavigationLoading = findViewById(R.id.navigation_loading);

        if (savedInstanceState == null) {
            new FragmentLoader(this).execute();
        } else {
            mFragments = savedInstanceState.getParcelableArrayList("fragments");
            init(savedInstanceState);
        }
    }

    private static class FragmentLoader extends AsyncTask<Void, Void, Void> {

        private final WeakReference<NavigationActivity> mRefActivity;

        private FragmentLoader(NavigationActivity activity) {
            mRefActivity = new WeakReference<>(activity);
        }

        @Override
        protected Void doInBackground(Void... voids) {
            NavigationActivity activity = mRefActivity.get();
            if (activity == null) return null;
            RootUtils.setCommandTimeoutForCurrentThread(STARTUP_ROOT_COMMAND_TIMEOUT_MS);
            try {
                activity.initFragments();
            } finally {
                RootUtils.clearCommandTimeoutForCurrentThread();
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            NavigationActivity activity = mRefActivity.get();
            if (activity == null) return;
            activity.init(null);
        }
    }

    private void initFragments() {
        mFragments.clear();
        mFragments.add(new NavigationActivity.NavigationFragment(R.string.statistics));
        mFragments.add(new NavigationActivity.NavigationFragment(R.string.overall, OverallFragment.class, R.drawable.ic_chart));
        mFragments.add(new NavigationActivity.NavigationFragment(R.string.device, DeviceFragment.class, R.drawable.ic_device));
        if (Device.MemInfo.getInstance().getItems().size() > 0) {
            mFragments.add(new NavigationActivity.NavigationFragment(R.string.memory, MemoryFragment.class, R.drawable.ic_save));
        }
        if (Device.Input.getInstance().supported()) {
            mFragments.add(new NavigationActivity.NavigationFragment(R.string.inputs, InputsFragment.class, R.drawable.ic_keyboard));
        }
        mFragments.add(new NavigationActivity.NavigationFragment(R.string.kernel));
        mFragments.add(new NavigationActivity.NavigationFragment(R.string.cpu, CPUFragment.class, R.drawable.ic_cpu));
        if (Hotplug.supported()) {
            mFragments.add(new NavigationActivity.NavigationFragment(R.string.cpu_hotplug, CPUHotplugFragment.class, R.drawable.ic_switch));
        }
        if (Hmp.getInstance().supported()) {
            mFragments.add(new NavigationActivity.NavigationFragment(R.string.hmp, HmpFragment.class, R.drawable.ic_cpu));
        }
        if (Thermal.supported()) {
            mFragments.add(new NavigationActivity.NavigationFragment(R.string.thermal, ThermalFragment.class, R.drawable.ic_temperature));
        }
        if (GPU.supported()) {
            mFragments.add(new NavigationActivity.NavigationFragment(R.string.gpu, GPUFragment.class, R.drawable.ic_gpu));
        }
        if (Dvfs.supported()) {
            mFragments.add(new NavigationActivity.NavigationFragment(R.string.dvfs_nav, DvfsFragment.class, R.drawable.ic_dvfs));
        }
        if (Screen.supported()) {
            mFragments.add(new NavigationActivity.NavigationFragment(R.string.screen, ScreenFragment.class, R.drawable.ic_display));
        }
        if (Wake.supported()) {
            mFragments.add(new NavigationActivity.NavigationFragment(R.string.gestures, WakeFragment.class, R.drawable.ic_touch));
        }
        if (Sound.getInstance().supported()) {
            mFragments.add(new NavigationActivity.NavigationFragment(R.string.sound, SoundFragment.class, R.drawable.ic_music));
        }
        if (Spectrum.supported(this)) {
            mFragments.add(new NavigationActivity.NavigationFragment(R.string.spectrum, SpectrumFragment.class, R.drawable.ic_spectrum_logo));
        }
        if (Battery.getInstance(this).supported()) {
            mFragments.add(new NavigationActivity.NavigationFragment(R.string.battery, BatteryFragment.class, R.drawable.ic_battery));
        }
        if (LED.getInstance().supported()) {
            mFragments.add(new NavigationActivity.NavigationFragment(R.string.led, LEDFragment.class, R.drawable.ic_led));
        }
        if (IO.getInstance().supported()) {
            mFragments.add(new NavigationActivity.NavigationFragment(R.string.io_scheduler, IOFragment.class, R.drawable.ic_sdcard));
        }
        if (KSM.getInstance().supported()) {
            if (KSM.getInstance().isUKSM()) {
                mFragments.add(new NavigationActivity.NavigationFragment(R.string.uksm_name, KSMFragment.class, R.drawable.ic_merge));
            } else {
                mFragments.add(new NavigationActivity.NavigationFragment(R.string.ksm_name, KSMFragment.class, R.drawable.ic_merge));
            }
        }
        if (LMK.supported()) {
            mFragments.add(new NavigationActivity.NavigationFragment(R.string.lmk, LMKFragment.class, R.drawable.ic_stackoverflow));
        }
        if (Wakelock.supported()) {
            mFragments.add(new NavigationActivity.NavigationFragment(R.string.wakelock_nav, WakelockFragment.class, R.drawable.ic_unlock));
        }
        if (BoefflaWakelock.supported()) {
            mFragments.add(new NavigationActivity.NavigationFragment(R.string.boeffla_wakelock, BoefflaWakelockFragment.class, R.drawable.ic_unlock));
        }
        mFragments.add(new NavigationActivity.NavigationFragment(R.string.virtual_memory, VMFragment.class, R.drawable.ic_server));
        if (Entropy.supported()) {
            mFragments.add(new NavigationActivity.NavigationFragment(R.string.entropy, EntropyFragment.class, R.drawable.ic_numbers));
        }
        mFragments.add(new NavigationActivity.NavigationFragment(R.string.misc, MiscFragment.class, R.drawable.ic_clear));
        mFragments.add(new NavigationActivity.NavigationFragment(R.string.voltage_control));
        if (VoltageCl1.supported()) {
            mFragments.add(new NavigationActivity.NavigationFragment(R.string.cpucl1_voltage, CPUVoltageCl1Fragment.class, R.drawable.ic_bolt));
        }
        if (VoltageCl0.supported()) {
            mFragments.add(new NavigationActivity.NavigationFragment(R.string.cpucl0_voltage, CPUVoltageCl0Fragment.class, R.drawable.ic_bolt));
        }
        if (VoltageMif.supported()) {
            mFragments.add(new NavigationActivity.NavigationFragment(R.string.busMif_voltage, BusMifFragment.class, R.drawable.ic_bolt));
        }
        if (VoltageInt.supported()) {
            mFragments.add(new NavigationActivity.NavigationFragment(R.string.busInt_voltage, BusIntFragment.class, R.drawable.ic_bolt));
        }
        if (VoltageDisp.supported()) {
            mFragments.add(new NavigationActivity.NavigationFragment(R.string.busDisp_voltage, BusDispFragment.class, R.drawable.ic_bolt));
        }
        if (VoltageCam.supported()) {
            mFragments.add(new NavigationActivity.NavigationFragment(R.string.busCam_voltage, BusCamFragment.class, R.drawable.ic_bolt));
        }
        mFragments.add(new NavigationActivity.NavigationFragment(R.string.tools));
        //mFragments.add(new NavigationActivity.NavigationFragment(R.string.data_sharing, DataSharingFragment.class, R.drawable.ic_database));
        mFragments.add(new NavigationActivity.NavigationFragment(R.string.custom_controls, CustomControlsFragment.class, R.drawable.ic_console));

        SupportedDownloads supportedDownloads = new SupportedDownloads(this);
        if (supportedDownloads.getLink() != null) {
            mFragments.add(new NavigationActivity.NavigationFragment(R.string.downloads, DownloadsFragment.class, R.drawable.ic_download));
        }
        if (Backup.hasBackup()) {
            mFragments.add(new NavigationActivity.NavigationFragment(R.string.backup, BackupFragment.class, R.drawable.ic_restore));
        }
        mFragments.add(new NavigationActivity.NavigationFragment(R.string.build_prop_editor, BuildpropFragment.class, R.drawable.ic_edit));
        mFragments.add(new NavigationActivity.NavigationFragment(R.string.profile, ProfileFragment.class, R.drawable.ic_layers));
        mFragments.add(new NavigationActivity.NavigationFragment(R.string.recovery, RecoveryFragment.class, R.drawable.ic_security));
        mFragments.add(new NavigationActivity.NavigationFragment(R.string.initd, InitdFragment.class, R.drawable.ic_shell));
        mFragments.add(new NavigationActivity.NavigationFragment(R.string.on_boot, OnBootFragment.class, R.drawable.ic_start));
        mFragments.add(new NavigationActivity.NavigationFragment(R.string.other));
        mFragments.add(new NavigationActivity.NavigationFragment(R.string.settings, SettingsFragment.class, R.drawable.ic_settings));
        mFragments.add(new NavigationActivity.NavigationFragment(R.string.donation_title, DonationFragment.class, R.drawable.ic_donation));
        mFragments.add(new NavigationActivity.NavigationFragment(R.string.about, AboutFragment.class, R.drawable.ic_about));
        //mFragments.add(new NavigationActivity.NavigationFragment(R.string.contributors, ContributorsFragment.class, R.drawable.ic_people));
        //mFragments.add(new NavigationActivity.NavigationFragment(R.string.help, HelpFragment.class, R.drawable.ic_help));
    }

    private void init(Bundle savedInstanceState) {
        MaterialToolbar toolbar = getToolBar();
        setSupportActionBar(toolbar);
        if (mUseTopTabs) {
            toolbar.setTitle(R.string.app_name);
            initTopTabs();
        } else {
            initDrawer(toolbar);
        }

        if (savedInstanceState != null) {
            mSelection = savedInstanceState.getInt(INTENT_SECTION);
        }

        appendFragments(false);
        String section = getIntent().getStringExtra(INTENT_SECTION);
        if (section != null) {
            for (Map.Entry<Integer, Class<? extends Fragment>> entry : mActualFragments.entrySet()) {
                Class<? extends Fragment> fragmentClass = entry.getValue();
                if (fragmentClass != null && fragmentClass.getCanonicalName().equals(section)) {
                    mSelection = entry.getKey();
                    break;
                }
            }
            getIntent().removeExtra(INTENT_SECTION);
        }

        if (mSelection == 0 || mActualFragments.get(mSelection) == null) {
            mSelection = firstTab();
        }
        onItemSelected(mSelection, false);

        if (AppSettings.isDataSharing(this)) {
            startService(new Intent(this, Monitor.class));
        }

        mNavigationContent.setVisibility(View.VISIBLE);
        mNavigationLoading.setVisibility(View.GONE);
    }

    private int firstTab() {
        for (Map.Entry<Integer, Class<? extends Fragment>> entry : mActualFragments.entrySet()) {
            if (entry.getValue() != null) {
                return entry.getKey();
            }
        }
        return 0;
    }

    public void appendFragments() {
        appendFragments(true);
    }

    private void appendFragments(boolean setShortcuts) {
        mActualFragments.clear();
        mTabIds.clear();

        SubMenu lastSubMenu = null;
        Menu menu = null;
        if (!mUseTopTabs) {
            menu = mNavigationView.getMenu();
            menu.clear();
        }
        for (NavigationFragment navigationFragment : mFragments) {
            Class<? extends Fragment> fragmentClass = navigationFragment.mFragmentClass;
            int id = navigationFragment.mId;

            if (fragmentClass == null) {
                if (!mUseTopTabs) {
                    lastSubMenu = menu.addSubMenu(id);
                }
                mActualFragments.put(id, null);
            } else if (AppSettings.isFragmentEnabled(fragmentClass, this)) {
                if (mUseTopTabs) {
                    mTabIds.add(id);
                } else {
                    Drawable drawable = ContextCompat.getDrawable(this,
                            AppSettings.isSectionIcons(this) && navigationFragment.mDrawable != 0
                                    ? navigationFragment.mDrawable
                                    : R.drawable.ic_blank);
                    MenuItem menuItem = lastSubMenu == null
                            ? menu.add(0, id, 0, id)
                            : lastSubMenu.add(0, id, 0, id);
                    menuItem.setIcon(drawable);
                    menuItem.setCheckable(true);
                }
                mActualFragments.put(id, fragmentClass);
            }
        }
        if (mUseTopTabs && mPagerAdapter != null) {
            mPagerFragments.keySet().removeIf(section -> !mTabIds.contains(section));
            mPagerAdapter.notifyDataSetChanged();
        }

        if (mActualFragments.get(mSelection) == null) {
            mSelection = firstTab();
        }
        selectNavigationSurface(mSelection);

        if (setShortcuts) {
            setShortcuts();
        }
    }

    private void initTopTabs() {
        mNavigationTabs = findViewById(R.id.navigation_tabs);
        mNavigationPager = findViewById(R.id.navigation_pager);
        mPagerAdapter = new NavigationPagerAdapter();
        mNavigationPager.setAdapter(mPagerAdapter);
        new TabLayoutMediator(mNavigationTabs, mNavigationPager,
                (tab, position) -> bindTab(tab, position)).attach();
        mNavigationPager.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
            @Override
            public void onPageSelected(int position) {
                if (position >= 0 && position < mTabIds.size()) {
                    int section = mTabIds.get(position);
                    if (section != mSelection) {
                        onItemSelected(section, true);
                    }
                }
            }
        });
    }

    private void initDrawer(MaterialToolbar toolbar) {
        mDrawer = findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, mDrawer, toolbar, 0, 0);
        mDrawer.addDrawerListener(toggle);
        toggle.syncState();

        mNavigationView = findViewById(R.id.nav_view);
        mNavigationView.setNavigationItemSelectedListener(this);
        mNavigationView.setOnFocusChangeListener((v, hasFocus) -> {
            if (hasFocus) {
                v.clearFocus();
            }
        });
    }

    private void bindTab(TabLayout.Tab tab, int position) {
        if (position < 0 || position >= mTabIds.size()) return;
        int id = mTabIds.get(position);
        tab.setText(id);
        NavigationFragment navigationFragment = findNavigationFragmentById(id);
        if (navigationFragment != null
                && AppSettings.isSectionIcons(this)
                && navigationFragment.mDrawable != 0) {
            tab.setIcon(navigationFragment.mDrawable);
        } else {
            tab.setIcon(null);
        }
    }

    private void selectNavigationSurface(int section) {
        if (mUseTopTabs) {
            int position = mTabIds.indexOf(section);
            if (position >= 0 && mNavigationPager.getCurrentItem() != position) {
                mNavigationPager.setCurrentItem(position, false);
            }
        } else {
            mNavigationView.setCheckedItem(section);
        }
    }

    private NavigationFragment findNavigationFragmentById(int id) {
        for (NavigationFragment navigationFragment : mFragments) {
            if (navigationFragment.mId == id) {
                return navigationFragment;
            }
        }
        return null;
    }

    private NavigationFragment findNavigationFragmentByClass(Class<? extends Fragment> fragmentClass) {
        if (fragmentClass == null) return null;
        for (NavigationFragment navigationFragment : mFragments) {
            if (fragmentClass == navigationFragment.mFragmentClass) {
                return navigationFragment;
            }
        }
        return null;
    }

    private void setShortcuts() {
        PriorityQueue<Class<? extends Fragment>> queue = new PriorityQueue<>(
                (o1, o2) -> {
                    int opened1 = AppSettings.getFragmentOpened(o1, this);
                    int opened2 = AppSettings.getFragmentOpened(o2, this);
                    return opened2 - opened1;
                });

        for (Map.Entry<Integer, Class<? extends Fragment>> entry : mActualFragments.entrySet()) {
            Class<? extends Fragment> fragmentClass = entry.getValue();
            if (fragmentClass == null || fragmentClass == SettingsFragment.class) continue;

            queue.offer(fragmentClass);
        }

        List<ShortcutInfo> shortcutInfos = new ArrayList<>();
        ShortcutManager shortcutManager = getSystemService(ShortcutManager.class);
        shortcutManager.removeAllDynamicShortcuts();
        for (int i = 0; i < 4; i++) {
            NavigationFragment fragment = findNavigationFragmentByClass(queue.poll());
            if (fragment == null || fragment.mFragmentClass == null) continue;
            Intent intent = new Intent(this, MainActivity.class);
            intent.setAction(Intent.ACTION_VIEW);
            intent.putExtra(INTENT_SECTION, fragment.mFragmentClass.getCanonicalName());
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);

            ShortcutInfo shortcut = new ShortcutInfo.Builder(this,
                    fragment.mFragmentClass.getSimpleName())
                    .setShortLabel(getString(fragment.mId))
                    .setLongLabel(Utils.strFormat(getString(R.string.open), getString(fragment.mId)))
                    .setIcon(Icon.createWithResource(this, fragment.mDrawable == 0 ?
                            R.drawable.ic_blank : fragment.mDrawable))
                    .setIntent(intent)
                    .build();
            shortcutInfos.add(shortcut);
        }
        shortcutManager.setDynamicShortcuts(shortcutInfos);
    }

    public ArrayList<NavigationFragment> getFragments() {
        return mFragments;
    }

    public Map<Integer, Class<? extends Fragment>> getActualFragments() {
        return mActualFragments;
    }

    public boolean usesFixedNavigationAppBar() {
        return mUseTopTabs;
    }

    @Override
    public void onBackPressed() {
        if (mDrawer != null && mDrawer.isDrawerOpen(GravityCompat.START)) {
            mDrawer.closeDrawer(GravityCompat.START);
            return;
        }

        Fragment currentFragment = getCurrentFragment();
        if (!(currentFragment instanceof BaseFragment)
                || !((BaseFragment) currentFragment).onBackPressed()) {
            long currentTime = SystemClock.elapsedRealtime();
            if (currentTime - mLastTimeBackbuttonPressed > 2000) {
                mLastTimeBackbuttonPressed = currentTime;
                Utils.toast(R.string.press_back_again_exit, this);
            } else {
                super.onBackPressed();
            }
        }
    }

    @Override
    public void finish() {
        super.finish();
        if (!mUseTopTabs) {
            FragmentManager fragmentManager = getSupportFragmentManager();
            FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
            for (int id : mActualFragments.keySet()) {
                Fragment fragment = fragmentManager.findFragmentByTag(id + "_key");
                if (fragment != null) {
                    fragmentTransaction.remove(fragment);
                }
            }
            fragmentTransaction.commitAllowingStateLoss();
        }
        RootUtils.closeSU();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

        outState.putParcelableArrayList("fragments", mFragments);
        outState.putInt(INTENT_SECTION, mSelection);
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        onItemSelected(item.getItemId(), true);
        return true;
    }

    private void onItemSelected(final int res, boolean saveOpened) {
        if (mActualFragments.get(res) == null) return;

        mSelection = res;
        Class<? extends Fragment> fragmentClass = mActualFragments.get(res);
        if (fragmentClass == null) return;
        selectNavigationSurface(res);

        if (saveOpened) {
            AppSettings.saveFragmentOpened(fragmentClass,
                    AppSettings.getFragmentOpened(fragmentClass, this) + 1,
                    this);
        }
        setShortcuts();

        if (mUseTopTabs) return;

        if (mDrawer != null) {
            mDrawer.closeDrawer(GravityCompat.START);
        }
        getSupportActionBar().setTitle(getString(res));
        final Fragment fragment = getFragment(res);
        if (fragment == null) return;
        getSupportFragmentManager().beginTransaction()
                .setCustomAnimations(
                        R.anim.m3_expressive_fade_enter,
                        R.anim.m3_expressive_fade_exit)
                .replace(R.id.content_frame, fragment, res + "_key")
                .commitAllowingStateLoss();
    }

    private Fragment getFragment(int res) {
        FragmentManager fragmentManager = getSupportFragmentManager();
        Fragment fragment = fragmentManager.findFragmentByTag(res + "_key");
        if (fragment == null && mActualFragments.containsKey(res)) {
            fragment = Fragment.instantiate(this,
                    mActualFragments.get(res).getCanonicalName());
        }
        return fragment;
    }

    private Fragment getCurrentFragment() {
        if (mUseTopTabs) {
            Fragment fragment = mPagerFragments.get(mSelection);
            if (fragment != null) {
                return fragment;
            }
            if (mPagerAdapter != null && mNavigationPager != null
                    && mNavigationPager.getCurrentItem() < mPagerAdapter.getItemCount()) {
                long itemId = mPagerAdapter.getItemId(mNavigationPager.getCurrentItem());
                fragment = getSupportFragmentManager().findFragmentByTag("f" + itemId);
                if (fragment != null) {
                    return fragment;
                }
            }
            return null;
        }
        return getFragment(mSelection);
    }

    private class NavigationPagerAdapter extends FragmentStateAdapter {

        NavigationPagerAdapter() {
            super(NavigationActivity.this);
        }

        @NonNull
        @Override
        public Fragment createFragment(int position) {
            int section = mTabIds.get(position);
            Fragment fragment = Fragment.instantiate(NavigationActivity.this,
                    mActualFragments.get(section).getCanonicalName());
            mPagerFragments.put(section, fragment);
            return fragment;
        }

        @Override
        public int getItemCount() {
            return mTabIds.size();
        }

        @Override
        public long getItemId(int position) {
            return mTabIds.get(position);
        }

        @Override
        public boolean containsItem(long itemId) {
            return mTabIds.contains((int) itemId);
        }
    }

    public static class NavigationFragment implements Parcelable {

        public int mId;
        public Class<? extends Fragment> mFragmentClass;
        private final int mDrawable;

        NavigationFragment(int id) {
            this(id, null, 0);
        }

        NavigationFragment(int id, Class<? extends Fragment> fragment, int drawable) {
            mId = id;
            mFragmentClass = fragment;
            mDrawable = drawable;
        }

        NavigationFragment(Parcel parcel) {
            mId = parcel.readInt();
            mFragmentClass = (Class<? extends Fragment>) parcel.readSerializable();
            mDrawable = parcel.readInt();
        }

        @Override
        public String toString() {
            return String.valueOf(mId);
        }

        @Override
        public int describeContents() {
            return 0;
        }

        @Override
        public void writeToParcel(Parcel dest, int flags) {
            dest.writeInt(mId);
            dest.writeSerializable(mFragmentClass);
            dest.writeInt(mDrawable);
        }

        public static final Creator CREATOR = new Creator<NavigationFragment>() {
            @Override
            public NavigationFragment createFromParcel(Parcel source) {
                return new NavigationFragment(source);
            }

            @Override
            public NavigationFragment[] newArray(int size) {
                return new NavigationFragment[0];
            }
        };
    }

}
