package com.pertamina.brightgasagen;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;

import com.pertamina.brightgasagen.model.IdName;
import com.pertamina.brightgasagen.model.OrderList;

import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;

public class FragmentDriverPicker extends Fragment implements RequestLoaderInterface{

    public static final int GET_DRIVER_DATA = 1;
    public static final int PICK_DRIVER = 2;

    AdapterDriverPicker adapter;
    OrderList data;

    public FragmentDriverPicker(){

    }

    public void setData(OrderList data){
        this.data = data;
    }

    @Override
    public void onResume() {
        super.onResume();
        ((BaseActivity) getActivity()).setTitle("Pilih Driver");
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_driver_picker,container,false);

        ListView listView = (ListView)rootView.findViewById(R.id.listview);
        adapter = new AdapterDriverPicker(getContext(),new ArrayList<IdName>());
        listView.setAdapter(adapter);
        adapter.notifyDataSetChanged();
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                IdName data = adapter.getItem(position);
                pickDriver(data.id);
            }
        });
        new RequestLoader(this).loadRequest(GET_DRIVER_DATA,new BasicNameValuePair[]{
                new BasicNameValuePair("agen_id",User.id)
        },"agen","get_driver",false,true);
        return rootView;
    }

    private void parseGetDriverData(String result){
        try{
            JSONObject jsonObject = new JSONObject(result);
            if(jsonObject.getInt("status") == 1){
                JSONArray jsonArray = jsonObject.getJSONArray("customer_id");
                adapter.clear();
                if(jsonArray.length()>0){
                    for(int i=0; i<jsonArray.length(); i++){
                        jsonObject = jsonArray.getJSONObject(i);
                        adapter.add(new IdName(jsonObject.getString("agd_id"),jsonObject.getString("agd_name")));
                    }
                }
                adapter.notifyDataSetChanged();
            }else{
                ((BaseActivity) getActivity()).showDialog("",jsonObject.getString("message"),"","Ok");
            }
        }catch(Exception ex){
            ((BaseActivity) getActivity()).showDialog("Error parsing",ex.toString(),"","Ok");
        }
    }

    private void pickDriver(String id){
        ((BaseActivity) getActivity()).showLoading(true);
        new RequestLoader(this).loadRequest(PICK_DRIVER,new BasicNameValuePair[]{
                new BasicNameValuePair("id",data.id),
                new BasicNameValuePair("driver_id",id)
        },"transaction","send_to_driver",true);
    }

    private void parsePickDriver(String result){
        try{
            JSONObject jsonObject = new JSONObject(result);
            if(jsonObject.getInt("status") == 1){
                ((BaseActivity) getActivity()).onBackPressed();
            }else{
                ((BaseActivity) getActivity()).showDialog("",jsonObject.getString("message"),"","Ok");
            }
        }catch(Exception ex){
            ((BaseActivity) getActivity()).showDialog("Error parsing",ex.toString(),"","Ok");
        }
    }

    @Override
    public void setData(int index, String result, View[] impactedViews) {
        ((BaseActivity) getActivity()).showLoading(false);
        switch (index){
            case GET_DRIVER_DATA:
                parseGetDriverData(result);
                break;
            case PICK_DRIVER:
                parsePickDriver(result);
                break;
        }
    }
}
