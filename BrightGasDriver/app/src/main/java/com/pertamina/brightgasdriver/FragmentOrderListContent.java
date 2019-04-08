package com.pertamina.brightgasdriver;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import com.pertamina.brightgasdriver.model.OrderList;

import java.util.ArrayList;

public class FragmentOrderListContent extends Fragment {

    ArrayList<OrderList> datas;

    public void setDatas(ArrayList<OrderList> datas){
        this.datas = datas;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        ListView rootView = (ListView) inflater.inflate(R.layout.fragment_order_list_content,container,false);
        AdapterOrderList adapter = new AdapterOrderList(getContext(),datas);
        rootView.setAdapter(adapter);
        adapter.notifyDataSetChanged();
        return rootView;
    }

}
