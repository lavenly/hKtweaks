package com.hades.hKtweaks.utils.kernel.cpuhotplug;

import android.content.Context;

import com.hades.hKtweaks.fragments.ApplyOnBootFragment;
import com.hades.hKtweaks.utils.Utils;
import com.hades.hKtweaks.utils.root.Control;

/**
 * Created by Morogoku on 25/04/2017.
 */

public class SamsungPlug {

    private static final String[] HOTPLUG_PATHS = {
            "/sys/power/cpuhotplug",
            "/sys/kernel/dyn_hotplug",
            "/sys/kernel/dynamic_hotplug",
            "/sys/module/dyn_hotplug/parameters",
            "/sys/module/dynamic_hotplug/parameters",
            "/sys/devices/system/cpu/cpuhotplug"
    };

    private static final String[] ENABLE_NAMES = {
            "enabled",
            "enable",
            "hotplug_enabled",
            "hotplug_enable"
    };

    private static final String[] MAX_ONLINE_CPU_NAMES = {
            "max_online_cpu",
            "max_online_cpus",
            "max_cpus_online",
            "max_cpus",
            "max_cpu_online",
            "max_core_online"
    };

    private static final String[] MIN_ONLINE_CPU_NAMES = {
            "min_online_cpu",
            "min_online_cpus",
            "min_cpus_online",
            "min_cpus",
            "min_cpu_online",
            "min_core_online"
    };

    public static void enableSamsungPlug(boolean enable, Context context) {
        String path = getEnablePath();
        if (path != null) {
            run(Control.write(enable ? "1" : "0", path), path, context);
        }
    }

    public static boolean isSamsungPlugEnabled() {
        String path = getEnablePath();
        return path != null && isEnabledValue(Utils.readFile(path));
    }

    public static String getMaxOnlineCpu() {
        return getCpuValue(getMaxOnlineCpuPath(), "max online cpu : ");
    }

    public static void setMaxOnlineCpu(int value, Context context) {
        String path = getMaxOnlineCpuPath();
        if (path != null) {
            run(Control.write(String.valueOf(value), path), path, context);
        }
    }

    public static String getMinOnlineCpu() {
        return getCpuValue(getMinOnlineCpuPath(), "min online cpu : ");
    }

    public static void setMinOnlineCpu(int value, Context context) {
        String path = getMinOnlineCpuPath();
        if (path != null) {
            run(Control.write(String.valueOf(value), path), path, context);
        }
    }

    public static boolean hasEnable() {
        return getEnablePath() != null;
    }

    public static boolean hasMaxOnlineCpu() {
        return getMaxOnlineCpuPath() != null;
    }

    public static boolean hasMinOnlineCpu() {
        return getMinOnlineCpuPath() != null;
    }

    public static boolean supported() {
        return getBasePath() != null;
    }

    public static boolean isDynamicHotplug() {
        String basePath = getBasePath();
        return basePath != null && !"/sys/power/cpuhotplug".equals(basePath);
    }

    private static String getBasePath() {
        for (String path : HOTPLUG_PATHS) {
            if (Utils.existFile(path) && hasAnyNode(path, ENABLE_NAMES,
                    MAX_ONLINE_CPU_NAMES, MIN_ONLINE_CPU_NAMES)) {
                return path;
            }
        }
        return firstExistingParent(ENABLE_NAMES, MAX_ONLINE_CPU_NAMES, MIN_ONLINE_CPU_NAMES);
    }

    private static String firstExistingParent(String[]... nodeNameGroups) {
        for (String basePath : HOTPLUG_PATHS) {
            for (String[] nodeNames : nodeNameGroups) {
                for (String nodeName : nodeNames) {
                    if (Utils.existFile(basePath + "/" + nodeName)) {
                        return basePath;
                    }
                }
            }
        }
        return null;
    }

    private static boolean hasAnyNode(String basePath, String[]... nodeNameGroups) {
        for (String[] nodeNames : nodeNameGroups) {
            for (String nodeName : nodeNames) {
                if (Utils.existFile(basePath + "/" + nodeName)) {
                    return true;
                }
            }
        }
        return false;
    }

    private static String getEnablePath() {
        return getNodePath(ENABLE_NAMES);
    }

    private static String getMaxOnlineCpuPath() {
        return getNodePath(MAX_ONLINE_CPU_NAMES);
    }

    private static String getMinOnlineCpuPath() {
        return getNodePath(MIN_ONLINE_CPU_NAMES);
    }

    private static String getNodePath(String... names) {
        String basePath = getBasePath();
        if (basePath == null) {
            return null;
        }
        for (String name : names) {
            String path = basePath + "/" + name;
            if (Utils.existFile(path)) {
                return path;
            }
        }
        return null;
    }

    private static String getCpuValue(String path, String label) {
        if (path == null) {
            return null;
        }
        String value = Utils.readFile(path);
        if (!value.isEmpty()) {
            return value.replace(label, "").trim();
        }
        return null;
    }

    private static boolean isEnabledValue(String value) {
        return "1".equals(value) || "Y".equalsIgnoreCase(value)
                || "on".equalsIgnoreCase(value) || "enabled".equalsIgnoreCase(value);
    }

    private static void run(String command, String id, Context context) {
        Control.runSetting(command, ApplyOnBootFragment.CPU_HOTPLUG, id, context);
    }
}
