package com.pertamina.brightgasse.util;

import android.content.Context;
import android.view.View;
import android.widget.TextView;

import com.pertamina.brightgasse.BaseActivity;
import com.pertamina.brightgasse.InterfacePesanItemDropdown;
import com.pertamina.brightgasse.R;

public class PesanItemDropdownHelper {

    private View rootView;
    private TextView title,quantity;
    private String baseTitle;
    private InterfacePesanItemDropdown interfacePesanItemDropdown;
    private boolean isDropdown = false;
    private int qty = 0;
    private int dropdownCount = 0;
    private int index = 0;
    PesanItemConfiguration configuration;

    private Context mContext;

    public PesanItemDropdownHelper(Context context, View rootView, String baseTitle, final InterfacePesanItemDropdown interfacePesanItemDropdown1){
        this.mContext = context;
        this.rootView = rootView;
        this.interfacePesanItemDropdown = interfacePesanItemDropdown1;
        this.baseTitle = baseTitle;
        title = (TextView) rootView.findViewById(R.id.mytitle);
        final View dropdown = rootView.findViewById(R.id.dropdown);
        dropdown.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                isDropdown = !isDropdown;
                int visibility;
                if(!isDropdown){
                    dropdown.startAnimation(((BaseActivity)mContext).createRotateAnimation(180,0,0.5f,0.5f,300,0,null));
                    visibility = View.GONE;
                }else{
                    dropdown.startAnimation(((BaseActivity)mContext).createRotateAnimation(0,180,0.5f,0.5f,300,0,null));
                    visibility = View.VISIBLE;
                }
                interfacePesanItemDropdown.showDropdown(visibility);
            }
        });

        quantity = (TextView) rootView.findViewById(R.id.quantity);
        View actionMinus = rootView.findViewById(R.id.actionminus);
        actionMinus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(qty>0){
                    qty--;
                }
                quantity.setText(qty+"");
                configuration.qty = qty;
            }
        });
        View actionPlus = rootView.findViewById(R.id.actionplus);
        actionPlus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                qty++;
                quantity.setText(qty+"");
                configuration.qty = qty;
            }
        });
    }

    public void setSubTitle(int index,String subTitle,PesanItemConfiguration configuration){
        this.configuration = configuration;
        this.index = index;
        title.setText(baseTitle+" "+subTitle);
        qty = configuration.qty;
        quantity.setText(qty+"");
    }

    public void saveConfiguration(){
        configuration.qty = qty;
//        configuration.setChecked(checkBox.isChecked());
    }

    public void setCheck(CheckboxHelper helper){
        configuration.setChecked(helper.isChecked(),helper.index);
    }

    public boolean isValid(){
        if(dropdownCount>0 && qty>0){
            return true;
        }
        return false;
    }

    public void setVisibility(int visibility){
        rootView.setVisibility(visibility);
    }

}
