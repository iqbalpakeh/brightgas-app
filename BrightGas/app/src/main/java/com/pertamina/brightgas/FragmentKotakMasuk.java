package com.pertamina.brightgas;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

public class FragmentKotakMasuk extends Fragment {

    private static final String TAG = "frag_kotak_masuk";

    @Override
    public void onResume() {
        super.onResume();
        getActivity().setTitle("Kotak Masuk");
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_kotak_masuk, container, false);
        ((BaseActivity)getActivity()).changeFragment(R.id.fragment_content, new FragmentPesanMasuk(), true, false);
        return rootView;
    }

}
