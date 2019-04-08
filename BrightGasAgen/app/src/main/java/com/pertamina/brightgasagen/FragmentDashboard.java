package com.pertamina.brightgasagen;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import com.pertamina.brightgasagen.model.Dashboard;

import java.util.ArrayList;

public class FragmentDashboard extends Fragment {

    public FragmentDashboard(){

    }

    @Override
    public void onResume() {
        super.onResume();
        ((BaseActivity) getActivity()).setTitle("Beranda");
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        ListView rootView = (ListView) inflater.inflate(R.layout.fragment_dashboard,container,false);
        ArrayList<Dashboard> datas = new ArrayList<>();
        datas.add(new Dashboard("Agus","inv/00123/291016","Kamis 3 November 2016","Jam 11.00 - 14.00"));
        datas.add(new Dashboard("Ahmad","inv/00123/351016","Kamis 3 November 2016","Jam 11.00 - 14.00"));
        datas.add(new Dashboard("Asep","inv/00123/351016","Kamis 3 November 2016","Jam 11.00 - 14.00"));
        AdapterDashboard adapter = new AdapterDashboard(getContext(),datas);
        rootView.setAdapter(adapter);
        adapter.notifyDataSetChanged();
        return rootView;
    }
}
