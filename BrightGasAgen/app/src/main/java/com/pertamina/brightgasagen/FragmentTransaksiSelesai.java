package com.pertamina.brightgasagen;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RatingBar;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONObject;

import de.hdodenhof.circleimageview.CircleImageView;

/**
 * Created by gumelartejasukma on 11/12/16.
 */
public class FragmentTransaksiSelesai extends Fragment implements RequestLoaderInterface{

    private static final int DATA = 0;

    private String profilePictureUrl = "http://cdn.jitunews.com/dynamic/thumb/2016/06/35c2a4f393b6427ff7a44e8c2dcb7152_630x420_thumb.jpg?w=630";
    private TextView customerName, customerAddress, driverPhone,driverName,invoiceNo,deliveryTime,deliveryDate;
    private RatingBar ratingBar;
    private TextView ulasan;
    String transactionId;

    public FragmentTransaksiSelesai(){

    }

    public void setData(String transactionId){
        this.transactionId = transactionId;
    }

    @Override
    public void onResume() {
        super.onResume();
        ((BaseActivity)getActivity()).setTitle("Ulasan Konsumen");
        FragmentInformasiTransaksi.counter = 300;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_transaksi_selesai,container,false);
        CircleImageView profilePicture = (CircleImageView)rootView.findViewById(R.id.profilpicture);
        Picasso.with(getContext()).load(profilePictureUrl).centerCrop().fit().placeholder(R.drawable.ic_brightgas_logo).into(profilePicture);

        invoiceNo = (TextView)rootView.findViewById(R.id.invoiceno);
        customerName = (TextView)rootView.findViewById(R.id.customername);
        customerAddress = (TextView)rootView.findViewById(R.id.agenaddress);
        driverPhone = (TextView)rootView.findViewById(R.id.driverphone);
        driverName = (TextView)rootView.findViewById(R.id.drivername);
        ratingBar = (RatingBar)rootView.findViewById(R.id.ratingbar);
        ulasan = (TextView) rootView.findViewById(R.id.ulasan);
        deliveryTime = (TextView) rootView.findViewById(R.id.deliverytime);
        deliveryDate = (TextView) rootView.findViewById(R.id.deliverydate);

        View selesai = rootView.findViewById(R.id.selesai);
        selesai.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ((BaseActivity)getActivity()).changeFragment(new FragmentDashboard());
            }
        });

        new RequestLoader(this).loadRequest(DATA,new BasicNameValuePair[]{
                new BasicNameValuePair("id",transactionId)
        },"transaction","get_rating_agen",false);

        return rootView;
    }

    private void parseData(String result){
        try{
            JSONObject jsonObject = new JSONObject(result);
            if(jsonObject.getInt("status")==1){
                JSONArray jsonArray = jsonObject.getJSONArray("data");
                jsonObject = jsonArray.getJSONObject(0);
                invoiceNo.setText(jsonObject.getString("tran_no_invoice"));
                customerName.setText(jsonObject.getString("cus_name"));
                customerAddress.setText(jsonObject.getString("tran_address_id"));
//                customerAddress.setText(jsonObject.getString("age_address"));
                driverPhone.setText(jsonObject.getString("agd_phone"));
                driverName.setText(jsonObject.getString("agd_name"));
                ratingBar.setRating(Float.parseFloat(jsonObject.getString("tran_rating")));
                ulasan.setText(jsonObject.getString("tran_comment"));
                String[] delivery = jsonObject.getString("tran_delivered_date").split(" ");
                deliveryDate.setText(delivery[0]);
                deliveryTime.setText(delivery[1]);

            }else{
                ((BaseActivity)getActivity()).showDialog("",jsonObject.getString("message"),"","Ok");
            }
        }catch (Exception ex){
            ((BaseActivity)getActivity()).showDialog("Error",ex.toString(),"","Ok");
        }
    }

    @Override
    public void setData(int index, String result, View[] impactedViews) {
        ((BaseActivity)getActivity()).showLoading(false);
        switch (index){
            case DATA:
                parseData(result);
                break;
        }
    }
}
