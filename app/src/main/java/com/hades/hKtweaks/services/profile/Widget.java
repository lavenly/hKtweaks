/*
 * Copyright (C) 2015-2018 Willi Ye <williye97@gmail.com>
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
package com.hades.hKtweaks.services.profile;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.SystemClock;
import android.util.SparseArray;
import android.util.TypedValue;
import android.view.View;
import android.widget.RemoteViews;
import android.widget.RemoteViewsService;

import com.hades.hKtweaks.R;
import com.hades.hKtweaks.activities.MainActivity;
import com.hades.hKtweaks.activities.NavigationActivity;
import com.hades.hKtweaks.database.tools.profiles.Profiles;
import com.hades.hKtweaks.fragments.tools.ProfileFragment;
import com.hades.hKtweaks.services.boot.ApplyOnBoot;
import com.hades.hKtweaks.utils.Themes;
import com.hades.hKtweaks.utils.Utils;
import com.hades.hKtweaks.utils.kernel.cpu.CPUFreq;
import com.hades.hKtweaks.utils.root.RootUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by willi on 17.07.16.
 */
public class Widget extends AppWidgetProvider {

    private static final String LIST_ITEM_CLICK = "list_item";
    private static final String ITEM_ARG = "item_extra";

    private static final SparseArray<Long> sProfileLastClicked = new SparseArray<>();

    public static void updateAll(Context context) {
        AppWidgetManager manager = AppWidgetManager.getInstance(context);
        int[] appWidgetIds = manager.getAppWidgetIds(new ComponentName(context, Widget.class));
        if (appWidgetIds.length == 0) {
            return;
        }
        new Widget().onUpdate(context, manager, appWidgetIds);
        manager.notifyAppWidgetViewDataChanged(appWidgetIds, R.id.profile_list);
    }

    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
        super.onUpdate(context, appWidgetManager, appWidgetIds);

        for (int appWidgetId : appWidgetIds) {
            Intent svcIntent = new Intent(context, WidgetService.class);
            svcIntent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, appWidgetId);
            svcIntent.setData(Uri.parse(svcIntent.toUri(Intent.URI_INTENT_SCHEME)));

            RemoteViews widget = new RemoteViews(context.getPackageName(), R.layout.widget_profile);
            widget.setRemoteAdapter(R.id.profile_list, svcIntent);
            widget.setEmptyView(R.id.profile_list, R.id.profile_empty);
            widget.setPendingIntentTemplate(R.id.profile_list,
                    getListPendingIntent(context, appWidgetId));
            widget.setOnClickPendingIntent(R.id.widget_header,
                    getProfilesPendingIntent(context, appWidgetId));
            applyWidgetColors(widget, context);

