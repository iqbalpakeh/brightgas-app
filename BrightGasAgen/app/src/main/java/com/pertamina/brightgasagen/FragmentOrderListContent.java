package com.pertamina.brightgasagen;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;

import com.pertamina.brightgasagen.model.OrderList;

import java.util.ArrayList;

/**
 * Created by gumelartejasukma on 11/23/16.
 */
public class FragmentOrderListContent extends Fragment {

    ArrayList<OrderList> datas;
    AdapterOrderList adapter;

    public FragmentOrderListContent(){

    }

    public void setDatas(ArrayList<OrderList> datas){
        this.datas = datas;
    }

    public void refreshDatas(ArrayList<OrderList> datas){
        this.datas = datas;
        if(adapter!=null){
            adapter.replaceAll(datas);
            adapter.notifyDataSetChanged();
        }
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        ListView rootView = (ListView) inflater.inflate(R.layout.fragment_order_list_content,container,false);
        adapter = new AdapterOrderList(getContext(),datas);
        rootView.setAdapter(adapter);
        adapter.notifyDataSetChanged();
        rootView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                OrderList data = adapter.getItem(position);
                FragmentHistoryDetail fragment = new FragmentHistoryDetail();
                fragment.setData(data.id);
                ((BaseActivity) getActivity()).changeFragment(fragment,false,true);
            }
        });
        return rootView;
    }

}
