package com.pertamina.brightgas;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ListView;

import com.pertamina.brightgas.model.IdName;

import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;

public class FragmentPickerOption extends Fragment implements RequestLoaderInterface {

    private static final String TAG = "frag_picker_option";

    public static final int TYPE_PROVINCE = 1;
    public static final int TYPE_CITY = 2;

    private int mType;

    private IdName mDatas;

    private AdapterIdName mAdapter;

    private InterfaceAddressOption mAddressOptionInterface;

    public void init(InterfaceAddressOption addressOptionInterface, int type, IdName data) {
        this.mAddressOptionInterface = addressOptionInterface;
        this.mType = type;
        this.mDatas = data;
    }

    @Override
    public void onStop() {
        super.onStop();
        ((BaseActivity)getActivity()).hideKeyboard();
    }

    @Override
    public void onResume() {
        super.onResume();
        String additional = "";
        switch (mType) {
            case TYPE_PROVINCE:
                additional = "Provinsi";
                break;

            case TYPE_CITY:
                additional = "Kota";
                break;
        }
        getActivity().setTitle("Pilih " + additional);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.fragment_address_option, container, false);

        ListView listview = (ListView) rootView.findViewById(R.id.list_view);

        mAdapter = new AdapterIdName(getContext(), new ArrayList<IdName>());
        listview.setAdapter(mAdapter);
        String controller;
        BasicNameValuePair[] valuePairs;
        switch (mType) {
            case TYPE_PROVINCE:
                controller = "province";
                valuePairs = new BasicNameValuePair[]{};
                new RequestLoader(this).getRequestRajaOngkir(mType, valuePairs, controller);
                break;

            case TYPE_CITY:
                controller = "city";
                if (mDatas != null) {
                    valuePairs = new BasicNameValuePair[]{
                            new BasicNameValuePair("get___city", ""),
                            new BasicNameValuePair("get___province", mDatas.id)
                    };
                } else {
                    valuePairs = new BasicNameValuePair[]{};
                }
                new RequestLoader(this).getRequestRajaOngkir(mType, valuePairs, controller);
                break;
        }

        listview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                IdName data = mAdapter.getItem(position);
                mAddressOptionInterface.setData(mType, data);
                getFragmentManager().popBackStack();
            }
        });


        EditText search = (EditText) rootView.findViewById(R.id.search);
        search.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                mAdapter.query(s.toString());
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        return rootView;
    }

    void parseCity(String result) {
        try {
            JSONObject jsonObject = new JSONObject(result);
            jsonObject = jsonObject.getJSONObject("rajaongkir");
            JSONArray jsonArray = jsonObject.getJSONArray("results");
            if (jsonArray.length() > 0) {
                for (int i = 0; i < jsonArray.length(); i++) {
                    jsonObject = jsonArray.getJSONObject(i);
                    mAdapter.add(new IdName(jsonObject.getString("city_id"), jsonObject.getString("type") + " " + jsonObject.getString("city_name"), jsonObject.getString("postal_code")));
                }
                mAdapter.notifyDataSetChanged();
            }
        } catch (Exception ex) {
            Log.d(TAG, ex.toString());
        }
    }

    void parseProvince(String result) {
        Log.d(TAG, "PARSE PROVINCE");
        try {
            JSONObject jsonObject = new JSONObject(result);
            jsonObject = jsonObject.getJSONObject("rajaongkir");
            JSONArray jsonArray = jsonObject.getJSONArray("results");
            if (jsonArray.length() > 0) {
                for (int i = 0; i < jsonArray.length(); i++) {
                    jsonObject = jsonArray.getJSONObject(i);
                    mAdapter.add(new IdName(jsonObject.getString("province_id"), jsonObject.getString("province")));
                }
                mAdapter.notifyDataSetChanged();
            }
        } catch (Exception ex) {
            Log.d(TAG, ex.toString());
        }
        Log.d(TAG, "END PARSE PROVINCE " + mAdapter.getCount());
    }

    @Override
    public void setData(int index, String result, View[] impactedViews) {
        Log.d(TAG, "BEGIN SET DATA : " + result);
        switch (mType) {
            case TYPE_PROVINCE:
                parseProvince(result);
                break;

            case TYPE_CITY:
                parseCity(result);
                break;
        }
    }
}
