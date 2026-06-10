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
import android.os.Bundle;

import androidx.annotation.Nullable;

import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.material.button.MaterialButton;
import com.hades.hKtweaks.R;
import com.hades.hKtweaks.utils.Utils;

/**
 * Created by willi on 14.04.16.
 */
public class TextActivity extends BaseActivity {

    public static final String MESSAGE_INTENT = "message_intent";
    public static final String SUMMARY_INTENT = "summary_intent";
    public static final String ISSUE_INTENT = "issue_intent";
    public static final int ISSUE_NO_ROOT = 0;
    public static final int ISSUE_NO_BUSYBOX = 1;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_text);

        String message = getIntent().getStringExtra(MESSAGE_INTENT);
        final String url = getIntent().getStringExtra(SUMMARY_INTENT);
        int issue = getIntent().getIntExtra(ISSUE_INTENT, ISSUE_NO_ROOT);

        TextView messageView = findViewById(R.id.message_text);
        TextView summaryView = findViewById(R.id.summary_text);
        ImageView statusIcon = findViewById(R.id.status_icon);
        MaterialButton helpButton = findViewById(R.id.help_button);

        if (message != null) {
            messageView.setText(message);
        }

        boolean noRoot = issue == ISSUE_NO_ROOT;
        summaryView.setText(noRoot ? R.string.no_root_summary : R.string.no_busybox_summary);
        statusIcon.setImageResource(noRoot ? R.drawable.ic_unlock : R.drawable.ic_shell);
        helpButton.setText(noRoot ? R.string.root_help : R.string.install_busybox);

        if (url == null) {
            helpButton.setVisibility(View.GONE);
        } else {
            helpButton.setOnClickListener(v -> Utils.launchUrl(url, TextActivity.this));
        }

        findViewById(R.id.retry_button).setOnClickListener(v -> {
            Intent intent = new Intent(TextActivity.this, MainActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(intent);
            overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
            finish();
        });
    }

}
