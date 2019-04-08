package com.pertamina.brightgasse;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.google.firebase.database.DataSnapshot;
import com.pertamina.brightgasse.firebase.FirebaseLoadable;
import com.pertamina.brightgasse.firebase.FirebaseQueryOrder;
import com.pertamina.brightgasse.firebase.models.Address;
import com.pertamina.brightgasse.firebase.models.Agent;
import com.pertamina.brightgasse.firebase.models.Customer;
import com.pertamina.brightgasse.firebase.models.Package;
import com.pertamina.brightgasse.model.Order;
import com.pertamina.brightgasse.model.SimpleOrder;

import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;

public class FragmentOrderBelumKirim extends Fragment
        implements RequestLoaderInterface, FirebaseLoadable {

    private static final String TAG = "frag_order_belum_kirim";

    private SimpleOrder mData;

    private TextView mInvoiceNo;
    private TextView mDistance;
    private TextView mHeaderTime;
    private TextView mHeaderDate;
    private TextView mName;
    private TextView mDate;
    private TextView mTime;
    private TextView mAddress;
    private TextView mTotal;
    private TextView mHargaTotalBarang;
    private TextView mOngkosKirim;
    private TextView mAgenName;
    private TextView mAgenAddress;
    private TextView mAgenDistance;
    private TextView mDriverName;

    private View mAction;
    private View mAgenContainer;
    private View mDriverContainer;

    private ViewGroup mItemContainer;

    public void setData(SimpleOrder data) {
        this.mData = data;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.fragment_order_belum_kirim, container, false);
        mAgenContainer = rootView.findViewById(R.id.agen_container);
        mDriverContainer = rootView.findViewById(R.id.driver_container);
        mDriverName = (TextView) rootView.findViewById(R.id.driver_name);
        mInvoiceNo = (TextView) rootView.findViewById(R.id.invoice_no);
        mDistance = (TextView) rootView.findViewById(R.id.distance);
        mHeaderTime = (TextView) rootView.findViewById(R.id.header_time);
        mHeaderDate = (TextView) rootView.findViewById(R.id.header_date);
        mName = (TextView) rootView.findViewById(R.id.name);
        mDate = (TextView) rootView.findViewById(R.id.my_date);
        mTime = (TextView) rootView.findViewById(R.id.my_time);
        mAddress = (TextView) rootView.findViewById(R.id.address);

        mItemContainer = (ViewGroup) rootView.findViewById(R.id.item_container);
        mTotal = (TextView) rootView.findViewById(R.id.total);
        mHargaTotalBarang = (TextView) rootView.findViewById(R.id.harga_total_barang);
        mOngkosKirim = (TextView) rootView.findViewById(R.id.ongkos_kirim);
        mAgenName = (TextView) rootView.findViewById(R.id.agen_name);
        mAgenAddress = (TextView) rootView.findViewById(R.id.agen_adress);
        mAgenDistance = (TextView) rootView.findViewById(R.id.agen_distance);
        mAction = rootView.findViewById(R.id.action);

        mAction.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FragmentPilihAgen fragment = new FragmentPilihAgen();
                fragment.setData(mData.id);
                fragment.setData(mData);
                ((BaseActivity) getActivity()).changeFragment(fragment, false, true);
            }
        });
        mAction.setVisibility(View.GONE);

        Log.d(TAG, "mData.id = " + mData.id);

        ((BaseActivity) getActivity()).showLoading(true);

        new RequestLoader(this).loadRequest(0, new BasicNameValuePair[]{
                new BasicNameValuePair("id", mData.id),
                new BasicNameValuePair("driver_id", mData.driverId),
                new BasicNameValuePair("agen_id", mData.agenId)
        }, "transaction", "get_detail", false, true);

        new FirebaseQueryOrder(getContext(), this)
                .queryOrderDetail(mData.customerId, mData.id);

        return rootView;
    }

    @Override
    public void setFirebaseData(ArrayList<DataSnapshot> dataSnapshots) {

        Log.d(TAG, "Order:" + dataSnapshots.get(FirebaseQueryOrder.ORDER_OFFSET).toString());
        Log.d(TAG, "Customer:" + dataSnapshots.get(FirebaseQueryOrder.CUSTOMER_OFFSET).toString());
        Log.d(TAG, "Customer Address:" + dataSnapshots.get(FirebaseQueryOrder.CUSTOMER_ADDRESS_OFFSET).toString());
        Log.d(TAG, "Agent:" + dataSnapshots.get(FirebaseQueryOrder.AGENT_OFFSET).toString());
        Log.d(TAG, "Agent Address:" + dataSnapshots.get(FirebaseQueryOrder.AGENT_ADDRESS_OFFSET).toString());

        Package aPackage = dataSnapshots.get(FirebaseQueryOrder.ORDER_OFFSET).getValue(Package.class);
        Customer customer = dataSnapshots.get(FirebaseQueryOrder.CUSTOMER_OFFSET).getValue(Customer.class);
        Address customerAddress = dataSnapshots.get(FirebaseQueryOrder.CUSTOMER_ADDRESS_OFFSET).getValue(Address.class);
        Agent agent = dataSnapshots.get(FirebaseQueryOrder.AGENT_OFFSET).getValue(Agent.class);
        Address agentAddress = dataSnapshots.get(FirebaseQueryOrder.AGENT_ADDRESS_OFFSET).getValue(Address.class);

        mInvoiceNo.setText(aPackage.invoice);
        mDistance.setText("TODO");

        mHeaderTime.setText(aPackage.deliveryTime);
        mHeaderDate.setText(aPackage.deliveryDate);
        mName.setText(customer.name);
        mDate.setText(aPackage.deliveryDate);
        mTime.setText(aPackage.startTime() + " - " + aPackage.endTime());
        mAddress.setText(customerAddress.address);
        mTotal.setText(((BaseActivity) getActivity()).getCalculatedPrice(Long.parseLong(aPackage.grandTotal)));
        mHargaTotalBarang.setText(((BaseActivity) getActivity()).getCalculatedPrice(Long.parseLong(aPackage.subTotal)));
        mOngkosKirim.setText(((BaseActivity) getActivity()).getCalculatedPrice(Long.parseLong(aPackage.totalOngkir)));

        if (Integer.parseInt(aPackage.statusId) > 0) {
            mAgenContainer.setVisibility(View.VISIBLE);
            mAgenName.setText(agent.name);
            mAgenAddress.setText(agentAddress.getAddress() + ", " + agent.getTelp() + "");
            mAgenDistance.setText("TODO");

            //TODO:after agent and driver
            //if (((BaseActivity) getActivity()).isValidString(secondJSONObject.getString("agd_id"))) {
            //    mDriverContainer.setVisibility(View.VISIBLE);
            //    mDriverName.setText(secondJSONObject.getString("agd_name"));
            //}
        }

        if (Integer.parseInt(aPackage.statusId) != 2) {
            mAction.setVisibility(View.VISIBLE);
        }

        for (Package.Item item : aPackage.items) {
            addOrder(new Order(
                    item.itemId,
                    item.type,
                    Long.parseLong(item.price),
                    Long.parseLong(item.quantity),
                    Long.parseLong(item.subTotal),
                    Long.parseLong(item.ongkir))
            );
        }

        ((BaseActivity) getActivity()).showLoading(false);

    }

    @Override
    public void setData(int index, String result, View[] impactedViews) {

        try {
            JSONObject jsonObject = new JSONObject(result);

            if (jsonObject.getInt("status") == 1) {

                jsonObject = jsonObject.getJSONObject("data");
                JSONObject secondJSONObject = jsonObject.getJSONArray("header").getJSONObject(0);
                mInvoiceNo.setText(secondJSONObject.getString("tran_no_invoice"));
                getActivity().setTitle(secondJSONObject.getString("tran_no_invoice"));

                try {
                    mDistance.setText(secondJSONObject.getString("distance"));
                } catch (Exception ex) {
                    mDistance.setText("-");
                }

                mHeaderTime.setText(secondJSONObject.getString("tran_time_start"));
                mHeaderDate.setText(secondJSONObject.getString("tran_date"));
                mName.setText(secondJSONObject.getString("cus_name"));
                mDate.setText(secondJSONObject.getString("tran_date"));
                mTime.setText(secondJSONObject.getString("tran_time_start") + " - " + secondJSONObject.getString("tran_time_end"));
                mAddress.setText(secondJSONObject.getString("cua_address") + "\n" + secondJSONObject.getString("cua_city_name") + "," + secondJSONObject.getString("cua_province_name"));
                mTotal.setText(((BaseActivity) getActivity()).getCalculatedPrice(secondJSONObject.getLong("tran_grand_total")));
                mHargaTotalBarang.setText(((BaseActivity) getActivity()).getCalculatedPrice(secondJSONObject.getLong("tran_sub_total")));
                mOngkosKirim.setText(((BaseActivity) getActivity()).getCalculatedPrice(secondJSONObject.getLong("tran_ongkir")));

                int statusId = secondJSONObject.getInt("tran_status_id");
                if (statusId > 0) {
                    mAgenContainer.setVisibility(View.VISIBLE);
                    mAgenName.setText(secondJSONObject.getString("age_name"));
                    mAgenAddress.setText(secondJSONObject.getString("age_address") + " (" + secondJSONObject.getString("age_phone") + ")");

                    try {
                        mAgenDistance.setText(secondJSONObject.getString("distance"));
                    } catch (Exception ex) {
                        mAgenDistance.setText("-");
                    }

                    if (((BaseActivity) getActivity()).isValidString(secondJSONObject.getString("agd_id"))) {
                        mDriverContainer.setVisibility(View.VISIBLE);
                        mDriverName.setText(secondJSONObject.getString("agd_name"));
                    }
                }

                if (secondJSONObject.getInt("tran_status_id") != 2) {
                    mAction.setVisibility(View.VISIBLE);
                }

                JSONArray jsonArray = jsonObject.getJSONArray("item");
                if (jsonArray.length() > 0) {
                    for (int i = 0; i < jsonArray.length(); i++) {
                        jsonObject = jsonArray.getJSONObject(i);
                        addOrder(new Order(
                                jsonObject.getString("tri_id"),
                                jsonObject.getString("tri_type_id"),
                                jsonObject.getLong("tri_price"),
                                jsonObject.getLong("tri_quantity"),
                                jsonObject.getLong("tri_total"),
                                jsonObject.getLong("tri_ongkir"))
                        );
                    }
                }

            } else {
                Log.d(TAG, jsonObject.getString("message"));
            }
        } catch (Exception ex) {
            Log.d(TAG, ex.toString());
        }

    }

    private void addOrder(Order data) {
        View rv = LayoutInflater.from(getContext()).inflate(R.layout.item_informasi_transaksi, mItemContainer, false);
        TextView name = (TextView) rv.findViewById(R.id.name);
        name.setText(data.name);
        TextView quantity = (TextView) rv.findViewById(R.id.quantity);
        quantity.setText(data.quantity + "");
        TextView price = (TextView) rv.findViewById(R.id.price);
        price.setText(data.getPrice());
        mItemContainer.addView(rv);
    }

}
