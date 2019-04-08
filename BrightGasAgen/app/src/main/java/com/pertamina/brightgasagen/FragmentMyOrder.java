package com.pertamina.brightgasagen;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.pertamina.brightgasagen.model.OrderList;
import com.pertamina.brightgasagen.model.RiwayatTransaksi;

import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;

public class FragmentMyOrder extends Fragment implements RequestLoaderInterface {

    BottomMenu jarakTerdekat, waktuPesan, waktuAntar, selectedMenu;
    private TextView[] menus = new TextView[3];
    private View menuLine;
    private int colorOn = ContextCompat.getColor(((BaseActivity) getActivity()), R.color.chatrightname);
    private int colorOff = ContextCompat.getColor(((BaseActivity) getActivity()), R.color.berandamenutextcolor);
    private int selectedTopMenu = 0;
    private ArrayList<OrderList> orderListsChooseDriver = new ArrayList<>();
    private ArrayList<OrderList> orderListsOnGoing = new ArrayList<>();
    private ArrayList<RiwayatTransaksi> riwayatTransaksi = new ArrayList<>();

    FragmentOrderListContent fragmentOrderListContent;
    FragmentRiwayatTransaksi fragmentRiwayatTransaksi;

    View rootView;

    public FragmentMyOrder() {

    }

    @Override
    public void onResume() {
        super.onResume();
        ((BaseActivity) getActivity()).setTitle("Transaksi Saya");
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putInt("selectedtopmenu", selectedTopMenu);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        if (rootView == null) {
            rootView = inflater.inflate(R.layout.fragment_my_order, container, false);
//        orderListsChooseDriver.add(new OrderList(OrderList.TYPE_CHOOSE_DRIVER));
//        orderListsChooseDriver.add(new OrderList(OrderList.TYPE_CHOOSE_DRIVER));
//        orderListsOnGoing.add(new OrderList(OrderList.TYPE_DRIVER));
//        orderListsOnGoing.add(new OrderList(OrderList.TYPE_ON_DELIVERY));
//        orderListsOnGoing.add(new OrderList(OrderList.TYPE_DRIVER));
            jarakTerdekat = new BottomMenu(rootView.findViewById(R.id.jarakterdekat), R.drawable.ic_delivery_truck, "Jarak Terdekat");
            waktuPesan = new BottomMenu(rootView.findViewById(R.id.waktupesan), R.drawable.ic_shopping_basket, "Total Belanja");
            waktuAntar = new BottomMenu(rootView.findViewById(R.id.waktuantar), R.drawable.ic_clock, "Waktu Antar");
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

            if (savedInstanceState != null) {
                Log.d("gugum", "SAVED INSTANCE STATE IS NOT NULL");
                if (savedInstanceState.containsKey("selectedtopmenu")) {
                    Log.d("gugum", "SAVED INSTANCE STATE IS VALID");
                    int temp = savedInstanceState.getInt("selectedtopmenu");
                    selectedTopMenu = -1;
                    setSelectedMenu(temp);
                }
            } else {
                fragmentOrderListContent = new FragmentOrderListContent();
                fragmentOrderListContent.setDatas(orderListsChooseDriver);
                ((BaseActivity) getActivity()).changeFragment(R.id.fragmentcontent, fragmentOrderListContent, true, false);
            }

        } else {
            menuLine.startAnimation(((BaseActivity) getActivity()).createTranslateAnimation(selectedTopMenu * menuLine.getWidth(), selectedTopMenu * menuLine.getWidth(), 0, 0, 0));
        }

        orderListsOnGoing.clear();
        orderListsChooseDriver.clear();
        riwayatTransaksi.clear();
        new RequestLoader(this).loadRequest(0, new BasicNameValuePair[]{
                new BasicNameValuePair("agen_id", User.id)
        }, "transaction", "get_trx_agen", true);

        return rootView;
    }

    private void setSelectedMenu(int index) {
        Log.d("gugum", "SET SELECTED MENU");
        if (selectedTopMenu != index) {
            menus[selectedTopMenu].setTextColor(colorOff);
            menuLine.startAnimation(((BaseActivity) getActivity()).createTranslateAnimation(selectedTopMenu * menuLine.getWidth(), index * menuLine.getWidth(), 0, 0, 500));
            selectedTopMenu = index;
            menus[selectedTopMenu].setTextColor(colorOn);

            switch (index) {
                case 0:
                    fragmentOrderListContent = new FragmentOrderListContent();
                    fragmentOrderListContent.setDatas(orderListsChooseDriver);
                    ((BaseActivity) getActivity()).changeFragment(R.id.fragmentcontent, fragmentOrderListContent, true, true);
                    break;
                case 1:
                    fragmentOrderListContent = new FragmentOrderListContent();
                    fragmentOrderListContent.setDatas(orderListsOnGoing);
                    ((BaseActivity) getActivity()).changeFragment(R.id.fragmentcontent, fragmentOrderListContent, true, true);
                    break;
                case 2:
                    fragmentRiwayatTransaksi = new FragmentRiwayatTransaksi();
                    fragmentRiwayatTransaksi.setDatas(riwayatTransaksi);
                    ((BaseActivity) getActivity()).changeFragment(R.id.fragmentcontent, fragmentRiwayatTransaksi, true, true);
                    break;
            }
        }
    }

