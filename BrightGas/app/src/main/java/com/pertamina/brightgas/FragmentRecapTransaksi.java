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
import com.pertamina.brightgas.firebase.models.Package;
import com.pertamina.brightgas.model.Alamat;
import com.pertamina.brightgas.model.Order;
import com.pertamina.brightgas.retrofit.createorder.CreateOrderClient;
import com.pertamina.brightgas.retrofit.createorder.CreateOrderResponse;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.List;

public class FragmentRecapTransaksi extends Fragment {

    private static final String TAG = "recap_transaksi";

    private ViewGroup mRecapContainer;
    private TextView mClock;
    private TextView mDate;
    private TextView mAddress;
    private JSONArray mData;
    private Package mPackage;
    private CreateOrderResponse mCreateOrderResponse;
    private Alamat mAlamat;
    private TextView mPinCode;

    public void setData(JSONArray data, Alamat alamat) {
        this.mData = data;
        this.mAlamat = alamat;
    }

    public void setData(DataSnapshot data, Alamat alamat) {
        this.mPackage = data.getValue(Package.class);
        this.mAlamat = alamat;
    }

    public void setData(CreateOrderResponse data, Alamat alamat) {
        this.mCreateOrderResponse = data;
        this.mAlamat = alamat;
    }

    @Override
    public void onResume() {
        super.onResume();
        getActivity().setTitle("Rangkuman Transaksi");
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.fragment_recap_transaksi, container, false);

        mClock = (TextView) rootView.findViewById(R.id.clock);
        mDate = (TextView) rootView.findViewById(R.id.date);
        mAddress = (TextView) rootView.findViewById(R.id.address);
        mAddress.setText(mAlamat.address);
        mPinCode = (TextView) rootView.findViewById(R.id.pincode);
        mRecapContainer = (ViewGroup) rootView.findViewById(R.id.recapcontainer);

        if (mData != null) {
            try {
                JSONObject secondJSONObject = mData.getJSONObject(0).getJSONObject("header");
                mDate.setText(secondJSONObject.getString("tran_date"));
                mClock.setText(secondJSONObject.getString("tran_time_start") + " - " + secondJSONObject.getString("tran_time_end"));
                mPinCode.setText(secondJSONObject.getString("tran_pin_code"));
            } catch (Exception ex) {
                Log.d(TAG, ex.toString());
            }
            parsingData();
        }

        if (mPackage != null) {
            mDate.setText(mPackage.deliveryDate);
            mClock.setText(mPackage.deliveryTime);
            mPinCode.setText(mPackage.pinCode);
            parsingPackage();
        }

        if (mCreateOrderResponse != null) {
            mDate.setText(mCreateOrderResponse.getData().getArriveDate().split("T")[0]);
            mClock.setText(mCreateOrderResponse.getData().getWaktuPengiriman());
            mPinCode.setText(mCreateOrderResponse.getData().getVerificationCode());
            parsingCreateOrderResponse();
        }

