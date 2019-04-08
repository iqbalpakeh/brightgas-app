package com.pertamina.brightgasse;

import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.pertamina.brightgasse.model.Order;
import com.pertamina.brightgasse.util.CheckboxHelper;
import com.pertamina.brightgasse.util.PesanItemConfiguration;
import com.pertamina.brightgasse.util.PesanItemDropdownHelper;
import com.pertamina.brightgasse.util.PesanItemHelper;

import java.util.ArrayList;

public class FragmentPesan extends Fragment implements InterfacePesanItemDropdown {

    private static final String INDEX_0_STRING = "5.5 Kg";
    private static final String INDEX_1_STRING = "12 Kg";
    private static final String INDEX_2_STRING = "220 gram";

    private int[] imageUrls = new int[]{
            R.drawable.ic_lpg_55_kg,
            R.drawable.ic_lpg_12_kg,
            R.drawable.ic_lpg_220_gr
    };

    private int[] textUrls = new int[]{
            R.drawable.ic_brightgas55,
            R.drawable.ic_brightgas12,
            R.drawable.ic_brightgas220
    };

    private String[] titles = new String[]{"TABUNG & ISI", "REFILL/ISI ULANG", "TRADE IN"};
    private long[] price = new long[]{125000, 75000, 10000};
    private long[] orderPrice = new long[]{8000, 15000, 3000};

    private TextView[] menus = new TextView[3];
    private View menuLine;
    private ImageView imageTabung, imageText;
    private int colorOn = ContextCompat.getColor(((BaseActivity) getActivity()), R.color.colorPrimary);
    private int colorOff = ContextCompat.getColor(((BaseActivity) getActivity()), R.color.beranda_menu_text_color);
    private int selectedMenu = 0;
    private PesanItemHelper tabungDanIsi, refillIsiUlang;
    private PesanItemDropdownHelper tradeIn;
    private CheckboxHelper[] checkboxHelpers = new CheckboxHelper[4];
    private View tradeInPopupContainer;
    private PesanItemConfiguration[] itemConfigurations5 = new PesanItemConfiguration[3];
    private PesanItemConfiguration[] itemConfigurations12 = new PesanItemConfiguration[3];
    private PesanItemConfiguration[] itemConfigurations220 = new PesanItemConfiguration[3];
    private PesanItemConfiguration[] selectedItemConfigurations;
    private ArrayList<Order> listOrder = new ArrayList<>();

    public FragmentPesan() {

    }

    @Override
    public void onResume() {
        super.onResume();
        ((BaseActivity) getActivity()).setTitle("Pesan");
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_pesan, container, false);
        imageTabung = (ImageView) rootView.findViewById(R.id.imagetabung);
        imageText = (ImageView) rootView.findViewById(R.id.imagetext);

        for (int i = 0; i < itemConfigurations5.length; i++) {
            itemConfigurations5[i] = new PesanItemConfiguration();
            itemConfigurations12[i] = new PesanItemConfiguration();
            itemConfigurations220[i] = new PesanItemConfiguration();
        }
        selectedItemConfigurations = itemConfigurations5;

