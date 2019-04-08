package com.pertamina.brightgasagen;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import com.pertamina.brightgasagen.model.Driver;

import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;

public class FragmentManageDriver extends Fragment implements RequestLoaderInterface{

    private static final int DATA = 0;

    AdapterDriver adapter;

    public FragmentManageDriver(){

    }

    @Override
    public void onResume() {
        super.onResume();
        ((BaseActivity) getActivity()).setTitle("Driver");
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_manage_driver,container,false);
        ListView listView = (ListView)rootView.findViewById(R.id.listview);
        adapter = new AdapterDriver(getContext(),new ArrayList<Driver>());
        listView.setAdapter(adapter);
        adapter.notifyDataSetChanged();
//        final EditText name = (EditText)rootView.findViewById(R.id.name);
//        final EditText userName = (EditText)rootView.findViewById(R.id.username);
//        final EditText phoneNumber = (EditText)rootView.findViewById(R.id.phonenumber);
//        View action = rootView.findViewById(R.id.action);
//        action.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                if(BaseActivity.self.isValidEditText(name)){
//                    if(BaseActivity.self.isValidEditText(userName)){
//                        if(BaseActivity.self.isValidEditText(phoneNumber)){
//                            adapter.add(new Driver(BaseActivity.self.getTextFromEditText(name),BaseActivity.self.getTextFromEditText(phoneNumber)));
//                            adapter.notifyDataSetChanged();
//                        }else{
//                            BaseActivity.self.showDialog("Pesan","Silahkan masukan nomor telepon terlebih dahulu");
//                        }
//                    }else{
//                        BaseActivity.self.showDialog("Pesan","Silahkan masukan username terlebih dahulu");
//                    }
//                }else{
//                    BaseActivity.self.showDialog("Pesan","Silahkan masukan nama terlebih dahulu");
//                }
//            }
//        });
        new RequestLoader(this).loadRequest(DATA,new BasicNameValuePair[]{
                new BasicNameValuePair("agen_id",User.id)
        },"agen","get_driver",false,true);
        return rootView;
    }

    private void parseData(String result){
        try{
            JSONObject jsonObject = new JSONObject(result);
            if(jsonObject.getInt("status")==1){
                JSONArray jsonArray = jsonObject.getJSONArray("customer_id");
                adapter.clear();
                for(int i=0; i<jsonArray.length(); i++){
                    jsonObject = jsonArray.getJSONObject(i);
                    adapter.add(new Driver(jsonObject.getString("agd_name"),jsonObject.getString("agd_phone")));
                }
                adapter.notifyDataSetChanged();
            }else{
                ((BaseActivity) getActivity()).showDialog("",jsonObject.getString("message"),"","Ok");
            }
        }catch(Exception ex){
            ((BaseActivity) getActivity()).showDialog("Error",ex.toString(),"","Ok");
        }
    }

    @Override
    public void setData(int index, String result, View[] impactedViews) {
        ((BaseActivity) getActivity()).showLoading(false);
        switch (index){
            case DATA:
                parseData(result);
                break;
        }
    }
}