        return rootView;
    }

    private void parsingCreateOrderResponse() {
        View view = LayoutInflater.from(getContext()).inflate(R.layout.item_recap, mRecapContainer, false);
        TextView invoiceNr = (TextView) view.findViewById(R.id.invoicenr);
        TextView total = (TextView) view.findViewById(R.id.total);
        TextView hargatotalbarang = (TextView) view.findViewById(R.id.harga_total_barang);
        TextView ongkoskirim = (TextView) view.findViewById(R.id.ongkos_kirim);
        TextView pinCode = (TextView) view.findViewById(R.id.pincode);

        invoiceNr.setText("???/???/???"); // todo: request api to provide Invoice Data
        total.setText(GlobalActivity.getCalculatedPrice(Long.parseLong(mCreateOrderResponse.getData().getTotalBelanja())));
        hargatotalbarang.setText(GlobalActivity.getCalculatedPrice(Long.parseLong(mCreateOrderResponse.getData().getHargaTotalBarang())));
        ongkoskirim.setText(GlobalActivity.getCalculatedPrice(Long.parseLong(mCreateOrderResponse.getData().getOngkosKirim())));
        pinCode.setText(mCreateOrderResponse.getData().getVerificationCode());

        ViewGroup itemContainer = (ViewGroup) view.findViewById(R.id.item_container);

        List<CreateOrderResponse.Data.Carts> cartses = mCreateOrderResponse.getData().getCarts();
        for (CreateOrderResponse.Data.Carts carts : cartses) {
            if (carts != null) {
                addOrder(itemContainer, new Order(
                        CreateOrderClient.getProductIdentifier(carts.getProductID()).getId(),
                        CreateOrderClient.getProductIdentifier(carts.getProductID()).getType(),
                        Long.parseLong(carts.getPrice()),
                        Long.parseLong(carts.getQuantity()),
                        Long.parseLong(carts.getTotalPrice()),
                        Long.parseLong(carts.getDeliveryFee())
                ));
            }
        }

        mRecapContainer.addView(view);
    }

    private void parsingPackage() {

        View view = LayoutInflater.from(getContext()).inflate(R.layout.item_recap, mRecapContainer, false);
        TextView invoiceNr = (TextView) view.findViewById(R.id.invoicenr);
        TextView total = (TextView) view.findViewById(R.id.total);
        TextView hargatotalbarang = (TextView) view.findViewById(R.id.harga_total_barang);
        TextView ongkoskirim = (TextView) view.findViewById(R.id.ongkos_kirim);
        TextView pinCode = (TextView) view.findViewById(R.id.pincode);

        invoiceNr.setText(mPackage.invoice);
        total.setText(mPackage.grandTotal);
        hargatotalbarang.setText(mPackage.subTotal);
        ongkoskirim.setText(mPackage.totalOngkir);
        pinCode.setText(mPackage.pinCode);

        ViewGroup itemContainer = (ViewGroup) view.findViewById(R.id.item_container);

        for (Package.Item item : mPackage.items) {
            if (item != null) {
                addOrder(itemContainer, new Order(
                        item.itemId,
                        item.type,
                        Long.parseLong(item.price),
                        Long.parseLong(item.quantity),
                        Long.parseLong(item.subTotal),
                        Long.parseLong(item.ongkir)
                ));
            }
        }

        mRecapContainer.addView(view);
    }

    private void parsingData() {
        try {
            for (int i = 0; i < mData.length(); i++) {
                addRecap(mData.getJSONObject(i));
            }
        } catch (Exception ex) {
            Log.d(TAG, ex.toString());
        }
    }

    private void addRecap(JSONObject jsonObject) {

        View rootView = LayoutInflater.from(getContext()).inflate(R.layout.item_recap, mRecapContainer, false);
        TextView invoiceNr = (TextView) rootView.findViewById(R.id.invoicenr);
        TextView total = (TextView) rootView.findViewById(R.id.total);
        TextView hargatotalbarang = (TextView) rootView.findViewById(R.id.harga_total_barang);
        TextView ongkoskirim = (TextView) rootView.findViewById(R.id.ongkos_kirim);
        TextView pinCode = (TextView) rootView.findViewById(R.id.pincode);

        try {

            JSONObject jsonObject1 = jsonObject.getJSONObject("header");
            invoiceNr.setText(jsonObject1.getString("tran_no_invoice"));
            total.setText(jsonObject1.getString("tran_grand_total"));
            hargatotalbarang.setText(jsonObject1.getString("tran_sub_total"));
            ongkoskirim.setText(jsonObject1.getString("tran_ongkir"));
            pinCode.setText(jsonObject1.getString("tran_pin_code"));

            ViewGroup itemContainer = (ViewGroup) rootView.findViewById(R.id.item_container);
            JSONArray jsonArray = jsonObject.getJSONArray("item");
            for (int i = 0; i < jsonArray.length(); i++) {
                jsonObject1 = jsonArray.getJSONObject(i);
                addOrder(itemContainer, new Order(
                        jsonObject1.getString("tri_item_id"),
                        jsonObject1.getString("tri_type_id"),
                        jsonObject1.getLong("tri_price"),
                        jsonObject1.getLong("tri_quantity"),
                        jsonObject1.getLong("tri_total"),
                        jsonObject1.getLong("tri_ongkir")));
            }
        } catch (Exception ex) {
            Log.d(TAG, ex.toString());
        }

        mRecapContainer.addView(rootView);
    }

    private void addOrder(ViewGroup container, Order data) {
        View rootView = LayoutInflater.from(getContext()).inflate(R.layout.item_informasi_transaksi, container, false);
        TextView name = (TextView) rootView.findViewById(R.id.name);
        name.setText(data.name);
        TextView quantity = (TextView) rootView.findViewById(R.id.quantity);
        if (data.id.equals("3")) {
            quantity.setText(data.quantity + " Lusin");
        } else {
            quantity.setText(data.quantity + "");
        }
        TextView price = (TextView) rootView.findViewById(R.id.price);
        price.setText(data.getPrice());
        container.addView(rootView);
    }

}
