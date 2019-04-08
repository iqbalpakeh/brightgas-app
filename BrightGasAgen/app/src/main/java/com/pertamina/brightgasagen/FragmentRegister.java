package com.pertamina.brightgasagen;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

/**
 * Created by gumelartejasukma on 11/9/16.
 */
public class FragmentRegister extends Fragment {

    public FragmentRegister(){

    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_register,container,false);
        ((BaseActivity)getActivity()).changeFragment(R.id.fragmentstep,new FragmentRegisterStep1(),true,false);
        return rootView;
    }
}
