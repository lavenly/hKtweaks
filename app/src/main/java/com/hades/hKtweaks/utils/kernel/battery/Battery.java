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
package com.hades.hKtweaks.utils.kernel.battery;

import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.BatteryManager;

import androidx.annotation.NonNull;

import com.hades.hKtweaks.R;
import com.hades.hKtweaks.fragments.ApplyOnBootFragment;
import com.hades.hKtweaks.utils.AppSettings;
import com.hades.hKtweaks.utils.Utils;
import com.hades.hKtweaks.utils.root.Control;

import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

/**
 * Created by willi on 26.06.16.
 */
public class Battery {

    private static Battery sInstance;
    private static int mCapacity;

    public static Battery getInstance(@NonNull Context context) {
        if (sInstance == null) {
            setValues();
            sInstance = new Battery(context);
        }
        return sInstance;
    }

    public static String BATTERY_NODE;
    private static String BATTERY_POWER_SUPPLY_NODE;
    private static String UNSTABLE_CHARGE;
    private static String HV_INPUT;
    private static String HV_CHARGE;
    private static String AC_INPUT;
    private static String AC_CHARGE;
    private static String AC_INPUT_SCREEN;
    private static String AC_CHARGE_SCREEN;
    private static String USB_INPUT;
    private static String USB_CHARGE;
    private static String WC_INPUT;
    private static String WC_CHARGE;
    private static String CAR_INPUT;
    private static String CAR_CHARGE;
    private static String CHARGE_SOURCE;
    private static String HEALTH;
    private static String FG_FULLCAPNOM;
    private static String STORE_MODE;
    private static String STORE_MODE_MAX;
    private static String STORE_MODE_MIN;
    private static final List<String> sStoreModeMaxFiles = new ArrayList<>();
    private static final List<String> sStoreModeMinFiles = new ArrayList<>();
    private static String DISABLE_CHARGING;
    private static String DISABLE_CHARGING_ENABLE_VALUE = "1";
    private static String DISABLE_CHARGING_DISABLE_VALUE = "0";
    private static final List<DisableChargingControl> sDisableChargingControls =
            new ArrayList<>();

    private static final String STANDARD_POWER_SUPPLY_BATTERY =
            "/sys/class/power_supply/battery";
    private static final String STANDARD_BATTERY_HEALTH =
            "/sys/class/power_supply/battery/health";

    public static void setValues() {
        BATTERY_NODE = null;
        BATTERY_POWER_SUPPLY_NODE = null;
        UNSTABLE_CHARGE = null;
        HV_INPUT = null;
        HV_CHARGE = null;
        AC_INPUT = null;
        AC_CHARGE = null;
        AC_INPUT_SCREEN = null;
        AC_CHARGE_SCREEN = null;
        USB_INPUT = null;
        USB_CHARGE = null;
        WC_INPUT = null;
        WC_CHARGE = null;
        CAR_INPUT = null;
        CAR_CHARGE = null;
        CHARGE_SOURCE = null;
        HEALTH = null;
        FG_FULLCAPNOM = null;

        for (String file : new String[] {
                // Add on this list needed values for battery sysfs nodes
                // TODO: Make sys nodes of battery device be auto detected as on gpu/cpu temps
                "/sys/devices/battery",
                "/sys/devices/battery.30",
                "/sys/devices/battery.53",
                "/sys/devices/battery.54",
                "/sys/devices/battery.55",
                "/sys/devices/platform/battery",
                "/sys/devices/platform/samsung_mobile_device/samsung_mobile_device:battery",
                "/sys/devices/platform/soc/soc:battery"}
                ) {
            if (Utils.existFile(file)) {
                BATTERY_NODE = file;
                UNSTABLE_CHARGE = BATTERY_NODE + "/unstable_power_detection";
                HV_INPUT = BATTERY_NODE + "/hv_input";
                HV_CHARGE = BATTERY_NODE + "/hv_charge";
                AC_INPUT = BATTERY_NODE + "/ac_input";
                AC_CHARGE = BATTERY_NODE + "/ac_charge";
                AC_INPUT_SCREEN = BATTERY_NODE + "/so_limit_input";
                AC_CHARGE_SCREEN = BATTERY_NODE + "/so_limit_charge";
                USB_INPUT = BATTERY_NODE + "/sdp_input";
                USB_CHARGE = BATTERY_NODE + "/sdp_charge";
                WC_INPUT = BATTERY_NODE + "/wc_input";
                WC_CHARGE = BATTERY_NODE + "/wc_charge";
                CAR_INPUT = BATTERY_NODE + "/car_input";
                CAR_CHARGE = BATTERY_NODE + "/car_charge";
                CHARGE_SOURCE = BATTERY_NODE + "/power_supply/battery/batt_charging_source";
                HEALTH = BATTERY_NODE + "/power_supply/battery/health";
                FG_FULLCAPNOM = BATTERY_NODE + "/power_supply/battery/fg_fullcapnom";
                break;
            }
        }
        detectPowerSupplyNode();
        detectFeatureFiles();
    }

