package com.pertamina.brightgas.util;

import android.view.View;
import android.widget.TextView;

import com.pertamina.brightgas.R;

public class ItemOrderHelper {

    ItemOrderConfiguration configuration;
    private View rootView;
    private TextView title, quantity, additionalText, itemPrice;
    private String baseTitle;
    private int qty = 0;

    public ItemOrderHelper(View rootView, String baseTitle) {
        this.rootView = rootView;
        this.baseTitle = baseTitle;

        title = (TextView) rootView.findViewById(R.id.my_title);

        itemPrice = (TextView) rootView.findViewById(R.id.item_price);

        quantity = (TextView) rootView.findViewById(R.id.quantity);

        additionalText = (TextView) rootView.findViewById(R.id.additional_text);

        View actionMinus = rootView.findViewById(R.id.action_minus);
        actionMinus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (qty > 0) {
                    qty--;
                }
                quantity.setText(qty + "");
            }
        });
        View actionPlus = rootView.findViewById(R.id.action_plus);
        actionPlus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                qty++;
                quantity.setText(qty + "");
            }
        });
    }

    public void saveConfiguration() {
        configuration.qty = qty;
    }

    public void setSubTitle(String subTitle, String price, ItemOrderConfiguration configuration) {
        this.configuration = configuration;
        title.setText(baseTitle + " " + subTitle);
        itemPrice.setText(price);
        qty = configuration.qty;
        quantity.setText(qty + "");
    }

    public boolean isValid() {
        if (qty > 0) {
            return true;
        }
        return false;
    }

    public void setVisibility(int visibility) {
        rootView.setVisibility(visibility);
    }

    public void setAdditionalVisible(int visibility) {
        additionalText.setVisibility(visibility);
    }

}
