package com.pertamina.brightgasagen.util;

import android.view.View;
import android.widget.CheckBox;
import android.widget.TextView;

import com.pertamina.brightgasagen.R;

/**
 * Created by gumelartejasukma on 11/12/16.
 */
public class PesanItemHelper {

    private View rootView;
    private TextView title,quantity,additionalText;
    private String baseTitle;
    private int qty = 0;
    private CheckBox checkBox;
    private int index = 0;
    PesanItemConfiguration configuration;

    public PesanItemHelper(View rootView, String baseTitle){
        this.rootView = rootView;
        this.baseTitle = baseTitle;
        title = (TextView) rootView.findViewById(R.id.mytitle);
        checkBox = (CheckBox) rootView.findViewById(R.id.mycheckbox);
        quantity = (TextView) rootView.findViewById(R.id.quantity);
        additionalText = (TextView) rootView.findViewById(R.id.additionaltext);
        View actionMinus = rootView.findViewById(R.id.actionminus);
        actionMinus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(qty>0){
                    qty--;
                }
                if(qty == 0 && checkBox.isChecked()){
                    checkBox.setChecked(false);
                }
                quantity.setText(qty+"");
            }
        });
        View actionPlus = rootView.findViewById(R.id.actionplus);
        actionPlus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(qty == 0 && !checkBox.isChecked()){
                    checkBox.setChecked(true);
                }
                qty++;
                quantity.setText(qty+"");
            }
        });

    }

    public void saveConfiguration(){
        configuration.qty = qty;
        configuration.setChecked(checkBox.isChecked());
    }

    public void setSubTitle(int index,String subTitle,PesanItemConfiguration configuration){
        this.configuration = configuration;
        this.index = index;
        title.setText(baseTitle+" "+subTitle);
        qty = configuration.qty;
        quantity.setText(qty+"");
        checkBox.setChecked(configuration.isChecked());
    }

    public boolean isValid(){
        if(qty>0 && checkBox.isChecked()){
            return true;
        }
        return false;
    }

    public void setVisibility(int visibility){
        rootView.setVisibility(visibility);
    }

    public void setAdditionalVisible(int visibility){
        additionalText.setVisibility(visibility);
    }

}
