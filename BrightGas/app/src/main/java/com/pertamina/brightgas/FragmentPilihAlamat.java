package com.pertamina.brightgas;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;

import com.google.firebase.database.DataSnapshot;
import com.pertamina.brightgas.firebase.FirebaseLoadable;
import com.pertamina.brightgas.firebase.FirebaseQueryAddress;
import com.pertamina.brightgas.firebase.models.Address;
import com.pertamina.brightgas.model.Alamat;
import com.pertamina.brightgas.retrofit.listaddress.ListAddressClient;
import com.pertamina.brightgas.retrofit.listaddress.ListAddressInterface;
import com.pertamina.brightgas.retrofit.listaddress.ListAddressResponse;

import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class FragmentPilihAlamat extends Fragment implements RequestLoaderInterface,
        FirebaseLoadable, ListAddressInterface {

    private static final String TAG = "pilih_alamat";

    private AdapterAlamat mAdapter;

    @Override
    public void onResume() {
        super.onResume();
        getActivity().setTitle("Pilih Alamat");
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.fragment_pilih_alamat, container, false);

        ListView listView = (ListView) rootView.findViewById(R.id.list_view);
        mAdapter = new AdapterAlamat(getContext(), new ArrayList<Alamat>());
        listView.setAdapter(mAdapter);
        mAdapter.notifyDataSetChanged();
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                FragmentPesan fragment = new FragmentPesan();
                fragment.setData(mAdapter.getItem(position));
                ((BaseActivity) getActivity()).changeFragment(fragment, false, true);
            }
        });

        View tambahAlamat = rootView.findViewById(R.id.tambah_alamat);
        tambahAlamat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ((BaseActivity) getActivity()).changeFragment(new FragmentAddAddress(), false, true);
            }
        });

        ((BaseActivity) getActivity()).showLoading(true);

        if (GlobalActivity.getBackEndServiceProvider() == GlobalActivity.LEGACY) {
            new RequestLoader(this).loadRequest(0, new BasicNameValuePair[]{
                    new BasicNameValuePair("customer_id", User.id)
            }, "customer", "get_address", false, true);
        }

        if (GlobalActivity.getBackEndServiceProvider() == GlobalActivity.FIREBASE) {
            new FirebaseQueryAddress(getActivity(), this).query();
        }

        if (GlobalActivity.getBackEndServiceProvider() == GlobalActivity.SWAGGER) {
            new ListAddressClient(getContext(), this).listAddress();
        }

        return rootView;
    }

    @Override
    public void setData(int index, String result, View[] impactedViews) {
        ((BaseActivity) getActivity()).showLoading(false);
        try {
            JSONObject jsonObject = new JSONObject(result);
            if (jsonObject.getInt("status") == 1) {
                JSONArray jsonArray = jsonObject.getJSONArray("data");
                mAdapter.clear();
                if (jsonArray.length() > 0) {
                    for (int i = 0; i < jsonArray.length(); i++) {
                        jsonObject = jsonArray.getJSONObject(i);
                        mAdapter.add(new Alamat(
                                jsonObject.getString("cua_id"),
                                jsonObject.getString("cua_address"),
                                jsonObject.getString("cua_lat"),
                                jsonObject.getString("cua_long")));
                    }
                }
                mAdapter.notifyDataSetChanged();
            } else {
                ((BaseActivity) getActivity()).showDialog("", jsonObject.getString("message"), "", "Ok");
            }
        } catch (Exception ex) {
            ((BaseActivity) getActivity()).showDialog("Error parsing", ex.toString(), "", "Ok");
        }
    }

    @Override
    public void setFirebaseData(DataSnapshot dataSnapshot) {
        ((BaseActivity) getActivity()).showLoading(false);
        mAdapter.clear();
        for (DataSnapshot children : dataSnapshot.getChildren()) {
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

    @Override
    public void retrofitListAddress(ListAddressResponse response) {
        ((BaseActivity) getActivity()).showLoading(false);
        mAdapter.clear();
        Log.d(TAG, "status:" + response.getStatus());
        List<ListAddressResponse.Data> addresses = response.getData();
        for (ListAddressResponse.Data address : addresses) {
            mAdapter.add(new Alamat(
                    address.getId(),
                    address.getAddressText(),
                    "",
                    ""
            ));
        }
        mAdapter.notifyDataSetChanged();
    }
}
