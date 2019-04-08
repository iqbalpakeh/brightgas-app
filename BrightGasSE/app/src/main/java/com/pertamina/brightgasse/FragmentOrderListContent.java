package com.pertamina.brightgasse;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;

import com.pertamina.brightgasse.model.SimpleOrder;

import java.util.ArrayList;

public class FragmentOrderListContent extends Fragment {

    private static final String TAG = "frag_order_list";

    private ArrayList<SimpleOrder> mDatas;

    private AdapterOrderList mAdapter;

    public void replaceDatas(ArrayList<SimpleOrder> datas) {
        this.mDatas = datas;
        mAdapter.replace(datas);
        mAdapter.notifyDataSetChanged();
    }

    public void setData(ArrayList<SimpleOrder> mDatas) {
        this.mDatas = mDatas;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        ListView rootView = (ListView) inflater.inflate(R.layout.fragment_order_list_content, container, false);
        mAdapter = new AdapterOrderList(getContext(), mDatas);
        rootView.setAdapter(mAdapter);
        mAdapter.notifyDataSetChanged();
        rootView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                SimpleOrder data = mAdapter.getItem(position);
                FragmentOrderBelumKirim fragment = new FragmentOrderBelumKirim();
                fragment.setData(data);
                ((BaseActivity) getActivity()).changeFragment(fragment, false, true);
            }
        });
        return rootView;
    }

}