    @Override
    public void setData(int index, String result, View[] impactedViews) {
        try {
            JSONObject jsonObject = new JSONObject(result);
            if (jsonObject.getInt("status") == 1) {
                JSONArray jsonArray = jsonObject.getJSONArray("data");
                if (jsonArray.length() > 0) {
                    JSONArray secondJSONArray;
                    for (int i = 0; i < jsonArray.length(); i++) {
                        jsonObject = jsonArray.getJSONObject(i);
                        secondJSONArray = jsonObject.getJSONArray("item");
                        jsonObject = jsonObject.getJSONObject("header");
                        int statusId = jsonObject.getInt("tran_status_id");
                        switch (statusId) {
                            case 1:
                                if (((BaseActivity) getActivity()).isValidString(jsonObject.getString("tran_driver_id"))) {
                                    orderListsOnGoing.add(new OrderList(OrderList.TYPE_CHOOSE_DRIVER, jsonObject.getString("tran_id"),
                                            jsonObject.getString("tran_no_invoice"),
                                            jsonObject.getString("tran_date"),
                                            jsonObject.getString("tran_time_start"),
                                            jsonObject.getString("tran_time_end"),
                                            jsonObject.getString("cus_name"),
                                            jsonObject.getString("cua_address") + "\n" + jsonObject.getString("cua_city_name") + ", " + jsonObject.getString("cua_province_name"),
                                            jsonObject.getString("agd_name"),
                                            jsonObject.getLong("tran_sub_total"),
                                            jsonObject.getLong("tran_ongkir"),
                                            jsonObject.getLong("tran_grand_total"),
                                            jsonObject.getString("distance"),
                                            secondJSONArray));
                                } else {
                                    orderListsChooseDriver.add(new OrderList(OrderList.TYPE_CHOOSE_DRIVER,
                                            jsonObject.getString("tran_id"),
                                            jsonObject.getString("tran_no_invoice"),
                                            jsonObject.getString("tran_date"),
                                            jsonObject.getString("tran_time_start"),
                                            jsonObject.getString("tran_time_end"),
                                            jsonObject.getString("cus_name"),
                                            jsonObject.getString("cua_address") + "\n" + jsonObject.getString("cua_city_name") + ", " + jsonObject.getString("cua_province_name"),
                                            jsonObject.getLong("tran_sub_total"),
                                            jsonObject.getLong("tran_ongkir"),
                                            jsonObject.getLong("tran_grand_total"),
                                            jsonObject.getString("distance"),
                                            secondJSONArray));
                                }
                                break;
                            case 2:
                                orderListsOnGoing.add(new OrderList(statusId,
                                        jsonObject.getString("tran_id"),
                                        jsonObject.getString("tran_no_invoice"),
                                        jsonObject.getString("tran_date"),
                                        jsonObject.getString("tran_time_start"),
                                        jsonObject.getString("tran_time_end"),
                                        jsonObject.getString("cus_name"),
                                        jsonObject.getString("cua_address") + "\n" + jsonObject.getString("cua_city_name") + ", " + jsonObject.getString("cua_province_name"),
                                        jsonObject.getString("agd_name"),
                                        jsonObject.getLong("tran_sub_total"),
                                        jsonObject.getLong("tran_ongkir"),
                                        jsonObject.getLong("tran_grand_total"),
                                        jsonObject.getString("distance"),
                                        secondJSONArray));
                                break;
                            case 3:
                                riwayatTransaksi.add(new RiwayatTransaksi(jsonObject.getString("tran_id"), jsonObject.getString("tran_date")
                                        , jsonObject.getString("tran_time_start"), jsonObject.getString("tran_time_end")
                                        , jsonObject.getString("tran_no_invoice"), jsonObject.getInt("tran_status_id")));
                                break;
                        }
                    }

                    switch (selectedTopMenu) {
                        case 0:
                            Log.d("gugum", "REFRESH DATA CHOOSE DRIVER");
                            fragmentOrderListContent.refreshDatas(orderListsChooseDriver);
                            break;
                        case 1:
                            Log.d("gugum", "REFRESH DATA ON GOING");
                            fragmentOrderListContent.refreshDatas(orderListsOnGoing);
                            break;
                        case 2:
                            Log.d("gugum", "REFRESH DATA RIWAYAT TRANSAKSI");
                            fragmentRiwayatTransaksi.replaceDatas(riwayatTransaksi);
                            break;

                    }

                }
            } else {
                ((BaseActivity) getActivity()).showDialog("", jsonObject.getString("message"), "", "Ok");
            }
        } catch (Exception ex) {
            ((BaseActivity) getActivity()).showDialog("Error parsing", ex.toString(), "", "Ok");
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
