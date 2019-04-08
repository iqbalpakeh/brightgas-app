package com.pertamina.brightgasse;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import com.pertamina.brightgasse.model.Promo;

import java.util.ArrayList;

public class FragmentPromo extends Fragment {

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.fragment_promo,container,false);
        ListView listView = (ListView)rootView.findViewById(R.id.list_view);

        ArrayList<Promo> datas = new ArrayList<>();
        datas.add(new Promo("7 november 2016", "13:56 WIB", "Promo ongkir 12 kg", "Masukan kode BG12 untuk mendapatkan ongkos kirim setiap pembelian tabung baru Bright Gas 12 Kg (Tidak berlaku kelipatan). Berlaku sampai dengan 12 Desember 2016"));
        datas.add(new Promo("7 november 2016", "13:56 WIB", "Promo ongkir 5.5 kg", "Masukan kode BG55 untuk mendapatkan ongkos kirim setiap pembelian tabung baru Bright Gas 5.5 Kg (Tidak berlaku kelipatan). Berlaku sampai dengan 12 Desember 2016"));
        datas.add(new Promo("7 november 2016", "13:56 WIB", "Promo ongkir 220 gram", "Masukan kode F220 untuk mendapatkan ongkos kirim setiap pembelian tabung baru Bright Gas 220 gram (Tidak berlaku kelipatan). Berlaku sampai dengan 12 Desember 2016"));

        final AdapterPromo adapter = new AdapterPromo(getContext(),datas);
        listView.setAdapter(adapter);
        adapter.notifyDataSetChanged();
        return rootView;
    }
}
