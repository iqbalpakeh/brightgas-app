package com.pertamina.brightgasdriver.util;

import android.view.View;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.TextView;

import com.pertamina.brightgasdriver.InterfacePesanItemDropdown;
import com.pertamina.brightgasdriver.R;

/**
 * Created by gumelartejasukma on 11/12/16.
 */
public class CheckboxHelper {

    private CheckBox checkBox;
    private InterfacePesanItemDropdown interfacePesanItemDropdown;
    String title;
    int index;
    public boolean isInitialSetting = false;

    public CheckboxHelper(int index,View rootView, String title, InterfacePesanItemDropdown interfacePesanItemDropdown1){
        this.index = index;
        this.title = title;
        this.interfacePesanItemDropdown = interfacePesanItemDropdown1;
        TextView myTitle = (TextView)rootView.findViewById(R.id.mytitle);
        myTitle.setText(title);
        checkBox = (CheckBox)rootView.findViewById(R.id.mycheckbox);
        checkBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                doCheck();
            }
        });
    }

    private void doCheck(){
        if(!isInitialSetting)
            interfacePesanItemDropdown.setChecked(this);
    }

    public void setChecked(boolean isChecked){
        checkBox.setChecked(isChecked);
    }

    public boolean isChecked(){
        return checkBox.isChecked();
    }

}
