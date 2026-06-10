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
package com.hades.hKtweaks.fragments.other;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.os.Messenger;
import androidx.fragment.app.Fragment;
import androidx.core.content.ContextCompat;
import androidx.core.hardware.fingerprint.FingerprintManagerCompat;
import androidx.appcompat.app.AlertDialog;
import androidx.preference.Preference;
import androidx.preference.PreferenceCategory;
import androidx.preference.PreferenceFragmentCompat;

import android.text.InputType;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.color.MaterialColors;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.hades.hKtweaks.R;
import com.hades.hKtweaks.activities.BannerResizerActivity;
import com.hades.hKtweaks.activities.MainActivity;
import com.hades.hKtweaks.activities.NavigationActivity;
import com.hades.hKtweaks.services.boot.ApplyOnBootService;
import com.hades.hKtweaks.services.profile.Widget;
import com.hades.hKtweaks.utils.AppSettings;
import com.hades.hKtweaks.utils.Themes;
import com.hades.hKtweaks.utils.AppUpdaterTask;
import com.hades.hKtweaks.utils.Utils;
import com.hades.hKtweaks.utils.ViewUtils;
import com.hades.hKtweaks.utils.root.RootUtils;
import com.hades.hKtweaks.views.BorderCircleView;
import com.hades.hKtweaks.views.dialog.Dialog;
import com.hades.hKtweaks.views.preference.MaterialListPreference;
import com.hades.hKtweaks.views.preference.MaterialSwitchPreference;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by willi on 13.08.16.
 */

