package com.pertamina.brightgas;

import android.app.IntentService;
import android.content.Intent;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Bundle;
import android.os.ResultReceiver;
import android.text.TextUtils;
import android.util.Log;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class AddressService extends IntentService {

    private final static String TAG = "address_service";

    private final static int HIDE_KEC_PROV = 2;

    protected ResultReceiver mReceiver;

    public AddressService() {
        super(TAG);
    }

    @Override
    protected void onHandleIntent(Intent intent) {

        String errorMessage = "";
        List<Address> addresses = null;
        Geocoder geocoder = new Geocoder(this, Locale.getDefault());

        mReceiver = intent.getParcelableExtra(Constants.RECEIVER);

        Location location = intent.getParcelableExtra(Constants.LOCATION_DATA_EXTRA);
        Log.d(TAG, String.valueOf(location.getLatitude()));
        Log.d(TAG, String.valueOf(location.getLongitude()));

        try {

            addresses = geocoder.getFromLocation(
                    location.getLatitude(),
                    location.getLongitude(),
                    1);

        } catch (IOException ioException) {
            errorMessage = getString(R.string.str_Service_is_not_available);
            Log.e(TAG, errorMessage, ioException);

        } catch (IllegalArgumentException illegalArgumentException) {
            errorMessage = getString(R.string.str_Invalid_latitude_or_longitude_values);
            Log.e(TAG, errorMessage + ". " +
                    "Latitude = " + location.getLatitude() + ", " +
                    "Longitude = " + location.getLongitude(), illegalArgumentException);
        }

        if (addresses == null || addresses.size() == 0) {
            if (errorMessage.isEmpty()) {
                errorMessage = getString(R.string.str_No_addresses_found);
                Log.e(TAG, errorMessage);
            }
            deliverResultToReceiver(Constants.FAILURE_RESULT, errorMessage);

        } else {

            ArrayList<String> addressFragments = new ArrayList<>();
            Address address = addresses.get(0);

            for (int i = 0; i < address.getMaxAddressLineIndex() - HIDE_KEC_PROV; i++) {
                Log.d(TAG, "addLine: " + address.getAddressLine(i));
                addressFragments.add(address.getAddressLine(i));
            }

            addressFragments.add(address.getPostalCode());

            Log.i(TAG, getString(R.string.str_Address_found));
            Log.i(TAG, "addresses.get(0).getAddressLine(0): " + addresses.get(0).getAddressLine(0));
            Log.i(TAG, "addresses.get(0).getLocality(): " + addresses.get(0).getLocality());
            Log.i(TAG, "addresses.get(0).getAdminArea(): " + addresses.get(0).getAdminArea());
            Log.i(TAG, "addresses.get(0).getCountryName(): " + addresses.get(0).getCountryName());
            Log.i(TAG, "addresses.get(0).getPostalCode(): " + addresses.get(0).getPostalCode());
            Log.i(TAG, "addresses.get(0).getFeatureName(): " + addresses.get(0).getFeatureName());
            Log.i(TAG, "addresses.get(0).getPremises(): " + addresses.get(0).getPremises());

            deliverResultToReceiver(Constants.SUCCESS_RESULT,
                    TextUtils.join(System.getProperty("line.separator"), addressFragments));
        }

    }

    private void deliverResultToReceiver(int resultCode, String message) {
        Bundle bundle = new Bundle();
        bundle.putString(Constants.RESULT_DATA_KEY, message);
        mReceiver.send(resultCode, bundle);
    }

    public final class Constants {
        public static final int SUCCESS_RESULT = 0;
        public static final int FAILURE_RESULT = 1;
        public static final String PACKAGE_NAME = "com.pertamina.brightgas";
        public static final String RECEIVER = PACKAGE_NAME + ".RECEIVER";
        public static final String RESULT_DATA_KEY = PACKAGE_NAME + ".RESULT_DATA_KEY";
        public static final String LOCATION_DATA_EXTRA = PACKAGE_NAME + ".LOCATION_DATA_EXTRA";
    }

}
