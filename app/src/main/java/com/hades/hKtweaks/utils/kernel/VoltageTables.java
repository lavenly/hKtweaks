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
package com.hades.hKtweaks.utils.kernel;

import com.hades.hKtweaks.utils.Utils;
import com.hades.hKtweaks.utils.root.RootUtils;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public final class VoltageTables {

    private static final List<String> sSingleClusterCpuVoltageTables = new ArrayList<>();

    private VoltageTables() {
    }

    public static String firstExisting(String... paths) {
        for (String path : paths) {
            if (path != null && Utils.existFile(path)) {
                return path;
            }
        }
        return null;
    }

    public static String findDevfreqVoltageTable(String defaultPath, String token) {
        return firstVoltageTable(defaultPath,
                findChildFile("/sys/class/devfreq", "volt_table", token),
                findChildFile("/sys/class/devfreq", "voltage_table", token));
    }

    public static String findCpuVoltageTable(String defaultPath, String clusterToken,
                                             String shortClusterToken) {
        String path = firstVoltageTable(defaultPath,
                findChildFile("/sys/devices/system/cpu/cpufreq/mp-cpufreq",
                        "volt_table", clusterToken),
                findChildFile("/sys/devices/system/cpu/cpufreq/mp-cpufreq",
                        "volt_table", shortClusterToken),
                findMatchingFile("/sys/devices/system/cpu/cpufreq/mp-cpufreq",
                        clusterToken, "volt"),
                findMatchingFile("/sys/devices/system/cpu/cpufreq/mp-cpufreq",
                        shortClusterToken, "volt"),
                findMatchingFile("/sys/devices/system/cpu/cpufreq",
                        clusterToken, "volt"),
                findMatchingFile("/sys/devices/system/cpu/cpufreq",
                        shortClusterToken, "volt"));
        return path != null ? path : findLegacyCpuVoltageTable(clusterToken, shortClusterToken);
    }

    private static String findLegacyCpuVoltageTable(String clusterToken, String shortClusterToken) {
        String cluster = clusterToken == null ? "" : clusterToken.toLowerCase(Locale.US);
        String shortCluster = shortClusterToken == null ? ""
                : shortClusterToken.toLowerCase(Locale.US);
        boolean littleCluster = cluster.contains("cluster0")
                || shortCluster.equals("cl0")
                || cluster.contains("little")
                || shortCluster.contains("little");
        boolean bigCluster = cluster.contains("cluster1")
                || shortCluster.equals("cl1")
                || cluster.contains("big")
                || shortCluster.contains("big")
                || shortCluster.contains("kfc");

        if (littleCluster) {
            return firstVoltageTable(
                    findSingleClusterCpuVoltageTable(),
                    "/sys/devices/system/cpu/cpufreq/mp-cpufreq/cpu_volt_table",
                    findMatchingFile("/sys/devices/system/cpu/cpufreq/mp-cpufreq",
                            "cpu", "volt", "table"),
                    findMatchingFile("/sys/devices/system/cpu/cpufreq",
                            "cpu", "volt", "table"),
                    findMatchingFile("/sys/devices/system/cpu/cpufreq/mp-cpufreq",
                            "little", "volt"),
                    findMatchingFile("/sys/devices/system/cpu/cpufreq",
                            "little", "volt"));
        }

        if (bigCluster) {
            return firstVoltageTable(
                    "/sys/devices/system/cpu/cpufreq/mp-cpufreq/kfc_volt_table",
                    findMatchingFile("/sys/devices/system/cpu/cpufreq/mp-cpufreq",
                            "kfc", "volt", "table"),
                    findMatchingFile("/sys/devices/system/cpu/cpufreq",
                            "kfc", "volt", "table"),
                    findMatchingFile("/sys/devices/system/cpu/cpufreq/mp-cpufreq",
                            "big", "volt"),
                    findMatchingFile("/sys/devices/system/cpu/cpufreq",
                            "big", "volt"));
        }

        return null;
    }

    private static String findSingleClusterCpuVoltageTable() {
        String path = firstVoltageTable(
                findChildFile("/sys/devices/system/cpu/cpufreq", "volt_table", "exynos"),
                findChildFile("/sys/devices/system/cpu/cpufreq", "voltage_table", "exynos"));
        if (path == null) {
            path = findNonClusterChildVoltageTable(
                    "/sys/devices/system/cpu/cpufreq", "volt_table");
        }
        if (path == null) {
            path = findNonClusterChildVoltageTable(
                    "/sys/devices/system/cpu/cpufreq", "voltage_table");
        }
        rememberSingleClusterCpuVoltageTable(path);
        return path;
    }

    private static String findNonClusterChildVoltageTable(String rootPath, String childFileName) {
        File root = new File(rootPath);
        File[] children = root.listFiles();
        if (children == null) {
            return findNonClusterChildVoltageTableWithRoot(rootPath, childFileName);
        }

        for (File child : children) {
            if (!child.isDirectory() || isClusterSpecificCpuVoltagePath(child.getAbsolutePath())) {
                continue;
            }

            String path = child.getAbsolutePath() + "/" + childFileName;
            if (Utils.existFile(path) && hasVoltageTable(path)) {
                return path;
            }
        }
        return findNonClusterChildVoltageTableWithRoot(rootPath, childFileName);
    }

    private static String findNonClusterChildVoltageTableWithRoot(String rootPath,
                                                                 String childFileName) {
        String output = RootUtils.runCommand("find -L '" + rootPath
                + "' -maxdepth 2 -type f -name '" + childFileName + "' 2>/dev/null");
        if (output == null || output.isEmpty()) return null;

        for (String path : output.split("\\r?\\n")) {
            if (path == null || isClusterSpecificCpuVoltagePath(path)) {
                continue;
            }
            if (Utils.existFile(path) && hasVoltageTable(path)) {
                return path;
            }
        }
        return null;
    }

    private static boolean isClusterSpecificCpuVoltagePath(String path) {
        if (path == null) return true;

        String lowerPath = path.toLowerCase(Locale.US);
        return lowerPath.contains("/mp-cpufreq/")
                || lowerPath.contains("cluster")
                || lowerPath.contains("/policy")
                || lowerPath.matches(".*/cpu[0-9]+(/.*)?");
    }

    private static void rememberSingleClusterCpuVoltageTable(String path) {
        if (path != null && !sSingleClusterCpuVoltageTables.contains(path)) {
            sSingleClusterCpuVoltageTables.add(path);
        }
    }

    public static boolean isSingleClusterCpuVoltageTable(String path) {
        return path != null && sSingleClusterCpuVoltageTables.contains(path);
    }

    public static String findMaliVoltageTable(String defaultPath) {
        return firstVoltageTable(defaultPath,
                findChildFile("/sys/devices/platform", "volt_table", "mali"),
                findChildFile("/sys/devices/platform", "voltage_table", "mali"),
                findChildFile("/sys/devices", "volt_table", "mali"),
                findChildFile("/sys/devices", "voltage_table", "mali"),
                findChildFile("/sys/class/devfreq", "volt_table", "mali"),
                findChildFile("/sys/class/devfreq", "voltage_table", "mali"));
    }

    private static String firstVoltageTable(String... paths) {
        for (String path : paths) {
            if (path != null && Utils.existFile(path) && hasVoltageTable(path)) {
                return path;
            }
        }
        return null;
    }

    public static String findChildFile(String rootPath, String childFileName, String... tokens) {
        File root = new File(rootPath);
        File[] children = root.listFiles();
        if (children == null) return findChildFileWithRoot(rootPath, childFileName, tokens);

        for (File child : children) {
            if (child.isDirectory() && matches(child.getName(), tokens)) {
                String path = child.getAbsolutePath() + "/" + childFileName;
                if (Utils.existFile(path) && hasVoltageTable(path)) {
                    return path;
                }
            }
        }
        return findChildFileWithRoot(rootPath, childFileName, tokens);
    }

    public static String findMatchingFile(String rootPath, String... tokens) {
        File root = new File(rootPath);
        File[] children = root.listFiles();
        if (children == null) return findMatchingFileWithRoot(rootPath, tokens);

        for (File child : children) {
            if (!child.isDirectory()
                    && matches(child.getName(), tokens)
                    && Utils.existFile(child.getAbsolutePath())
                    && hasVoltageTable(child.getAbsolutePath())) {
                return child.getAbsolutePath();
            }
        }
        return findMatchingFileWithRoot(rootPath, tokens);
    }

    public static boolean hasVoltageTable(String path) {
        String value = readFile(path);
        List<String> freqs = parseTableColumn(value, 0, 1);
        List<String> volts = parseTableColumn(value, 1, 1);
        return freqs != null && volts != null && freqs.size() == volts.size();
    }

    public static List<String> parseTableColumn(String value, int column, int offset) {
        if (value == null || value.isEmpty()) return null;

        int safeOffset = offset == 0 ? 1 : offset;
        List<String> values = new ArrayList<>();
        for (String line : value.split("\\r?\\n")) {
            if (line == null) continue;

            String trimmed = line.trim();
            if (trimmed.isEmpty() || trimmed.startsWith("#")) continue;

            String[] parts = trimmed.split("\\s+");
            if (parts.length <= column) continue;

            String number = cleanNumber(parts[column]);
            if (number.isEmpty()) continue;

            values.add(String.valueOf(Utils.strToFloat(number) / safeOffset));
        }
        return values.isEmpty() ? null : values;
    }

    public static List<String> parseFrequencyColumn(String value, int offset) {
        if (value == null || value.isEmpty()) return null;

        int safeOffset = offset == 0 ? 1 : offset;
        List<String> values = new ArrayList<>();
        for (String line : value.split("\\r?\\n")) {
            if (line == null) continue;

            String trimmed = line.trim();
            if (trimmed.isEmpty() || trimmed.startsWith("#")) continue;

            String[] parts = trimmed.split("\\s+");
            if (parts.length == 0) continue;

            String number = cleanNumber(parts[0]);
            if (number.isEmpty()) continue;

            values.add(String.valueOf(Utils.strToInt(number) / safeOffset));
        }
        return values.isEmpty() ? null : values;
    }

    public static String readFile(String path) {
        return path == null ? null : Utils.readFile(path);
    }

    private static String findChildFileWithRoot(String rootPath, String childFileName,
                                                String... tokens) {
        return firstMatchingPath("find -L '" + rootPath + "' -maxdepth 2 -type f -name '"
                + childFileName + "' 2>/dev/null", tokens);
    }

    private static String findMatchingFileWithRoot(String rootPath, String... tokens) {
        return firstMatchingPath("find -L '" + rootPath + "' -maxdepth 1 -type f 2>/dev/null",
                tokens);
    }

    private static String firstMatchingPath(String command, String... tokens) {
        String output = RootUtils.runCommand(command);
        if (output == null || output.isEmpty()) return null;

        for (String path : output.split("\\r?\\n")) {
            if (path != null && matches(path, tokens)
                    && Utils.existFile(path)
                    && hasVoltageTable(path)) {
                return path;
            }
        }
        return null;
    }

    private static String cleanNumber(String value) {
        return value == null ? "" : value.replaceAll("[^0-9.\\-]", "");
    }

    private static boolean matches(String name, String... tokens) {
        if (name == null) return false;

        String lowerName = name.toLowerCase(Locale.US);
        for (String token : tokens) {
            if (token == null || !lowerName.contains(token.toLowerCase(Locale.US))) {
                return false;
            }
        }
        return true;
    }
}
