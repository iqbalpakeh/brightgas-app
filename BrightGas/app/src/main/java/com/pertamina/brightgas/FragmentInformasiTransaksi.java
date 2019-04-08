package com.pertamina.brightgas;

import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.google.android.gms.maps.model.LatLng;
import com.google.firebase.database.DataSnapshot;
import com.pertamina.brightgas.firebase.FirebaseQueryOrder;
import com.pertamina.brightgas.firebase.models.Address;
import com.pertamina.brightgas.firebase.models.Agent;
import com.pertamina.brightgas.firebase.models.Driver;
import com.pertamina.brightgas.firebase.models.Package;
import com.pertamina.brightgas.model.Order;
import com.pertamina.brightgas.model.RiwayatTransaksi;
import com.pertamina.brightgas.retrofit.createorder.CreateOrderClient;
import com.pertamina.brightgas.retrofit.detailorder.DetailOrderClient;
import com.pertamina.brightgas.retrofit.detailorder.DetailOrderInterface;
import com.pertamina.brightgas.retrofit.detailorder.DetailOrderResponse;
import com.pertamina.brightgas.retrofit.listorder.ListOrderClient;

import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class FragmentInformasiTransaksi extends Fragment
        implements RequestLoaderInterface, FirebaseQueryOrder.FirebaseOrderLoadable, DetailOrderInterface {

    private static final String TAG = "informasi_transaksi";

    private static final int CANCEL = 0;
    private static final int DETAIL = 1;

    public static int COUNTER = 300;

    private static ArrayList<Order> mDatas;
    private RiwayatTransaksi mData;

    private LatLng mDriverLocation;
    private LatLng mDestinationLocation;

    private String mRemarkVal;
    private String mDriverName;

    private ViewGroup mItemContainer;
    private View mPendingContainer;
    private View mAgenPengirimContainer;
    private View mDeliveryContainer;
    private MenuInflater mInflater;
    private Menu mMenu;

    private TextView mTotal;
    private TextView mHargaTotalBarang;
    private TextView mOngkosKirim;
    private TextView mInvoice;
    private TextView mClock;
    private TextView mDate;
    private TextView mAddress;
    private TextView mPinCode;
    private TextView mAgenNameOne;
    private TextView mAgenNameTwo;
    private TextView mAgenAddressOne;
    private TextView mAgenAddressTwo;
    private TextView mAgenPhone;
    private TextView mAgenDriver;
    private TextView mRemark;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        this.mInflater = inflater;
        this.mMenu = menu;
        inflater.inflate(R.menu.empty, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        switch (id) {
            case R.id.reschedule:
                Log.d(TAG, "RESCHEDULE");
                FragmentReschedule fragment = new FragmentReschedule();
                fragment.setData(mData.id);
                ((BaseActivity) getActivity()).changeFragment(fragment, false, true);
                break;
            case R.id.cancel:
                ((BaseActivity) getActivity()).showDialog("", "Apakah anda yakin ingin membatalkan pesanan ?", "Ya", "Tidak", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        doCancel();
                        dialog.dismiss();
                    }
                }, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });
                Log.d(TAG, "CANCEL");
                break;
        }
        return false;
    }

    private void doCancel() {
        if (GlobalActivity.getBackEndServiceProvider() == GlobalActivity.LEGACY) {
            ((BaseActivity) getActivity()).showLoading(true);
            new RequestLoader(this).loadRequest(CANCEL, new BasicNameValuePair[]{
                    new BasicNameValuePair("id", mData.id)
            }, "transaction", "cancel", true, true);
        }
        if (GlobalActivity.getBackEndServiceProvider() == GlobalActivity.FIREBASE) {
            //TODO: add firebase query here
        }
        if (GlobalActivity.getBackEndServiceProvider() == GlobalActivity.SWAGGER) {
            //TODO: add firebase query here
        }
    }

    public void setData(RiwayatTransaksi data) {
        this.mData = data;
    }

    @Override
    public void onResume() {
        super.onResume();
        getActivity().setTitle("Informasi Transaksi");
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View mRootView = inflater.inflate(R.layout.fragment_informasi_transaksi, container, false);
        mPendingContainer = mRootView.findViewById(R.id.pending_container);
        mPendingContainer.setVisibility(View.GONE);
        mRemark = (TextView) mRootView.findViewById(R.id.remark);
        mInvoice = (TextView) mRootView.findViewById(R.id.invoicenr);
        mClock = (TextView) mRootView.findViewById(R.id.clock);
        mDate = (TextView) mRootView.findViewById(R.id.date);
        mAddress = (TextView) mRootView.findViewById(R.id.address);
        mPinCode = (TextView) mRootView.findViewById(R.id.pincode);
        mAgenNameOne = (TextView) mRootView.findViewById(R.id.agen_name_1);
        mAgenNameTwo = (TextView) mRootView.findViewById(R.id.agen_name_2);
        mAgenAddressOne = (TextView) mRootView.findViewById(R.id.agen_address_1);
        mAgenAddressTwo = (TextView) mRootView.findViewById(R.id.agen_address_2);
        mAgenPhone = (TextView) mRootView.findViewById(R.id.agen_phone);
        mAgenDriver = (TextView) mRootView.findViewById(R.id.agen_driver);
        mTotal = (TextView) mRootView.findViewById(R.id.total);
        mHargaTotalBarang = (TextView) mRootView.findViewById(R.id.harga_total_barang);
        mOngkosKirim = (TextView) mRootView.findViewById(R.id.ongkos_kirim);
        mAgenPengirimContainer = mRootView.findViewById(R.id.agen_pengirim_container);
        mDeliveryContainer = mRootView.findViewById(R.id.delivery_container);
        TextView topText = (TextView) mRootView.findViewById(R.id.top_text);
        if (mData != null) {
            Log.d(TAG, "mData.statusId: " + mData.statusId);
            switch (mData.statusId) {
                case RiwayatTransaksi.STATUS_PENDING:
                    Log.d(TAG, "STATUS_PENDING");
                    mAgenPengirimContainer.setVisibility(View.GONE);
                    mDeliveryContainer.setVisibility(View.GONE);
                    break;
                case RiwayatTransaksi.STATUS_DIPROSES:
                    Log.d(TAG, "STATUS_DIPROSES");
                    mDeliveryContainer.setVisibility(View.GONE);
                    topText.setVisibility(View.GONE);
                    break;
                case RiwayatTransaksi.STATUS_DIKIRIM:
                    Log.d(TAG, "STATUS_DIKIRIM");
                    topText.setVisibility(View.GONE);
                    mAgenPengirimContainer.setVisibility(View.GONE);
                    View lacak = mRootView.findViewById(R.id.lacak);
                    lacak.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            FragmentLacak fragment = new FragmentLacak();
                            fragment.setData(mDriverName, mDriverLocation, mDestinationLocation);
                            ((BaseActivity) getActivity()).changeFragment(fragment, false, true);
                        }
                    });
                    COUNTER = 200;
                    break;
            }
            ((BaseActivity) getActivity()).showLoading(true);
            if (GlobalActivity.getBackEndServiceProvider() == GlobalActivity.LEGACY) {
                new RequestLoader(this).loadRequest(DETAIL, new BasicNameValuePair[]{
                        new BasicNameValuePair("id", mData.id),
                        new BasicNameValuePair("agen_id", mData.agenId),
                        new BasicNameValuePair("driver_id", mData.driverId)
                }, "transaction", "get_detail", true);
            }
            if (GlobalActivity.getBackEndServiceProvider() == GlobalActivity.FIREBASE) {
                Log.d(TAG, "mData.id = " + mData.id);
                Log.d(TAG, "mData.agenId = " + mData.agenId);
                Log.d(TAG, "mData.driverId = " + mData.driverId);
                new FirebaseQueryOrder(getActivity(), this).queryTransaction(
                        mData.id,
                        mData.agenId,
                        mData.driverId
                );
            }
            if (GlobalActivity.getBackEndServiceProvider() == GlobalActivity.SWAGGER) {
                new DetailOrderClient(getContext(), this).detailOrder(mData.id);
            }
        } else {
            COUNTER++;
            switch (COUNTER) {
                case 1:
                    mAgenPengirimContainer.setVisibility(View.GONE);
                    mDeliveryContainer.setVisibility(View.GONE);
                    break;
                case 2:
                    mDeliveryContainer.setVisibility(View.GONE);
                    topText.setText("Pesanan sudah diteruskan ke agen");
                    break;
                case 3:
                    topText.setVisibility(View.GONE);
                    mAgenPengirimContainer.setVisibility(View.GONE);
                    View lacak = mRootView.findViewById(R.id.lacak);
                    lacak.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            ((BaseActivity) getActivity()).changeFragment(new FragmentLacak(), false, true);
                        }
                    });
                    COUNTER = 200;
                    break;
            }
        }
        mItemContainer = (ViewGroup) mRootView.findViewById(R.id.item_container);
        if (mDatas != null) {
            for (int i = 0; i < mDatas.size(); i++) {
                addOrder(mDatas.get(i));
            }
            recalculateTotal();
        }
        View pinContainer = mRootView.findViewById(R.id.pincontainer);
        pinContainer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FragmentTransaksiSelesai fragment = new FragmentTransaksiSelesai();
                fragment.setData(mData.id);
                ((BaseActivity) getActivity()).changeFragment(fragment, false, true);
            }
        });
        return mRootView;
    }

    private void addOrder(final Order data) {
        View rootView = LayoutInflater.from(getContext()).inflate(R.layout.item_informasi_transaksi, mItemContainer, false);
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
        mItemContainer.addView(rootView);
    }

    public void recalculateTotal() {
        long totalHarga = 0;
        long totalOngkir = 0;
        long totalPrice = 0;
        for (int i = 0; i < mDatas.size(); i++) {
            totalHarga += mDatas.get(i).getTotal();
            totalOngkir += mDatas.get(i).orderPrice;
        }
        totalPrice = totalHarga + totalOngkir;
        mTotal.setText(GlobalActivity.getCalculatedPrice(totalPrice));
        mHargaTotalBarang.setText(GlobalActivity.getCalculatedPrice(totalHarga));
        mOngkosKirim.setText(GlobalActivity.getCalculatedPrice(totalOngkir));
    }

    private void activatePending() {
        mPendingContainer.setVisibility(View.VISIBLE);
        mRemark.setText(mRemarkVal);
    }

    private void parseDetail(String result) {
        ((BaseActivity) getActivity()).showLoading(false);
        try {
            JSONObject jsonObject = new JSONObject(result);
            if (jsonObject.getInt("status") == 1) {
                jsonObject = jsonObject.getJSONObject("data");
                JSONObject secondJSONObject = jsonObject.getJSONArray("header").getJSONObject(0);
                mInvoice.setText(secondJSONObject.getString("tran_no_invoice"));
                if (secondJSONObject.getInt("tran_pending_flag") == 1) {
                    mRemarkVal = secondJSONObject.getString("tran_pending_remark");
                    activatePending();
                }
                mDate.setText(secondJSONObject.getString("tran_date"));
                mClock.setText(secondJSONObject.getString("tran_time_start") + " - " + secondJSONObject.getString("tran_time_end"));
                mAddress.setText(secondJSONObject.getString("cua_address") + "\n" + secondJSONObject.getString("cua_city_name") + ", " + secondJSONObject.getString("cua_province_name"));
                mPinCode.setText(secondJSONObject.getString("tran_pin_code"));
                mHargaTotalBarang.setText(GlobalActivity.getCalculatedPrice(secondJSONObject.getLong("tran_sub_total")));
                mOngkosKirim.setText(GlobalActivity.getCalculatedPrice(secondJSONObject.getLong("tran_ongkir")));
                mTotal.setText(GlobalActivity.getCalculatedPrice(secondJSONObject.getLong("tran_grand_total")));
                mAgenNameOne.setText(secondJSONObject.getString("age_name"));
                mAgenNameTwo.setText(secondJSONObject.getString("age_name"));
                mAgenAddressOne.setText(secondJSONObject.getString("age_address"));
                mAgenAddressTwo.setText(secondJSONObject.getString("age_address"));
                mAgenPhone.setText(secondJSONObject.getString("age_phone"));
                mDriverName = secondJSONObject.getString("agd_name");
                mAgenDriver.setText(mDriverName);
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
                try {
                    mDriverLocation = new LatLng(secondJSONObject.getDouble("agd_lat"), secondJSONObject.getDouble("agd_long"));
                    mDestinationLocation = new LatLng(secondJSONObject.getDouble("cua_lat"), secondJSONObject.getDouble("cua_long"));
                } catch (Exception ex) {
                    Log.d(TAG, ex.toString());
                }
                int status = secondJSONObject.getInt("tran_status_id");
                if (status < 2) {
                    setHasOptionsMenu(true);
                    mInflater.inflate(R.menu.informasi_transaksi, mMenu);
                }
            }
        } catch (Exception ex) {
            Log.d(TAG, ex.toString());
        }
    }

    private void parseCancel(String result) {
        try {
            JSONObject jsonObject = new JSONObject(result);
            if (jsonObject.getInt("status") == 1) {
                ((BaseActivity) getActivity()).changeFragment(new FragmentBeranda());
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
        switch (index) {
            case DETAIL:
                parseDetail(result);
                break;
            case CANCEL:
                parseCancel(result);
                break;
        }
    }

    @Override
    public void retrofitDetailOrder(DetailOrderResponse response) {
        ((BaseActivity) getActivity()).showLoading(false);
        if (response.getData().getStatus().equals(ListOrderClient.STATUS_PENDING)) {
            mRemarkVal = "Informasi Agen Pengirim akan diinformasikan selanjutnya";
            activatePending();
        }
        mInvoice.setText("???/???/???"); // todo: request this to API provider
        mDate.setText(response.getData().getArriveDate().split("T")[0]);
        mClock.setText("???"); // todo: request this to API provider
        mAddress.setText(response.getData().getDeliveryAddress());
        mPinCode.setText("???"); // todo: api return NULL verification code
        mHargaTotalBarang.setText(GlobalActivity.getCalculatedPrice(Long.parseLong(response.getData().getGrandTotalPrice())));
        mOngkosKirim.setText(GlobalActivity.getCalculatedPrice(Long.parseLong(response.getData().getOngkosKirim())));
        mTotal.setText(GlobalActivity.getCalculatedPrice(Long.parseLong(response.getData().getGrandTotalPrice()))); // todo: is this supposed to be grandtotalprice or total harga barang
        List<DetailOrderResponse.Data.Carts> cartses = response.getData().getCartsList();
        for (DetailOrderResponse.Data.Carts carts : cartses) {
            addOrder(new Order(
                    CreateOrderClient.getProductIdentifier(carts.getProductID()).getId(),
                    CreateOrderClient.getProductIdentifier(carts.getProductID()).getType(),
                    Long.parseLong(carts.getPrice()),
                    Long.parseLong(carts.getQuantity()),
                    Long.parseLong(carts.getTotalPrice()),
                    Long.parseLong(carts.getDeliveryFee()))
            );
        }
        if (ListOrderClient.definedStatus(response.getData().getStatus()) < 2) {
            setHasOptionsMenu(true);
            mInflater.inflate(R.menu.informasi_transaksi, mMenu);
        }
    }

    @Override
    public void setTransactionData(DataSnapshot packageSnapshot,
                                   DataSnapshot addressSnapshot,
                                   DataSnapshot agentSnapshot,
                                   DataSnapshot driverSnapshot) {
        ((BaseActivity) getActivity()).showLoading(false);
        Log.d(TAG, packageSnapshot.toString());
        Log.d(TAG, addressSnapshot.toString());
        Log.d(TAG, agentSnapshot.toString());
        Log.d(TAG, driverSnapshot.toString());
        Package aPackage = packageSnapshot.getValue(Package.class);
        Address address = addressSnapshot.getValue(Address.class);
        Agent agent = agentSnapshot.getValue(Agent.class);
        Driver driver = driverSnapshot.getValue(Driver.class);
        Log.d(TAG, "latitude: " + address.latitude);
        Log.d(TAG, "longitude: " + address.longitude);
        if (aPackage.statusId.equals(Package.STATUS_PENDING + "")) {
            mRemarkVal = "Informasi Agen Pengirim akan diinformasikan selanjutnya";
            activatePending();
        }
        mInvoice.setText(aPackage.invoice);
        mDate.setText(aPackage.deliveryDate);
        mClock.setText(aPackage.deliveryTime);
        mAddress.setText(address.address);
        mPinCode.setText(aPackage.pinCode);
        mHargaTotalBarang.setText(aPackage.subTotal);
        mOngkosKirim.setText(aPackage.totalOngkir);
        mTotal.setText(aPackage.grandTotal);
        if (agent != null) {
            mAgenNameOne.setText(agent.name);
            mAgenNameTwo.setText(agent.name);
            mAgenAddressOne.setText(agent.address);
            mAgenAddressTwo.setText(agent.address);
            mAgenPhone.setText(agent.phone);
        }
        if (driver != null) {
            mDriverName = driver.name;
            mAgenDriver.setText(mDriverName);
        }
        for (Package.Item item : aPackage.items) {
            if (item != null) {
                addOrder(new Order(
                        item.itemId,
                        item.type,
                        Long.parseLong(item.price),
                        Long.parseLong(item.quantity),
                        Long.parseLong(item.subTotal),
                        Long.parseLong(item.ongkir))
                );
            }
        }
        //todo: implement this
        //try {
        //    mDriverLocation = new LatLng(secondJSONObject.getDouble("agd_lat"), secondJSONObject.getDouble("agd_long"));
        //    mDestinationLocation = new LatLng(secondJSONObject.getDouble("cua_lat"), secondJSONObject.getDouble("cua_long"));
        //} catch (Exception ex) {
        //    Log.d(TAG, ex.toString());
        //}
        if (Integer.parseInt(aPackage.statusId) < 2) {
            setHasOptionsMenu(true);
            mInflater.inflate(R.menu.informasi_transaksi, mMenu);
        }

    }
}
