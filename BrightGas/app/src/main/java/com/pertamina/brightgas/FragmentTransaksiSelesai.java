package com.pertamina.brightgas;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.RatingBar;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONObject;

import de.hdodenhof.circleimageview.CircleImageView;

public class FragmentTransaksiSelesai extends Fragment implements RequestLoaderInterface {

    private static final String TAG = "frag_trans_selesai";

    private static final int DATA = 0;

    private static final int ACTION = 1;

    String transactionId;

    private String profilePictureUrl = "http://cdn.jitunews.com/dynamic/thumb/2016/06/35c2a4f393b6427ff7a44e8c2dcb7152_630x420_thumb.jpg?w=630";

    private TextView mAgenName;

    private TextView mAgenAddress;

    private TextView mAgenPhone;

    private TextView mDriverName;

    private RatingBar mRatingBar;

    private EditText mUlasan;

    public void setData(String transactionId) {
        this.transactionId = transactionId;
    }

    @Override
    public void onResume() {
        super.onResume();
        getActivity().setTitle("Transaksi Selesai");
        FragmentInformasiTransaksi.COUNTER = 300;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.fragment_transaksi_selesai, container, false);
        CircleImageView profilePicture = (CircleImageView) rootView.findViewById(R.id.profilpicture);
        Picasso.with(getContext()).load(profilePictureUrl).centerCrop().fit().placeholder(R.drawable.ic_brightgas_logo).into(profilePicture);

        mAgenName = (TextView) rootView.findViewById(R.id.agenname);
        mAgenAddress = (TextView) rootView.findViewById(R.id.agenaddress);
        mAgenPhone = (TextView) rootView.findViewById(R.id.agen_phone);
        mDriverName = (TextView) rootView.findViewById(R.id.drivername);
        mRatingBar = (RatingBar) rootView.findViewById(R.id.ratingbar);
        mUlasan = (EditText) rootView.findViewById(R.id.ulasan);

        View selesai = rootView.findViewById(R.id.selesai);
        selesai.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                doAction();
            }
        });

        new RequestLoader(this).loadRequest(DATA, new BasicNameValuePair[]{
                new BasicNameValuePair("id", transactionId)
        }, "transaction", "get_rating", false);

        return rootView;
    }

    private void doAction() {
        ((BaseActivity)getActivity()).showLoading(true);
        new RequestLoader(this).loadRequest(ACTION, new BasicNameValuePair[]{
                new BasicNameValuePair("id", transactionId),
                new BasicNameValuePair("comment", ((BaseActivity)getActivity()).getTextFromEditText(mUlasan)),
                new BasicNameValuePair("rating", mRatingBar.getRating() + "")
        }, "transaction", "set_rating", true);
    }

    private void parseData(String result) {
        try {
            JSONObject jsonObject = new JSONObject(result);
            if (jsonObject.getInt("status") == 1) {
                JSONArray jsonArray = jsonObject.getJSONArray("data");
                jsonObject = jsonArray.getJSONObject(0);
                mAgenName.setText(jsonObject.getString("age_name"));
                mAgenAddress.setText(jsonObject.getString("age_address"));
                mAgenPhone.setText(jsonObject.getString("age_phone"));
                mDriverName.setText(jsonObject.getString("agd_name"));
                mRatingBar.setRating(Float.parseFloat(jsonObject.getString("tran_rating")));
            } else {
                ((BaseActivity)getActivity()).showDialog("", jsonObject.getString("message"), "", "Ok");
            }
        } catch (Exception ex) {
            ((BaseActivity)getActivity()).showDialog("Error", ex.toString(), "", "Ok");
        }
    }

    private void parseAction(String result) {
        try {
            JSONObject jsonObject = new JSONObject(result);
            if (jsonObject.getInt("status") == 1) {
                ((BaseActivity)getActivity()).changeFragment(new FragmentBeranda());
            } else {
                ((BaseActivity)getActivity()).showDialog("", jsonObject.getString("message"), "", "Ok");
            }
        } catch (Exception ex) {
            ((BaseActivity)getActivity()).showDialog("Error", ex.toString(), "", "Ok");
        }
    }

    @Override
    public void setData(int index, String result, View[] impactedViews) {
        ((BaseActivity)getActivity()).showLoading(false);
        switch (index) {
            case DATA:
                parseData(result);
                break;
            case ACTION:
                parseAction(result);
                break;
        }
    }
}
