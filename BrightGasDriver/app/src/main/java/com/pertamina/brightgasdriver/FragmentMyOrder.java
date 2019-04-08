package com.pertamina.brightgasdriver;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.pertamina.brightgasdriver.model.OrderList;

import java.util.ArrayList;

/**
 * Created by gumelartejasukma on 11/23/16.
 */
public class FragmentMyOrder extends Fragment {

    BottomMenu jarakTerdekat, waktuPesan, waktuAntar, selectedMenu;
    private TextView[] menus = new TextView[3];
    private View menuLine;
    private int colorOn = ContextCompat.getColor(((BaseActivity) getActivity()), R.color.chatrightname);
    private int colorOff = ContextCompat.getColor(((BaseActivity) getActivity()), R.color.berandamenutextcolor);
    private int selectedTopMenu = 0;
    private ArrayList<OrderList> orderListsChooseDriver = new ArrayList<>();
    private ArrayList<OrderList> orderListsOnGoing = new ArrayList<>();

    public FragmentMyOrder() {

    }

    @Override
    public void onResume() {
        super.onResume();
        ((BaseActivity) getActivity()).setTitle("Order List");
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_my_order, container, false);
        orderListsChooseDriver.add(new OrderList(OrderList.TYPE_CHOOSE_DRIVER));
        orderListsChooseDriver.add(new OrderList(OrderList.TYPE_CHOOSE_DRIVER));
        orderListsOnGoing.add(new OrderList(OrderList.TYPE_DRIVER));
        orderListsOnGoing.add(new OrderList(OrderList.TYPE_ON_DELIVERY));
        orderListsOnGoing.add(new OrderList(OrderList.TYPE_DRIVER));
        jarakTerdekat = new BottomMenu(rootView.findViewById(R.id.jarak_terdekat), R.drawable.ic_delivery_truck, "Jarak Terdekat");
        waktuPesan = new BottomMenu(rootView.findViewById(R.id.waktu_pesan), R.drawable.ic_clock, "Waktu Pesan");
        waktuAntar = new BottomMenu(rootView.findViewById(R.id.waktu_antar), R.drawable.ic_clock, "Waktu Antar");
        jarakTerdekat.setSelected(true);

        menus[0] = (TextView) rootView.findViewById(R.id.menu1);
        menus[1] = (TextView) rootView.findViewById(R.id.menu2);
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
        menuLine = rootView.findViewById(R.id.menuline);

        FragmentOrderListContent fragment = new FragmentOrderListContent();
        fragment.setDatas(orderListsChooseDriver);
        ((BaseActivity) getActivity()).changeFragment(R.id.fragmentcontent, fragment, true, false);
        return rootView;
    }

    private void setSelectedMenu(int index) {
        if (selectedTopMenu != index) {
            menus[selectedTopMenu].setTextColor(colorOff);
            menuLine.startAnimation(((BaseActivity) getActivity()).createTranslateAnimation(selectedTopMenu * menuLine.getWidth(), index * menuLine.getWidth(), 0, 0, 500));
            selectedTopMenu = index;
            menus[selectedTopMenu].setTextColor(colorOn);

            switch (index) {
                case 0:
                    FragmentOrderListContent fragment = new FragmentOrderListContent();
                    fragment.setDatas(orderListsChooseDriver);
                    ((BaseActivity) getActivity()).changeFragment(R.id.fragmentcontent, fragment, true, true);
                    break;
                case 1:
                    FragmentOrderListContent fragment1 = new FragmentOrderListContent();
                    fragment1.setDatas(orderListsOnGoing);
                    ((BaseActivity) getActivity()).changeFragment(R.id.fragmentcontent, fragment1, true, true);
                    break;
                case 2:
                    ((BaseActivity) getActivity()).changeFragment(R.id.fragmentcontent, new FragmentRiwayatTransaksi(), true, true);
                    break;
            }
        }
    }

    class BottomMenu {
        public ImageView imageView;
        public TextView textView;
        public boolean isSelected;

        public BottomMenu(View rootView, int imageId, String text) {
            imageView = (ImageView) rootView.findViewById(R.id.imageview);
            imageView.setImageResource(imageId);
            textView = (TextView) rootView.findViewById(R.id.textview);
            textView.setText(text);
            rootView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    setSelected(true);
                }
            });
        }

        public void setSelected(boolean val) {
            if (isSelected != val) {
                isSelected = val;
                int color;
                if (isSelected) {
                    if (selectedMenu != null) {
                        selectedMenu.setSelected(false);
                    }
                    selectedMenu = this;
                    color = ContextCompat.getColor(getContext(), R.color.chatrightname);
                } else {
                    color = ContextCompat.getColor(getContext(), R.color.berhasilgrey);
                }
                imageView.setColorFilter(color);
                textView.setTextColor(color);
            }
        }

    }

}
