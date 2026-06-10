/*
 * Copyright (C) 2015-2017 Willi Ye <williye97@gmail.com>
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

import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.content.res.ColorStateList;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import androidx.annotation.Nullable;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.material.color.MaterialColors;
import com.hades.hKtweaks.R;
import com.hades.hKtweaks.database.tools.profiles.Profiles;
import com.hades.hKtweaks.fragments.kernel.BusCamFragment;
import com.hades.hKtweaks.fragments.kernel.BusDispFragment;
import com.hades.hKtweaks.fragments.kernel.BusIntFragment;
import com.hades.hKtweaks.fragments.kernel.BusMifFragment;
import com.hades.hKtweaks.fragments.kernel.CPUVoltageCl0Fragment;
import com.hades.hKtweaks.fragments.kernel.CPUVoltageCl1Fragment;
import com.hades.hKtweaks.fragments.kernel.GPUFragment;
import com.hades.hKtweaks.services.profile.Tile;
import com.hades.hKtweaks.utils.AppSettings;
import com.hades.hKtweaks.utils.Device;
import com.hades.hKtweaks.utils.ExpressiveMotion;
import com.hades.hKtweaks.utils.Log;
import com.hades.hKtweaks.utils.Utils;
import com.hades.hKtweaks.utils.kernel.battery.Battery;
import com.hades.hKtweaks.utils.kernel.bus.VoltageCam;
import com.hades.hKtweaks.utils.kernel.bus.VoltageDisp;
import com.hades.hKtweaks.utils.kernel.bus.VoltageInt;
import com.hades.hKtweaks.utils.kernel.bus.VoltageMif;
import com.hades.hKtweaks.utils.kernel.cpu.CPUBoost;
import com.hades.hKtweaks.utils.kernel.cpu.CPUFreq;
import com.hades.hKtweaks.utils.kernel.cpu.MSMPerformance;
import com.hades.hKtweaks.utils.kernel.cpu.Temperature;
import com.hades.hKtweaks.utils.kernel.cpuhotplug.Hotplug;
import com.hades.hKtweaks.utils.kernel.cpuhotplug.QcomBcl;
import com.hades.hKtweaks.utils.kernel.cpuvoltage.VoltageCl0;
import com.hades.hKtweaks.utils.kernel.cpuvoltage.VoltageCl1;
import com.hades.hKtweaks.utils.kernel.gpu.GPU;
import com.hades.hKtweaks.utils.kernel.gpu.GPUFreqExynos;
import com.hades.hKtweaks.utils.kernel.io.IO;
import com.hades.hKtweaks.utils.kernel.ksm.KSM;
import com.hades.hKtweaks.utils.kernel.misc.Vibration;
import com.hades.hKtweaks.utils.kernel.screen.Screen;
import com.hades.hKtweaks.utils.kernel.sound.Sound;
import com.hades.hKtweaks.utils.kernel.spectrum.Spectrum;
import com.hades.hKtweaks.utils.kernel.thermal.Thermal;
import com.hades.hKtweaks.utils.kernel.vm.ZSwap;
import com.hades.hKtweaks.utils.kernel.wake.Wake;
import com.hades.hKtweaks.utils.kernel.boefflawakelock.BoefflaWakelock;
import com.hades.hKtweaks.utils.root.RootUtils;

import java.lang.ref.WeakReference;

/**
 * Created by willi on 14.04.16.
 */
public class MainActivity extends BaseActivity {

    private static final long STARTUP_ROOT_COMMAND_TIMEOUT_MS = 10_000;

