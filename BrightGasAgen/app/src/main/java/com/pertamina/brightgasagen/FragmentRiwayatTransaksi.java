package com.pertamina.brightgasagen;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;

import com.pertamina.brightgasagen.model.RiwayatTransaksi;

import java.util.ArrayList;

/**
 * Created by gumelartejasukma on 11/10/16.
 */
public class FragmentRiwayatTransaksi extends Fragment {

    AdapterRiwayatTransaksi adapter;
    ArrayList<RiwayatTransaksi> datas;

    public void setDatas(ArrayList<RiwayatTransaksi> datas){
        this.datas = datas;
    }


    public void replaceDatas(ArrayList<RiwayatTransaksi> datas){
        this.datas = datas;
        if(adapter!=null){
            adapter.replaceAll(datas);
            adapter.notifyDataSetChanged();
        }
    }

    public FragmentRiwayatTransaksi(){

    }

//    @Override
//    public void onResume() {
//        super.onResume();
//        BaseActivity.self.setTitle("Riwayat Transaksi");
//    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        ListView rootView = (ListView)inflater.inflate(R.layout.fragment_riwayat_transaksi,container,false);
        adapter = new AdapterRiwayatTransaksi(getContext(),datas);
        rootView.setAdapter(adapter);
        adapter.notifyDataSetChanged();
        rootView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                RiwayatTransaksi data = adapter.getItem(position);
                FragmentHistoryDetail fragment = new FragmentHistoryDetail();
                fragment.setData(data.id);
                ((BaseActivity) getActivity()).changeFragment(fragment,false,true);
            }
        });
        return rootView;
    }
}