    private static void detectPowerSupplyNode() {
        for (String root : getBatteryRoots()) {
            if (isPowerSupplyRoot(root)) {
                BATTERY_POWER_SUPPLY_NODE = root;
                break;
            }
        }

        if (BATTERY_POWER_SUPPLY_NODE != null) {
            CHARGE_SOURCE = BATTERY_POWER_SUPPLY_NODE + "/batt_charging_source";
            HEALTH = BATTERY_POWER_SUPPLY_NODE + "/health";
            FG_FULLCAPNOM = BATTERY_POWER_SUPPLY_NODE + "/fg_fullcapnom";
        }
    }

    private static boolean isPowerSupplyRoot(String root) {
        return exists(root + "/capacity")
                || exists(root + "/status")
                || exists(root + "/current_now")
                || exists(root + "/voltage_now")
                || exists(root + "/health");
    }

    private static void detectFeatureFiles() {
        STORE_MODE = findBatteryFile("store_mode");
        detectStoreModeLimitFiles();
        detectDisableChargingFile();
    }

    private static void detectStoreModeLimitFiles() {
        sStoreModeMaxFiles.clear();
        sStoreModeMinFiles.clear();

        addBatteryFileCandidates(sStoreModeMaxFiles, "store_mode_max");
        addExistingFile(sStoreModeMaxFiles, STORE_MODE_MAX_MODULE);
        addBatteryFileCandidates(sStoreModeMinFiles, "store_mode_min");
        addExistingFile(sStoreModeMinFiles, STORE_MODE_MIN_MODULE);

        STORE_MODE_MAX = firstStoreModeLimitFile(sStoreModeMaxFiles);
        STORE_MODE_MIN = firstStoreModeLimitFile(sStoreModeMinFiles);
    }

    private static void addBatteryFileCandidates(List<String> files, String fileName) {
        for (String root : getBatteryRoots()) {
            addExistingFile(files, root + "/" + fileName);
        }
    }

    private static void addExistingFile(List<String> files, String path) {
        if (exists(path) && !files.contains(path)) {
            files.add(path);
        }
    }

    private static String firstStoreModeLimitFile(List<String> files) {
        return files.isEmpty() ? null : files.get(0);
    }

    private static void detectDisableChargingFile() {
        DISABLE_CHARGING = null;
        DISABLE_CHARGING_ENABLE_VALUE = "1";
        DISABLE_CHARGING_DISABLE_VALUE = "0";
        sDisableChargingControls.clear();

        for (String root : getBatteryRoots()) {
            if (addDisableChargingFiles(root) > 0) {
                return;
            }
        }

        addDisableChargingFile("/sys/class/power_supply/usb/input_suspend", "1", "0");
    }

