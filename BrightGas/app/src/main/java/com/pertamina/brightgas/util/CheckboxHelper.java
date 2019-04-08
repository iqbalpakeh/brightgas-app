package com.pertamina.brightgas.util;

import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.pertamina.brightgas.GlobalActivity;
import com.pertamina.brightgas.InterfacePesanItemDropdown;
import com.pertamina.brightgas.R;

public class CheckboxHelper {

    private static final String TAG = "check_box_helper";

    public int qty = 0;
    public String title;
    public long price;
    public int index;

    private InterfacePesanItemDropdown interfacePesanItemDropdown;
    private TextView quantity;

    private TextView myTitle;
    private TextView myPrice;
    private View actionMinus;
    private View actionPlus;

    public CheckboxHelper(int index, View rootView, String title, long price, InterfacePesanItemDropdown interfacePesanItemDropdown) {
        setCheckBoxPointer(index, rootView, title, price, interfacePesanItemDropdown);
    }

    public void setQty(int qty) {
        this.qty = qty;
        quantity.setText(qty + "");
    }

    public boolean isNotEmpty() {
        if (qty > 0) {
            return true;
        }
        return false;
    }

    private String showPrice(long price) {
        return "Harga Rp " + GlobalActivity.getCalculatedPrice(price) + " / tabung";
    }

    public void setCheckBoxPointer(int index,
                                   View rootView,
                                   String title,
                                   long price,
                                   InterfacePesanItemDropdown interfacePesanItemDropdown) {

        this.index = index;
        this.title = title;
        this.price = price;
        this.interfacePesanItemDropdown = interfacePesanItemDropdown;

        quantity = (TextView) rootView.findViewById(R.id.quantity);
        quantity.setText(qty + "");

        myTitle = (TextView) rootView.findViewById(R.id.my_title);
        myTitle.setText(title);

        myPrice = (TextView) rootView.findViewById(R.id.price);
        myPrice.setText(showPrice(price));

        actionMinus = rootView.findViewById(R.id.action_minus);
        actionMinus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (qty > 0) {
                    qty--;
                }
                quantity.setText(qty + "");
                Log.d(TAG, "qty = " + qty);
            }
        });

        actionPlus = rootView.findViewById(R.id.action_plus);
        actionPlus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                qty++;
                quantity.setText(qty + "");
                Log.d(TAG, "qty = " + qty);
            }
        });
    }

}
