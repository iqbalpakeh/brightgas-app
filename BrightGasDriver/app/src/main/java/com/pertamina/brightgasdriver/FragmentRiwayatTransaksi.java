package com.pertamina.brightgasdriver;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;

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

public class FragmentRiwayatTransaksi extends Fragment
        implements RequestLoaderInterface, FirebaseLoadable {

    private static final String TAG = "frag_riwayat_trans";

    private AdapterRiwayatTransaksi mAdapter;

    @Override
    public void onResume() {
        super.onResume();
        getActivity().setTitle("Riwayat Transaksi");
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        ListView rootView = (ListView) inflater.inflate(R.layout.fragment_riwayat_transaksi, container, false);

        mAdapter = new AdapterRiwayatTransaksi(getContext(), new ArrayList<OrderList>());
        rootView.setAdapter(mAdapter);
        mAdapter.notifyDataSetChanged();
        rootView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                OrderList data = mAdapter.getItem(position);
                FragmentHistoryDetail fragment = new FragmentHistoryDetail();
                fragment.setData(data);
                ((BaseActivity) getActivity()).changeFragment(fragment, false, true);
            }
        });

        ((BaseActivity) getActivity()).showLoading(true);

        new RequestLoader(this).loadRequest(0, new BasicNameValuePair[]{
                new BasicNameValuePair("driver_id", User.id)
        }, "driver", "transaction_history", false, true);

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

    private void parseData(String result) {
        try {
            JSONObject jsonObject = new JSONObject(result);
            if (jsonObject.getInt("status") == 1) {
                JSONArray jsonArray = jsonObject.getJSONArray("data");
                mAdapter.clear();
                JSONArray secondJSONArray;

                for (int i = 0; i < jsonArray.length(); i++) {

                    jsonObject = jsonArray.getJSONObject(i);
                    secondJSONArray = jsonObject.getJSONArray("item");
                    jsonObject = jsonObject.getJSONObject("header");
                    OrderList data = new OrderList(jsonObject.getInt("tran_status_id"),
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
                            secondJSONArray);

                    mAdapter.add(data);
                    data.setDeliveryDate(jsonObject.getString("tran_delivered_date"));

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

            } else {
                ((BaseActivity) getActivity()).showDialog("", jsonObject.getString("message"), "", "Ok");
            }

        } catch (Exception ex) {
            ((BaseActivity) getActivity()).showDialog("Error", ex.toString(), "", "Ok");
        }
    }

    @Override
    public void setData(int index, String result, View[] impactedViews) {
        ((BaseActivity) getActivity()).showLoading(false);
        parseData(result);
    }
}