    private static int addDisableChargingFiles(String root) {
        int count = 0;
        count += addDisableChargingFile(root + "/charging_disabled", "1", "0") ? 1 : 0;
        count += addDisableChargingFile(root + "/charge_disabled", "1", "0") ? 1 : 0;
        count += addDisableChargingFile(root + "/charge_disable", "1", "0") ? 1 : 0;
        count += addDisableChargingFile(root + "/batt_slate_mode", "1", "0") ? 1 : 0;
        count += addDisableChargingFile(root + "/input_suspend", "1", "0") ? 1 : 0;
        count += addDisableChargingFile(root + "/charging_enabled", "0", "1") ? 1 : 0;
        count += addDisableChargingFile(root + "/charge_enabled", "0", "1") ? 1 : 0;
        count += addDisableChargingFile(root + "/battery_charging_enabled", "0", "1") ? 1 : 0;
        count += addDisableChargingFile(root + "/batt_charging_enabled", "0", "1") ? 1 : 0;
        return count;
    }

    private static boolean addDisableChargingFile(String path, String disableValue,
                                                  String enableValue) {
        if (!exists(path) || hasDisableChargingFile(path)) return false;

        if (DISABLE_CHARGING == null) {
            DISABLE_CHARGING = path;
            DISABLE_CHARGING_DISABLE_VALUE = disableValue;
            DISABLE_CHARGING_ENABLE_VALUE = enableValue;
        }
        sDisableChargingControls.add(
                new DisableChargingControl(path, disableValue, enableValue));
        return true;
    }

    private static boolean hasDisableChargingFile(String path) {
        for (DisableChargingControl control : sDisableChargingControls) {
            if (control.path.equals(path)) {
                return true;
            }
        }
        return false;
    }

    private static String findBatteryFile(String fileName) {
        for (String root : getBatteryRoots()) {
            String path = root + "/" + fileName;
            if (exists(path)) {
                return path;
            }
        }
        return null;
    }

    private static List<String> getBatteryRoots() {
        List<String> roots = new ArrayList<>();
        if (BATTERY_NODE != null) {
            addRoot(roots, BATTERY_NODE + "/power_supply/battery");
            addRoot(roots, BATTERY_NODE);
        }
        addRoot(roots, STANDARD_POWER_SUPPLY_BATTERY);
        addRoot(roots, "/sys/devices/platform/battery/power_supply/battery");
        addRoot(roots, "/sys/devices/battery/power_supply/battery");
        addRoot(roots, "/sys/devices/platform/sec-battery/power_supply/battery");
        return roots;
    }

    private static void addRoot(List<String> roots, String root) {
        if (root != null && !roots.contains(root)) {
            roots.add(root);
        }
    }

    private static String readBatteryFile(String fileName) {
        if (BATTERY_POWER_SUPPLY_NODE == null) {
            detectPowerSupplyNode();
        }
        if (BATTERY_POWER_SUPPLY_NODE == null) {
            return null;
        }
        return Utils.readFile(BATTERY_POWER_SUPPLY_NODE + "/" + fileName);
    }

    private static int readBatteryInt(String fileName) {
        String value = readBatteryFile(fileName);
        return Utils.strToInt(value == null ? "" : value);
    }

    private static final String FORCE_FAST_CHARGE = "/sys/kernel/fast_charge/force_fast_charge";
    private static final String ADAPTIVE_FAST_CHARGE = "/sys/class/sec/switch/afc_disable";
    private static final String BLX = "/sys/devices/virtual/misc/batterylifeextender/charging_limit";
    private static final String CHARGE_RATE = "/sys/kernel/thundercharge_control";
    private static final String CHARGE_RATE_ENABLE = CHARGE_RATE + "/enabled";
    private static final String CUSTOM_CURRENT = CHARGE_RATE + "/custom_current";
    private static final String STORE_MODE_MAX_MODULE = "/sys/module/sec_battery/parameters/store_mode_max";
    private static final String STORE_MODE_MIN_MODULE = "/sys/module/sec_battery/parameters/store_mode_min";

    private Battery(Context context) {
        if (BATTERY_NODE == null) {
            setValues();
        }
        if (mCapacity == 0) {
            try {
                Class<?> powerProfile = Class.forName("com.android.internal.os.PowerProfile");
                Constructor constructor = powerProfile.getDeclaredConstructor(Context.class);
                Object powerProInstance = constructor.newInstance(context);
                Method batteryCap = powerProfile.getMethod("getBatteryCapacity");
                mCapacity = Math.round((long) (double) batteryCap.invoke(powerProInstance));
            } catch (Exception e) {
                e.printStackTrace();
                mCapacity = 0;
            }
        }
    }

