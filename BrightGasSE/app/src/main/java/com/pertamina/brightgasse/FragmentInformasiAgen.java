package com.pertamina.brightgasse;

import android.app.Service;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RatingBar;
import android.widget.TextView;

import com.google.android.gms.common.ConnectionResult;
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
import com.pertamina.brightgasse.firebase.FirebaseLoadable;
import com.pertamina.brightgasse.firebase.FirebaseQueryAddress;
import com.pertamina.brightgasse.firebase.models.Address;
import com.pertamina.brightgasse.model.Agen;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;

public class FragmentInformasiAgen extends Fragment
        implements OnMapReadyCallback, GoogleMap.OnMarkerDragListener, GoogleMap.OnMapLongClickListener,
        GoogleMap.OnMarkerClickListener, RequestLoaderInterface, GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener, InterfaceOnRequestPermissionsResult, LocationListener, FirebaseLoadable {

    private static final String TAG = "frag_informasi_agen";

    private static final int PERMISSION_ACCESS_FINE_LOCATION = 0;
    private static final int PERMISSION_WRITE_GSERVICE = 2;

    private static final int DEFAULT_ZOOM = 18;

    private Agen mAgen;

    private String mDummyPictureUrl = "http://cdn.jitunews.com/dynamic/thumb/2016/06/35c2a4f393b6427ff7a44e8c2dcb7152_630x420_thumb.jpg?w=630";

    private LatLng mAgentCoordinate;

    private GoogleMap mMap;

    private GoogleApiClient mGoogleApiClient;

    private Location mLastLocation;

    private boolean mIsLocation = false;

    private LatLng mCurrentLatLng;

    private boolean mCanGetLocation;

    private Marker mMyMarker;

    private TextView mAddress;

    @Override
    public void onStop() {
        super.onStop();
        if (mGoogleApiClient != null)
            mGoogleApiClient.disconnect();
    }

    public void setData(Agen data) {
        this.mAgen = data;
    }

    @Override
    public void onResume() {
        super.onResume();
        getActivity().setTitle("Informasi Agen");
        ((BaseActivity) getActivity()).setInterfaceOnRequestPermissionsResult(this);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.fragment_informasi_agen, container, false);

        CircleImageView profilePicture = (CircleImageView) rootView.findViewById(R.id.profil_picture);
        Picasso.with(getContext()).load(mDummyPictureUrl).centerCrop().fit().placeholder(R.drawable.ic_brightgas_logo).into(profilePicture);

        TextView name = (TextView) rootView.findViewById(R.id.name);
        name.setText(mAgen.getName());

        RatingBar ratingBar = (RatingBar) rootView.findViewById(R.id.rating_bar);
        ratingBar.setRating(mAgen.getRating());

        TextView phoneNumber = (TextView) rootView.findViewById(R.id.phone_number);
        phoneNumber.setText("Telp. " + mAgen.getTelp());

        mAddress = (TextView) rootView.findViewById(R.id.address);

        getFragmentManager().beginTransaction()
                .setCustomAnimations(R.anim.slide_in_left, R.anim.slide_out_right)
                .replace(R.id.map, new SupportMapFragment())
                .commit();

        Log.d(TAG, "uid:" + mAgen.getId());
        new FirebaseQueryAddress(getContext(), this).queryAddress(mAgen.id);

        return rootView;
    }

    @Override
    public void setFirebaseData(ArrayList<DataSnapshot> dataSnapshots) {

        Log.d(TAG, "setFirebaseData:" + dataSnapshots.get(0).toString());

        if (mGoogleApiClient == null) {
            mGoogleApiClient = new GoogleApiClient.Builder(getContext())
                    .addConnectionCallbacks(this)
                    .addOnConnectionFailedListener(this)
                    .addApi(LocationServices.API)
                    .build();

            mGoogleApiClient.connect();

            SupportMapFragment supportMapFragment = (SupportMapFragment) getFragmentManager().findFragmentById(R.id.map);
            supportMapFragment.getMapAsync(FragmentInformasiAgen.this);
        }

        Address address = dataSnapshots.get(0).getValue(Address.class);
        Log.d(TAG, "longitude:" + address.longitude);
        Log.d(TAG, "latitude:" + address.latitude);

        mAddress.setText(address.getAddress());
        mAgentCoordinate = new LatLng(Double.parseDouble(address.latitude), Double.parseDouble(address.longitude));
    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {
        getLastLocation();
    }

    @Override
    public void onConnectionSuspended(int i) {
        Log.i(TAG, "connection suspended");
        mGoogleApiClient.connect();
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

        if (!mIsLocation) {

            mIsLocation = true;

            Log.d(TAG, "check latitude : " + mAgentCoordinate.latitude);
            Log.d(TAG, "check longitude : " + mAgentCoordinate.longitude);

            CameraPosition cameraPosition = new CameraPosition.Builder().target(mAgentCoordinate).zoom(DEFAULT_ZOOM).build();
            mMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
            MarkerOptions markerOptions = new MarkerOptions();
            markerOptions.position(mAgentCoordinate)
                    .snippet("Pengantar bright gas")
                    .title("Asep Suherman")
                    .draggable(false);
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

        }

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

        googleMap.setMyLocationEnabled(true);
        googleMap.getUiSettings().setZoomControlsEnabled(true);
        googleMap.setOnCameraChangeListener(new GoogleMap.OnCameraChangeListener() {
            @Override
            public void onCameraChange(CameraPosition cameraPosition) {
                mCurrentLatLng = new LatLng(cameraPosition.target.latitude, cameraPosition.target.longitude);
            }
        });
    }

    @Override
    public boolean onMarkerClick(Marker marker) {
        if (marker.isInfoWindowShown()) {
            marker.hideInfoWindow();
        } else {
            marker.showInfoWindow();
        }
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
        mLastLocation = location;
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

    @Override
    public void setData(int index, String result, View[] impactedViews) {
        ((BaseActivity) getActivity()).showLoading(false);
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
                || ActivityCompat.checkSelfPermission(getActivity(), android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(getActivity(),
                    new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION, android.Manifest.permission.ACCESS_COARSE_LOCATION}, PERMISSION_ACCESS_FINE_LOCATION);
            return;
        }

        mLastLocation = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);
        if (mLastLocation == null) {
            Log.d(TAG, "LAST FROM FUSED LOCAITON API LOCATION NULL");
            mLastLocation = getLocation();
        } else {
            Log.d(TAG, "LAST FROM FUSED LOCAITON API LOCATION NOT NULL");
        }

        setMyMarker();
    }

    private void setMyMarker() {

        if (mLastLocation != null) {

            if (mMyMarker != null) {
                mMyMarker.remove();
                mMyMarker = null;
            }

            MarkerOptions markerOptions = new MarkerOptions();
            markerOptions.position(new LatLng(mLastLocation.getLatitude(), mLastLocation.getLongitude()))
                    .snippet("This is your location")
                    .title("Your location")
                    .draggable(false);
            mMyMarker = mMap.addMarker(markerOptions);

        } else {
            Log.d(TAG, "FINAL LOCATION NULL");
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
                mCanGetLocation = true;

                if (isNetworkEnabled) {
                    if (ActivityCompat.checkSelfPermission(getContext(), android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                            && ActivityCompat.checkSelfPermission(getContext(), android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
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
}
