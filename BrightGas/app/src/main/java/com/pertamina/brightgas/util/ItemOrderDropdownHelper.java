package com.pertamina.brightgas.util;

import android.content.Context;
import android.view.View;
import android.widget.TextView;

import com.pertamina.brightgas.BaseActivity;
import com.pertamina.brightgas.InterfacePesanItemDropdown;
import com.pertamina.brightgas.R;

import java.util.ArrayList;

public class ItemOrderDropdownHelper {

    ItemOrderConfiguration configuration;
    private View rootView;
    private TextView title;
    private String baseTitle;
    private InterfacePesanItemDropdown interfacePesanItemDropdown;
    private boolean isDropdown = false;
    private int qty = 0;
    private int dropdownCount = 0;
    private ArrayList<Integer> listCheckbox;
    private ArrayList<Integer> listQty;

    public ItemOrderDropdownHelper(final Context context,
                                   View rootView,
                                   String baseTitle,
                                   final InterfacePesanItemDropdown interfacePesanItemDropdown) {

        this.rootView = rootView;
        this.interfacePesanItemDropdown = interfacePesanItemDropdown;
        this.baseTitle = baseTitle;

        title = (TextView) rootView.findViewById(R.id.my_title);
        final View dropdown = rootView.findViewById(R.id.dropdown);
        dropdown.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                isDropdown = !isDropdown;
                int visibility;
                if (!isDropdown) {
                    dropdown.startAnimation(((BaseActivity)context)
                            .createRotateAnimation(180, 0, 0.5f, 0.5f, 300, 0, null));
                    visibility = View.GONE;
                } else {
                    dropdown.startAnimation(((BaseActivity)context)
                            .createRotateAnimation(0, 180, 0.5f, 0.5f, 300, 0, null));
                    visibility = View.VISIBLE;
                }
                ItemOrderDropdownHelper.this.interfacePesanItemDropdown.showDropdown(visibility);
            }
        });
    }

    public void setSubTitle(String subTitle, ItemOrderConfiguration configuration) {
        this.configuration = configuration;
        title.setText(baseTitle + " " + subTitle);
        qty = configuration.qty;
        listCheckbox = configuration.listCheckbox;
        listQty = configuration.listQty;
    }

    public void saveConfiguration() {
        configuration.qty = qty;
        configuration.listCheckbox = listCheckbox;
        configuration.listQty = listQty;
    }

    public void setCheck(CheckboxHelper helper) {
        configuration.setChecked(helper.index, helper.qty);
        configuration.qty = configuration.listCheckbox.size();
        qty = configuration.qty;
    }

    public boolean isValid() {
        if (configuration.listCheckbox.size() > 0) {
            return true;
        }
        return false;
    }

    public void setVisibility(int visibility) {
        rootView.setVisibility(visibility);
    }

}
