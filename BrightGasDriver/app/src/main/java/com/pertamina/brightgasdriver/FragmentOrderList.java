package com.pertamina.brightgasdriver;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.google.firebase.database.DataSnapshot;
import com.pertamina.brightgasdriver.firebase.FirebaseLoadable;
import com.pertamina.brightgasdriver.firebase.FirebaseQueryOrder;
import com.pertamina.brightgasdriver.firebase.model.Package;
import com.pertamina.brightgasdriver.model.OrderList;

import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class FragmentOrderList extends Fragment
        implements RequestLoaderInterface, FirebaseLoadable {

    private static final String TAG = "frag_order_list";

    private BottomMenu mJarakTerdekat;

    private BottomMenu mWaktuPesan;

    private BottomMenu mWaktuAntar;

    private BottomMenu mSelectedMenu;

    private AdapterOrderList mAdapter;

    @Override
    public void onResume() {
        super.onResume();
        getActivity().setTitle("Transaksi");
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.fragment_order_list, container, false);
        ListView listView = (ListView) rootView.findViewById(R.id.list_view);

        mAdapter = new AdapterOrderList(getContext(), new ArrayList<OrderList>());
        listView.setAdapter(mAdapter);
        mAdapter.notifyDataSetChanged();
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                OrderList orderList = mAdapter.getItem(position);
                FragmentDelivery fragment = new FragmentDelivery();
                fragment.setData(orderList);
                ((BaseActivity) getActivity()).changeFragment(fragment, false, true);
            }
        });

        mJarakTerdekat = new BottomMenu(rootView.findViewById(R.id.jarak_terdekat), R.drawable.ic_delivery_truck, "Jarak Terdekat");
        mWaktuPesan = new BottomMenu(rootView.findViewById(R.id.waktu_pesan), R.drawable.ic_clock, "Waktu Pesan");
        mWaktuAntar = new BottomMenu(rootView.findViewById(R.id.waktu_antar), R.drawable.ic_clock, "Waktu Antar");

        mJarakTerdekat.setSelected(true);

        ((BaseActivity) getActivity()).showLoading(true);

        new RequestLoader(this).loadRequest(0, new BasicNameValuePair[]{
                new BasicNameValuePair("driver_id", User.id)
        }, "transaction", "get_trx_driver", false, true);

        new FirebaseQueryOrder(getContext(), this).queryAllDriverOrderList(User.id);

        return rootView;
    }

    @Override
    public void setFirebaseData(ArrayList<DataSnapshot> dataSnapshots) {

        Log.d(TAG, "setFirebaseData(): " + dataSnapshots.size());
        Log.d(TAG, "dataSnapshots: " + dataSnapshots.toString());

        mAdapter.clear();

        for (DataSnapshot dataSnapshot : dataSnapshots) {

            Log.d(TAG, "children:" + dataSnapshot.toString());
            Package aPackage = dataSnapshot.getValue(Package.class);

            OrderList data = new OrderList(
                    Integer.parseInt(aPackage.statusId),
                    dataSnapshot.getKey(),
                    aPackage.invoice,
                    aPackage.deliveryDate,
                    aPackage.startTime(),
                    aPackage.endTime(),
                    "todo",
                    "todo",
                    Long.parseLong(aPackage.subTotal),
                    Long.parseLong(aPackage.totalOngkir),
                    Long.parseLong(aPackage.grandTotal),
                    "",
                    aPackage.uid,
                    convertToJSONArray(aPackage.items)
            );

            mAdapter.add(data);

            data.setDeliveryDate(aPackage.deliveryDate);

            // todo: get this value
            try {
                data.setLocation(
                        0.0,
                        0.0,
                        0.0,
                        0.0
                );
            } catch (Exception ex) {
                Log.d(TAG, ex.toString());
            }

            mAdapter.notifyDataSetChanged();
        }

        ((BaseActivity) getActivity()).showLoading(false);
    }

    private JSONArray convertToJSONArray(ArrayList<Package.Item> items) {

        JSONArray jsonArray = new JSONArray();

        for (Package.Item item : items) {
            try {
                JSONObject jsonObject = new JSONObject();
                jsonObject.put("tri_item_id", item.itemId);
                jsonObject.put("tri_type_id", item.type);
                jsonObject.put("tri_price", item.price);
                jsonObject.put("tri_quantity", item.quantity);
                jsonObject.put("tri_total", item.subTotal);
                jsonObject.put("tri_ongkir", item.ongkir);
                jsonArray.put(jsonObject);
            } catch (JSONException ex) {
                Log.d(TAG, ex.toString());
            }
        }
        return jsonArray;
    }

    @Override
    public void setData(int index, String result, View[] impactedViews) {
        try {
            JSONObject jsonObject = new JSONObject(result);
            if (jsonObject.getInt("status") == 1) {
                JSONArray jsonArray = jsonObject.getJSONArray("data");
                if (jsonArray.length() > 0) {
                    mAdapter.clear();
                    JSONArray secondJSONArray;

                    for (int i = 0; i < jsonArray.length(); i++) {

                        jsonObject = jsonArray.getJSONObject(i);
                        secondJSONArray = jsonObject.getJSONArray("item");
                        jsonObject = jsonObject.getJSONObject("header");
                        OrderList data = new OrderList(
                                jsonObject.getInt("tran_status_id"),
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
                                "",
                                secondJSONArray
                        );

                        mAdapter.add(data);
                        data.setDeliveryDate(jsonObject.getString("date"));

                        try {
                            data.setLocation(
                                    jsonObject.getDouble("agd_lat"),
                                    jsonObject.getDouble("agd_long"),
                                    jsonObject.getDouble("cua_lat"),
                                    jsonObject.getDouble("cua_long")
                            );
                        } catch (Exception ex) {
                            Log.d(TAG, ex.toString());
                        }
                    }
                    mAdapter.notifyDataSetChanged();
                }
            } else {
                ((BaseActivity) getActivity()).showDialog("", jsonObject.getString("message"), "", "Ok");
            }
        } catch (Exception ex) {
            ((BaseActivity) getActivity()).showDialog("Error parsing", ex.toString(), "", "Ok");
        }

        ((BaseActivity) getActivity()).showLoading(false);
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
                    if (mSelectedMenu != null) {
                        mSelectedMenu.setSelected(false);
                    }
                    mSelectedMenu = this;
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
