package com.pertamina.brightgasse;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

public class FragmentKontakPertamina extends Fragment {

    public FragmentKontakPertamina(){

    }

    @Override
    public void onResume() {
        super.onResume();
        getActivity().setTitle("Hubungi Kami");
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_kontak_pertamina,container,false);
        return rootView;
    }
}