        menus[0] = (TextView) rootView.findViewById(R.id.menu_one);
        menus[1] = (TextView) rootView.findViewById(R.id.menu_two);
        menus[2] = (TextView) rootView.findViewById(R.id.menu3);
        menus[0].post(new Runnable() {
            @Override
            public void run() {
                ViewGroup.LayoutParams params = menuLine.getLayoutParams();
                params.width = menus[0].getWidth();
                menuLine.setLayoutParams(params);
            }
        });
        for (int i = 0; i < menus.length; i++) {
            final int menusIndex = i;
            menus[i].setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    setSelectedMenu(menusIndex);
                }
            });
        }
        menuLine = rootView.findViewById(R.id.menu_line);

        tabungDanIsi = new PesanItemHelper(rootView.findViewById(R.id.tabungdanisi), titles[0]);
        tabungDanIsi.setSubTitle(0, INDEX_0_STRING, selectedItemConfigurations[0]);
        refillIsiUlang = new PesanItemHelper(rootView.findViewById(R.id.refillisiulang), titles[1]);
        refillIsiUlang.setSubTitle(0, INDEX_0_STRING, selectedItemConfigurations[1]);
        tradeIn = new PesanItemDropdownHelper(getActivity(), rootView.findViewById(R.id.tradein), titles[2], this);
        tradeIn.setSubTitle(0, INDEX_0_STRING, selectedItemConfigurations[2]);

        tradeInPopupContainer = rootView.findViewById(R.id.tradeinpopupcontainer);
        checkboxHelpers[0] = new CheckboxHelper(0, tradeInPopupContainer.findViewById(R.id.tradeinelpiji3kg), "TRADE IN DENGAN 1 TABUNG ELPIJI 3 Kg", this);
        checkboxHelpers[1] = new CheckboxHelper(1, tradeInPopupContainer.findViewById(R.id.tradeineasegas9kg), "TRADE IN DENGAN 1 TABUNG EASE GAS 9 Kg", this);
        checkboxHelpers[2] = new CheckboxHelper(2, tradeInPopupContainer.findViewById(R.id.tradeinjoycook), "TRADE IN DENGAN 1 TABUNG JOYCOOK", this);
        checkboxHelpers[3] = new CheckboxHelper(3, tradeInPopupContainer.findViewById(R.id.tradeinelpiji6kg), "TRADE IN DENGAN 2 TABUNG ELPIJI 6 Kg", this);

        View beli = rootView.findViewById(R.id.beli);
        beli.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                doBeli();
            }
        });
        return rootView;
    }

    private void doBeli() {
        saveConfiguration();
        if (tabungDanIsi.isValid() || refillIsiUlang.isValid() || tradeIn.isValid()) {
            ViewGroup customView = (ViewGroup) LayoutInflater.from(((BaseActivity) getActivity())).inflate(R.layout.dialog_container, null, false);
            listOrder.clear();
            generateConfirmationView(customView, itemConfigurations5, INDEX_0_STRING, orderPrice[0], imageUrls[0]);
            generateConfirmationView(customView, itemConfigurations12, INDEX_1_STRING, orderPrice[1], imageUrls[1]);
            generateConfirmationView(customView, itemConfigurations220, INDEX_2_STRING, orderPrice[2], imageUrls[2]);
            final AlertDialog alertDialog = ((BaseActivity) getActivity()).showDialog("Anda akan memesan", customView, "SELESAI", "LANJUT BELANJA", null, null);
            alertDialog.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    FragmentPeriksa fragment = new FragmentPeriksa();
                    fragment.init(listOrder);
                    ((BaseActivity) getActivity()).changeFragment(fragment, false, true);
                    alertDialog.dismiss();
                }
            });
            alertDialog.getButton(AlertDialog.BUTTON_NEGATIVE).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    alertDialog.dismiss();
                }
            });
        } else {
            ((BaseActivity) getActivity()).showDialog("Pesan", "Salah satu opsi atau lebih harus terisi untuk melanjutkan pesanan", "Ok", "", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.dismiss();
                }
            }, null);
        }
    }

    private void generateConfirmationView(ViewGroup parentView, PesanItemConfiguration[] configurations, String type, long orderPrice, int drawableResId) {
        for (int i = 0; i < configurations.length; i++) {
            if (configurations[i].isValid()) {
                String name = "BRIGHT GAS " + type + " " + titles[i];
                String title = configurations[i].qty + " " + name;
                View itemView = LayoutInflater.from(((BaseActivity) getActivity())).inflate(R.layout.item_konfirmasi, null, false);
                parentView.addView(itemView);
                TextView textView = (TextView) itemView.findViewById(R.id.textview);
                textView.setText(title);
//                listOrder.add(new Order(name,configurations[i].qty,price[i],orderPrice,drawableResId));
            }
        }
    }

    private void setSelectedMenu(int index) {
        if (selectedMenu != index) {
            menus[selectedMenu].setTextColor(colorOff);
            menuLine.startAnimation(((BaseActivity) getActivity()).createTranslateAnimation(selectedMenu * menuLine.getWidth(), index * menuLine.getWidth(), 0, 0, 500));
            selectedMenu = index;
            menus[selectedMenu].setTextColor(colorOn);
            String subString = "";

            saveConfiguration();

            switch (selectedMenu) {
                case 0:
                    subString = INDEX_0_STRING;
                    selectedItemConfigurations = itemConfigurations5;
                    break;
                case 1:
                    subString = INDEX_1_STRING;
                    selectedItemConfigurations = itemConfigurations12;
                    break;
                case 2:
                    subString = INDEX_2_STRING;
                    selectedItemConfigurations = itemConfigurations220;
                    break;
            }

            imageTabung.setImageResource(imageUrls[selectedMenu]);
            imageText.setImageResource(textUrls[selectedMenu]);

            tabungDanIsi.setSubTitle(selectedMenu, subString, selectedItemConfigurations[0]);
            if (selectedMenu == 2) {
                tabungDanIsi.setAdditionalVisible(View.VISIBLE);
                refillIsiUlang.setVisibility(View.GONE);
                tradeIn.setVisibility(View.GONE);
            } else {
                tabungDanIsi.setAdditionalVisible(View.GONE);
                refillIsiUlang.setVisibility(View.VISIBLE);
                tradeIn.setVisibility(View.VISIBLE);
                refillIsiUlang.setSubTitle(selectedMenu, subString, selectedItemConfigurations[1]);
                tradeIn.setSubTitle(selectedMenu, subString, selectedItemConfigurations[2]);
            }
            setChecked(selectedItemConfigurations[2].listCheckbox);
        }
    }

    private void saveConfiguration() {
        tabungDanIsi.saveConfiguration();
        refillIsiUlang.saveConfiguration();
        tradeIn.saveConfiguration();
    }

    @Override
    public void showDropdown(int visibility) {
        tradeInPopupContainer.setVisibility(visibility);
    }

    @Override
    public void setChecked(CheckboxHelper helper) {
        tradeIn.setCheck(helper);
    }

    public void setChecked(ArrayList<Integer> listToBeChecked) {
        for (CheckboxHelper helper : checkboxHelpers) {
            helper.isInitialSetting = true;
            helper.setChecked(false);
        }

        for (int i = 0; i < listToBeChecked.size(); i++) {
            checkboxHelpers[listToBeChecked.get(i).intValue()].setChecked(true);
        }

        for (CheckboxHelper helper : checkboxHelpers) {
            helper.isInitialSetting = false;
        }
    }

}