public class SettingsFragment extends PreferenceFragmentCompat implements
        Preference.OnPreferenceChangeListener, Preference.OnPreferenceClickListener {

    private static final String KEY_RESET_DATA = "reset_data";
    private static final String KEY_UPDATE_NOTIFICATION = "app_update_notif";
    private static final String KEY_CHECK_UPDATE = "check_update";
    private static final String KEY_FORCE_ENGLISH = "forceenglish";
    //private static final String KEY_USER_INTERFACE = "user_interface";
    private static final String KEY_THEME_MODE = "theme_mode";
    //private static final String KEY_MATERIAL_ICON = "materialicon";
    private static final String KEY_BANNER_RESIZER = "banner_resizer";
    private static final String KEY_HIDE_BANNER = "hide_banner";
    private static final String KEY_THEME_COLOR = "theme_color";
    private static final String KEY_SECTIONS_ICON = "section_icons";
    private static final String KEY_APPLY_ON_BOOT_TEST = "applyonboottest";
    private static final String KEY_DEBUGGING_CATEGORY = "debugging_category";
    private static final String KEY_LOGCAT = "logcat";
    private static final String KEY_LAST_KMSG = "lastkmsg";
    private static final String KEY_DMESG = "dmesg";
    private static final String KEY_SECURITY_CATEGORY = "security_category";
    private static final String KEY_SET_PASSWORD = "set_password";
    private static final String KEY_DELETE_PASSWORD = "delete_password";
    private static final String KEY_FINGERPRINT = "fingerprint";
    private static final String KEY_SECTIONS = "sections";

    private Preference mFingerprint;

    private String mOldPassword;
    private String mDeletePassword;
    private int mColorSelection;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRetainInstance(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = super.onCreateView(inflater, container, savedInstanceState);
        rootView.setPadding(rootView.getPaddingLeft(),
                Math.round(ViewUtils.getActionBarSize(getActivity())),
                rootView.getPaddingRight(), rootView.getPaddingBottom());
        return rootView;
    }

    @Override
    public void onResume() {
        super.onResume();
        if (mOldPassword != null) {
            editPasswordDialog(mOldPassword);
        }
        if (mDeletePassword != null) {
            deletePasswordDialog(mDeletePassword);
        }
    }

    @Override
    @SuppressLint("RestrictedApi")
    public void onCreatePreferences(Bundle bundle, String s) {
        addPreferencesFromResource(R.xml.settings);

        MaterialListPreference themeMode =
                (MaterialListPreference) findPreference(KEY_THEME_MODE);
        themeMode.setValue(Themes.getThemeMode(requireContext()));

        MaterialSwitchPreference forceEnglish =
                (MaterialSwitchPreference) findPreference(KEY_FORCE_ENGLISH);
        if (Resources.getSystem().getConfiguration().locale.getLanguage().startsWith("en")) {
            getPreferenceScreen().removePreference(forceEnglish);
        } else {
            forceEnglish.setOnPreferenceChangeListener(this);
        }
/*
        if (Utils.hideStartActivity()) {
            ((PreferenceCategory) findPreference(KEY_USER_INTERFACE))
                    .removePreference(findPreference(KEY_MATERIAL_ICON));
        } else {
            findPreference(KEY_MATERIAL_ICON).setOnPreferenceChangeListener(this);
        }
*/
        findPreference(KEY_RESET_DATA).setOnPreferenceClickListener(this);
        findPreference(KEY_UPDATE_NOTIFICATION).setOnPreferenceChangeListener(this);
        findPreference(KEY_CHECK_UPDATE).setOnPreferenceClickListener(this);
        themeMode.setOnPreferenceChangeListener(this);
        findPreference(KEY_BANNER_RESIZER).setOnPreferenceClickListener(this);
        findPreference(KEY_HIDE_BANNER).setOnPreferenceChangeListener(this);
        findPreference(KEY_THEME_COLOR).setOnPreferenceClickListener(this);
        findPreference(KEY_SECTIONS_ICON).setOnPreferenceChangeListener(this);
        findPreference(KEY_APPLY_ON_BOOT_TEST).setOnPreferenceClickListener(this);
        findPreference(KEY_LOGCAT).setOnPreferenceClickListener(this);

        if (Utils.existFile("/proc/last_kmsg") || Utils.existFile("/sys/fs/pstore/console-ramoops")) {
            findPreference(KEY_LAST_KMSG).setOnPreferenceClickListener(this);
        } else {
            ((PreferenceCategory) findPreference(KEY_DEBUGGING_CATEGORY)).removePreference(
                    findPreference(KEY_LAST_KMSG));
        }

        findPreference(KEY_DMESG).setOnPreferenceClickListener(this);
        findPreference(KEY_SET_PASSWORD).setOnPreferenceClickListener(this);
        findPreference(KEY_DELETE_PASSWORD).setOnPreferenceClickListener(this);

        if (!FingerprintManagerCompat.from(requireContext()).isHardwareDetected()) {
            ((PreferenceCategory) findPreference(KEY_SECURITY_CATEGORY)).removePreference(
                    findPreference(KEY_FINGERPRINT));
        } else {
            mFingerprint = findPreference(KEY_FINGERPRINT);
            mFingerprint.setEnabled(!AppSettings.getPassword(getActivity()).isEmpty());
        }

        NavigationActivity activity = (NavigationActivity) getActivity();
        PreferenceCategory sectionsCategory = (PreferenceCategory) findPreference(KEY_SECTIONS);
        for (NavigationActivity.NavigationFragment navigationFragment : activity.getFragments()) {
            Class<? extends Fragment> fragmentClass = navigationFragment.mFragmentClass;
            int id = navigationFragment.mId;

            if (fragmentClass != null && fragmentClass != SettingsFragment.class) {
                MaterialSwitchPreference switchPreference =
                        new MaterialSwitchPreference(getActivity());
                switchPreference.setTitle(getString(id));
                switchPreference.setKey(fragmentClass.getSimpleName() + "_enabled");
                switchPreference.setChecked(AppSettings.isFragmentEnabled(fragmentClass, getActivity()));
                switchPreference.setOnPreferenceChangeListener(this);
                switchPreference.setPersistent(true);
                sectionsCategory.addPreference(switchPreference);
            }
        }
    }

    @Override
    public boolean onPreferenceChange(Preference preference, Object o) {
        String key = preference.getKey();
        switch (key) {
            case KEY_UPDATE_NOTIFICATION:
                if (o instanceof Boolean){
                    boolean checked = (boolean) o;
                    AppSettings.saveBoolean("show_update_notif", checked, getActivity());
                }
                return true;
            case KEY_FORCE_ENGLISH:
                updateWidgetsAfterPreferenceChange();
                restartSettings();
                return true;
            case KEY_THEME_MODE:
                Themes.saveThemeMode(String.valueOf(o), requireContext());
                updateWidgetsAfterPreferenceChange();
                restartSettings();
                return true;
/*
            case KEY_MATERIAL_ICON:
                Utils.setStartActivity(checked, getActivity());
                return true;
*/
            case KEY_HIDE_BANNER:
                return true;
            case KEY_SECTIONS_ICON:
                return updateNavigationPreference(key, o);
            default:
                if (key.endsWith("_enabled")) {
                    return updateNavigationPreference(key, o);
                }
                break;
        }
        return false;
    }

    private boolean updateNavigationPreference(String key, Object value) {
        if (!(value instanceof Boolean)) {
            return false;
        }
        AppSettings.saveBoolean(key, (Boolean) value, requireContext());
        ((NavigationActivity) requireActivity()).appendFragments();
        return true;
    }

    private void updateWidgetsAfterPreferenceChange() {
        Context applicationContext = requireContext().getApplicationContext();
        new Handler(Looper.getMainLooper()).post(() -> Widget.updateAll(applicationContext));
    }

    private void restartSettings() {
        requireActivity().finish();
        Intent intent = new Intent(requireContext(), MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
        intent.putExtra(NavigationActivity.INTENT_SECTION,
                SettingsFragment.class.getCanonicalName());
        startActivity(intent);
    }

    private static class MessengerHandler extends Handler {

        private final Context mContext;

        private MessengerHandler(Context context) {
            mContext = context;
        }

        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            if (msg.arg1 == 1 && mContext != null) {
                Utils.toast(R.string.nothing_apply, mContext);
            }
        }
    }

    @Override
    public boolean onPreferenceClick(Preference preference) {
        String key = preference.getKey();
        switch (key) {
            case KEY_RESET_DATA:
                resetDataDialog();
                return true;
            case KEY_CHECK_UPDATE:
                AppUpdaterTask.appCheckDialogAllways(getActivity());
                return true;
            case KEY_BANNER_RESIZER:
                Intent intent = new Intent(getActivity(), BannerResizerActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
                return true;
            case KEY_THEME_COLOR:
                colorDialog();
                return true;
            case KEY_APPLY_ON_BOOT_TEST:
                if (Utils.isServiceRunning(ApplyOnBootService.class, getActivity())) {
                    Utils.toast(R.string.apply_on_boot_running, getActivity());
                } else {
                    Intent intent2 = new Intent(getActivity(), ApplyOnBootService.class);
                    intent2.putExtra("messenger", new Messenger(new MessengerHandler(getActivity())));
                    Utils.startService(getActivity(), intent2);
                }
                return true;
            case KEY_LOGCAT:
                new Execute(getActivity()).execute("logcat -d > /sdcard/logcat.txt");
                return true;
            case KEY_LAST_KMSG:
                if (Utils.existFile("/proc/last_kmsg")) {
                    new Execute(getActivity()).execute("cat /proc/last_kmsg > /sdcard/last_kmsg.txt");
                } else if (Utils.existFile("/sys/fs/pstore/console-ramoops")) {
                    new Execute(getActivity()).execute("cat /sys/fs/pstore/console-ramoops > /sdcard/last_kmsg.txt");
                }
                return true;
            case KEY_DMESG:
                new Execute(getActivity()).execute("dmesg > /sdcard/dmesg.txt");
                return true;
            case KEY_SET_PASSWORD:
                editPasswordDialog(AppSettings.getPassword(getActivity()));
                return true;
            case KEY_DELETE_PASSWORD:
                deletePasswordDialog(AppSettings.getPassword(getActivity()));
                return true;
        }
        return false;
    }

    private static class Execute extends AsyncTask<String, Void, Void> {
        private final AlertDialog mProgressDialog;

        private Execute(Context context) {
            View progressView = LayoutInflater.from(context)
                    .inflate(R.layout.dialog_progress_material3, null);
            mProgressDialog = new MaterialAlertDialogBuilder(context)
                    .setView(progressView)
                    .setCancelable(false)
                    .create();
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            mProgressDialog.show();
        }

        @Override
        protected Void doInBackground(String... params) {
            RootUtils.runCommand(params[0]);
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            mProgressDialog.dismiss();
        }
    }

    private void resetDataDialog(){
        MaterialAlertDialogBuilder alert = new MaterialAlertDialogBuilder(getActivity());
        alert.setTitle(getString(R.string.reset_data_title));
        alert.setMessage(getString(R.string.reset_data_dialog1));
        alert.setNegativeButton(getString(R.string.cancel), (dialogInterface, i) -> {
        });
        alert.setPositiveButton(getString(R.string.ok), (dialog, id) -> {
                RootUtils.runCommand("rm -rf /data/.hKtweaks");
                RootUtils.runCommand("pm clear com.hades.hKtweaks");
        });
        alert.show();
    }

    private void editPasswordDialog(final String oldPass) {
        mOldPassword = oldPass;

        LinearLayout linearLayout = new LinearLayout(getActivity());
        linearLayout.setOrientation(LinearLayout.VERTICAL);
        linearLayout.setGravity(Gravity.CENTER);
        int padding = Math.round(getResources().getDimension(R.dimen.dialog_padding));
        linearLayout.setPadding(padding, padding, padding, padding);

        final TextInputEditText oldPassword;
        if (!oldPass.isEmpty()) {
            oldPassword = addPasswordInput(linearLayout, R.string.old_password);
            oldPassword.setInputType(InputType.TYPE_CLASS_TEXT
                    | InputType.TYPE_TEXT_VARIATION_PASSWORD);
        } else {
            oldPassword = null;
        }

        final TextInputEditText newPassword =
                addPasswordInput(linearLayout, R.string.new_password);
        newPassword.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);

        final TextInputEditText confirmNewPassword =
                addPasswordInput(linearLayout, R.string.confirm_new_password);
        confirmNewPassword.setInputType(InputType.TYPE_CLASS_TEXT
                | InputType.TYPE_TEXT_VARIATION_PASSWORD);

        new Dialog(getActivity()).setView(linearLayout)
                .setNegativeButton(getString(R.string.cancel), (dialogInterface, i) -> {
                })
                .setPositiveButton(getString(R.string.ok), (dialogInterface, i) -> {
                    if (!oldPass.isEmpty() && !oldPassword.getText().toString().equals(Utils
                            .decodeString(oldPass))) {
                        Utils.toast(getString(R.string.old_password_wrong), getActivity());
                        return;
                    }

                    if (newPassword.getText().toString().isEmpty()) {
                        Utils.toast(getString(R.string.password_empty), getActivity());
                        return;
                    }

                    if (!newPassword.getText().toString().equals(confirmNewPassword.getText()
                            .toString())) {
                        Utils.toast(getString(R.string.password_not_match), getActivity());
                        return;
                    }

                    if (newPassword.getText().toString().length() > 32) {
                        Utils.toast(getString(R.string.password_too_long), getActivity());
                        return;
                    }

                    AppSettings.savePassword(Utils.encodeString(newPassword.getText()
                            .toString()), getActivity());
                    if (mFingerprint != null) {
                        mFingerprint.setEnabled(true);
                    }
                })
                .setOnDismissListener(dialogInterface -> mOldPassword = null).show();
    }

    private void deletePasswordDialog(final String password) {
        if (password.isEmpty()) {
            Utils.toast(getString(R.string.set_password_first), getActivity());
            return;
        }

        mDeletePassword = password;

        LinearLayout linearLayout = new LinearLayout(getActivity());
        linearLayout.setOrientation(LinearLayout.VERTICAL);
        linearLayout.setGravity(Gravity.CENTER);
        int padding = Math.round(getResources().getDimension(R.dimen.dialog_padding));
        linearLayout.setPadding(padding, padding, padding, padding);

        final TextInputEditText mPassword =
                addPasswordInput(linearLayout, R.string.password);
        mPassword.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);

        new Dialog(getActivity()).setView(linearLayout)
                .setPositiveButton(getString(R.string.ok), (dialogInterface, i) -> {
                    if (!mPassword.getText().toString().equals(Utils.decodeString(password))) {
                        Utils.toast(getString(R.string.password_wrong), getActivity());
                        return;
                    }

                    AppSettings.resetPassword(getActivity());
                    if (mFingerprint != null) {
                        mFingerprint.setEnabled(false);
                    }
                })
                .setOnDismissListener(dialogInterface -> mDeletePassword = null).show();
    }

    private TextInputEditText addPasswordInput(LinearLayout parent, int hintRes) {
        TextInputLayout inputLayout = new TextInputLayout(requireContext());
        inputLayout.setBoxBackgroundMode(TextInputLayout.BOX_BACKGROUND_OUTLINE);
        inputLayout.setHint(getString(hintRes));
        inputLayout.setEndIconMode(TextInputLayout.END_ICON_PASSWORD_TOGGLE);
        inputLayout.setLayoutParams(new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));

        TextInputEditText editText = new TextInputEditText(inputLayout.getContext());
        editText.setLayoutParams(new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
        inputLayout.addView(editText);
        parent.addView(inputLayout);
        return editText;
    }

    private void colorDialog() {
        mColorSelection = -1;
        List<String> colors = new ArrayList<>(Themes.THEME_COLORS);
        int selection = colors.indexOf(Themes.getThemeColor(getActivity()));
        int outlineColor = MaterialColors.getColor(requireContext(),
                R.attr.colorOutline, ContextCompat.getColor(requireContext(), R.color.textcolor_light));
        LinearLayout linearLayout = new LinearLayout(getActivity());
        linearLayout.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT));
        linearLayout.setOrientation(LinearLayout.VERTICAL);
        int padding = (int) getResources().getDimension(R.dimen.dialog_padding);
        linearLayout.setPadding(padding, padding, padding, padding);

        final List<BorderCircleView> circles = new ArrayList<>();

        LinearLayout subView = null;
        for (int i = 0; i < colors.size(); i++) {
            if (subView == null || i % 5 == 0) {
                subView = new LinearLayout(getActivity());
                subView.setLayoutParams(new ViewGroup.LayoutParams(
                        ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
                linearLayout.addView(subView);
            }

            BorderCircleView circle = new BorderCircleView(getActivity());
            circle.setChecked(i == selection);
            int seedColor = ContextCompat.getColor(getActivity(),
                    Themes.getColor(colors.get(i)));
            circle.setCircleColor(seedColor);
            circle.setCheckColor(MaterialColors.isColorLight(seedColor)
                    ? Color.BLACK : Color.WHITE);
            circle.setBorderColor(outlineColor);
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT, 1);
            int margin = (int) getResources().getDimension(R.dimen.color_dialog_margin);
            params.setMargins(margin, margin, margin, margin);
            circle.setLayoutParams(params);
            circle.setOnClickListener(v -> {
                for (BorderCircleView borderCircleView : circles) {
                    if (v == borderCircleView) {
                        borderCircleView.setChecked(true);
                        mColorSelection = circles.indexOf(borderCircleView);
                    } else {
                        borderCircleView.setChecked(false);
                    }
                }
            });

            circles.add(circle);
            subView.addView(circle);
        }

        new Dialog(getActivity()).setTitle(getString(R.string.theme_color))
                .setView(linearLayout)
                .setNegativeButton(getString(R.string.cancel), (dialog, which) -> {
                })
                .setPositiveButton(getString(R.string.ok), (dialog, which) -> {
                    if (mColorSelection < 0) {
                        return;
                    }
                    Themes.saveThemeColor(colors.get(mColorSelection), getActivity());
                    Widget.updateAll(requireContext().getApplicationContext());
                    getActivity().finish();
                    Intent intent = new Intent(getActivity(), MainActivity.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    intent.putExtra(NavigationActivity.INTENT_SECTION,
                            SettingsFragment.class.getCanonicalName());
                    startActivity(intent);
                })
                .setOnDismissListener(dialog -> mColorSelection = -1).show();
    }

}