            appWidgetManager.updateAppWidget(appWidgetId, widget);
        }
    }

    @Override
    public void onAppWidgetOptionsChanged(Context context, AppWidgetManager appWidgetManager,
                                          int appWidgetId, Bundle newOptions) {
        super.onAppWidgetOptionsChanged(context, appWidgetManager, appWidgetId, newOptions);
        onUpdate(context, appWidgetManager, new int[]{appWidgetId});
    }

    private PendingIntent getListPendingIntent(Context context, int appWidgetId) {
        Intent intent = new Intent(context, getClass());
        intent.setAction(LIST_ITEM_CLICK);
        intent.setData(Uri.parse("hKtweaks://profile-widget/" + appWidgetId));
        int flags = PendingIntent.FLAG_UPDATE_CURRENT;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            flags |= PendingIntent.FLAG_MUTABLE;
        }
        return PendingIntent.getBroadcast(context, appWidgetId, intent, flags);
    }

    private PendingIntent getProfilesPendingIntent(Context context, int appWidgetId) {
        Intent intent = new Intent(context, MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
        intent.putExtra(NavigationActivity.INTENT_SECTION,
                ProfileFragment.class.getCanonicalName());
        return PendingIntent.getActivity(context, appWidgetId, intent,
                PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE);
    }

    private static void applyWidgetColors(RemoteViews views, Context context) {
        boolean darkTheme = Themes.isDarkTheme(context);
        boolean amoledTheme = darkTheme && Themes.isAmoledBlack(context);

        Context themedContext = getThemedContext(context);
        int primaryContainer = resolveColor(themedContext, R.attr.colorPrimaryContainer,
                R.color.widget_primary_container);
        int onPrimaryContainer = resolveColor(themedContext, R.attr.colorOnPrimaryContainer,
                R.color.widget_on_primary_container);
        int fallbackSurface = amoledTheme
                ? R.color.widget_surface_amoled : R.color.widget_surface;
        int surface = resolveColor(themedContext, R.attr.colorSurface,
                fallbackSurface);
        int onSurfaceVariant = resolveColor(themedContext, R.attr.colorOnSurfaceVariant,
                R.color.widget_on_surface_variant);
        int outline = resolveColor(themedContext, R.attr.colorOutlineVariant,
                R.color.widget_outline_variant);

        views.setInt(R.id.widget_surface_background, "setColorFilter", surface);
        views.setInt(R.id.widget_header_background, "setColorFilter", primaryContainer);
        views.setViewVisibility(R.id.widget_surface_outline,
                amoledTheme ? View.VISIBLE : View.GONE);
        if (amoledTheme) {
            views.setInt(R.id.widget_surface_outline, "setColorFilter", outline);
        }
        views.setTextColor(R.id.widget_title, onPrimaryContainer);
        views.setTextColor(R.id.profile_empty, onSurfaceVariant);
        views.setInt(R.id.widget_header_icon, "setColorFilter", onPrimaryContainer);
    }

    private static Context getThemedContext(Context context) {
        boolean darkTheme = Themes.isDarkTheme(context);
        return Themes.createThemedContext(context, Themes.getThemeColor(context),
                darkTheme, darkTheme && Themes.isAmoledBlack(context));
    }

    private static int resolveColor(Context context, int attribute, int fallbackColor) {
        TypedValue value = new TypedValue();
        if (context.getTheme().resolveAttribute(attribute, value, true)) {
            if (value.resourceId != 0) {
                return context.getColor(value.resourceId);
            }
            return value.data;
        }
        return context.getColor(fallbackColor);
    }

    @Override
    public void onReceive(final Context context, Intent intent) {
        super.onReceive(context, intent);

        if (LIST_ITEM_CLICK.equals(intent.getAction())) {
            final int position = intent.getIntExtra(ITEM_ARG, 0);
            List<Profiles.ProfileItem> profiles = new Profiles(context).getAllProfiles();
            if (position < 0 || position >= profiles.size()) {
                return;
            }
            Profiles.ProfileItem profileItem = profiles.get(position);

            long lastClicked = sProfileLastClicked.get(position, 0L);
            long currentTime = SystemClock.elapsedRealtime();
            if (currentTime - lastClicked > 2000) {
                sProfileLastClicked.put(position, currentTime);
                Utils.toast(context.getString(R.string.press_again_to_apply, profileItem.getName()),
                        context);
            } else {
                RootUtils.SU su = new RootUtils.SU(true, true);

                List<String> adjustedCommands = new ArrayList<>();
                for (Profiles.ProfileItem.CommandItem command : profileItem.getCommands()) {
                    CPUFreq.ApplyCpu applyCpu;
                    synchronized (this) {
                        if (command.getCommand().startsWith("#")
                                && (applyCpu = new CPUFreq.ApplyCpu(command.getCommand()
                                .substring(1))).toString() != null) {
                            adjustedCommands.addAll(ApplyOnBoot.getApplyCpu(applyCpu, su));
                        } else {
                            adjustedCommands.add(command.getCommand());
                        }
                    }
                }

                for (String command : adjustedCommands) {
                    su.runCommand(command);
                }
                su.close();
                Utils.toast(context.getString(R.string.applied), context);
            }
        }

    }

    private static class ListViewFactory implements RemoteViewsService.RemoteViewsFactory {

        private final Context mContext;
        private List<Profiles.ProfileItem> mItems;

        private ListViewFactory(Context context) {
            mContext = context;
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public void onCreate() {
            mItems = new Profiles(mContext).getAllProfiles();
        }

        @Override
        public int getCount() {
            return mItems.size();
        }

        @Override
        public void onDataSetChanged() {
            onCreate();
        }

        @Override
        public int getViewTypeCount() {
            return 1;
        }

        @Override
        public RemoteViews getViewAt(int position) {
            RemoteViews row = new RemoteViews(mContext.getPackageName(), R.layout.widget_profile_item);

            row.setTextViewText(R.id.text, mItems.get(position).getName());
            Context themedContext = getThemedContext(mContext);
            int onSurface = resolveColor(themedContext, R.attr.colorOnSurface,
                    R.color.widget_on_surface);
            int onSurfaceVariant = resolveColor(themedContext, R.attr.colorOnSurfaceVariant,
                    R.color.widget_on_surface_variant);
            row.setTextColor(R.id.text, onSurface);
            row.setInt(R.id.profile_item_icon, "setColorFilter", onSurfaceVariant);

            Intent i = new Intent();
            Bundle extras = new Bundle();

            extras.putInt(ITEM_ARG, position);
            i.putExtras(extras);
            row.setOnClickFillInIntent(R.id.profile_item, i);

            return (row);
        }

        @Override
        public RemoteViews getLoadingView() {
            return null;
        }

        @Override
        public boolean hasStableIds() {
            return true;
        }

        @Override
        public void onDestroy() {
        }
    }

    public static class WidgetService extends RemoteViewsService {

        @Override
        public RemoteViewsFactory onGetViewFactory(Intent intent) {
            return new ListViewFactory(getApplicationContext());
        }
    }
}
