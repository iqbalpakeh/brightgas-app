package com.pertamina.brightgas;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.firebase.ui.storage.images.FirebaseImageLoader;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.pertamina.brightgas.firebase.FirebaseQueryAddress;
import com.pertamina.brightgas.firebase.FirebaseRegistration;
import com.pertamina.brightgas.firebase.models.Address;
import com.pertamina.brightgas.firebase.models.Customer;
import com.pertamina.brightgas.model.Alamat;
import com.pertamina.brightgas.retrofit.listaddress.ListAddressClient;
import com.pertamina.brightgas.retrofit.listaddress.ListAddressInterface;
import com.pertamina.brightgas.retrofit.listaddress.ListAddressResponse;
import com.pertamina.brightgas.retrofit.customerprofile.CustomerProfileClient;
import com.pertamina.brightgas.retrofit.customerprofile.CustomerProfileInterface;
import com.pertamina.brightgas.retrofit.customerprofile.CustomerProfileResponse;

import java.util.ArrayList;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class FragmentProfil extends Fragment implements FirebaseQueryAddress.FirebaseAddressLoadable,
        CustomerProfileInterface, ListAddressInterface {

    private static final String TAG = "frag_profil";

    private TextView mUserName;
    private TextView mUserGender;
    private TextView mUserBirthday;
    private TextView mUserPhone;
    private TextView mUserEmail;
    private CircleImageView mCircleImageView;

    private AdapterAlamatProfil mAdapter;

    @Override
    public void onResume() {
        super.onResume();
        getActivity().setTitle("Profil");
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_profil, container, false);

        ListView listView = (ListView) view.findViewById(R.id.list_view);
        mAdapter = new AdapterAlamatProfil(getContext(), new ArrayList<Alamat>());
        listView.setAdapter(mAdapter);
        mAdapter.notifyDataSetChanged();

        mUserName = (TextView) view.findViewById(R.id.user_name);
        mUserGender = (TextView) view.findViewById(R.id.user_gender);
        mUserBirthday = (TextView) view.findViewById(R.id.user_birthday);
        mUserPhone = (TextView) view.findViewById(R.id.user_phone);
        mUserEmail = (TextView) view.findViewById(R.id.user_email);
        mCircleImageView = (CircleImageView) view.findViewById(R.id.user_picture);

        View tambahAlamat = view.findViewById(R.id.tambah_alamat);
        tambahAlamat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ((BaseActivity) getActivity()).changeFragment(new FragmentAddAddress(), false, true);
            }
        });

        ((BaseActivity) getActivity()).showLoading(true);

        if (GlobalActivity.getBackEndServiceProvider() == GlobalActivity.FIREBASE) {
            new FirebaseQueryAddress(getActivity(), this).queryProfilData();
        }

        if (GlobalActivity.getBackEndServiceProvider() == GlobalActivity.SWAGGER) {
            new CustomerProfileClient(getContext(), this).showProfile();
        }

        return view;
    }

    @Override
    public void retrofitShowProfile(CustomerProfileResponse response) {

        mUserName.setText(response.getData().getName());
        mUserGender.setText(response.getData().getGender());
        mUserBirthday.setText(response.getData().getDob().split("T")[0]);
        mUserPhone.setText(response.getData().getPhone());
        mUserEmail.setText(response.getData().getEmail());

        // todo: show customer profile picture if any

        new ListAddressClient(getContext(), this).listAddress();
    }

    @Override
    public void retrofitListAddress(ListAddressResponse response) {
        ((BaseActivity) getActivity()).showLoading(false);
        Log.d(TAG, "status:" + response.getStatus());
        List<ListAddressResponse.Data> addresses = response.getData();
        for(ListAddressResponse.Data address : addresses) {
            mAdapter.add(new Alamat(
                    address.getId(),
                    address.getAddressText(),
                    "",
                    ""
            ));
        }
        mAdapter.notifyDataSetChanged();
    }

    @Override
    public void setProfilData(DataSnapshot profilSnapshot, DataSnapshot addressSnapshot) {

        ((BaseActivity) getActivity()).showLoading(false);

        Customer customer =
                profilSnapshot.getValue(Customer.class);

        mUserName.setText(customer.name);
        mUserGender.setText(customer.gender);
        mUserBirthday.setText(customer.birthdate);
        mUserPhone.setText(customer.phoneNumber);
        mUserEmail.setText(customer.email);

        if (!customer.pictureUrl.equals(FirebaseRegistration.NO_URL)) {
            StorageReference storageReference = FirebaseStorage.getInstance().getReferenceFromUrl(customer.pictureUrl);
            Glide.with(getActivity())
                    .using(new FirebaseImageLoader())
                    .load(storageReference)
                    .into(mCircleImageView);
        }

        mAdapter.clear();

        for (DataSnapshot children : addressSnapshot.getChildren()) {

            Address address = children.getValue(Address.class);

            Log.d(TAG, "address = " + address.address);
            Log.d(TAG, "latitude = " + address.latitude);
            Log.d(TAG, "longitude = " + address.longitude);

            mAdapter.add(new Alamat(
                    children.getKey(),
                    address.address,
                    address.latitude,
                    address.longitude
            ));
        }

        mAdapter.notifyDataSetChanged();
    }

}
