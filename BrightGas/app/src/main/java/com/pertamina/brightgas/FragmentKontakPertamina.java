package com.pertamina.brightgas;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

public class FragmentKontakPertamina extends Fragment {

    private static final String TAG = "fragment_kontak_pertamina";

    @Override
    public void onResume() {
        super.onResume();
        getActivity().setTitle("Hubungi Kami");
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_kontak_pertamina, container, false);
        return rootView;
    }
}
