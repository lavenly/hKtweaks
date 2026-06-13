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

import java.util.List;

/**
 * Created by willi on 28.06.16.
 */
public class InputsFragment extends RecyclerViewFragment {

    @Override
    protected boolean showViewPager() {
        return false;
    }

    @Override
    protected void addItems(List<RecyclerViewItem> items) {
        List<Device.Input.Item> inputs = Device.Input.getInstance().getItems();
        for (Device.Input.Item input : inputs) {
            String name;
            if ((name = input.getName()) != null) {
                String[][] list = {
                        {getString(R.string.bus), input.getBus()},
                        {getString(R.string.vendor), input.getVendor()},
                        {getString(R.string.product), input.getProduct()},
                        {getString(R.string.version), input.getVersion()},
                        {getString(R.string.sysfs), input.getSysfs()},
                        {getString(R.string.handlers), input.getHandlers()}
                };

                CardView inputCard = new CardView(getActivity());
                inputCard.setTitle(name);

                for (String[] inputsList : list) {
                    if (inputsList[1] != null && !inputsList[1].isEmpty()) {
                        DescriptionView inputView = new InputDescriptionView();
                        inputView.setTitle(inputsList[0]);
                        inputView.setSummary(inputsList[1]);
                        inputCard.addItem(inputView);
                    }
                }

                if (inputCard.size() > 0) {
                    items.add(inputCard);
                }
            }
        }
    }

    private static class InputDescriptionView extends DescriptionView {

        @Override
        public int getLayoutRes() {
            return R.layout.rv_input_description_view;
        }
    }
}
