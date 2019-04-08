package com.pertamina.brightgas;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;

import com.pertamina.brightgas.model.RiwayatTransaksi;

import java.util.ArrayList;

public class FragmentListTransaksi extends Fragment {

    private static final String TAG = "frag_list_transaksi";

    private AdapterRiwayatTransaksi mAdapter;
    private ArrayList<RiwayatTransaksi> mDatas;

    @Override
    public void onResume() {
        super.onResume();
        getActivity().setTitle("Transaksi");
    }

    public void setData(ArrayList<RiwayatTransaksi> datas) {
        this.mDatas = datas;
        if (mAdapter != null) {
            mAdapter.replace(datas);
            mAdapter.notifyDataSetChanged();
        }
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        ListView rootView = (ListView) inflater.inflate(R.layout.fragment_list_transaksi, container, false);
        if (mDatas == null) mDatas = new ArrayList<>();
        mAdapter = new AdapterRiwayatTransaksi(getContext(), mDatas);
        rootView.setAdapter(mAdapter);
        mAdapter.notifyDataSetChanged();
        rootView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                RiwayatTransaksi data = mAdapter.getItem(position);
                switch (data.statusId) {
                    case RiwayatTransaksi.STATUS_DITERIMA:
                        FragmentRincianRiwayatTransaksi fragmentRincianRiwayatTransaksi = new FragmentRincianRiwayatTransaksi();
                        fragmentRincianRiwayatTransaksi.setData(data);
                        ((BaseActivity) getActivity()).changeFragment(fragmentRincianRiwayatTransaksi, false, true);
                        break;
                    default:
                        FragmentInformasiTransaksi fragmentInformasiTransaksi = new FragmentInformasiTransaksi();
                        fragmentInformasiTransaksi.setData(data);
                        ((BaseActivity) getActivity()).changeFragment(fragmentInformasiTransaksi, false, true);
                        break;
                }
            }
        });
        return rootView;
    }
}
