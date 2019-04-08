package com.pertamina.brightgas;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

public class FragmentRegister extends Fragment {

    private static final String TAG = "frag_register";

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.fragment_register, container, false);

        ((BaseActivity)getActivity()).changeFragment(
                R.id.fragment_step,
                new FragmentRegisterStepOne(),
                true,
                false
        );

        return rootView;
    }
}
