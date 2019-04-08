package com.pertamina.brightgas;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;

import com.pertamina.brightgas.model.PesanMasuk;

import java.util.ArrayList;

public class FragmentPesanMasuk extends Fragment {

    private static final String TAG = "frag_pesan_masuk";

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.fragment_pesan_masuk, container, false);

        ListView listView = (ListView) rootView.findViewById(R.id.list_view);

        ArrayList<PesanMasuk> datas = new ArrayList<>();

        datas.add(new PesanMasuk("pt berlian abadi", AdapterPesanMasuk.TYPE_AGEN, "7 november 2016", "13:56 WIB", "judul pesan", "Lorem ipsum dolor sit amet, consectetur adipiscing elit, sed do eiusmod tempor incididunt ut labore et dolore magna aliqua"));
        datas.add(new PesanMasuk("pt dwi citra prameswati", AdapterPesanMasuk.TYPE_AGEN, "5 november 2016", "10:11 WIB", "judul pesan", "Lorem ipsum dolor sit amet, consectetur adipiscing elit, sed do eiusmod tempor incididunt ut labore et dolore magna aliqua"));
        datas.add(new PesanMasuk("admin", AdapterPesanMasuk.TYPE_PERTAMINA, "1 november 2016", "16:01 WIB", "judul pesan", "Lorem ipsum dolor sit amet, consectetur adipiscing elit, sed do eiusmod tempor incididunt ut labore et dolore magna aliqua"));
        datas.add(new PesanMasuk("admin", AdapterPesanMasuk.TYPE_PERTAMINA, "25 OKTOBER 2016", "8:08 WIB", "judul pesan", "Lorem ipsum dolor sit amet, consectetur adipiscing elit, sed do eiusmod tempor incididunt ut labore et dolore magna aliqua"));
        datas.add(new PesanMasuk("pt dwi citra prameswati", AdapterPesanMasuk.TYPE_AGEN, "15 oktober 2016", "12:00 WIB", "judul pesan", "Lorem ipsum dolor sit amet, consectetur adipiscing elit, sed do eiusmod tempor incididunt ut labore et dolore magna aliqua"));
        datas.add(new PesanMasuk("pt berlian abadi", AdapterPesanMasuk.TYPE_AGEN, "7 oktober 2016", "17:07 WIB", "judul pesan", "Lorem ipsum dolor sit amet, consectetur adipiscing elit, sed do eiusmod tempor incididunt ut labore et dolore magna aliqua"));
        datas.add(new PesanMasuk("admin", AdapterPesanMasuk.TYPE_PERTAMINA, "1 oktober 2016", "14:19 WIB", "judul pesan", "Lorem ipsum dolor sit amet, consectetur adipiscing elit, sed do eiusmod tempor incididunt ut labore et dolore magna aliqua"));
        datas.add(new PesanMasuk("pt dwi ditra prameswati", AdapterPesanMasuk.TYPE_AGEN, "15 september 2016", "20:14 WIB", "judul pesan", "Lorem ipsum dolor sit amet, consectetur adipiscing elit, sed do eiusmod tempor incididunt ut labore et dolore magna aliqua"));

        final AdapterPesanMasuk adapter = new AdapterPesanMasuk(getContext(), datas);
        listView.setAdapter(adapter);
        adapter.notifyDataSetChanged();
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                PesanMasuk data = adapter.getItem(position);
                FragmentMessage fragment = new FragmentMessage();
                fragment.init(data);
                ((BaseActivity)getActivity()).changeFragment(fragment, false, true);
            }
        });
        return rootView;
    }
}