    public static String getHealthValue() {
        String state = HEALTH == null ? null : Utils.readFile(HEALTH);
        if (state == null || state.isEmpty()) {
            state = Utils.readFile(STANDARD_BATTERY_HEALTH);
        }
        if (state == null || state.isEmpty()) {
            return null;
        }
        if (FG_FULLCAPNOM != null && getCapacity() > 0) {
            float cap = Utils.strToInt(Utils.readFile(FG_FULLCAPNOM));
            if (cap != 0) {
                float value = ((cap * 2) / getCapacity()) * 100;
                value = (value > 100) ? (value / 2) : value;
                value = (value > 100) ? 100 : value;
                return state + " / "
                        + String.format(Locale.getDefault(), "%.2f", value) + "%";
            }
        }
        return state;
    }

    public static int getCurrentNow() {
        return readBatteryInt("current_now");
    }

    public static int getCurrentAvg() {
        return readBatteryInt("current_avg");
    }

    public static String getStatus(Context context) {
        String status = readBatteryFile("status");
        if (status != null && !status.isEmpty()) {
            return status;
        }

        Intent intent = context.registerReceiver(
                null, new IntentFilter(Intent.ACTION_BATTERY_CHANGED));
        if (intent == null) {
            return context.getString(R.string.unknown);
        }

        switch (intent.getIntExtra(
                BatteryManager.EXTRA_STATUS, BatteryManager.BATTERY_STATUS_UNKNOWN)) {
            case BatteryManager.BATTERY_STATUS_CHARGING:
                return "Charging";
            case BatteryManager.BATTERY_STATUS_DISCHARGING:
                return "Discharging";
            case BatteryManager.BATTERY_STATUS_FULL:
                return "Full";
            case BatteryManager.BATTERY_STATUS_NOT_CHARGING:
                return "Not charging";
            default:
                return context.getString(R.string.unknown);
        }
    }

    public static String getHealthValue(@NonNull Context context) {
        getInstance(context);

        String health = getHealthValue();
        if (health != null) {
            return health;
        }

        Intent status = context.registerReceiver(
                null, new IntentFilter(Intent.ACTION_BATTERY_CHANGED));
        if (status == null) {
            return context.getString(R.string.unknown);
        }

        switch (status.getIntExtra(
                BatteryManager.EXTRA_HEALTH, BatteryManager.BATTERY_HEALTH_UNKNOWN)) {
            case BatteryManager.BATTERY_HEALTH_GOOD:
                return "Good";
            case BatteryManager.BATTERY_HEALTH_OVERHEAT:
                return "Overheat";
            case BatteryManager.BATTERY_HEALTH_DEAD:
                return "Dead";
            case BatteryManager.BATTERY_HEALTH_OVER_VOLTAGE:
                return "Over voltage";
            case BatteryManager.BATTERY_HEALTH_UNSPECIFIED_FAILURE:
                return "Unspecified failure";
            case BatteryManager.BATTERY_HEALTH_COLD:
                return "Cold";
            default:
                return context.getString(R.string.unknown);
        }
    }

