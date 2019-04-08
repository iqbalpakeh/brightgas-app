package com.pertamina.brightgasdriver;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

public class FragmentSuccessDelivery extends Fragment {

    @Override
    public void onResume() {
        super.onResume();
        getActivity().setTitle("Sukses");
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_success_delivery, container, false);
        return rootView;
    }
}