    private TextView mRootAccess;
    private TextView mBusybox;
    private TextView mCollectInfo;
    private ImageView mRootAccessStatus;
    private ImageView mBusyboxStatus;
    private ImageView mCollectInfoStatus;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);

        mRootAccess = findViewById(R.id.root_access_text);
        mBusybox = findViewById(R.id.busybox_text);
        mCollectInfo = findViewById(R.id.info_collect_text);
        mRootAccessStatus = findViewById(R.id.root_access_status);
        mBusyboxStatus = findViewById(R.id.busybox_status);
        mCollectInfoStatus = findViewById(R.id.info_collect_status);

        if (savedInstanceState == null) {
            /*
             * Launch password activity when one is set,
             * otherwise run {@link #CheckingTask}
             */
            String password;
            if (!(password = AppSettings.getPassword(this)).isEmpty()) {
                Intent intent = new Intent(this, SecurityActivity.class);
                intent.putExtra(SecurityActivity.PASSWORD_INTENT, password);
                startActivityForResult(intent, 1);
            } else {
                new CheckingTask(this).execute();
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        /*
         * 1: Password check result
         */
        if (requestCode == 1) {
            /*
             * 0: Password is wrong
             * 1: Password is correct
             */
            if (resultCode == 1) {
                new CheckingTask(this).execute();
            } else {
                finish();
            }
        }
    }

    private void launch() {
        Intent intent = new Intent(this, NavigationActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        if (getIntent().getExtras() != null) {
            intent.putExtras(getIntent().getExtras());
        }
        startActivity(intent);
        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
        finish();
    }

    private void updateCheckStatus(TextView textView, ImageView statusView, boolean success) {
        int containerColor = MaterialColors.getColor(statusView,
                success ? R.attr.colorPrimaryContainer : R.attr.colorErrorContainer);
        int contentColor = MaterialColors.getColor(statusView,
                success ? R.attr.colorOnPrimaryContainer : R.attr.colorOnErrorContainer);

        statusView.setImageResource(success ? R.drawable.ic_done : R.drawable.ic_cancel);
        statusView.setBackgroundTintList(ColorStateList.valueOf(containerColor));
        statusView.setImageTintList(ColorStateList.valueOf(contentColor));
        textView.setTextColor(contentColor);

        statusView.setAlpha(0f);
        statusView.setScaleX(0.75f);
        statusView.setScaleY(0.75f);
        AnimatorSet reveal = new AnimatorSet();
        reveal.playTogether(
                ObjectAnimator.ofFloat(statusView, View.ALPHA, 0f, 1f),
                ObjectAnimator.ofFloat(statusView, View.SCALE_X, 0.75f, 1f),
                ObjectAnimator.ofFloat(statusView, View.SCALE_Y, 0.75f, 1f));
        ExpressiveMotion.applyEmphasizedDecelerate(reveal, this,
                com.google.android.material.R.attr.motionDurationMedium2, 300);
        reveal.start();
    }

    private static class CheckingTask
            extends AsyncTask<Void, CheckingTask.CheckProgress, CheckingTask.StartupResult> {

        private static final int CHECK_ROOT = 0;
        private static final int CHECK_BUSYBOX = 1;
        private static final int CHECK_INFO = 2;

        private final WeakReference<MainActivity> mRefActivity;

        private CheckingTask(MainActivity activity) {
            mRefActivity = new WeakReference<>(activity);
        }

        private enum StartupResult {
            NO_ROOT,
            NO_BUSYBOX,
            READY
        }

        private static class CheckProgress {

            private final int stage;
            private final boolean success;

            private CheckProgress(int stage, boolean success) {
                this.stage = stage;
                this.success = success;
            }
        }

        private void checkInitVariables(){

            //Initialize Boeffla Wakelock Blocker Files
            if(BoefflaWakelock.supported()) {
                BoefflaWakelock.CopyWakelockBlockerDefault();
            }

            // If voltages are saved on Service.java, mVoltageSaved = 1
            int mVoltageSaved = Utils.strToInt(RootUtils.getProp("hKtweaks.voltage_saved"));

            // Check if system is rebooted
            boolean mIsBooted = AppSettings.getBoolean("is_booted", true, mRefActivity.get());
            if (mIsBooted) {
                // reset the Global voltages seekbar
                if (!AppSettings.getBoolean("cpucl1voltage_onboot", false, mRefActivity.get())) {
                    AppSettings.saveInt("CpuCl1_seekbarPref_value", CPUVoltageCl1Fragment.mDefZeroPosition, mRefActivity.get());
                }
                if (!AppSettings.getBoolean("cpucl0voltage_onboot", false, mRefActivity.get())) {
                    AppSettings.saveInt("CpuCl0_seekbarPref_value", CPUVoltageCl0Fragment.mDefZeroPosition, mRefActivity.get());
                }
                if (!AppSettings.getBoolean("busMif_onboot", false, mRefActivity.get())) {
                    AppSettings.saveInt("busMif_seekbarPref_value", BusMifFragment.mDefZeroPosition, mRefActivity.get());
                }
                if (!AppSettings.getBoolean("busInt_onboot", false, mRefActivity.get())) {
                    AppSettings.saveInt("busInt_seekbarPref_value", BusIntFragment.mDefZeroPosition, mRefActivity.get());
                }
                if (!AppSettings.getBoolean("busDisp_onboot", false, mRefActivity.get())) {
                    AppSettings.saveInt("busDisp_seekbarPref_value", BusDispFragment.mDefZeroPosition, mRefActivity.get());
                }
                if (!AppSettings.getBoolean("busCam_onboot", false, mRefActivity.get())) {
                    AppSettings.saveInt("busCam_seekbarPref_value", BusCamFragment.mDefZeroPosition, mRefActivity.get());
                }
                if (!AppSettings.getBoolean("gpu_onboot", false, mRefActivity.get())) {
                    AppSettings.saveInt("gpu_seekbarPref_value", GPUFragment.mDefZeroPosition, mRefActivity.get());
                }

                // update spectrum support and profile
                AppSettings.saveBoolean("spectrum_supported", Spectrum.suSupported(), mRefActivity.get());
                AppSettings.saveInt("spectrum_profile", Spectrum.getSuProfile(), mRefActivity.get());
            }
            AppSettings.saveBoolean("is_booted", false, mRefActivity.get());

            // Check if exist /data/.hKtweaks folder
            if (!Utils.existFile("/data/.hKtweaks")) {
                RootUtils.runCommand("mkdir /data/.hKtweaks");
            }

            // Check if kernel is changed
            String kernel_old = AppSettings.getString("kernel_version_old", "", mRefActivity.get());
            String kernel_new = Device.getKernelVersion(true);

            if (!kernel_old.equals(kernel_new)){
                // Reset max limit of max_poll_percent
                AppSettings.saveBoolean("max_pool_percent_saved", false, mRefActivity.get());
                AppSettings.saveBoolean("memory_pool_percent_saved", false, mRefActivity.get());
                AppSettings.saveString("kernel_version_old", kernel_new, mRefActivity.get());

                if (mVoltageSaved != 1) {
                    // Reset voltage_saved to recopy voltage stock files
                    AppSettings.saveBoolean("cl0_voltage_saved", false, mRefActivity.get());
                    AppSettings.saveBoolean("cl1_voltage_saved", false, mRefActivity.get());
                    AppSettings.saveBoolean("busMif_voltage_saved", false, mRefActivity.get());
                    AppSettings.saveBoolean("busInt_voltage_saved", false, mRefActivity.get());
                    AppSettings.saveBoolean("busDisp_voltage_saved", false, mRefActivity.get());
                    AppSettings.saveBoolean("busCam_voltage_saved", false, mRefActivity.get());
                    AppSettings.saveBoolean("gpu_voltage_saved", false, mRefActivity.get());
                }

                // Reset battery_saved to recopy battery stock values
                AppSettings.saveBoolean("battery_saved", false, mRefActivity.get());
            }

            // Check if hKtweaks version is changed
            String appVersionOld = AppSettings.getString("app_version_old", "", mRefActivity.get());
            String appVersionNew = Utils.appVersion();
            AppSettings.saveBoolean("show_changelog", true, mRefActivity.get());

            if (appVersionOld.equals(appVersionNew)){
                AppSettings.saveBoolean("show_changelog", false, mRefActivity.get());
            } else {
                AppSettings.saveString("app_version_old", appVersionNew, mRefActivity.get());
            }

            // save battery stock values
            if (!AppSettings.getBoolean("battery_saved", false, mRefActivity.get())){
                Battery.getInstance(mRefActivity.get()).saveStockValues(mRefActivity.get());
            }

            // Save backup of Cluster0 stock voltages
            if (!Utils.existFile(VoltageCl0.BACKUP) || !AppSettings.getBoolean("cl0_voltage_saved", false, mRefActivity.get()) ){
                if (VoltageCl0.supported()){
                    RootUtils.runCommand("cp " + VoltageCl0.CL0_VOLTAGE + " " + VoltageCl0.BACKUP);
                    AppSettings.saveBoolean("cl0_voltage_saved", true, mRefActivity.get());
                }
            }

            // Save backup of Cluster1 stock voltages
            if (!Utils.existFile(VoltageCl1.BACKUP) || !AppSettings.getBoolean("cl1_voltage_saved", false, mRefActivity.get())){
                if (VoltageCl1.supported()){
                    RootUtils.runCommand("cp " + VoltageCl1.CL1_VOLTAGE + " " + VoltageCl1.BACKUP);
                    AppSettings.saveBoolean("cl1_voltage_saved", true, mRefActivity.get());
                }
            }

            // Save backup of Bus Mif stock voltages
            if (!Utils.existFile(VoltageMif.BACKUP) || !AppSettings.getBoolean("busMif_voltage_saved", false, mRefActivity.get())){
                if (VoltageMif.supported()){
                    RootUtils.runCommand("cp " + VoltageMif.VOLTAGE + " " + VoltageMif.BACKUP);
                    AppSettings.saveBoolean("busMif_voltage_saved", true, mRefActivity.get());
                }
            }

            // Save backup of Bus Int stock voltages
            if (!Utils.existFile(VoltageInt.BACKUP) || !AppSettings.getBoolean("busInt_voltage_saved", false, mRefActivity.get())){
                if (VoltageInt.supported()){
                    RootUtils.runCommand("cp " + VoltageInt.VOLTAGE + " " + VoltageInt.BACKUP);
                    AppSettings.saveBoolean("busInt_voltage_saved", true, mRefActivity.get());
                }
            }

            // Save backup of Bus Disp stock voltages
            if (!Utils.existFile(VoltageDisp.BACKUP) || !AppSettings.getBoolean("busDisp_voltage_saved", false, mRefActivity.get())){
                if (VoltageDisp.supported()){
                    RootUtils.runCommand("cp " + VoltageDisp.VOLTAGE + " " + VoltageDisp.BACKUP);
                    AppSettings.saveBoolean("busDisp_voltage_saved", true, mRefActivity.get());
                }
            }

            // Save backup of Bus Cam stock voltages
            if (!Utils.existFile(VoltageCam.BACKUP) || !AppSettings.getBoolean("busCam_voltage_saved", false, mRefActivity.get())){
                if (VoltageCam.supported()){
                    RootUtils.runCommand("cp " + VoltageCam.VOLTAGE + " " + VoltageCam.BACKUP);
                    AppSettings.saveBoolean("busCam_voltage_saved", true,mRefActivity.get() );
                }
            }

            // Save backup of GPU stock voltages
            if (!Utils.existFile(GPUFreqExynos.BACKUP) || !AppSettings.getBoolean("gpu_voltage_saved", false, mRefActivity.get())){
                if (GPUFreqExynos.getInstance().supported() && GPUFreqExynos.getInstance().hasVoltage()){
                    RootUtils.runCommand("cp " + GPUFreqExynos.getInstance().AVAILABLE_VOLTS + " " + GPUFreqExynos.BACKUP);
                    AppSettings.saveBoolean("gpu_voltage_saved", true, mRefActivity.get());
                }
            }

            // If has MaxPoolPercent save file
            if (!AppSettings.getBoolean("max_pool_percent_saved", false, mRefActivity.get())) {
                if (ZSwap.hasMaxPoolPercent()) {
                    RootUtils.runCommand("cp /sys/module/zswap/parameters/max_pool_percent /data/.hKtweaks/max_pool_percent");
                    AppSettings.saveBoolean("max_pool_percent_saved", true, mRefActivity.get());
                }
            }

            //Check memory pool percent unit
            if (!AppSettings.getBoolean("memory_pool_percent_saved", false, mRefActivity.get())){
                int pool = ZSwap.getMaxPoolPercent();
                if (pool >= 100) AppSettings.saveBoolean("memory_pool_percent", false, mRefActivity.get());
                if (pool < 100) AppSettings.saveBoolean("memory_pool_percent", true, mRefActivity.get());
                AppSettings.saveBoolean("memory_pool_percent_saved", true, mRefActivity.get());
            }

            // Save GPU libs version
            AppSettings.saveString("gpu_lib_version",
                    RootUtils.runCommand("dumpsys SurfaceFlinger | grep GLES | head -n 1 | cut -f 3,4,5 -d ','"), mRefActivity.get());
        }

        @Override
        protected StartupResult doInBackground(Void... params) {
            RootUtils.setCommandTimeoutForCurrentThread(STARTUP_ROOT_COMMAND_TIMEOUT_MS);
            try {
                boolean hasRoot = RootUtils.rootAccess();
                publishProgress(new CheckProgress(CHECK_ROOT, hasRoot));
                if (!hasRoot) {
                    return StartupResult.NO_ROOT;
                }

                boolean hasBusybox = RootUtils.busyboxInstalled();
                publishProgress(new CheckProgress(CHECK_BUSYBOX, hasBusybox));
                if (!hasBusybox) {
                    return StartupResult.NO_BUSYBOX;
                }

                try {
                    collectData();
                } catch (RuntimeException exception) {
                    Log.e("Startup data collection failed: " + exception);
                }

                try {
                    checkInitVariables();
                } catch (RuntimeException exception) {
                    Log.e("Startup initialization failed: " + exception);
                }
                publishProgress(new CheckProgress(CHECK_INFO, true));
                return StartupResult.READY;
            } finally {
                RootUtils.clearCommandTimeoutForCurrentThread();
            }
        }

        /**
         * Determinate what sections are supported
         */
        private void collectData() {
            MainActivity activity = mRefActivity.get();
            if (activity == null) return;

            Battery.getInstance(activity);
            CPUBoost.getInstance();

            // Assign core ctl min cpu
            CPUFreq.getInstance(activity);

            Device.CPUInfo.getInstance();
            Device.Input.getInstance();
            Device.MemInfo.getInstance();
            Device.ROMInfo.getInstance();
            Device.TrustZone.getInstance();
            GPU.supported();
            Hotplug.supported();
            IO.getInstance();
            KSM.getInstance();
            MSMPerformance.getInstance();
            QcomBcl.supported();
            Screen.supported();
            Sound.getInstance();
            Temperature.getInstance(activity);
            Thermal.supported();
            Tile.publishProfileTile(new Profiles(activity).getAllProfiles(), activity);
            Vibration.getInstance();
            VoltageCl0.supported();
            VoltageCl1.supported();
            Wake.supported();

        }

        /**
         * Let the user know what we are doing right now
         *
         * @param values progress updates
         */
        @Override
        protected void onProgressUpdate(CheckProgress... values) {
            super.onProgressUpdate(values);
            MainActivity activity = mRefActivity.get();
            if (activity == null || values.length == 0) return;

            CheckProgress progress = values[0];
            switch (progress.stage) {
                case CHECK_ROOT:
                    activity.updateCheckStatus(activity.mRootAccess,
                            activity.mRootAccessStatus, progress.success);
                    break;
                case CHECK_BUSYBOX:
                    activity.updateCheckStatus(activity.mBusybox,
                            activity.mBusyboxStatus, progress.success);
                    break;
                case CHECK_INFO:
                    activity.updateCheckStatus(activity.mCollectInfo,
                            activity.mCollectInfoStatus, progress.success);
                    break;
            }
        }

        @Override
        protected void onPostExecute(StartupResult result) {
            super.onPostExecute(result);
            MainActivity activity = mRefActivity.get();
            if (activity == null) return;

            /*
             * If root or busybox/toybox are not available,
             * launch text activity which let the user know
             * what the problem is.
             */
            if (result != StartupResult.READY) {
                boolean hasRoot = result != StartupResult.NO_ROOT;
                Intent intent = new Intent(activity, TextActivity.class);
                intent.putExtra(TextActivity.MESSAGE_INTENT, activity.getString(hasRoot ?
                        R.string.no_busybox : R.string.no_root));
                intent.putExtra(TextActivity.SUMMARY_INTENT,
                        hasRoot ? "https://play.google.com/store/apps/details?id=stericson.busybox" :
                                "https://www.google.com/search?site=&source=hp&q=root+"
                                        + Device.getVendor() + "+" + Device.getModel());
                activity.startActivity(intent);
                activity.overridePendingTransition(
                        android.R.anim.fade_in, android.R.anim.fade_out);
                activity.finish();

                return;
            }

            activity.launch();
        }
    }

}
