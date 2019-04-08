package com.pertamina.brightgas;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.google.firebase.database.DataSnapshot;
import com.pertamina.brightgas.firebase.FirebaseQueryOrder;
import com.pertamina.brightgas.model.Order;
import com.pertamina.brightgas.model.RiwayatTransaksi;

import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONObject;

public class FragmentRincianRiwayatTransaksi extends Fragment
        implements RequestLoaderInterface, FirebaseQueryOrder.FirebaseOrderLoadable{

    private static final String TAG = "frag_riwayat_trans";

    private RiwayatTransaksi mData;

    private ViewGroup mItemContainer;

    private TextView mTotal;
    private TextView mHargaTotalBarang;
    private TextView mOngkosKirim;
    private TextView mInvoiceNr;
    private TextView mClock;
    private TextView mDate;
    private TextView mAddress;
    private TextView mAgenName;
    private TextView mAgenAddress;
    private TextView mAgenDriver;

    @Override
    public void onResume() {
        super.onResume();
        getActivity().setTitle("Rincian Riwayat Transaksi");
    }

    public void setData(RiwayatTransaksi data) {
        this.mData = data;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.fragment_rincian_riwayat_transaksi, container, false);

        mInvoiceNr = (TextView) rootView.findViewById(R.id.invoicenr);
        mClock = (TextView) rootView.findViewById(R.id.myclock);
        mDate = (TextView) rootView.findViewById(R.id.date);
        mAddress = (TextView) rootView.findViewById(R.id.address);
        mAgenName = (TextView) rootView.findViewById(R.id.agen);
        mAgenAddress = (TextView) rootView.findViewById(R.id.agenadress);
        mAgenDriver = (TextView) rootView.findViewById(R.id.driver);
        mTotal = (TextView) rootView.findViewById(R.id.total);
        mHargaTotalBarang = (TextView) rootView.findViewById(R.id.harga_total_barang);
        mOngkosKirim = (TextView) rootView.findViewById(R.id.ongkos_kirim);
        mItemContainer = (ViewGroup) rootView.findViewById(R.id.item_container);

        requestTransaction();

        return rootView;
    }

    private void requestTransaction() {
        if (GlobalActivity.getBackEndServiceProvider() == GlobalActivity.LEGACY) {
            new RequestLoader(this).loadRequest(0, new BasicNameValuePair[]{
                    new BasicNameValuePair("id", mData.id),
                    new BasicNameValuePair("agen_id", mData.agenId),
                    new BasicNameValuePair("driver_id", mData.driverId)
            }, "transaction", "get_detail", true);
        }

        if (GlobalActivity.getBackEndServiceProvider() == GlobalActivity.FIREBASE) {
            new FirebaseQueryOrder(getActivity(), this).queryTransaction(
                    mData.id,
                    mData.agenId,
                    mData.driverId
            );
        }

        if (GlobalActivity.getBackEndServiceProvider() == GlobalActivity.SWAGGER) {
            // todo: add swagger code here
        }
    }

    private void addOrder(final Order data) {

        View rootView = LayoutInflater.from(getContext()).inflate(R.layout.item_informasi_transaksi, mItemContainer, false);

        TextView name = (TextView) rootView.findViewById(R.id.name);
        name.setText(data.name);

        TextView quantity = (TextView) rootView.findViewById(R.id.quantity);
        quantity.setText(data.quantity + "");

        TextView price = (TextView) rootView.findViewById(R.id.price);
        price.setText(data.getPrice());

        mItemContainer.addView(rootView);
    }

    @Override
    public void setData(int index, String result, View[] impactedViews) {

        try {

            JSONObject jsonObject = new JSONObject(result);

            if (jsonObject.getInt("status") == 1) {

                jsonObject = jsonObject.getJSONObject("data");
                JSONObject secondJSONObject = jsonObject.getJSONArray("header").getJSONObject(0);

                mInvoiceNr.setText(secondJSONObject.getString("tran_no_invoice"));
                mDate.setText(secondJSONObject.getString("tran_date"));
                mClock.setText(secondJSONObject.getString("tran_time_start") + " - " + secondJSONObject.getString("tran_time_end"));
                mAddress.setText(secondJSONObject.getString("cua_address") + "\n" + secondJSONObject.getString("cua_city_name") + ", " + secondJSONObject.getString("cua_province_name"));
                mHargaTotalBarang.setText(GlobalActivity.getCalculatedPrice(secondJSONObject.getLong("tran_sub_total")));
                mOngkosKirim.setText(GlobalActivity.getCalculatedPrice(secondJSONObject.getLong("tran_ongkir")));
                mTotal.setText(GlobalActivity.getCalculatedPrice(secondJSONObject.getLong("tran_grand_total")));
                mAgenName.setText(secondJSONObject.getString("age_name"));
                mAgenAddress.setText(secondJSONObject.getString("age_address"));
                mAgenDriver.setText(secondJSONObject.getString("agd_name"));

                JSONArray jsonArray = jsonObject.getJSONArray("item");
                if (jsonArray.length() > 0) {
                    for (int i = 0; i < jsonArray.length(); i++) {
                        jsonObject = jsonArray.getJSONObject(i);

                        addOrder(new Order(jsonObject.getString("tri_item_id"),
                                jsonObject.getString("tri_item_id"),
                                jsonObject.getLong("tri_price"),
                                jsonObject.getLong("tri_quantity"),
                                jsonObject.getLong("tri_total"),
                                jsonObject.getLong("tri_ongkir")));
                    }
                }

            }
        } catch (Exception ex) {
            Log.d(TAG, ex.toString());
        }
    }


    @Override
    public void setTransactionData(DataSnapshot packageSnapshot,
                                   DataSnapshot addressSnapshot,
                                   DataSnapshot agentSnapshot,
                                   DataSnapshot driverSnapshot) {

        Log.d(TAG, packageSnapshot.toString());
        Log.d(TAG, addressSnapshot.toString());
        Log.d(TAG, agentSnapshot.toString());
        Log.d(TAG, driverSnapshot.toString());
    }
}