    public void saveStockValues(Context context) {
        if (hasHvInput()) {
            AppSettings.saveString("bat_hv_input", getHvInput(), context);
        } else {
            AppSettings.remove("bat_hv_input", context);
        }
        if (hasHvCharge()) {
            AppSettings.saveString("bat_hv_charge", getHvCharge(), context);
        } else {
            AppSettings.remove("bat_hv_charge", context);
        }
        if (hasAcInput()) {
            AppSettings.saveString("bat_ac_input", getAcInput(), context);
        } else {
            AppSettings.remove("bat_ac_input", context);
        }
        if (hasAcCharge()) {
            AppSettings.saveString("bat_ac_charge", getAcCharge(), context);
        } else {
            AppSettings.remove("bat_ac_charge", context);
        }
        if (hasAcInputScreen()) {
            AppSettings.saveString("bat_ac_input_screen", getAcInputScreen(), context);
        } else {
            AppSettings.remove("bat_ac_input_screen", context);
        }
        if (hasAcChargeScreen()) {
            AppSettings.saveString("bat_ac_charge_screen", getAcChargeScreen(), context);
        } else {
            AppSettings.remove("bat_ac_charge_screen", context);
        }
        if (hasUsbInput()) {
            AppSettings.saveString("bat_usb_input", getUsbInput(), context);
        } else {
            AppSettings.remove("bat_usb_input", context);
        }
        if (hasUsbCharge()) {
            AppSettings.saveString("bat_usb_charge", getUsbCharge(), context);
        } else {
            AppSettings.remove("bat_usb_charge", context);
        }
        if (hasWcInput()) {
            AppSettings.saveString("bat_wc_input", getWcInput(), context);
        } else {
            AppSettings.remove("bat_wc_input", context);
        }
        if (hasWcCharge()) {
            AppSettings.saveString("bat_wc_charge", getWcCharge(), context);
        } else {
            AppSettings.remove("bat_wc_charge", context);
        }
        if (hasCarInput()) {
            AppSettings.saveString("bat_car_input", getCarInput(), context);
        } else {
            AppSettings.remove("bat_car_input", context);
        }
        if (hasCarCharge()) {
            AppSettings.saveString("bat_car_charge", getCarCharge(), context);
        } else {
            AppSettings.remove("bat_car_charge", context);
        }

        AppSettings.saveBoolean("battery_saved", true, context);
    }


    public boolean hasStoreMode(){
        return hasStoreModeToggle() || hasStoreModeMax() || hasStoreModeMin();
    }

    public boolean hasStoreModeToggle() {
        return exists(STORE_MODE);
    }

    public boolean hasStoreModeMax() {
        return !sStoreModeMaxFiles.isEmpty();
    }

    public boolean hasStoreModeMin() {
        return !sStoreModeMinFiles.isEmpty();
    }

    public boolean isStoreModeEnabled(){
        return "1".equals(Utils.readFile(STORE_MODE));
    }

    public void enableStoreMode(boolean enable, Context context){
        if (!hasStoreModeToggle()) return;
        run(Control.write(enable ? "1" : "0", STORE_MODE), STORE_MODE, context);
    }

    public String getStoreModeMax(){
        return readStoreModeLimit(sStoreModeMaxFiles);
    }

    public void setStoreModeMax(int value, Context context){
        if (!hasStoreModeMax()) return;
        run(buildStoreModeLimitCommand(value, sStoreModeMaxFiles), STORE_MODE_MAX, context);
    }

    public String getStoreModeMin(){
        return readStoreModeLimit(sStoreModeMinFiles);
    }

    public void setStoreModeMin(int value, Context context){
        if (!hasStoreModeMin()) return;
        run(buildStoreModeLimitCommand(value, sStoreModeMinFiles), STORE_MODE_MIN, context);
    }

    private String readStoreModeLimit(List<String> files) {
        for (String file : files) {
            String value = Utils.readFile(file);
            if (value != null && !value.isEmpty()) {
                return value;
            }
        }
        return "";
    }

    private String buildStoreModeLimitCommand(int value, List<String> files) {
        StringBuilder command = new StringBuilder();
        for (String file : files) {
            if (command.length() > 0) {
                command.append("; ");
            }
            command.append(Control.write(String.valueOf(value), file));
        }
        return command.toString();
    }

    public boolean hasDisableCharging(){
        return !sDisableChargingControls.isEmpty();
    }

    public boolean isDisableChargingEnabled(){
        for (DisableChargingControl control : sDisableChargingControls) {
            if (control.disableValue.equals(Utils.readFile(control.path))) {
                return true;
            }
        }
        return false;
    }

    public void enableDisableCharging(boolean enable, Context context){
        if (!hasDisableCharging()) return;

        StringBuilder command = new StringBuilder();
        for (DisableChargingControl control : sDisableChargingControls) {
            if (command.length() > 0) {
                command.append("; ");
            }
            command.append(Control.write(enable ? control.disableValue : control.enableValue,
                    control.path));
        }
        run(command.toString(), DISABLE_CHARGING, context);
    }

