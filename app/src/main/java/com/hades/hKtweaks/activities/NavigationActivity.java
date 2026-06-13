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

import android.content.Context;
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
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.SubMenu;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.core.content.ContextCompat;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.DiffUtil;
import androidx.viewpager2.adapter.FragmentStateAdapter;
import androidx.viewpager2.widget.ViewPager2;

import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.color.MaterialColors;
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
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class NavigationActivity extends BaseActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    private static final String PACKAGE = NavigationActivity.class.getCanonicalName();
    private static final long STARTUP_ROOT_COMMAND_TIMEOUT_MS = 10_000;
    private static final long OPTIONAL_FRAGMENT_SCAN_DELAY_MS = 500;
    private static final String STATE_FRAGMENTS_COMPLETE = "fragments_complete";
    public static final String INTENT_SECTION = PACKAGE + ".INTENT.SECTION";

    private static ArrayList<NavigationFragment> sDetectedFragments;

    private ArrayList<NavigationFragment> mFragments = new ArrayList<>();
    private Map<Integer, Class<? extends Fragment>> mActualFragments = new LinkedHashMap<>();
    private List<Integer> mTabIds = new ArrayList<>();
    private final Map<Integer, NavigationPageFragment> mPagerFragments = new LinkedHashMap<>();

    private DrawerLayout mDrawer;
    private NavigationView mNavigationView;
    private TabLayout mNavigationTabs;
    private ViewPager2 mNavigationPager;
    private NavigationPagerAdapter mPagerAdapter;
    private TabLayoutMediator mNavigationTabMediator;
    private ViewPager2.OnPageChangeCallback mNavigationPageChangeCallback;
    private View mNavigationContent;
    private View mNavigationLoading;
    private long mLastTimeBackbuttonPressed;
    private boolean mUseTopTabs;
    private boolean mUpdatingNavigation;
    private boolean mFragmentsComplete;
    private ArrayList<NavigationFragment> mPendingFragments;
    private final ExecutorService mShortcutExecutor = Executors.newSingleThreadExecutor();
    private volatile int mShortcutGeneration;

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
            if (sDetectedFragments == null) {
                mFragments = createInitialFragments();
            } else {
                mFragments = new ArrayList<>(sDetectedFragments);
                mFragmentsComplete = true;
            }
            init(null);
            if (!mFragmentsComplete) {
                scheduleOptionalFragmentScan();
            }
        } else {
            mFragments = savedInstanceState.getParcelableArrayList("fragments");
            if (mFragments == null || mFragments.isEmpty()) {
                mFragments = createInitialFragments();
            }
            mFragmentsComplete = savedInstanceState.getBoolean(
                    STATE_FRAGMENTS_COMPLETE, true);
            init(savedInstanceState);
            if (!mFragmentsComplete) {
                scheduleOptionalFragmentScan();
            }
        }
    }

    private void scheduleOptionalFragmentScan() {
        mNavigationContent.postDelayed(() -> {
            if (!isFinishing() && !isDestroyed() && !mFragmentsComplete) {
                new FragmentLoader(this).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
            }
        }, OPTIONAL_FRAGMENT_SCAN_DELAY_MS);
    }

    private static class FragmentLoader
            extends AsyncTask<Void, Void, ArrayList<NavigationFragment>> {

        private final WeakReference<NavigationActivity> mRefActivity;

        private FragmentLoader(NavigationActivity activity) {
            mRefActivity = new WeakReference<>(activity);
        }

        @Override
        protected ArrayList<NavigationFragment> doInBackground(Void... voids) {
            NavigationActivity activity = mRefActivity.get();
            if (activity == null) return null;
            RootUtils.setCommandTimeoutForCurrentThread(STARTUP_ROOT_COMMAND_TIMEOUT_MS);
            try {
                return activity.createDetectedFragments();
            } finally {
                RootUtils.clearCommandTimeoutForCurrentThread();
            }
        }

        @Override
        protected void onPostExecute(ArrayList<NavigationFragment> fragments) {
            super.onPostExecute(fragments);
            NavigationActivity activity = mRefActivity.get();
            if (activity == null || fragments == null
                    || activity.isFinishing() || activity.isDestroyed()) {
                return;
            }
            activity.onDetectedFragmentsLoaded(fragments);
        }
    }

    private ArrayList<NavigationFragment> createInitialFragments() {
        ArrayList<NavigationFragment> fragments = new ArrayList<>();
        fragments.add(new NavigationFragment(R.string.statistics));
        fragments.add(new NavigationFragment(
                R.string.overall, OverallFragment.class, R.drawable.ic_chart));
        fragments.add(new NavigationFragment(
                R.string.device, DeviceFragment.class, R.drawable.ic_device));
        fragments.add(new NavigationFragment(R.string.kernel));
        fragments.add(new NavigationFragment(
                R.string.cpu, CPUFragment.class, R.drawable.ic_cpu));
        fragments.add(new NavigationFragment(
                R.string.virtual_memory, VMFragment.class, R.drawable.ic_server));
        fragments.add(new NavigationFragment(
                R.string.misc, MiscFragment.class, R.drawable.ic_clear));
        fragments.add(new NavigationFragment(R.string.tools));
        fragments.add(new NavigationFragment(
                R.string.custom_controls, CustomControlsFragment.class, R.drawable.ic_console));
        fragments.add(new NavigationFragment(
                R.string.build_prop_editor, BuildpropFragment.class, R.drawable.ic_edit));
        fragments.add(new NavigationFragment(
                R.string.profile, ProfileFragment.class, R.drawable.ic_layers));
        fragments.add(new NavigationFragment(
                R.string.recovery, RecoveryFragment.class, R.drawable.ic_security));
        fragments.add(new NavigationFragment(
                R.string.initd, InitdFragment.class, R.drawable.ic_shell));
        fragments.add(new NavigationFragment(
                R.string.on_boot, OnBootFragment.class, R.drawable.ic_start));
        fragments.add(new NavigationFragment(R.string.other));
        fragments.add(new NavigationFragment(
                R.string.settings, SettingsFragment.class, R.drawable.ic_settings));
        fragments.add(new NavigationFragment(
                R.string.donation_title, DonationFragment.class, R.drawable.ic_donation));
        fragments.add(new NavigationFragment(
                R.string.about, AboutFragment.class, R.drawable.ic_about));
        return fragments;
    }

    private ArrayList<NavigationFragment> createDetectedFragments() {
        ArrayList<NavigationFragment> fragments = new ArrayList<>();
        fragments.add(new NavigationFragment(R.string.statistics));
        fragments.add(new NavigationFragment(R.string.overall, OverallFragment.class, R.drawable.ic_chart));
        fragments.add(new NavigationFragment(R.string.device, DeviceFragment.class, R.drawable.ic_device));
        if (Device.MemInfo.getInstance().getItems().size() > 0) {
            fragments.add(new NavigationFragment(R.string.memory, MemoryFragment.class, R.drawable.ic_save));
        }
        if (Device.Input.getInstance().supported()) {
            fragments.add(new NavigationFragment(R.string.inputs, InputsFragment.class, R.drawable.ic_keyboard));
        }
        fragments.add(new NavigationFragment(R.string.kernel));
        fragments.add(new NavigationFragment(R.string.cpu, CPUFragment.class, R.drawable.ic_cpu));
        if (Hotplug.supported()) {
            fragments.add(new NavigationFragment(R.string.cpu_hotplug, CPUHotplugFragment.class, R.drawable.ic_switch));
        }
        if (Hmp.getInstance().supported()) {
            fragments.add(new NavigationFragment(R.string.hmp, HmpFragment.class, R.drawable.ic_cpu));
        }
        if (Thermal.supported()) {
            fragments.add(new NavigationFragment(R.string.thermal, ThermalFragment.class, R.drawable.ic_temperature));
        }
        if (GPU.supported()) {
            fragments.add(new NavigationFragment(R.string.gpu, GPUFragment.class, R.drawable.ic_gpu));
        }
        if (Dvfs.supported()) {
            fragments.add(new NavigationFragment(R.string.dvfs_nav, DvfsFragment.class, R.drawable.ic_dvfs));
        }
        if (Screen.supported()) {
            fragments.add(new NavigationFragment(R.string.screen, ScreenFragment.class, R.drawable.ic_display));
        }
        if (Wake.supported()) {
            fragments.add(new NavigationFragment(R.string.gestures, WakeFragment.class, R.drawable.ic_touch));
        }
        if (Sound.getInstance().supported()) {
            fragments.add(new NavigationFragment(R.string.sound, SoundFragment.class, R.drawable.ic_music));
        }
        if (Spectrum.supported(this)) {
            fragments.add(new NavigationFragment(R.string.spectrum, SpectrumFragment.class, R.drawable.ic_spectrum_logo));
        }
        if (Battery.getInstance(this).supported()) {
            fragments.add(new NavigationFragment(R.string.battery, BatteryFragment.class, R.drawable.ic_battery));
        }
        if (LED.getInstance().supported()) {
            fragments.add(new NavigationFragment(R.string.led, LEDFragment.class, R.drawable.ic_led));
        }
        if (IO.getInstance().supported()) {
            fragments.add(new NavigationFragment(R.string.io_scheduler, IOFragment.class, R.drawable.ic_sdcard));
        }
        if (KSM.getInstance().supported()) {
            if (KSM.getInstance().isUKSM()) {
                fragments.add(new NavigationFragment(R.string.uksm_name, KSMFragment.class, R.drawable.ic_merge));
            } else {
                fragments.add(new NavigationFragment(R.string.ksm_name, KSMFragment.class, R.drawable.ic_merge));
            }
        }
        if (LMK.supported()) {
            fragments.add(new NavigationFragment(R.string.lmk, LMKFragment.class, R.drawable.ic_stackoverflow));
        }
        if (Wakelock.supported()) {
            fragments.add(new NavigationFragment(R.string.wakelock_nav, WakelockFragment.class, R.drawable.ic_unlock));
        }
        if (BoefflaWakelock.supported()) {
            fragments.add(new NavigationFragment(R.string.boeffla_wakelock, BoefflaWakelockFragment.class, R.drawable.ic_unlock));
        }
        fragments.add(new NavigationFragment(R.string.virtual_memory, VMFragment.class, R.drawable.ic_server));
        if (Entropy.supported()) {
            fragments.add(new NavigationFragment(R.string.entropy, EntropyFragment.class, R.drawable.ic_numbers));
        }
        fragments.add(new NavigationFragment(R.string.misc, MiscFragment.class, R.drawable.ic_clear));
        fragments.add(new NavigationFragment(R.string.voltage_control));
        if (VoltageCl1.supported()) {
            fragments.add(new NavigationFragment(R.string.cpucl1_voltage, CPUVoltageCl1Fragment.class, R.drawable.ic_bolt));
        }
        if (VoltageCl0.supported()) {
            fragments.add(new NavigationFragment(R.string.cpucl0_voltage, CPUVoltageCl0Fragment.class, R.drawable.ic_bolt));
        }
        if (VoltageMif.supported()) {
            fragments.add(new NavigationFragment(R.string.busMif_voltage, BusMifFragment.class, R.drawable.ic_bolt));
        }
        if (VoltageInt.supported()) {
            fragments.add(new NavigationFragment(R.string.busInt_voltage, BusIntFragment.class, R.drawable.ic_bolt));
        }
        if (VoltageDisp.supported()) {
            fragments.add(new NavigationFragment(R.string.busDisp_voltage, BusDispFragment.class, R.drawable.ic_bolt));
        }
        if (VoltageCam.supported()) {
            fragments.add(new NavigationFragment(R.string.busCam_voltage, BusCamFragment.class, R.drawable.ic_bolt));
        }
        fragments.add(new NavigationFragment(R.string.tools));
        //fragments.add(new NavigationFragment(R.string.data_sharing, DataSharingFragment.class, R.drawable.ic_database));
        fragments.add(new NavigationFragment(R.string.custom_controls, CustomControlsFragment.class, R.drawable.ic_console));

        SupportedDownloads supportedDownloads = new SupportedDownloads(this);
        if (supportedDownloads.getLink() != null) {
            fragments.add(new NavigationFragment(R.string.downloads, DownloadsFragment.class, R.drawable.ic_download));
        }
        if (Backup.hasBackup()) {
            fragments.add(new NavigationFragment(R.string.backup, BackupFragment.class, R.drawable.ic_restore));
        }
        fragments.add(new NavigationFragment(R.string.build_prop_editor, BuildpropFragment.class, R.drawable.ic_edit));
        fragments.add(new NavigationFragment(R.string.profile, ProfileFragment.class, R.drawable.ic_layers));
        fragments.add(new NavigationFragment(R.string.recovery, RecoveryFragment.class, R.drawable.ic_security));
        fragments.add(new NavigationFragment(R.string.initd, InitdFragment.class, R.drawable.ic_shell));
        fragments.add(new NavigationFragment(R.string.on_boot, OnBootFragment.class, R.drawable.ic_start));
        fragments.add(new NavigationFragment(R.string.other));
        fragments.add(new NavigationFragment(R.string.settings, SettingsFragment.class, R.drawable.ic_settings));
        fragments.add(new NavigationFragment(R.string.donation_title, DonationFragment.class, R.drawable.ic_donation));
        fragments.add(new NavigationFragment(R.string.about, AboutFragment.class, R.drawable.ic_about));
        //fragments.add(new NavigationFragment(R.string.contributors, ContributorsFragment.class, R.drawable.ic_people));
        //fragments.add(new NavigationFragment(R.string.help, HelpFragment.class, R.drawable.ic_help));
        return fragments;
    }

    private void onDetectedFragmentsLoaded(ArrayList<NavigationFragment> fragments) {
        if (getSupportFragmentManager().isStateSaved()) {
            mPendingFragments = fragments;
            return;
        }
        applyDetectedFragments(fragments);
    }

    private void applyDetectedFragments(ArrayList<NavigationFragment> fragments) {
        mFragments = fragments;
        sDetectedFragments = new ArrayList<>(fragments);
        mFragmentsComplete = true;
        appendFragments(true);
        if (selectRequestedSection()) {
            onItemSelected(mSelection, false);
        }
    }

    private void init(Bundle savedInstanceState) {
        MaterialToolbar toolbar = getToolBar();
        setSupportActionBar(toolbar);

        if (savedInstanceState != null) {
            mSelection = savedInstanceState.getInt(INTENT_SECTION);
        }

        if (mUseTopTabs) {
            toolbar.setTitle(R.string.app_name);
            mNavigationTabs = findViewById(R.id.navigation_tabs);
            mNavigationPager = findViewById(R.id.navigation_pager);
        } else {
            initDrawer(toolbar);
        }

        appendFragments(false);
        selectRequestedSection();

        if (mSelection == 0 || mActualFragments.get(mSelection) == null) {
            mSelection = firstTab();
        }
        if (mUseTopTabs) {
            initTopTabs();
        }
        onItemSelected(mSelection, false);

        if (AppSettings.isDataSharing(this)) {
            startService(new Intent(this, Monitor.class));
        }

        mNavigationContent.setVisibility(View.VISIBLE);
        mNavigationLoading.setVisibility(View.GONE);
    }

    private boolean selectRequestedSection() {
        String section = getIntent().getStringExtra(INTENT_SECTION);
        if (section == null) return false;

        for (Map.Entry<Integer, Class<? extends Fragment>> entry : mActualFragments.entrySet()) {
            Class<? extends Fragment> fragmentClass = entry.getValue();
            if (fragmentClass != null && fragmentClass.getCanonicalName().equals(section)) {
                mSelection = entry.getKey();
                getIntent().removeExtra(INTENT_SECTION);
                return true;
            }
        }
        if (mFragmentsComplete) {
            getIntent().removeExtra(INTENT_SECTION);
        }
        return false;
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
        Map<Integer, Class<? extends Fragment>> actualFragments = new LinkedHashMap<>();
        List<Integer> tabIds = new ArrayList<>();

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
                actualFragments.put(id, null);
            } else if (AppSettings.isFragmentEnabled(fragmentClass, this)) {
                if (mUseTopTabs) {
                    tabIds.add(id);
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
                actualFragments.put(id, fragmentClass);
            }
        }

        List<Integer> previousTabIds = mTabIds;
        mUpdatingNavigation = mUseTopTabs && mPagerAdapter != null;
        try {
            mActualFragments = actualFragments;
            mTabIds = tabIds;

            if (mUseTopTabs && mPagerAdapter != null) {
                mPagerFragments.keySet().removeIf(section -> !mTabIds.contains(section));
                dispatchTabUpdates(previousTabIds, mTabIds);
            }

            if (mActualFragments.get(mSelection) == null) {
                mSelection = firstTab();
            }
            selectNavigationSurface(mSelection);
        } finally {
            mUpdatingNavigation = false;
        }

        if (setShortcuts && mFragmentsComplete) {
            setShortcuts();
        }
    }

    private void dispatchTabUpdates(List<Integer> oldTabs, List<Integer> newTabs) {
        DiffUtil.calculateDiff(new DiffUtil.Callback() {
            @Override
            public int getOldListSize() {
                return oldTabs.size();
            }

            @Override
            public int getNewListSize() {
                return newTabs.size();
            }

            @Override
            public boolean areItemsTheSame(int oldItemPosition, int newItemPosition) {
                return oldTabs.get(oldItemPosition).equals(newTabs.get(newItemPosition));
            }

            @Override
            public boolean areContentsTheSame(int oldItemPosition, int newItemPosition) {
                return true;
            }
        }).dispatchUpdatesTo(mPagerAdapter);
    }

    private void initTopTabs() {
        mPagerAdapter = new NavigationPagerAdapter();
        mNavigationPager.setAdapter(mPagerAdapter);
        mNavigationTabMediator = new TabLayoutMediator(mNavigationTabs, mNavigationPager,
                this::bindTab);
        mNavigationTabMediator.attach();
        mNavigationPageChangeCallback = new ViewPager2.OnPageChangeCallback() {
            @Override
            public void onPageSelected(int position) {
                if (mUpdatingNavigation) return;

                Integer section = mPagerAdapter.getSection(position);
                if (section != null && section != mSelection) {
                    onItemSelected(section, true);
                }
            }
        };
        mNavigationPager.registerOnPageChangeCallback(mNavigationPageChangeCallback);
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
            if (mNavigationPager == null || mPagerAdapter == null) return;
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

    private void registerPagerFragment(int section, NavigationPageFragment fragment) {
        mPagerFragments.put(section, fragment);
    }

    private void unregisterPagerFragment(int section, NavigationPageFragment fragment) {
        if (mPagerFragments.get(section) == fragment) {
            mPagerFragments.remove(section);
        }
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
        int generation = ++mShortcutGeneration;
        mShortcutExecutor.execute(() -> {
            if (generation == mShortcutGeneration) {
                shortcutManager.setDynamicShortcuts(shortcutInfos);
            }
        });
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
    protected void onDestroy() {
        if (mNavigationPager != null && mNavigationPageChangeCallback != null) {
            mNavigationPager.unregisterOnPageChangeCallback(mNavigationPageChangeCallback);
            mNavigationPageChangeCallback = null;
        }
        if (mNavigationTabMediator != null) {
            mNavigationTabMediator.detach();
            mNavigationTabMediator = null;
        }
        mShortcutExecutor.shutdownNow();
        super.onDestroy();
    }

    @Override
    protected void onPostResume() {
        super.onPostResume();
        if (mPendingFragments != null) {
            ArrayList<NavigationFragment> fragments = mPendingFragments;
            mPendingFragments = null;
            applyDetectedFragments(fragments);
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

        outState.putParcelableArrayList("fragments", mFragments);
        outState.putInt(INTENT_SECTION, mSelection);
        outState.putBoolean(STATE_FRAGMENTS_COMPLETE, mFragmentsComplete);
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
        if (mFragmentsComplete && (!mUseTopTabs || !saveOpened)) {
            setShortcuts();
        }

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
            NavigationPageFragment page = mPagerFragments.get(mSelection);
            if (page != null) {
                return page.getLoadedFragment();
            }
            if (mPagerAdapter != null && mNavigationPager != null
                    && mNavigationPager.getCurrentItem() < mPagerAdapter.getItemCount()) {
                long itemId = mPagerAdapter.getItemId(mNavigationPager.getCurrentItem());
                Fragment fragment = getSupportFragmentManager().findFragmentByTag("f" + itemId);
                if (fragment instanceof NavigationPageFragment) {
                    return ((NavigationPageFragment) fragment).getLoadedFragment();
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
            Integer section = getSection(position);
            if (section == null) {
                throw new IllegalStateException("Invalid navigation page position " + position);
            }
            Class<? extends Fragment> fragmentClass = mActualFragments.get(section);
            if (fragmentClass == null) {
                throw new IllegalStateException("Missing fragment for section " + section);
            }
            NavigationPageFragment fragment = NavigationPageFragment.newInstance(
                    section, fragmentClass.getCanonicalName());
            mPagerFragments.put(section, fragment);
            return fragment;
        }

        @Override
        public int getItemCount() {
            return mTabIds.size();
        }

        @Override
        public long getItemId(int position) {
            Integer section = getSection(position);
            if (section == null) {
                return -1L;
            }
            return section;
        }

        @Override
        public boolean containsItem(long itemId) {
            return mTabIds.contains((int) itemId);
        }

        @Nullable
        Integer getSection(int position) {
            return position >= 0 && position < mTabIds.size()
                    ? mTabIds.get(position)
                    : null;
        }
    }

    public static class NavigationPageFragment extends Fragment {

        private static final String ARG_SECTION = "section";
        private static final String ARG_FRAGMENT_CLASS = "fragment_class";
        private static final String TAG_CONTENT = "content";

        private Fragment mLoadedFragment;

        static NavigationPageFragment newInstance(int section, String fragmentClass) {
            NavigationPageFragment fragment = new NavigationPageFragment();
            Bundle args = new Bundle();
            args.putInt(ARG_SECTION, section);
            args.putString(ARG_FRAGMENT_CLASS, fragmentClass);
            fragment.setArguments(args);
            return fragment;
        }

        @Override
        public void onAttach(@NonNull Context context) {
            super.onAttach(context);
            if (context instanceof NavigationActivity) {
                ((NavigationActivity) context).registerPagerFragment(getSection(), this);
            }
        }

        @NonNull
        @Override
        public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                                 @Nullable Bundle savedInstanceState) {
            FrameLayout view = new FrameLayout(requireContext());
            view.setId(R.id.navigation_page_container);
            view.setBackgroundColor(MaterialColors.getColor(
                    requireContext(), R.attr.colorSurface, 0));
            view.setLayoutParams(new FrameLayout.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.MATCH_PARENT));
            return view;
        }

        @Override
        public void onResume() {
            super.onResume();
            loadContent();
        }

        @Nullable
        Fragment getLoadedFragment() {
            if (mLoadedFragment == null) {
                mLoadedFragment = getChildFragmentManager().findFragmentByTag(TAG_CONTENT);
            }
            return mLoadedFragment;
        }

        private void loadContent() {
            if (!isResumed() || getView() == null || getLoadedFragment() != null) {
                return;
            }

            FragmentManager fragmentManager = getChildFragmentManager();
            if (fragmentManager.isStateSaved()) return;

            String fragmentClass = requireArguments().getString(ARG_FRAGMENT_CLASS);
            if (fragmentClass == null) return;

            mLoadedFragment = fragmentManager.getFragmentFactory().instantiate(
                    requireContext().getClassLoader(), fragmentClass);
            fragmentManager.beginTransaction()
                    .setReorderingAllowed(true)
                    .replace(R.id.navigation_page_container, mLoadedFragment, TAG_CONTENT)
                    .commitNow();
        }

        private int getSection() {
            return requireArguments().getInt(ARG_SECTION);
        }

        @Override
        public void onDetach() {
            if (getActivity() instanceof NavigationActivity) {
                ((NavigationActivity) getActivity()).unregisterPagerFragment(getSection(), this);
            }
            super.onDetach();
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
