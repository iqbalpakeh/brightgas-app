package com.pertamina.brightgas;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

public class FragmentNoTransaction extends Fragment {

    private static final String TAG = "frag_no_transaction";

    @Override
    public void onResume() {
        super.onResume();
        getActivity().setTitle("Transaksi");
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_no_transaction, container, false);
        return rootView;
    }
}
