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
import android.widget.ImageButton;
import android.widget.TextView;

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

import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

public class FragmentMapPicker extends Fragment implements OnMapReadyCallback {

    private static final String TAG = "frag_map_picker";

    private static final int DEFAULT_ZOOM = 15;

    private GoogleApiClient mGoogleApiClient;

    private Location mLastLocation;

    private Marker myMarker;

    private TextView mFinishButton;

    private EditText mAddressLocation;

    private InterfaceMapPicker mInterfaceMapPicker;

    private GoogleMap mGoogleMap;

    private AddressResultReceiver mResultReceiver;

    public void setInterfaceMapPicker(InterfaceMapPicker interfaceMapPicker) {
        this.mInterfaceMapPicker = interfaceMapPicker;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.fragment_map_picker, container, false);

        mResultReceiver = new AddressResultReceiver(new Handler());

        if (mGoogleApiClient == null) {
            mGoogleApiClient = new GoogleApiClient.Builder(getContext())
                    .addApi(LocationServices.API)
                    .build();
        }

        mAddressLocation = (EditText) rootView.findViewById(R.id.user_location_view);

        mFinishButton = (TextView) rootView.findViewById(R.id.finish_button);
        mFinishButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (myMarker != null && mInterfaceMapPicker != null) {
                    mInterfaceMapPicker.setLatLng(myMarker.getPosition());
                    mInterfaceMapPicker.setAddress(mAddressLocation.getText().toString());
                }
                getActivity().onBackPressed();
            }
        });

        ImageButton myLocationButton = (ImageButton) rootView.findViewById(R.id.my_location_button);
        myLocationButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                moveCameraToMyLocation();
                startAddressProviderService();
            }
        });

        return rootView;
    }

    @Override
    public void onStart() {
        super.onStart();
        new MapTask().executeOnExecutor(new ThreadPoolExecutor(
                5, 10, 5000, TimeUnit.MILLISECONDS, new LinkedBlockingQueue<Runnable>()), null);
    }

    @Override
    public void onStop() {
        super.onStop();
        if (mGoogleApiClient != null) {
            mGoogleApiClient.disconnect();
        }
    }

    private void connectMap() {
        SupportMapFragment supportMapFragment = (SupportMapFragment) getFragmentManager().findFragmentById(R.id.map);
        supportMapFragment.getMapAsync(this);
        mGoogleApiClient.connect();
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
        mGoogleMap.getUiSettings().setMyLocationButtonEnabled(false);
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

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        configureMap();
        moveCameraToMyLocation();
        startAddressProviderService();
    }

    private void addMarker(LatLng data) {
        mGoogleMap.clear();
        MarkerOptions markerOptions = new MarkerOptions();
        markerOptions.position(data)
                .title("Lokasi alamat kirim")
                .draggable(false);
        myMarker = mGoogleMap.addMarker(markerOptions);
        myMarker.showInfoWindow();
        mFinishButton.setText("Pilih Alamat");
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
                mAddressLocation.setText(address.replace("\n", ", "));
            }
        }
    }
}