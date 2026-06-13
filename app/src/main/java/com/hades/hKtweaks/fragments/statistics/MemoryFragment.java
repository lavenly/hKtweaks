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
package com.hades.hKtweaks.fragments.statistics;

import com.hades.hKtweaks.R;
import com.hades.hKtweaks.fragments.recyclerview.RecyclerViewFragment;
import com.hades.hKtweaks.utils.Device;
import com.hades.hKtweaks.views.recyclerview.CardView;
import com.hades.hKtweaks.views.recyclerview.DescriptionView;
import com.hades.hKtweaks.views.recyclerview.RecyclerViewItem;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by willi on 05.08.16.
 */
public class MemoryFragment extends RecyclerViewFragment {

    private Device.MemInfo mMemInfo;

    @Override
    protected void init() {
        super.init();

        mMemInfo = Device.MemInfo.getInstance();
    }

    @Override
    protected boolean showViewPager() {
        return false;
    }

    @Override
    protected void addItems(List<RecyclerViewItem> items) {
        Map<MemorySection, CardView> sections = new LinkedHashMap<>();
        sections.put(MemorySection.OVERVIEW, createCard(R.string.memory_overview));
        sections.put(MemorySection.ACTIVITY, createCard(R.string.memory_activity));
        sections.put(MemorySection.SWAP, createCard(R.string.swap));
        sections.put(MemorySection.KERNEL, createCard(R.string.kernel_memory));
        sections.put(MemorySection.OTHER, createCard(R.string.other));

        List<String> mems = mMemInfo.getItems();
        for (String mem : mems) {
            DescriptionView memView = new MemoryDescriptionView();
            memView.setTitle(mem);
            memView.setSummary(formatValue(mMemInfo.getItem(mem)));
            sections.get(getSection(mem)).addItem(memView);
        }

        for (CardView card : sections.values()) {
            if (card.size() > 0) {
                items.add(card);
            }
        }
    }

    private CardView createCard(int titleRes) {
        CardView card = new CardView(getActivity());
        card.setTitle(getString(titleRes));
        return card;
    }

    private String formatValue(String value) {
        return value.trim().replaceAll("\\s+kB$", " " + getString(R.string.kb));
    }

    private MemorySection getSection(String name) {
        if (name.startsWith("Mem") || name.equals("Buffers") || name.equals("Cached")) {
            return MemorySection.OVERVIEW;
        }
        if (name.startsWith("Active") || name.startsWith("Inactive")
                || name.equals("Unevictable") || name.equals("Mlocked")
                || name.equals("AnonPages") || name.equals("Mapped")
                || name.startsWith("Shmem")) {
            return MemorySection.ACTIVITY;
        }
        if (name.startsWith("Swap") || name.startsWith("Zswap")
                || name.startsWith("Zswapped")) {
            return MemorySection.SWAP;
        }
        if (name.startsWith("Slab") || name.startsWith("SReclaimable")
                || name.startsWith("SUnreclaim") || name.startsWith("KReclaimable")
                || name.startsWith("KernelStack") || name.startsWith("PageTables")
                || name.startsWith("NFS_") || name.startsWith("Bounce")
                || name.startsWith("WritebackTmp") || name.startsWith("Percpu")
                || name.startsWith("Vmalloc") || name.startsWith("Cma")
                || name.startsWith("DirectMap")) {
            return MemorySection.KERNEL;
        }
        return MemorySection.OTHER;
    }

    private enum MemorySection {
        OVERVIEW,
        ACTIVITY,
        SWAP,
        KERNEL,
        OTHER
    }

    private static class MemoryDescriptionView extends DescriptionView {

        @Override
        public int getLayoutRes() {
            return R.layout.rv_memory_description_view;
        }
    }
}
