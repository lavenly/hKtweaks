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
package com.hades.hKtweaks.utils;

import android.content.Context;
import android.content.DialogInterface;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.Drawable;

import androidx.core.content.ContextCompat;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.bumptech.glide.Glide;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.hades.hKtweaks.R;
import com.hades.hKtweaks.views.dialog.Dialog;

/**
 * Created by willi on 16.04.16.
 */
public class ViewUtils {

    public static int getTextSecondaryColor(Context context) {
        TypedValue value = new TypedValue();
        context.getTheme().resolveAttribute(android.R.attr.textColorSecondary, value, true);
        return value.data;
    }

    public static Drawable getSelectableBackground(Context context) {
        TypedArray typedArray = context.obtainStyledAttributes(new int[]{R.attr.selectableItemBackground});
        Drawable drawable = typedArray.getDrawable(0);
        typedArray.recycle();
        return drawable;
    }

    public static void showDialog(FragmentManager manager, DialogFragment fragment) {
        FragmentTransaction ft = manager.beginTransaction();
        fragment.show(ft, "dialog");
    }

    public static void dismissDialog(FragmentManager manager) {
        Fragment fragment = manager.findFragmentByTag("dialog");
        if (fragment instanceof DialogFragment) {
            ((DialogFragment) fragment).dismiss();
        }
    }

    public static float getActionBarSize(Context context) {
        TypedArray typedArray = context.obtainStyledAttributes(new int[]{R.attr.actionBarSize});
        float size = typedArray.getDimension(0, 0);
        typedArray.recycle();
        return size;
    }

    public static int getColorPrimaryColor(Context context) {
        return getThemeColor(context, R.attr.colorPrimary);
    }

    public static int getColorSecondaryColor(Context context) {
        return getThemeColor(context, R.attr.colorSecondary);
    }

    public static int getColorOutlineColor(Context context) {
        return getThemeColor(context, R.attr.colorOutline);
    }

    public static int getThemeColor(Context context, int attribute) {
        TypedValue value = new TypedValue();
        if (!context.getTheme().resolveAttribute(attribute, value, true)) {
            return Color.TRANSPARENT;
        }
        return value.resourceId != 0
                ? ContextCompat.getColor(context, value.resourceId)
                : value.data;
    }

    public interface OnDialogEditTextListener {
        void onClick(String text);
    }

    public interface onDialogEditTextsListener {
        void onClick(String text, String text2);
    }

    public static Dialog dialogDonate(final Context context) {
        return new Dialog(context).setTitle(context.getString(R.string.donate))
                .setMessage(context.getString(R.string.donate_summary))
                .setNegativeButton(context.getString(R.string.donate_nope), (dialog, which) -> {
                })
                .setPositiveButton(context.getString(R.string.donate_yes), (dialog, which)
                        -> Utils.launchUrl("https://play.google.com/store/apps/details?id=com.grarak.kerneladiutordonate", context));
    }

    public static Dialog dialogEditTexts(String text, String text2, String hint, String hint2,
                                         final DialogInterface.OnClickListener negativeListener,
                                         final onDialogEditTextsListener onDialogEditTextListener,
                                         Context context) {
        LinearLayout layout = new LinearLayout(context);
        layout.setOrientation(LinearLayout.VERTICAL);
        int padding = (int) context.getResources().getDimension(R.dimen.dialog_padding);
        layout.setPadding(padding, padding, padding, padding);

        final TextInputEditText editText = addTextInput(layout, hint, context);
        if (text != null) {
            editText.append(text);
        }
        editText.setSingleLine(true);

        final TextInputEditText editText2 = addTextInput(layout, hint2, context);
        if (text2 != null) {
            editText2.setText(text2);
        }
        editText2.setSingleLine(true);

        Dialog dialog = new Dialog(context).setView(layout);
        if (negativeListener != null) {
            dialog.setNegativeButton(context.getString(R.string.cancel), negativeListener);
        }
        if (onDialogEditTextListener != null) {
            dialog
                    .setPositiveButton(context.getString(R.string.ok), (dialog1, which)
                            -> onDialogEditTextListener.onClick(
                            editText.getText().toString(), editText2.getText().toString()))
                    .setOnDismissListener(dialog1 -> {
                        if (negativeListener != null) {
                            negativeListener.onClick(dialog1, 0);
                        }
                    });
        }
        return dialog;
    }

