package com.pertamina.brightgasagen;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

/**
 * Created by gumelartejasukma on 11/10/16.
 */
public class FragmentProfil extends Fragment {

    public FragmentProfil(){

    }

    @Override
    public void onResume() {
        super.onResume();
        ((BaseActivity)getActivity()).setTitle("Profil");
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_profil,container,false);
        View tambahAlamat = rootView.findViewById(R.id.tambahalamat);
        tambahAlamat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ((BaseActivity)getActivity()).changeFragment(new FragmentTambahAlamat(),false,true);
            }
        });
        return rootView;
    }
}
