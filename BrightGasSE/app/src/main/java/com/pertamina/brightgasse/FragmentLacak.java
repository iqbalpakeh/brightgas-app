package com.pertamina.brightgasse;

import android.app.Service;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

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
import com.google.android.gms.maps.model.PolylineOptions;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

public class FragmentLacak extends Fragment implements OnMapReadyCallback, GoogleMap.OnMarkerDragListener, GoogleMap.OnMapLongClickListener, GoogleMap.OnMarkerClickListener, RequestLoaderInterface, GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener, InterfaceOnRequestPermissionsResult, LocationListener {
    public static final int PERMISSION_ACCESS_FINE_LOCATION = 0;
    public static final int PERMISSION_WRITE_GSERVICE = 2;
    public static final int DEFAULT_ZOOM = 13;
    public static final String TAG = "gugum";
    public static final int TAKE = 1;
    public static final int SERVICE = 2;


    LatLng pengantarLatLng = new LatLng(-6.943730, 107.659057);
    private GoogleMap mMap;
    GoogleApiClient mGoogleApiClient;
    Location mLastLocation;
    boolean isLocation = false;
    SupportMapFragment supportMapFragment;
    LatLng currentLatLng;
    boolean canGetLocation;
    Marker myMarker;

    public FragmentLacak() {

    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
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
        ((BaseActivity) getActivity()).setTitle("Lacak");
        ((BaseActivity) getActivity()).setInterfaceOnRequestPermissionsResult(this);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_lacak, container, false);
        if (mGoogleApiClient == null) {
            mGoogleApiClient = new GoogleApiClient.Builder(getContext())
                    .addConnectionCallbacks(this)
                    .addOnConnectionFailedListener(this)
                    .addApi(LocationServices.API)
                    .build();
        }
        View back = rootView.findViewById(R.id.back);
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ((BaseActivity) getActivity()).onBackPressed();
            }
        });
        return rootView;
    }

    private void initMap() {
        supportMapFragment = (SupportMapFragment) getFragmentManager().findFragmentById(R.id.map);
        supportMapFragment.getMapAsync(this);
        mGoogleApiClient.connect();
    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {
        getLastLocation();
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
        if (!isLocation) {
            isLocation = true;
            CameraPosition cameraPosition = new CameraPosition.Builder().target(pengantarLatLng).zoom(DEFAULT_ZOOM).build();
            mMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
            MarkerOptions markerOptions = new MarkerOptions();
            markerOptions.position(pengantarLatLng)
                    .snippet("Pengantar bright gas")
                    .title("Asep Suherman")
                    .draggable(false);
            mMap.addMarker(markerOptions);
            if (ActivityCompat.checkSelfPermission(getContext(), android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(getContext(), android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
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
        Log.d("gugum", "map ready");
        if (ActivityCompat.checkSelfPermission(getContext(), android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(getContext(), android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
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
                currentLatLng = new LatLng(cameraPosition.target.latitude, cameraPosition.target.longitude);
            }
        });

        Log.d("gugum drivers", "init drivers count");
//        googleMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
//            @Override
//            public boolean onMarkerClick(Marker marker) {
//                return true;
////                return false;
//            }
//        });
    }

    @Override
    public boolean onMarkerClick(Marker marker) {
        if(marker.isInfoWindowShown())
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
        Log.d("gugum","ON LOCATON CHANGED");
        mLastLocation = location;
        setMyMarker();
    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {
        Log.d("gugum","ON STATUS CHANGED : "+provider);
    }

    @Override
    public void onProviderEnabled(String provider) {
        Log.d("gugum","ON PROVIDER ENABLED "+provider);
    }

    @Override
    public void onProviderDisabled(String provider) {
        Log.d("gugum","ON PROVIDER DISABLED "+provider);
    }

    @Override
    public void setData(int index, String result, View[] impactedViews) {

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
        Log.d("gugum", "ON REQUEST PERMISSION RESULT : " + requestCode);
        Log.d("gugum", "GRANT RESULT GRANTED : " + PackageManager.PERMISSION_GRANTED);
        Log.d("gugum", "GRANT RESULT DENIED : " + PackageManager.PERMISSION_DENIED);
        if (grantResults.length > 0) {
            for (int i = 0; i < grantResults.length; i++) {
                Log.d("gugum", "GRANT RESULT " + i + " = " + grantResults[i]);
            }
        }
        if (grantResults.length > 0
                && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            Log.d("gugum", "PERMISSION GRANTED");
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
            Log.d("gugum", "PERMISSION NOT GRANTED");
        }
    }

    private void getLastLocation() {
        Log.d("gugum", "GET LAST LOCATION");
        if (ActivityCompat.checkSelfPermission(((BaseActivity) getActivity()), android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED || ActivityCompat.checkSelfPermission(((BaseActivity) getActivity()), android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(((BaseActivity) getActivity()),
                    new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION, android.Manifest.permission.ACCESS_COARSE_LOCATION}, PERMISSION_ACCESS_FINE_LOCATION);
            return;
        }

        mLastLocation = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);
        if (mLastLocation == null) {
            Log.d("gugum", "LAST FROM FUSED LOCAITON API LOCATION NULL");
            mLastLocation = getLocation();
        } else {
            Log.d("gugum", "LAST FROM FUSED LOCAITON API LOCATION NOT NULL");
        }

        setMyMarker();
    }

    private void setMyMarker(){
        if (mLastLocation != null) {
            if(myMarker!=null){
                myMarker.remove();
                myMarker = null;
            }
            MarkerOptions markerOptions = new MarkerOptions();
            markerOptions.position(new LatLng(mLastLocation.getLatitude(), mLastLocation.getLongitude()))
                    .snippet("This is your location")
                    .title("Your location")
                    .draggable(false);
            myMarker = mMap.addMarker(markerOptions);

            DownloadTask downloadTask = new DownloadTask();
            downloadTask.executeOnExecutor(new ThreadPoolExecutor(5, 10, 5000, TimeUnit.MILLISECONDS, new LinkedBlockingQueue<Runnable>()), ((BaseActivity) getActivity()).getDirectionsUrl(pengantarLatLng, new LatLng(mLastLocation.getLatitude(),mLastLocation.getLongitude())));
        } else {
            Log.d("gugum", "FINAL LOCATION NULL");
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
                canGetLocation = true;

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
                    Log.d("gugum", "Network Enabled");
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
                    Log.d("gugum", "GPS Enabled");
                    if (locationManager != null) {
                        if(locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER)!=null){
                            result = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
                        }
                    }
                }

            }

        } catch (Exception e) {
            e.printStackTrace();
            Log.d("gugum","GET LOCATION ERROR : "+e.toString());
        }

        return result;
    }


    private class DownloadTask extends AsyncTask<String, Void, String>{

        // Downloading data in non-ui thread
        @Override
        protected String doInBackground(String... url) {

            // For storing data from web service
            String data = "";
            Log.d("gugum direction","start download task");
            try{
                // Fetching the data from web service
                data = ((BaseActivity) getActivity()).downloadUrl(url[0]);
            }catch(Exception e){
                Log.d("gugum",e.toString());
            }
            return data;
        }

        // Executes in UI thread, after the execution of
        // doInBackground()
        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);
            Log.d("gugum", "start parser task");
            ParserTask parserTask = new ParserTask();

            // Invokes the thread for parsing the JSON data
            parserTask.executeOnExecutor(new ThreadPoolExecutor(5, 10, 5000, TimeUnit.MILLISECONDS, new LinkedBlockingQueue<Runnable>()), result);
        }
    }

    /** A class to parse the Google Places in JSON format */
    private class ParserTask extends AsyncTask<String, Integer, List<List<HashMap<String,String>>> >{

        // Parsing the data in non-ui thread
        @Override
        protected List<List<HashMap<String, String>>> doInBackground(String... jsonData) {

            JSONObject jObject;
            List<List<HashMap<String, String>>> routes = null;

            try{
                jObject = new JSONObject(jsonData[0]);
                Log.d("gugum direction", "start parsing direction "+jsonData[0]);
                DirectionsJSONParser parser = new DirectionsJSONParser();

                // Starts parsing data
                routes = parser.parse(jObject);
            }catch(Exception e){
                e.printStackTrace();
                Log.d("gugum direction", e.toString());
            }
            return routes;
        }

        // Executes in UI thread, after the parsing process
        @Override
        protected void onPostExecute(List<List<HashMap<String, String>>> result) {
            ArrayList<LatLng> points = null;
            PolylineOptions lineOptions = null;
            MarkerOptions markerOptions = new MarkerOptions();
            String distance = "";
            String duration = "";

            if(result.size()<1){
                Toast.makeText(((BaseActivity) getActivity()).getBaseContext(), "No Points", Toast.LENGTH_SHORT).show();
                return;
            }

            // Traversing through all the routes
            for(int i=0;i<result.size();i++){
                points = new ArrayList<LatLng>();
                lineOptions = new PolylineOptions();

                // Fetching i-th route
                List<HashMap<String, String>> path = result.get(i);

                // Fetching all the points in i-th route
                for(int j=0;j<path.size();j++){
                    HashMap<String,String> point = path.get(j);

                    if(j==0){    // Get distance from the list
                        distance = (String)point.get("distance");
                        continue;
                    }else if(j==1){ // Get duration from the list
                        duration = (String)point.get("duration");
                        continue;
                    }

                    double lat = Double.parseDouble(point.get("lat"));
                    double lng = Double.parseDouble(point.get("lng"));
                    LatLng position = new LatLng(lat, lng);

                    points.add(position);
                }

                // Adding all the points in the route to LineOptions
                lineOptions.addAll(points);
                lineOptions.width(2);
                lineOptions.color(Color.RED);
            }

//            distance.setText("Distance:"+distance + ", Duration:"+duration);

            // Drawing polyline in the Google Map for the i-th route
            mMap.addPolyline(lineOptions);
        }
    }

}