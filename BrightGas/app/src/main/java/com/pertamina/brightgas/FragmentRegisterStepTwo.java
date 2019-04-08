package com.pertamina.brightgas;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.media.ExifInterface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

import com.google.android.gms.maps.model.LatLng;
import com.google.firebase.database.DataSnapshot;
import com.pertamina.brightgas.firebase.FirebaseLoadable;
import com.pertamina.brightgas.firebase.FirebaseRegistration;
import com.pertamina.brightgas.firebase.models.Customer;
import com.pertamina.brightgas.retrofit.login.LoginResponse;
import com.pertamina.brightgas.retrofit.register.RegisterClient;
import com.pertamina.brightgas.retrofit.register.RegisterInterface;
import com.pertamina.brightgas.retrofit.register.RegisterRequest;
import com.pertamina.brightgas.retrofit.register.RegisterResponse;

import org.apache.http.message.BasicNameValuePair;
import org.json.JSONObject;

import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStream;

import de.hdodenhof.circleimageview.CircleImageView;

public class FragmentRegisterStepTwo extends Fragment
        implements RequestLoaderInterface, InterfaceOnRequestPermissionsResult, FirebaseLoadable, RegisterInterface {

    private static final String TAG = "register_two";

    private static final int CAMERA_REQUEST_CODE = 1255;

    private String mName;
    private String mGenderId;
    private String mBirthdate;
    private String mFullAddress;
    private LatLng mLatLng;
    private EditText mNomorHandphone;
    private EditText mEmail;
    private EditText mKataSandi;
    private EditText mUlangiKataSandi;

    private CircleImageView mProfilePicture;
    private Bitmap mBitmap;

    @Override
    public void onResume() {
        super.onResume();
        ((BaseActivity) getActivity()).setInterfaceOnRequestPermissionsResult(this);
    }

    public void setData(String name,
                        String genderId,
                        String birthdate,
                        String fullAddress,
                        LatLng latLng) {
        this.mName = name;
        this.mGenderId = genderId;
        this.mBirthdate = birthdate;
        this.mFullAddress = fullAddress;
        this.mLatLng = latLng;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_register_step2, container, false);

        mNomorHandphone = (EditText) view.findViewById(R.id.nomor_handphone);
        mEmail = (EditText) view.findViewById(R.id.email);
        mKataSandi = (EditText) view.findViewById(R.id.kata_sandi);
        mUlangiKataSandi = (EditText) view.findViewById(R.id.ulangi_kata_sandi);

        doRegister(view.findViewById(R.id.register));

        mProfilePicture = (CircleImageView) view.findViewById(R.id.profile_picture);
        mProfilePicture.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivityForResult(
                        new Intent(getActivity(), CameraActivity.class),
                        CAMERA_REQUEST_CODE
                );
            }
        });

        return view;
    }

    private void doRegister(View view) {

        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (((BaseActivity) getActivity()).isValidEditText(mNomorHandphone)) {

                    if (((BaseActivity) getActivity()).isValidEditText(mEmail)) {

                        if (((BaseActivity) getActivity()).isValidEditText(mKataSandi)) {

                            if (((BaseActivity) getActivity()).isValidEditText(mUlangiKataSandi)) {

                                if (((BaseActivity) getActivity()).getTextFromEditText(mKataSandi).equals(((BaseActivity) getActivity()).getTextFromEditText(mUlangiKataSandi))) {

                                    doRegister();

                                } else {
                                    ((BaseActivity) getActivity()).showDialog("", "Kata sandi dan ulangi kata sandi tidak sesuai", "", "Ok");
                                }
                            } else {
                                ((BaseActivity) getActivity()).showDialog("", "Silahkan isi ulangi kata sandi terlebih dahulu", "", "Ok");
                            }
                        } else {
                            ((BaseActivity) getActivity()).showDialog("", "Silahkan isi kata sandi terlebih dahulu", "", "Ok");
                        }
                    } else {
                        ((BaseActivity) getActivity()).showDialog("", "Silahkan isi email terlebih dahulu", "", "Ok");
                    }
                } else {
                    ((BaseActivity) getActivity()).showDialog("", "Silahkan isi nomor handphone terlebih dahulu", "", "Ok");
                }
            }
        });
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        doRegister();
    }

    private void doRegister() {

        if (!((BaseActivity) getActivity()).isPermissionGranted(android.Manifest.permission.READ_PHONE_STATE)) {
            ((BaseActivity) getActivity()).requestPermissions(new String[]{android.Manifest.permission.READ_PHONE_STATE});
        } else {

            ((BaseActivity) getActivity()).showLoading(true, "Daftar");

            if (GlobalActivity.getBackEndServiceProvider() == GlobalActivity.LEGACY) {
                new RequestLoader(this).loadRequest(0, new BasicNameValuePair[]{
                        new BasicNameValuePair("name", mName),
                        new BasicNameValuePair("gender", mGenderId),
                        new BasicNameValuePair("birth_date", mBirthdate),
                        new BasicNameValuePair("pone", ((BaseActivity) getActivity()).getTextFromEditText(mNomorHandphone)),
                        new BasicNameValuePair("address", mFullAddress),
                        new BasicNameValuePair("province_name", ""),
                        new BasicNameValuePair("city_name", ""),
                        new BasicNameValuePair("province_id", ""),
                        new BasicNameValuePair("city_id", ""),
                        new BasicNameValuePair("lat", mLatLng.latitude + ""),
                        new BasicNameValuePair("long", mLatLng.longitude + ""),
                        new BasicNameValuePair("email", ((BaseActivity) getActivity()).getTextFromEditText(mEmail)),
                        new BasicNameValuePair("password", ((BaseActivity) getActivity()).getTextFromEditText(mKataSandi)),
                        new BasicNameValuePair("imeii", ""),
                        new BasicNameValuePair("gcm_id", "")
                }, "register", "customer", true);
            }

            if (GlobalActivity.getBackEndServiceProvider() == GlobalActivity.FIREBASE) {
                new FirebaseRegistration(getActivity(), this).registration(new BasicNameValuePair[]{
                        new BasicNameValuePair("name", mName),
                        new BasicNameValuePair("gender", mGenderId),
                        new BasicNameValuePair("birth_date", mBirthdate),
                        new BasicNameValuePair("pone", ((BaseActivity) getActivity()).getTextFromEditText(mNomorHandphone)),
                        new BasicNameValuePair("address", mFullAddress),
                        new BasicNameValuePair("lat", mLatLng.latitude + ""),
                        new BasicNameValuePair("long", mLatLng.longitude + ""),
                        new BasicNameValuePair("email", ((BaseActivity) getActivity()).getTextFromEditText(mEmail)),
                        new BasicNameValuePair("password", ((BaseActivity) getActivity()).getTextFromEditText(mKataSandi)),
                        new BasicNameValuePair("imeii", ""),
                        new BasicNameValuePair("gcm_id", "")
                }, mBitmap);
            }

            if (GlobalActivity.getBackEndServiceProvider() == GlobalActivity.SWAGGER) {
                new RegisterClient(getContext(), this).register(new RegisterRequest(
                        ((BaseActivity) getActivity()).getTextFromEditText(mEmail),
                        mName,
                        ((BaseActivity) getActivity()).getTextFromEditText(mNomorHandphone),
                        ((BaseActivity) getActivity()).getTextFromEditText(mKataSandi),
                        ((BaseActivity) getActivity()).getTextFromEditText(mKataSandi),
                        mFullAddress,
                        mLatLng.latitude + "",
                        mLatLng.longitude + "",
                        mGenderId,
                        mBirthdate
                ));
            }
        }
    }

    @Override
    public void setData(int index, String result, View[] impactedViews) {
        ((BaseActivity) getActivity()).showLoading(false);
        try {
            JSONObject jsonObject = new JSONObject(result);
            if (jsonObject.getInt("status") == 1) {
                User.id = jsonObject.getString("customer_id");
                User.name = mName;
                Data.self.insertUser();
                ((BaseActivity) getActivity()).changeActivity(MainActivity.class);
            } else {
                ((BaseActivity) getActivity()).showDialog("Gagal", jsonObject.getString("message"));
            }
        } catch (Exception ex) {
            ((BaseActivity) getActivity()).showDialog("Gagal", ex.toString());
        }
    }

    @Override
    public void setFirebaseData(DataSnapshot dataSnapshot) {
        ((BaseActivity) getActivity()).showLoading(false);
        Log.d(TAG, "setFirebaseData: " + dataSnapshot.toString());
        Customer customer = dataSnapshot.getValue(Customer.class);
        User.id = customer.uid;
        User.name = customer.name;
        User.picture = customer.pictureUrl;
        Data.self.insertUser();
        ((BaseActivity) getActivity()).changeActivity(MainActivity.class);
    }

    @Override
    public void retrofitRegister(RegisterResponse registerResponse, LoginResponse loginResponse) {
        ((BaseActivity) getActivity()).showLoading(false);
        User.id = loginResponse.getData().getUsername();
        User.name = loginResponse.getData().getName();
        User.picture = "no_url";
        Data.self.insertUser();
        ((BaseActivity) getActivity()).changeActivity(MainActivity.class);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        Log.d(TAG, "onActivityResult is called");
        Log.d(TAG, "request code = " + requestCode);

        if (requestCode == CAMERA_REQUEST_CODE) {

            File directoryPath = new File(getActivity().getFilesDir(), CameraActivity.PROFILE_PICTURE_PATH);
            File filePath = new File(directoryPath.getPath() + File.separator + CameraActivity.PROFILE_PICTURE_FILE_NAME + ".jpg");

            try {

                mBitmap = BitmapFactory.decodeFile(filePath.getAbsolutePath());

                ExifInterface exif = new ExifInterface(filePath.getAbsolutePath());
                int orientation = exif.getAttributeInt(ExifInterface.TAG_ORIENTATION, 1);

                Log.d(TAG, "Picture orientation: " + orientation);

                Matrix matrix = new Matrix();
                if (orientation == 6) {
                    matrix.postRotate(90);
                } else if (orientation == 3) {
                    matrix.postRotate(180);
                } else if (orientation == 8) {
                    matrix.postRotate(270);
                }

                mBitmap = Bitmap.createBitmap(mBitmap, 0, 0, mBitmap.getWidth(), mBitmap.getHeight(), matrix, true);

                OutputStream fOut = new FileOutputStream(filePath);
                mBitmap.compress(Bitmap.CompressFormat.PNG, 100, fOut);
                fOut.flush();
                fOut.close();

                mProfilePicture.setImageBitmap(mBitmap);

            } catch (Exception exception) {
                exception.printStackTrace();
            }
        }
    }
}