    public static Dialog dialogEditText(String text, final DialogInterface.OnClickListener negativeListener,
                                        final OnDialogEditTextListener onDialogEditTextListener,
                                        Context context) {
        return dialogEditText(text, negativeListener, onDialogEditTextListener, -1, context);
    }

    public static Dialog dialogEditText(String text, final DialogInterface.OnClickListener negativeListener,
                                        final OnDialogEditTextListener onDialogEditTextListener, int inputType,
                                        Context context) {
        LinearLayout layout = new LinearLayout(context);
        int padding = (int) context.getResources().getDimension(R.dimen.dialog_padding);
        layout.setPadding(padding, padding, padding, padding);

        final TextInputEditText editText = addTextInput(layout, null, context);
        editText.setGravity(Gravity.CENTER);
        if (text != null) {
            editText.append(text);
        }
        editText.setSingleLine(true);
        if (inputType >= 0) {
            editText.setInputType(inputType);
        }
        Dialog dialog = new Dialog(context).setView(layout);
        if (negativeListener != null) {
            dialog.setNegativeButton(context.getString(R.string.cancel), negativeListener);
        }
        if (onDialogEditTextListener != null) {
            dialog.setPositiveButton(context.getString(R.string.ok), (dialog1, which)
                    -> onDialogEditTextListener.onClick(editText.getText().toString()))
                    .setOnDismissListener(dialog1 -> {
                        if (negativeListener != null) {
                            negativeListener.onClick(dialog1, 0);
                        }
                    });
        }
        return dialog;
    }

    private static TextInputEditText addTextInput(LinearLayout parent, String hint,
                                                   Context context) {
        TextInputLayout inputLayout = new TextInputLayout(context);
        inputLayout.setBoxBackgroundMode(TextInputLayout.BOX_BACKGROUND_OUTLINE);
        inputLayout.setHint(hint);
        inputLayout.setLayoutParams(new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));

        TextInputEditText editText = new TextInputEditText(inputLayout.getContext());
        editText.setLayoutParams(new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
        inputLayout.addView(editText);
        parent.addView(inputLayout);
        return editText;
    }

    public static Dialog dialogBuilder(CharSequence message, DialogInterface.OnClickListener negativeListener,
                                       DialogInterface.OnClickListener positiveListener,
                                       DialogInterface.OnDismissListener dismissListener, Context context) {
        Dialog dialog = new Dialog(context).setMessage(message);
        if (negativeListener != null) {
            dialog.setNegativeButton(context.getString(R.string.cancel), negativeListener);
        }
        if (positiveListener != null) {
            dialog.setPositiveButton(context.getString(R.string.ok), positiveListener);
        }
        if (dismissListener != null) {
            dialog.setOnDismissListener(dismissListener);
        }
        return dialog;
    }

    public static void loadImagefromUrl(String url, ImageView imageView) {
        Glide.with(imageView.getContext()).load(url).into(imageView);
    }

    public static Bitmap scaleDownBitmap(Bitmap bitmap, int maxWidth, int maxHeight) {
        int width = bitmap.getWidth();
        int height = bitmap.getHeight();

        int newWidth = width;
        int newHeight = height;

        if (maxWidth != 0 && newWidth > maxWidth) {
            newHeight = Math.round((float) maxWidth / newWidth * newHeight);
            newWidth = maxWidth;
        }

        if (maxHeight != 0 && newHeight > maxHeight) {
            newWidth = Math.round((float) maxHeight / newHeight * newWidth);
            newHeight = maxHeight;
        }

        return width != newWidth || height != newHeight ? resizeBitmap(bitmap, newWidth, newHeight) : bitmap;
    }

    private static Bitmap resizeBitmap(Bitmap bitmap, int newWidth, int newHeight) {
        return Bitmap.createScaledBitmap(bitmap, newWidth, newHeight, false);
    }

}