    public void setChargingCurrent(int value, Context context) {
        run(Control.write(String.valueOf(value), CUSTOM_CURRENT), CUSTOM_CURRENT, context);
    }

    public int getChargingCurrent() {
        return Utils.strToInt(Utils.readFile(CUSTOM_CURRENT));
    }

    public boolean hasChargingCurrent() {
        return Utils.existFile(CUSTOM_CURRENT);
    }

    public void enableChargeRate(boolean enable, Context context) {
        run(Control.write(enable ? "1" : "0", CHARGE_RATE_ENABLE), CHARGE_RATE_ENABLE, context);
    }

    public boolean isChargeRateEnabled() {
        return Utils.readFile(CHARGE_RATE_ENABLE).equals("1");
    }

    public boolean hasChargeRateEnable() {
        return Utils.existFile(CHARGE_RATE_ENABLE);
    }

    public void setBlx(int value, Context context) {
        run(Control.write(String.valueOf(value == 0 ? 101 : value - 1), BLX), BLX, context);
    }

    public int getBlx() {
        int value = Utils.strToInt(Utils.readFile(BLX));
        return value > 100 ? 0 : value + 1;
    }

    public boolean hasBlx() {
        return Utils.existFile(BLX);
    }

    public void enableForceFastCharge(boolean enable, Context context) {
        run(Control.write(enable ? "1" : "0", FORCE_FAST_CHARGE), FORCE_FAST_CHARGE, context);
    }

    public boolean isForceFastChargeEnabled() {
        return Utils.readFile(FORCE_FAST_CHARGE).equals("1");
    }

    public boolean hasForceFastCharge() {
        return Utils.existFile(FORCE_FAST_CHARGE);
    }

    public void enableAdaptiveFastCharge(boolean enable, Context context) {
        run(Control.write(!enable ? "1" : "0", ADAPTIVE_FAST_CHARGE), ADAPTIVE_FAST_CHARGE, context);
    }

    public boolean isAdaptiveFastChargeEnabled() {
        return !(Utils.readFile(ADAPTIVE_FAST_CHARGE).equals("1"));
    }

    public boolean hasAdaptiveFastCharge() {
        return Utils.existFile(ADAPTIVE_FAST_CHARGE);
    }

    public static int getCapacity() {
        return mCapacity;
    }

    public boolean hasCapacity() {
        return getCapacity() != 0;
    }

    public boolean supported() {
        return hasCapacity();
    }

    private void run(String command, String id, Context context) {
        Control.runSetting(command, ApplyOnBootFragment.BATTERY, id, context);
    }

    /* Init Battery */

    public boolean hasHvInput() {
        return exists(HV_INPUT);
    }

    public void setHvInput(int value, Context context) {
        run(Control.write(String.valueOf(value), HV_INPUT), HV_INPUT, context);
    }

    public String getHvInput() {
        return Utils.readFile(HV_INPUT);
    }

    public boolean hasHvCharge() {
        return exists(HV_CHARGE);
    }

    public void setHvCharge(int value, Context context) {
        run(Control.write(String.valueOf(value), HV_CHARGE), HV_CHARGE, context);
    }

    public String getHvCharge() {
        return Utils.readFile(HV_CHARGE);
    }

    public boolean hasAcInput() {
        return exists(AC_INPUT);
    }

    public void setAcInput(int value, Context context) {
        run(Control.write(String.valueOf(value), AC_INPUT), AC_INPUT, context);
    }

    public String getAcInput() {
        return Utils.readFile(AC_INPUT);
    }

    public boolean hasAcCharge() {
        return exists(AC_CHARGE);
    }

    public void setAcCharge(int value, Context context) {
        run(Control.write(String.valueOf(value), AC_CHARGE), AC_CHARGE, context);
    }

    public String getAcCharge() {
        return Utils.readFile(AC_CHARGE);
    }

