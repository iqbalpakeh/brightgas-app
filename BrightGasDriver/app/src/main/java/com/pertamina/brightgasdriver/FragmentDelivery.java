package com.pertamina.brightgasdriver;

import android.app.Service;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;

import com.directions.route.Route;
import com.directions.route.RouteException;
import com.directions.route.Routing;
import com.directions.route.RoutingListener;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.firebase.database.DataSnapshot;
import com.pertamina.brightgasdriver.firebase.FirebaseLoadable;
import com.pertamina.brightgasdriver.firebase.FirebasePendingOrder;
import com.pertamina.brightgasdriver.firebase.FirebasePinVerification;
import com.pertamina.brightgasdriver.firebase.FirebaseQueryOrder;
import com.pertamina.brightgasdriver.firebase.FirebaseTakeOrder;
import com.pertamina.brightgasdriver.firebase.model.Address;
import com.pertamina.brightgasdriver.firebase.model.Agent;
import com.pertamina.brightgasdriver.firebase.model.Customer;
import com.pertamina.brightgasdriver.firebase.model.Package;
import com.pertamina.brightgasdriver.model.Order;
import com.pertamina.brightgasdriver.model.OrderList;

import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

public class FragmentDelivery extends Fragment
        implements OnMapReadyCallback, GoogleMap.OnMarkerDragListener, GoogleMap.OnMapLongClickListener,
        GoogleMap.OnMarkerClickListener, RequestLoaderInterface, GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener, InterfaceOnRequestPermissionsResult, LocationListener,
        FirebaseLoadable, FirebasePinVerification.FirebasePinVerificationLoadable, FirebaseTakeOrder.FirebaseTakeOrderLoadable,
        FirebasePendingOrder.FirebasePendingOrderLoadable, RoutingListener {

    private static final String TAG = "frag_delivery";

    private static final int PERMISSION_ACCESS_FINE_LOCATION = 0;
    private static final int PERMISSION_WRITE_GSERVICE = 2;

    private static final int VALIDATE_PIN = 0;
    private static final int DATA = 1;
    private static final int TAKE_ORDER = 2;
    private static final int SET_PENDING = 3;
    private static final int SET_LOKASI = 4;

    private static final int DEFAULT_ZOOM = 13;

    private OrderList mOrderList;
    private GoogleMap mMap;
    private GoogleApiClient mGoogleApiClient;
    private Marker myMarker;
    private EditText mVerifikasiPin;
    private TextView mDurationTextView;
    private LatLng mDestinationLocation, mDriverLocation, mAgenLocation;
    private TextView mTotal;
    private TextView mHargaTotalBarang;
    private TextView mOngkosKirim;
    private TextView mInvoiceNo;
    private TextView mDistance;
    private TextView mHeaderTime;
    private TextView mHeaderDate;
    private TextView mName;
    private TextView mDate;
    private TextView mMyTime;
    private TextView mAddress;
    private TextView mAction;
    private TextView mRemark;
    private ViewGroup mItemContainer;
    private View mTypeDriverContainer, mPendingContainer;
    private boolean mIsPutMarker;
    private int mStatus;
    private String remarkVal;

    private Package mPackage;
    private Customer mCustomer;
    private Address mCustomerAddress;
    private Agent mAgent;
    private Address mAgentAddress;

    private static final int[] COLORS = new int[]{
            R.color.primary_dark,
            R.color.primary,
            R.color.primary_light,
            R.color.accent,
            R.color.primary_dark_material_light
    };

    private List<Polyline> mPolylines;

    @Override
    public void onStart() {
        super.onStart();
        new MapTask().executeOnExecutor(new ThreadPoolExecutor(5, 10, 5000, TimeUnit.MILLISECONDS, new LinkedBlockingQueue<Runnable>()), null);
    }

    @Override
    public void onStop() {
        super.onStop();
        if (mGoogleApiClient != null)
            mGoogleApiClient.disconnect();
    }

    public void setData(OrderList orderList) {
        this.mOrderList = orderList;
    }

    @Override
    public void onResume() {
        super.onResume();
        ((BaseActivity) getActivity()).setInterfaceOnRequestPermissionsResult(this);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_delivery, container, false);

        mPolylines = new ArrayList<>();

        mItemContainer = (ViewGroup) rootView.findViewById(R.id.item_container);
        mTotal = (TextView) rootView.findViewById(R.id.total);
        mRemark = (TextView) rootView.findViewById(R.id.remark);
        mTypeDriverContainer = rootView.findViewById(R.id.type_driver_container);
        mPendingContainer = rootView.findViewById(R.id.pending_container);
        mHargaTotalBarang = (TextView) rootView.findViewById(R.id.harga_total_barang);
        mOngkosKirim = (TextView) rootView.findViewById(R.id.ongkos_kirim);
        mInvoiceNo = (TextView) rootView.findViewById(R.id.invoice_no);
        mDistance = (TextView) rootView.findViewById(R.id.distance);
        mHeaderTime = (TextView) rootView.findViewById(R.id.header_time);
        mHeaderDate = (TextView) rootView.findViewById(R.id.header_date);
        mName = (TextView) rootView.findViewById(R.id.name);
        mDate = (TextView) rootView.findViewById(R.id.date);
        mMyTime = (TextView) rootView.findViewById(R.id.my_time);
        mAddress = (TextView) rootView.findViewById(R.id.address);
        mDurationTextView = (TextView) rootView.findViewById(R.id.duration);
        mVerifikasiPin = (EditText) rootView.findViewById(R.id.verifikasi_pin);
        mAction = (TextView) rootView.findViewById(R.id.action);

        mTypeDriverContainer.setVisibility(View.GONE);
        mPendingContainer.setVisibility(View.GONE);
        mVerifikasiPin.setVisibility(View.GONE);
        mAction.setVisibility(View.GONE);

        if (mGoogleApiClient == null) {
            mGoogleApiClient = new GoogleApiClient.Builder(getContext())
                    .addConnectionCallbacks(this)
                    .addOnConnectionFailedListener(this)
                    .addApi(LocationServices.API)
                    .build();
        }

        mVerifikasiPin.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (s.length() == 4) {
                    validatePin(s.toString());
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        ((BaseActivity) getActivity()).showLoading(true);

        new RequestLoader(this).loadRequest(DATA, new BasicNameValuePair[]{
                new BasicNameValuePair("id", mOrderList.id),
                new BasicNameValuePair("agen_id", User.agenId),
                new BasicNameValuePair("driver_id", User.id)
        }, "transaction", "get_detail", true);

        new FirebaseQueryOrder(getContext(), this).queryOrderDetail(mOrderList.customerId, mOrderList.id);

        return rootView;
    }

    @Override
    public void setFirebaseData(ArrayList<DataSnapshot> dataSnapshots) {

        Log.d(TAG, "Order:" + dataSnapshots.get(FirebaseQueryOrder.ORDER_OFFSET).toString());
        Log.d(TAG, "Customer:" + dataSnapshots.get(FirebaseQueryOrder.CUSTOMER_OFFSET).toString());
        Log.d(TAG, "Customer Address:" + dataSnapshots.get(FirebaseQueryOrder.CUSTOMER_ADDRESS_OFFSET).toString());
        Log.d(TAG, "Agent:" + dataSnapshots.get(FirebaseQueryOrder.AGENT_OFFSET).toString());
        Log.d(TAG, "Agent Address:" + dataSnapshots.get(FirebaseQueryOrder.AGENT_ADDRESS_OFFSET).toString());

        mPackage = dataSnapshots.get(FirebaseQueryOrder.ORDER_OFFSET).getValue(Package.class);
        mCustomer = dataSnapshots.get(FirebaseQueryOrder.CUSTOMER_OFFSET).getValue(Customer.class);
        mCustomerAddress = dataSnapshots.get(FirebaseQueryOrder.CUSTOMER_ADDRESS_OFFSET).getValue(Address.class);
        mAgent = dataSnapshots.get(FirebaseQueryOrder.AGENT_OFFSET).getValue(Agent.class);
        mAgentAddress = dataSnapshots.get(FirebaseQueryOrder.AGENT_ADDRESS_OFFSET).getValue(Address.class);

        if (Integer.parseInt(mPackage.statusId) == 1) {

            mInvoiceNo.setText(mPackage.invoice);
            getActivity().setTitle(mPackage.invoice);

            if (!mPackage.pendingRemarks.equals("")) {
                remarkVal = mPackage.pendingRemarks;
                activatePending();
            }

            mDistance.setText("");
            mDate.setText(mPackage.deliveryDate);
            mHeaderDate.setText(mPackage.deliveryDate);
            mHeaderTime.setText(mPackage.deliveryDate);
            mMyTime.setText(mPackage.startTime());
            mAddress.setText(mCustomerAddress.address + "\n" + mCustomerAddress.city + ", " + mCustomerAddress.province);
            mName.setText(mCustomer.name);
            mHargaTotalBarang.setText(((BaseActivity) getActivity()).getCalculatedPrice(Long.parseLong(mPackage.subTotal)));
            mOngkosKirim.setText(((BaseActivity) getActivity()).getCalculatedPrice(Long.parseLong(mPackage.totalOngkir)));
            mTotal.setText(((BaseActivity) getActivity()).getCalculatedPrice(Long.parseLong(mPackage.grandTotal)));

            for (Package.Item item : mPackage.items) {
                addOrder(new Order(
                        item.itemId,
                        item.type,
                        Long.parseLong(item.price),
                        Long.parseLong(item.quantity),
                        Long.parseLong(item.subTotal),
                        Long.parseLong(item.ongkir))
                );
            }

            try {
                //todo: this is still dummy address
                mDriverLocation = new LatLng(-6.897971, 107.611493);
                mDestinationLocation = new LatLng(-6.9285779174219755, 107.6090894266963);
                mAgenLocation = new LatLng(-6.900971, 107.597406);
                putMarker();
            } catch (Exception ex) {
                Log.d(TAG, ex.toString());
            }

            mStatus = Integer.parseInt(mPackage.statusId);

            setStatus();

        }

        ((BaseActivity) getActivity()).showLoading(false);

    }

    private void validatePin(String pinCode) {

        ((BaseActivity) getActivity()).showLoading(true, "Validate");

        new RequestLoader(this).loadRequest(VALIDATE_PIN, new BasicNameValuePair[]{
                new BasicNameValuePair("id", mOrderList.id),
                new BasicNameValuePair("pin_code", pinCode)
        }, "transaction", "validate_pin_code", true);

        new FirebasePinVerification(getContext(), this).verifyPin(mOrderList.customerId, mOrderList.id, pinCode);

    }

    @Override
    public void pinVerificationResult(int result) {

        ((BaseActivity) getActivity()).showLoading(false);
        mVerifikasiPin.setText("");

        if (result == FirebasePinVerification.PIN_VERIFICATION_SUCCESS) {
            ((BaseActivity) getActivity()).changeFragment(new FragmentSuccessDelivery(), false, true);
        } else {
            ((BaseActivity) getActivity()).showDialog("", "Pin verification failed", "", "Ok");
        }
    }

    private void initMap() {
        SupportMapFragment supportMapFragment = (SupportMapFragment) getFragmentManager().findFragmentById(R.id.map);
        supportMapFragment.getMapAsync(this);
        mGoogleApiClient.connect();
    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {

    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }

    @Override
    public void onMapLongClick(LatLng latLng) {

    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        putMarker();

    }

    private int senderMarkerIcon() {
        if (mStatus == 2) {
            return R.drawable.ic_map_pin_driver_l;
        } else {
            return R.drawable.ic_map_pin_agen_shadow;
        }
    }

    private LatLng fromLocation() {
        LatLng fromLocation;
        if (mStatus == 2) {
            fromLocation = mDriverLocation;
        } else {
            fromLocation = mAgenLocation;
        }
        return fromLocation;
    }

    private void putMarker() {
        if (mMap != null) {
            mMap.clear();
            if (!mIsPutMarker) {
                if (mDestinationLocation != null && fromLocation() != null) {

                    mIsPutMarker = true;

                    CameraPosition cameraPosition = new CameraPosition.Builder().target(fromLocation()).zoom(DEFAULT_ZOOM).build();
                    mMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
                    MarkerOptions markerOptions = new MarkerOptions();
                    markerOptions.position(fromLocation())
                            .snippet("Posisi anda")
                            .title(User.name)
                            .draggable(false)
                            .icon(BitmapDescriptorFactory.fromResource(senderMarkerIcon()));
                    mMap.addMarker(markerOptions);

                    if (ActivityCompat.checkSelfPermission(getContext(), android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                            && ActivityCompat.checkSelfPermission(getContext(), android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                        // TODO: Consider calling
                        //    ActivityCompat#requestPermissions
                        // here to request the missing permissions, and then overriding
                        //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                        //                                          int[] grantResults)
                        // to handle the case where the user grants the permission. See the documentation
                        // for ActivityCompat#requestPermissions for more details.
                        return;
                    }

                    mMap.setMyLocationEnabled(true);
                    Log.d(TAG, "map ready");

                    if (ActivityCompat.checkSelfPermission(getContext(), android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                            && ActivityCompat.checkSelfPermission(getContext(), android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                        // TODO: Consider calling
                        //    ActivityCompat#requestPermissions
                        // here to request the missing permissions, and then overriding
                        //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                        //                                          int[] grantResults)
                        // to handle the case where the user grants the permission. See the documentation
                        // for ActivityCompat#requestPermissions for more details.
                        return;
                    }

                    mMap.setMyLocationEnabled(true);
                    mMap.getUiSettings().setZoomControlsEnabled(true);
                    mMap.setOnCameraChangeListener(new GoogleMap.OnCameraChangeListener() {
                        @Override
                        public void onCameraChange(CameraPosition cameraPosition) {
                        }
                    });
                    setMyMarker();
                }
            }
        }
    }

    @Override
    public boolean onMarkerClick(Marker marker) {
        if (marker.isInfoWindowShown())
            marker.hideInfoWindow();
        else
            marker.showInfoWindow();
        return false;
    }

    @Override
    public void onMarkerDragStart(Marker marker) {

    }

    @Override
    public void onMarkerDrag(Marker marker) {

    }

    @Override
    public void onMarkerDragEnd(Marker marker) {

    }


    @Override
    public void onLocationChanged(Location location) {
        Log.d(TAG, "ON LOCATON CHANGED");
        if (mStatus == 2) {
            mDriverLocation = new LatLng(location.getLatitude(), location.getLongitude());
        }
        setMyMarker();
    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {
        Log.d(TAG, "ON STATUS CHANGED : " + provider);
    }

    @Override
    public void onProviderEnabled(String provider) {
        Log.d(TAG, "ON PROVIDER ENABLED " + provider);
    }

    @Override
    public void onProviderDisabled(String provider) {
        Log.d(TAG, "ON PROVIDER DISABLED " + provider);
    }

    private void parseValidate(String result) {
        try {
            JSONObject jsonObject = new JSONObject(result);
            if (jsonObject.getInt("status") == 1) {
                ((BaseActivity) getActivity()).changeFragment(new FragmentSuccessDelivery(), true, true);
            } else {
                ((BaseActivity) getActivity()).showDialog("", jsonObject.getString("message"), "", "Ok");
                mVerifikasiPin.setText("");
            }
        } catch (Exception ex) {
            ((BaseActivity) getActivity()).showDialog("Error", ex.toString(), "", "Ok");
            mVerifikasiPin.setText("");
        }
    }

    private void activatePending() {
        mPendingContainer.setVisibility(View.VISIBLE);
        mRemark.setText(remarkVal);
    }

    private void parseData(String result) {
        try {
            JSONObject jsonObject = new JSONObject(result);
            if (jsonObject.getInt("status") == 1) {
                jsonObject = jsonObject.getJSONObject("data");
                JSONObject secondJSONObject = jsonObject.getJSONArray("header").getJSONObject(0);
                String invoiceValue = secondJSONObject.getString("tran_no_invoice");
                mInvoiceNo.setText(invoiceValue);
                getActivity().setTitle(invoiceValue);

                if (secondJSONObject.getInt("tran_pending_flag") == 1) {
                    remarkVal = secondJSONObject.getString("tran_pending_remark");
                    activatePending();
                }

                mDistance.setText(secondJSONObject.getString("distance"));
                mDate.setText(secondJSONObject.getString("tran_date"));
                mHeaderDate.setText(secondJSONObject.getString("tran_date"));
                mHeaderTime.setText(secondJSONObject.getString("tran_time_start"));
                mMyTime.setText(secondJSONObject.getString("tran_time_start"));
                mAddress.setText(secondJSONObject.getString("cua_address") + "\n" + secondJSONObject.getString("cua_city_name") + ", " + secondJSONObject.getString("cua_province_name"));
                mName.setText(secondJSONObject.getString("cus_name"));
                mHargaTotalBarang.setText(((BaseActivity) getActivity()).getCalculatedPrice(secondJSONObject.getLong("tran_sub_total")));
                mOngkosKirim.setText(((BaseActivity) getActivity()).getCalculatedPrice(secondJSONObject.getLong("tran_ongkir")));
                mTotal.setText(((BaseActivity) getActivity()).getCalculatedPrice(secondJSONObject.getLong("tran_grand_total")));

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
                    mAgenLocation = new LatLng(secondJSONObject.getDouble("age_lat"), secondJSONObject.getDouble("age_long"));
                    putMarker();
                } catch (Exception ex) {
                    Log.d(TAG, ex.toString());
                }

                mStatus = secondJSONObject.getInt("tran_status_id");

                setStatus();

            }
        } catch (Exception ex) {
            Log.d(TAG, ex.toString());
        }

        ((BaseActivity) getActivity()).showLoading(false);
    }

    private void setStatus() {
        switch (mStatus) {
            case OrderList.TYPE_CHOOSE_DRIVER:
                mVerifikasiPin.setVisibility(View.GONE);
                mTypeDriverContainer.setVisibility(View.GONE);
                mAction.setVisibility(View.VISIBLE);
                mAction.setText("KIRIM SEKARANG");
                mAction.setBackgroundResource(R.drawable.ic_rectangle_orderlistactionorange_solid);
                mAction.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        takeOrder();
                    }
                });
                mIsPutMarker = false;
                putMarker();
                break;

            case OrderList.TYPE_DRIVER:
                mVerifikasiPin.setVisibility(View.VISIBLE);
                mTypeDriverContainer.setVisibility(View.VISIBLE);
                View diLokasi = mTypeDriverContainer.findViewById(R.id.dilokasi);
                diLokasi.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        doSetLocation();
                    }
                });
                View pending = mTypeDriverContainer.findViewById(R.id.pending);
                pending.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        showPending();
                    }
                });
                mAction.setVisibility(View.GONE);
                mIsPutMarker = false;
                putMarker();
                break;

            case OrderList.TYPE_ON_DELIVERY:
                mVerifikasiPin.setVisibility(View.GONE);
                mTypeDriverContainer.setVisibility(View.GONE);
                mAction.setVisibility(View.GONE);
                break;
        }
    }

    private void doSetLocation() {
        ((BaseActivity) getActivity()).showLoading(true, "");
        new RequestLoader(this).loadRequest(SET_LOKASI, new BasicNameValuePair[]{
                new BasicNameValuePair("id", mOrderList.id)
        }, "transaction", "set_onlocation", true);
    }

    private void showPending() {
        View customView = LayoutInflater.from(getContext()).inflate(R.layout.fragment_pending, null, false);
        final int redColor = ContextCompat.getColor(getContext(), R.color.closered);
        final TextView textView = (TextView) customView.findViewById(R.id.textview);
        final EditText alasan = (EditText) customView.findViewById(R.id.alasan);
        final AlertDialog dialog = ((BaseActivity) getActivity()).showDialog("", customView, "Ok", "Batal", null, null);
        dialog.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (((BaseActivity) getActivity()).isValidEditText(alasan)) {
                    remarkVal = ((BaseActivity) getActivity()).getTextFromEditText(alasan);
                    doPending();
                    dialog.dismiss();
                } else {
                    textView.setText("Alasan harus diisi : ");
                    textView.setTextColor(redColor);
                }
            }
        });
        dialog.getButton(AlertDialog.BUTTON_NEGATIVE).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
    }

    private void doPending() {

        ((BaseActivity) getActivity()).showLoading(true);

        new RequestLoader(this).loadRequest(SET_PENDING, new BasicNameValuePair[]{
                new BasicNameValuePair("id", mOrderList.id),
                new BasicNameValuePair("remark", remarkVal)
        }, "transaction", "set_pending", true);

        new FirebasePendingOrder(getContext(), this)
                .pendingOrder(mOrderList.customerId, mOrderList.id, remarkVal);

    }

    @Override
    public void pendingOrderReady(int result) {
        ((BaseActivity) getActivity()).showLoading(false);
        mStatus = 1;
        setStatus();
        activatePending();
    }

    private void takeOrder() {
        ((BaseActivity) getActivity()).showLoading(true, "Kirim sekarang");

        new RequestLoader(this).loadRequest(TAKE_ORDER, new BasicNameValuePair[]{
                new BasicNameValuePair("id", mOrderList.id)
        }, "transaction", "delivery", true);

        new FirebaseTakeOrder(getContext(), this).takeOrder(mOrderList.customerId, mOrderList.id);
    }

    @Override
    public void takeOrderReady(int result) {

        ((BaseActivity) getActivity()).showLoading(false);

        if (result == FirebaseTakeOrder.TAKE_ORDER_SUCCESS) {
            mStatus = 2;
            setStatus();
        } else {
            ((BaseActivity) getActivity()).showDialog("", "Take order failed", "", "Ok");
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

    private void parseTakeOrder(String result) {
        try {
            JSONObject jsonObject = new JSONObject(result);
            if (jsonObject.getInt("status") == 1) {
                mStatus = 2;
                setStatus();
            } else {
                ((BaseActivity) getActivity()).showDialog("", jsonObject.getString("message"), "", "Ok");
            }
        } catch (Exception ex) {
            ((BaseActivity) getActivity()).showDialog("Eror", ex.toString(), "", "Ok");
        }
    }

    private void parseSetPending(String result) {
        try {
            JSONObject jsonObject = new JSONObject(result);
            if (jsonObject.getInt("status") == 1) {
                mStatus = 1;
                setStatus();
                activatePending();
            } else {
                ((BaseActivity) getActivity()).showDialog("", jsonObject.getString("message"), "", "Ok");
            }
        } catch (Exception ex) {
            ((BaseActivity) getActivity()).showDialog("Eror", ex.toString(), "", "Ok");
        }
    }

    private void parseSetLokasi(String result) {
        try {
            JSONObject jsonObject = new JSONObject(result);
            ((BaseActivity) getActivity()).showDialog("", jsonObject.getString("message"), "", "Ok");
        } catch (Exception ex) {
            ((BaseActivity) getActivity()).showDialog("Eror", ex.toString(), "", "Ok");
        }
    }

    @Override
    public void setData(int index, String result, View[] impactedViews) {
        ((BaseActivity) getActivity()).showLoading(false);
        switch (index) {
            case VALIDATE_PIN:
                parseValidate(result);
                break;
            case DATA:
                parseData(result);
                break;
            case TAKE_ORDER:
                parseTakeOrder(result);
                break;
            case SET_PENDING:
                parseSetPending(result);
                break;
            case SET_LOKASI:
                parseSetLokasi(result);
                break;
        }
    }

    private class MapTask extends AsyncTask<View, Void, String> {

        @Override
        protected String doInBackground(View... params) {
            getFragmentManager().beginTransaction()
                    .setCustomAnimations(R.anim.slide_in_left, R.anim.slide_out_right)
                    .replace(R.id.map, new SupportMapFragment())
                    .commit();
            return null;
        }

        @Override
        protected void onPostExecute(String val) {
            initMap();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        Log.d(TAG, "ON REQUEST PERMISSION RESULT : " + requestCode);
        Log.d(TAG, "GRANT RESULT GRANTED : " + PackageManager.PERMISSION_GRANTED);
        Log.d(TAG, "GRANT RESULT DENIED : " + PackageManager.PERMISSION_DENIED);
        if (grantResults.length > 0) {
            for (int i = 0; i < grantResults.length; i++) {
                Log.d(TAG, "GRANT RESULT " + i + " = " + grantResults[i]);
            }
        }
        if (grantResults.length > 0
                && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            Log.d(TAG, "PERMISSION GRANTED");
            if (requestCode >= 1000) {

            } else {
                switch (requestCode) {
                    case PERMISSION_ACCESS_FINE_LOCATION:
                        getLastLocation();
                        break;
                    case PERMISSION_WRITE_GSERVICE:
                        getLastLocation();
                        break;
                }
            }
        } else {
            Log.d(TAG, "PERMISSION NOT GRANTED");
        }
    }

    private void getLastLocation() {
        Log.d(TAG, "GET LAST LOCATION");
        if (ActivityCompat.checkSelfPermission(getActivity(), android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                || ActivityCompat.checkSelfPermission(((BaseActivity) getActivity()), android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(getActivity(),
                    new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION, android.Manifest.permission.ACCESS_COARSE_LOCATION}, PERMISSION_ACCESS_FINE_LOCATION);
            return;
        }

        Location mLastLocation = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);
        if (mLastLocation == null) {
            Log.d(TAG, "LAST FROM FUSED LOCAITON API LOCATION NULL");
            mLastLocation = getLocation();
        } else {
            Log.d(TAG, "LAST FROM FUSED LOCAITON API LOCATION NOT NULL");
        }
        if (mStatus == 2) {
            mDriverLocation = new LatLng(mLastLocation.getLatitude(), mLastLocation.getLongitude());
        }
        setMyMarker();
    }

    private void setMyMarker() {
        if (mDestinationLocation != null && fromLocation() != null) {

            Log.d(TAG, "start: " + fromLocation());
            Log.d(TAG, "end: " + mDestinationLocation);

            Routing routing = new Routing.Builder()
                    .travelMode(Routing.TravelMode.DRIVING)
                    .withListener(this)
                    .waypoints(fromLocation(), mDestinationLocation)
                    .build();
            routing.execute();

        }
    }

    public Location getLocation() {
        Location result = null;
        long MIN_TIME_BW_UPDATES = 1000 * 1; // 1 second
        float MIN_DISTANCE_CHANGE_FOR_UPDATES = 1; // 0 meters
        try {
            LocationManager locationManager = (LocationManager) getContext()
                    .getSystemService(Service.LOCATION_SERVICE);

            // getting GPS status
            boolean isGPSEnabled = locationManager
                    .isProviderEnabled(LocationManager.GPS_PROVIDER);

            // getting network status
            boolean isNetworkEnabled = locationManager
                    .isProviderEnabled(LocationManager.NETWORK_PROVIDER);

            if (!isGPSEnabled && !isNetworkEnabled) {
                // no network provider is enabled
                Log.d(TAG, "NO NETWORK PROVIDER IS ENABLED");
            } else {
                if (isNetworkEnabled) {
                    if (ActivityCompat.checkSelfPermission(getContext(), android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(getContext(), android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                        // TODO: Consider calling
                        //    ActivityCompat#requestPermissions
                        // here to request the missing permissions, and then overriding
                        //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                        //                                          int[] grantResults)
                        // to handle the case where the user grants the permission. See the documentation
                        // for ActivityCompat#requestPermissions for more details.
                        return null;
                    }
                    locationManager.requestLocationUpdates(
                            LocationManager.NETWORK_PROVIDER,
                            MIN_TIME_BW_UPDATES,
                            MIN_DISTANCE_CHANGE_FOR_UPDATES, this);
                    Log.d(TAG, "Network Enabled");
                    if (locationManager != null) {
                        result = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
                    }
                }

                // if GPS Enabled get lat/long using GPS Services
                if (isGPSEnabled) {
                    locationManager.requestLocationUpdates(
                            LocationManager.GPS_PROVIDER,
                            MIN_TIME_BW_UPDATES,
                            MIN_DISTANCE_CHANGE_FOR_UPDATES, this);
                    Log.d(TAG, "GPS Enabled");
                    if (locationManager != null) {
                        if (locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER) != null) {
                            result = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
                        }
                    }
                }

            }

        } catch (Exception e) {
            e.printStackTrace();
            Log.d(TAG, "GET LOCATION ERROR : " + e.toString());
        }

        return result;
    }

    @Override
    public void onRoutingFailure(RouteException e) {

    }

    @Override
    public void onRoutingStart() {

    }

    private void clearPolylines() {
        if (mPolylines.size() > 0) {
            for (Polyline poly : mPolylines) {
                poly.remove();
            }
        }
    }

    @Override
    public void onRoutingSuccess(ArrayList<Route> route, int shortestRouteIndex) {

        CameraPosition cameraPosition = new CameraPosition.Builder().target(fromLocation()).zoom(DEFAULT_ZOOM).build();
        mMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));

        clearPolylines();

        String distance = "";
        String duration = "";

        for (int iterator = 0; iterator < route.size(); iterator++) {

            //In case of more than 5 alternative routes
            int colorIndex = iterator % COLORS.length;

            PolylineOptions polyOptions = new PolylineOptions();
            polyOptions.color(getResources().getColor(COLORS[colorIndex]));
            polyOptions.width(10);
            polyOptions.addAll(route.get(iterator).getPoints());
            Polyline polyline = mMap.addPolyline(polyOptions);
            mPolylines.add(polyline);

            Log.d(TAG, "Route " + (iterator + 1)
                    + ": distance = " + route.get(iterator).getDistanceText()
                    + ": duration = " + route.get(iterator).getDurationText());

            /*
            Todo: handle generic cases
             */
            distance = route.get(iterator).getDistanceText();
            duration = route.get(iterator).getDurationText();

        }

        mDistance.setText(distance);

        if (myMarker != null) {
            myMarker.remove();
            myMarker = null;
        }
        MarkerOptions markerOptions = new MarkerOptions();
        markerOptions.position(mDestinationLocation)
                .snippet("jarak " + distance + ", waktu " + duration)
                .title("Lokasi pelanggan")
                .draggable(false)
                .icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_map_pin_cust_shadow));
        myMarker = mMap.addMarker(markerOptions);
    }

    @Override
    public void onRoutingCancelled() {

    }

}
