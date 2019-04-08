package com.pertamina.brightgasagen;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

/**
 * Created by gumelartejasukma on 11/12/16.
 */
public class FragmentRincianRiwayatTransaksi extends Fragment {

    public FragmentRincianRiwayatTransaksi(){

    }

    @Override
    public void onResume() {
        super.onResume();
        ((BaseActivity)getActivity()).setTitle("Rincian Riwayat Transaksi");
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_rincian_riwayat_transaksi,container,false);
        return rootView;
    }
}
