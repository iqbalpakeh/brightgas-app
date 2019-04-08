package com.pertamina.brightgasagen;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.pertamina.brightgasagen.model.OrderList;

import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * Created by gumelartejasukma on 11/23/16.
 */
public class FragmentOrderList extends Fragment implements RequestLoaderInterface{

    private static final int DATA = 0;

    BottomMenu jarakTerdekat,waktuPesan,waktuAntar,selectedMenu;
    AdapterOrderList adapter;

    public FragmentOrderList(){

    }

    @Override
    public void onResume() {
        super.onResume();
        ((BaseActivity) getActivity()).setTitle("Transaksi Baru");
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_order_list,container,false);
        ListView listView = (ListView) rootView.findViewById(R.id.listview);
        adapter = new AdapterOrderList(getContext(),new ArrayList<OrderList>());
        listView.setAdapter(adapter);
        adapter.notifyDataSetChanged();
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                OrderList data = adapter.getItem(position);
                FragmentHistoryDetail fragment = new FragmentHistoryDetail();
                fragment.setData(data.id);
                ((BaseActivity) getActivity()).changeFragment(fragment,false,true);
            }
        });
        jarakTerdekat = new BottomMenu(rootView.findViewById(R.id.jarakterdekat),R.drawable.ic_delivery_truck,"Jarak Terdekat");
        waktuPesan = new BottomMenu(rootView.findViewById(R.id.waktupesan),R.drawable.ic_shopping_basket,"Total Belanja");
        waktuAntar = new BottomMenu(rootView.findViewById(R.id.waktuantar),R.drawable.ic_clock,"Waktu Antar");
        jarakTerdekat.setSelected(true);

        new RequestLoader(this).loadRequest(DATA,new BasicNameValuePair[]{
                new BasicNameValuePair("id",User.id)
        },"transaction","get_trx_pending",false,true);

        return rootView;
    }

    private void parseData(String result){
        try{
            JSONObject jsonObject = new JSONObject(result);
            adapter.clear();
            if(jsonObject.getInt("status") == 1){
                JSONArray jsonArray = jsonObject.getJSONArray("data");
                if(jsonArray.length()>0){
                    for(int i=0; i<jsonArray.length(); i++){
                        jsonObject = jsonArray.getJSONObject(i);
                        JSONArray jsonArray1 = jsonObject.getJSONArray("item");
                        jsonObject = jsonObject.getJSONObject("header");
                        adapter.add(new OrderList(OrderList.TYPE_TAKE_ORDER,
                                jsonObject.getString("tran_id"),
                                jsonObject.getString("tran_no_invoice"),
                                jsonObject.getString("tran_date"),
                                jsonObject.getString("tran_time_start"),
                                jsonObject.getString("tran_time_end"),
                                jsonObject.getString("cus_name"),
                                jsonObject.getString("cua_address")+"\n"+jsonObject.getString("cua_city_name")+", "+jsonObject.getString("cua_province_name"),
                                jsonObject.getLong("tran_sub_total"),
                                jsonObject.getLong("tran_ongkir"),
                                jsonObject.getLong("tran_grand_total"),
                                jsonObject.getString("distance"),jsonArray1));
                    }
                }
            }else{
                ((BaseActivity) getActivity()).showDialog("",jsonObject.getString("message"),"","Ok");
            }
            adapter.notifyDataSetChanged();
        }catch(Exception ex){
            ((BaseActivity) getActivity()).showDialog("Error parsing",ex.toString(),"","Ok");
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

    class BottomMenu{
        public ImageView imageView;
        public TextView textView;
        public boolean isSelected;

        public BottomMenu(View rootView, int imageId, String text){
            imageView = (ImageView)rootView.findViewById(R.id.imageview);
            imageView.setImageResource(imageId);
            textView = (TextView)rootView.findViewById(R.id.textview);
            textView.setText(text);
            rootView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    setSelected(true);
                }
            });
        }

        public void setSelected(boolean val){
            if(isSelected!=val){
                isSelected = val;
                int color;
                if(isSelected){
                    if(selectedMenu!=null){
                        selectedMenu.setSelected(false);
                    }
                    selectedMenu = this;
                    color = ContextCompat.getColor(getContext(),R.color.chatrightname);
                }else{
                    color = ContextCompat.getColor(getContext(),R.color.berhasilgrey);
                }
                imageView.setColorFilter(color);
                textView.setTextColor(color);
            }
        }

    }

}