    public boolean hasAcInputScreen() {
        return exists(AC_INPUT_SCREEN);
    }

    public void setAcInputScreen(int value, Context context) {
        run(Control.write(String.valueOf(value), AC_INPUT_SCREEN), AC_INPUT_SCREEN, context);
    }

    public String getAcInputScreen() {
        return Utils.readFile(AC_INPUT_SCREEN);
    }

    public boolean hasAcChargeScreen() {
        return exists(AC_CHARGE_SCREEN);
    }

    public void setAcChargeScreen(int value, Context context) {
        run(Control.write(String.valueOf(value), AC_CHARGE_SCREEN), AC_CHARGE_SCREEN, context);
    }

    public String getAcChargeScreen() {
        return Utils.readFile(AC_CHARGE_SCREEN);
    }

    public boolean hasUsbInput() {
        return exists(USB_INPUT);
    }

    public void setUsbInput(int value, Context context) {
        run(Control.write(String.valueOf(value), USB_INPUT), USB_INPUT, context);
    }

    public String getUsbInput() {
        return Utils.readFile(USB_INPUT);
    }

    public boolean hasUsbCharge() {
        return exists(USB_CHARGE);
    }

    public void setUsbCharge(int value, Context context) {
        run(Control.write(String.valueOf(value), USB_CHARGE), USB_CHARGE, context);
    }

    public String getUsbCharge() {
        return Utils.readFile(USB_CHARGE);
    }

    public boolean hasWcInput() {
        return exists(WC_INPUT);
    }

    public void setWcInput(int value, Context context) {
        run(Control.write(String.valueOf(value), WC_INPUT), WC_INPUT, context);
    }

    public String getWcInput() {
        return Utils.readFile(WC_INPUT);
    }

    public boolean hasWcCharge() {
        return exists(WC_CHARGE);
    }

    public void setWcCharge(int value, Context context) {
        run(Control.write(String.valueOf(value), WC_CHARGE), WC_CHARGE, context);
    }

    public String getWcCharge() {
        return Utils.readFile(WC_CHARGE);
    }

    public boolean hasCarCharge() {
        return exists(CAR_CHARGE);
    }

    public void setCarCharge(int value, Context context) {
        run(Control.write(String.valueOf(value), CAR_CHARGE), CAR_CHARGE, context);
    }

    public String getCarCharge() {
        return Utils.readFile(CAR_CHARGE);
    }

    public boolean hasCarInput() {
        return exists(CAR_INPUT);
    }

    public void setCarInput(int value, Context context) {
        run(Control.write(String.valueOf(value), CAR_INPUT), CAR_INPUT, context);
    }

    public String getCarInput() {
        return Utils.readFile(CAR_INPUT);
    }

