package com.pertamina.brightgas;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.ResultReceiver;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.firebase.database.DataSnapshot;
import com.pertamina.brightgas.firebase.FirebaseLoadable;
import com.pertamina.brightgas.firebase.FirebaseStoreAddress;
import com.pertamina.brightgas.retrofit.addaddress.AddAddressClient;
import com.pertamina.brightgas.retrofit.addaddress.AddAddressInterface;
import com.pertamina.brightgas.retrofit.addaddress.AddAddressRequest;

import org.apache.http.message.BasicNameValuePair;
import org.json.JSONObject;

import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

public class FragmentAddAddress extends Fragment implements RequestLoaderInterface,
        OnMapReadyCallback, FirebaseLoadable, AddAddressInterface {

    public static final String TAG = "frag_add_address";

    public static final int DEFAULT_ZOOM = 15;

    private GoogleApiClient mGoogleApiClient;

    private Location mLastLocation;

    private Marker mMyMarker;

    private EditText mAlamatLengkap;

    private GoogleMap mGoogleMap;

    private AddressResultReceiver mResultReceiver;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.fragment_tambah_alamat, container, false);

        mResultReceiver = new AddressResultReceiver(new Handler());

        if (mGoogleApiClient == null) {
            mGoogleApiClient = new GoogleApiClient.Builder(getActivity())
                    .addApi(LocationServices.API)
                    .build();
        }

        mAlamatLengkap = (EditText) rootView.findViewById(R.id.alamat_lengkap);

        View addAddressButton = rootView.findViewById(R.id.tambah_alamat);
        addAddressButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (((BaseActivity) getActivity()).isValidEditText(mAlamatLengkap)) {
                    if (mMyMarker != null) {
                        addCustomerAddress();
                    } else {
                        ((BaseActivity) getActivity()).showDialog("", "Silahkan pilih lokasi di peta terlebih dahulu", "", "Ok");
                    }
                } else {
                    ((BaseActivity) getActivity()).showDialog("", "Silahkan isi alamat lengkap terlebih dahulu", "", "Ok");
                }
            }
        });

        return rootView;
    }

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

    @Override
    public void onResume() {
        super.onResume();
        getActivity().setTitle("Tambah Alamat");
    }

    private void addCustomerAddress() {
        ((BaseActivity) getActivity()).showLoading(true, "Menyimpan alamat");

        if (GlobalActivity.getBackEndServiceProvider() == GlobalActivity.LEGACY) {
            new RequestLoader(this).loadRequest(0, new BasicNameValuePair[]{
                    new BasicNameValuePair("customer_id", User.id),
                    new BasicNameValuePair("address", ((BaseActivity) getActivity()).getTextFromEditText(mAlamatLengkap)),
                    new BasicNameValuePair("province_name", ""),
                    new BasicNameValuePair("city_name", ""),
                    new BasicNameValuePair("province_id", ""),
                    new BasicNameValuePair("city_id", ""),
                    new BasicNameValuePair("lat", mMyMarker.getPosition().latitude + ""),
                    new BasicNameValuePair("long", mMyMarker.getPosition().longitude + "")
            }, "customer", "set_address", true);
        }

        if (GlobalActivity.getBackEndServiceProvider() == GlobalActivity.FIREBASE) {
            new FirebaseStoreAddress(getActivity(), this).storeAddress(new BasicNameValuePair[]{
                    new BasicNameValuePair("customer_id", User.id),
                    new BasicNameValuePair("address", ((BaseActivity) getActivity()).getTextFromEditText(mAlamatLengkap)),
                    new BasicNameValuePair("lat", mMyMarker.getPosition().latitude + ""),
                    new BasicNameValuePair("long", mMyMarker.getPosition().longitude + "")
            });
        }

        if (GlobalActivity.getBackEndServiceProvider() == GlobalActivity.SWAGGER) {
            new AddAddressClient(getContext(), this).addAddress(new AddAddressRequest(
                    ((BaseActivity) getActivity()).getTextFromEditText(mAlamatLengkap),
                    mMyMarker.getPosition().latitude + "",
                    mMyMarker.getPosition().longitude + "",
                    "",
                    "",
                    "",
                    "",
                    ""
            ));
        }
    }

    private void connectMap() {
        SupportMapFragment supportMapFragment = (SupportMapFragment) getFragmentManager().findFragmentById(R.id.map);
        supportMapFragment.getMapAsync(this);
        mGoogleApiClient.connect();
    }

    @Override
    public void setData(int index, String result, View[] impactedViews) {
        ((BaseActivity) getActivity()).showLoading(false);
        try {
            JSONObject jsonObject = new JSONObject(result);
            if (jsonObject.getInt("status") == 1) {
                getActivity().onBackPressed();
            } else {
                ((BaseActivity) getActivity()).showDialog("", jsonObject.getString("message"), "", "Ok");
            }
        } catch (Exception ex) {
            ((BaseActivity) getActivity()).showDialog("Error", ex.toString(), "", "Ok");
        }
    }

    @Override
    public void setFirebaseData(DataSnapshot dataSnapshot) {
        ((BaseActivity) getActivity()).showLoading(false);
        getActivity().onBackPressed();
    }

    @Override
    public void retrofitAddAddress(Boolean result) {
        ((BaseActivity) getActivity()).showLoading(false);
        getActivity().onBackPressed();
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {

        Log.d(TAG, "ON MAP READY");

        mGoogleMap = googleMap;

        if (ActivityCompat.checkSelfPermission(getActivity(), android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                || ActivityCompat.checkSelfPermission(getActivity(), android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

            requestPermissions(new String[]{
                    android.Manifest.permission.ACCESS_FINE_LOCATION,
                    android.Manifest.permission.ACCESS_COARSE_LOCATION
            }, 0);

            return;
        }
        configureMap();
        moveCameraToMyLocation();
        startAddressProviderService();
    }

    private void configureMap() {
        mGoogleMap.setMyLocationEnabled(true);
        mGoogleMap.getUiSettings().setZoomControlsEnabled(true);
        mGoogleMap.getUiSettings().setMyLocationButtonEnabled(true);
        mGoogleMap.getUiSettings().setMapToolbarEnabled(false);
        mGoogleMap.setOnMapClickListener(new GoogleMap.OnMapClickListener() {
            @Override
            public void onMapClick(LatLng latLng) {
                if (mLastLocation != null) {
                    addMarker(latLng);
                    mLastLocation.setLatitude(latLng.latitude);
                    mLastLocation.setLongitude(latLng.longitude);
                    debugLastLocation(mLastLocation);
                    AppLocal.storeLocation(getContext(), mLastLocation.getLatitude(), mLastLocation.getLongitude());
                    startAddressProviderService();
                }
            }
        });
        mGoogleMap.setOnCameraIdleListener(new GoogleMap.OnCameraIdleListener() {
            @Override
            public void onCameraIdle() {
                Log.d(TAG, "onCameraIdle()");
                if (mLastLocation != null) {
                    addMarker(new LatLng(mLastLocation.getLatitude(), mLastLocation.getLongitude()));
                    AppLocal.storeLocation(getContext(), mLastLocation.getLatitude(), mLastLocation.getLongitude());
                }
            }
        });
        mGoogleMap.setOnMyLocationButtonClickListener(new GoogleMap.OnMyLocationButtonClickListener() {
            @Override
            public boolean onMyLocationButtonClick() {
                moveCameraToMyLocation();
                startAddressProviderService();
                return false;
            }
        });
    }

    private void moveCameraToMyLocation() {

        mLastLocation = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);
        if (mLastLocation == null) {
            mLastLocation = AppLocal.getLocation(getContext());
            debugLastLocation(mLastLocation);
        }

        try {
            debugLastLocation(mLastLocation);
            CameraPosition cameraPosition = new CameraPosition.Builder()
                    .target(new LatLng(mLastLocation.getLatitude(), mLastLocation.getLongitude())).zoom(DEFAULT_ZOOM).build();
            mGoogleMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
        } catch (Exception error) {
            Log.d(TAG, error.getStackTrace().toString());
        }
    }

    private void debugLastLocation(Location lastLocation) {
        Log.d(TAG, "Lat: " + lastLocation.getLatitude());
        Log.d(TAG, "Long: " + lastLocation.getLongitude());
    }

    private void startAddressProviderService() {
        if (mLastLocation != null) {
            Log.d(TAG, "start address provider service");
            Intent intent = new Intent(getActivity(), AddressService.class);
            intent.putExtra(AddressService.Constants.RECEIVER, mResultReceiver);
            intent.putExtra(AddressService.Constants.LOCATION_DATA_EXTRA, mLastLocation);
            getActivity().startService(intent);
        }
    }

    private void addMarker(LatLng data) {
        mGoogleMap.clear();
        MarkerOptions markerOptions = new MarkerOptions();
        markerOptions.position(data)
                .title("Lokasi alamat kirim")
                .draggable(false);
        mMyMarker = mGoogleMap.addMarker(markerOptions);
        mMyMarker.showInfoWindow();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        configureMap();
        moveCameraToMyLocation();
        startAddressProviderService();
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
            connectMap();
        }
    }

    private class AddressResultReceiver extends ResultReceiver {

        AddressResultReceiver(Handler handler) {
            super(handler);
        }

        @Override
        protected void onReceiveResult(int resultCode, Bundle resultData) {
            if (resultCode == AddressService.Constants.SUCCESS_RESULT) {
                String address = resultData.getString(AddressService.Constants.RESULT_DATA_KEY);
                Log.d(TAG, "Address = " + address);
                mAlamatLengkap.setText(address.replace("\n", ", "));
            }
        }
    }
}
