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
package com.hades.hKtweaks.utils.kernel.bus;

import android.content.Context;

import com.hades.hKtweaks.fragments.ApplyOnBootFragment;
import com.hades.hKtweaks.utils.Utils;
import com.hades.hKtweaks.utils.kernel.VoltageTables;
import com.hades.hKtweaks.utils.root.Control;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

/**
 * Created by morogoku on 06.11.18.
 */
public class VoltageInt {

    public static final String BACKUP = "/data/.hKtweaks/busInt_stock_voltage";

    public static final String VOLTAGE = "/sys/class/devfreq/17000020.devfreq_int/volt_table";

    private static final HashMap<String, Boolean> sVoltages = new HashMap<>();
    private static final HashMap<String, Integer> sOffset = new HashMap<>();
    private static final HashMap<String, Integer> sOffsetFreq = new HashMap<>();
    private static final HashMap<String, String> sSplitNewline = new HashMap<>();
    private static final HashMap<String, String> sSplitLine = new HashMap<>();
    private static final HashMap<String, Boolean> sAppend = new HashMap<>();

    static {
        registerVoltagePath(VOLTAGE);
    }

    private static String PATH;
    private static String[] sFreqs;

    public static void setVoltage(String freq, String voltage, Context context) {
        if (!supported()) return;

        List<String> freqs = getFreqs();
        if (freqs == null) return;

        int position = freqs.indexOf(freq);
        if (sAppend.get(PATH)) {
            String command = "";
            List<String> voltages = getVoltages();
            if (voltages == null || position < 0) return;
            for (int i = 0; i < voltages.size(); i++) {
                if (i == position) {
                    command += command.isEmpty() ? voltage : " " + voltage;
                } else {
                    command += command.isEmpty() ? voltages.get(i) : " " + voltages.get(i);
                }
            }
            run(Control.write(command, PATH), PATH, context);
        } else {
            freq = String.valueOf(Utils.strToInt(freq) * sOffsetFreq.get(PATH));
            String volt = String.valueOf((int)(Utils.strToFloat(voltage) * sOffset.get(PATH)));
            run(Control.write(freq + " " + volt, PATH), PATH + freq, context);
        }

    }

    public static int getOffset () {
        Integer offset = sOffset.get(PATH);
        return offset != null && offset != 0 ? offset : 1;
    }

    public static List<String> getStockVoltages() {
        if (!supported()) return null;

        String value = VoltageTables.readFile(BACKUP);
        if (value == null || value.isEmpty()) {
            value = VoltageTables.readFile(PATH);
        }
        return VoltageTables.parseTableColumn(value, 1, getOffset());
    }

    public static List<String> getVoltages() {
        if (!supported()) return null;

        return VoltageTables.parseTableColumn(VoltageTables.readFile(PATH), 1, getOffset());
    }

    public static List<String> getFreqs() {
        if (!supported()) return null;

        if (sFreqs == null) {
            List<String> freqs = VoltageTables.parseFrequencyColumn(
                    VoltageTables.readFile(PATH), sOffsetFreq.get(PATH));
            if (freqs != null) {
                sFreqs = freqs.toArray(new String[0]);
            }
        }
        if (sFreqs == null) return null;
        return Arrays.asList(sFreqs);
    }

    public static boolean supported() {
        if (PATH != null && Utils.existFile(PATH) && VoltageTables.hasVoltageTable(PATH)) {
            return true;
        }

        PATH = null;
        sFreqs = null;
        String path = VoltageTables.findDevfreqVoltageTable(VOLTAGE, "int");
        if (path != null && VoltageTables.hasVoltageTable(path)) {
            registerVoltagePath(path);
            PATH = path;
        }
        return PATH != null;
    }

    public static String getPath() {
        supported();
        return PATH;
    }

    private static void registerVoltagePath(String path) {
        if (path == null || sVoltages.containsKey(path)) return;

        sVoltages.put(path, false);
        sOffsetFreq.put(path, 1000);
        sOffset.put(path, 1000);
        sSplitNewline.put(path, "\\r?\\n");
        sSplitLine.put(path, " ");
        sAppend.put(path, false);
    }

    private static void run(String command, String id, Context context) {
        Control.runSetting(command, ApplyOnBootFragment.BUS_INT, id, context);
    }

}