    public static String getChargeSource(Context context) {
        if (CHARGE_SOURCE == null) {
            return getPluggedSource(context);
        }
        String value = Utils.readFile(CHARGE_SOURCE);
        if (value == null || value.isEmpty()) {
            return getPluggedSource(context);
        }
        switch (value){
            case "0" :
                return context.getResources().getString(R.string.cs_unknown);
            case "1" :
                return context.getResources().getString(R.string.cs_battery);
            case "2" :
                return context.getResources().getString(R.string.cs_ups);
            case "3" :
                return context.getResources().getString(R.string.cs_main_ac);
            case "4" :
                return context.getResources().getString(R.string.cs_usb);
            case "5" :
                return context.getResources().getString(R.string.cs_usb_dedeicated);
            case "6" :
                return context.getResources().getString(R.string.cs_usb_charging);
            case "7" :
                return context.getResources().getString(R.string.cs_usb_accesory);
            case "8" :
                return context.getResources().getString(R.string.cs_battery_monitor);
            case "9" :
                return context.getResources().getString(R.string.cs_misc);
            case "10" :
                return context.getResources().getString(R.string.cs_wireless);
            case "11" :
                return context.getResources().getString(R.string.cs_hv_wireless);
            case "12" :
                return context.getResources().getString(R.string.cs_pma_wireless);
            case "13" :
                return context.getResources().getString(R.string.cs_car);
            case "14" :
                return context.getResources().getString(R.string.cs_uart_off);
            case "15" :
                return context.getResources().getString(R.string.cs_otg);
            case "16" :
                return context.getResources().getString(R.string.cs_lan);
            case "17" :
                return context.getResources().getString(R.string.cs_mhl_500);
            case "18" :
                return context.getResources().getString(R.string.cs_mhl_900);
            case "19" :
                return context.getResources().getString(R.string.cs_mhl_1500);
            case "20" :
                return context.getResources().getString(R.string.cs_mhl_usb);
            case "21" :
                return context.getResources().getString(R.string.cs_smart_otg);
            case "22" :
                return context.getResources().getString(R.string.cs_smart_notg);
            case "23" :
                return context.getResources().getString(R.string.cs_power_sharing);
            case "24" :
                return context.getResources().getString(R.string.cs_hv_mains);
            case "25" :
                return context.getResources().getString(R.string.cs_hv_mains_12);
            case "26" :
                return context.getResources().getString(R.string.cs_hv_prepare);
            case "27" :
                return context.getResources().getString(R.string.cs_hv_error);
            case "28" :
                return context.getResources().getString(R.string.cs_mhl_100);
            case "29" :
                return context.getResources().getString(R.string.cs_mhl_2000);
            case "30" :
                return context.getResources().getString(R.string.cs_hv_unknown);
            case "31" :
                return context.getResources().getString(R.string.cs_mdock);
            case "32" :
                return context.getResources().getString(R.string.cs_hmt_conected);
            case "33" :
                return context.getResources().getString(R.string.cs_hmt_charge);
            case "34" :
                return context.getResources().getString(R.string.cs_wireless_pack);
            case "35" :
                return context.getResources().getString(R.string.cs_wireless_pack_ta);
            case "36" :
                return context.getResources().getString(R.string.cs_wireless_stand);
            case "37" :
                return context.getResources().getString(R.string.cs_wireless_hv_stand);
            case "38" :
                return context.getResources().getString(R.string.cs_pdic);
            case "39" :
                return context.getResources().getString(R.string.cs_hv_mains);
            case "40" :
                return context.getResources().getString(R.string.cs_qc20);
            case "41" :
                return context.getResources().getString(R.string.cs_qc30);
        }
        return "Unknown source";
    }

    private static String getPluggedSource(Context context) {
        Intent intent = context.registerReceiver(
                null, new IntentFilter(Intent.ACTION_BATTERY_CHANGED));
        if (intent == null) {
            return context.getString(R.string.cs_unknown);
        }

        switch (intent.getIntExtra(BatteryManager.EXTRA_PLUGGED, 0)) {
            case BatteryManager.BATTERY_PLUGGED_AC:
                return context.getResources().getString(R.string.cs_main_ac);
            case BatteryManager.BATTERY_PLUGGED_USB:
                return context.getResources().getString(R.string.cs_usb);
            case BatteryManager.BATTERY_PLUGGED_WIRELESS:
                return context.getResources().getString(R.string.cs_wireless);
            default:
                return context.getResources().getString(R.string.cs_battery);
        }
    }

    public boolean hasCharge() {
        return exists(BATTERY_POWER_SUPPLY_NODE) || exists(BATTERY_NODE);
    }

    public boolean hasUnstableCharge() {
        return exists(UNSTABLE_CHARGE);
    }

    private static boolean exists(String path) {
        return path != null && Utils.existFile(path);
    }

    private static class DisableChargingControl {
        private final String path;
        private final String disableValue;
        private final String enableValue;

        private DisableChargingControl(String path, String disableValue, String enableValue) {
            this.path = path;
            this.disableValue = disableValue;
            this.enableValue = enableValue;
        }
    }

    public boolean isUnstableChargeEnabled() {
        return Utils.readFile(UNSTABLE_CHARGE).equals("1");
    }

    public void enableUnstableCharge(boolean enable, Context context) {
        run(Control.write(enable ? "1" : "0", UNSTABLE_CHARGE), UNSTABLE_CHARGE, context);
    }
}
