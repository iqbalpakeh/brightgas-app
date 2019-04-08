package com.pertamina.brightgasse;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

public class FragmentKotakMasuk extends Fragment {

    private static final String TAG = "frag_kotak_masuk";

    private TextView[] mMenus = new TextView[2];

    private View mMenuLine;

    private int mColorOn;

    private int mColorOff;

    private int mSelectedMenu = 0;

    @Override
    public void onResume() {
        super.onResume();
        getActivity().setTitle("Kotak Masuk");
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        mColorOn = ContextCompat.getColor(getActivity(), R.color.colorPrimary);
        mColorOff = ContextCompat.getColor(getActivity(), R.color.beligrey);

        View rootView = inflater.inflate(R.layout.fragment_kotak_masuk, container, false);

        mMenus[0] = (TextView) rootView.findViewById(R.id.menu_one);
        mMenus[1] = (TextView) rootView.findViewById(R.id.menu_two);
        mMenus[0].post(new Runnable() {
            @Override
            public void run() {
                ViewGroup.LayoutParams params = mMenuLine.getLayoutParams();
                params.width = mMenus[0].getWidth();
                mMenuLine.setLayoutParams(params);
            }
        });

        for (int i = 0; i < mMenus.length; i++) {
            final int menusIndex = i;
            mMenus[i].setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    setSelectedMenu(menusIndex);
                }
            });
        }

        mMenuLine = rootView.findViewById(R.id.menu_line);
        ((BaseActivity) getActivity()).changeFragment(R.id.fragment_content, new FragmentPesanMasuk(), true, false);
        return rootView;
    }

    private void setSelectedMenu(int index) {
        if (mSelectedMenu != index) {
            mMenus[mSelectedMenu].setTextColor(mColorOff);
            mMenuLine.startAnimation(((BaseActivity) getActivity()).createTranslateAnimation(mSelectedMenu * mMenuLine.getWidth(), index * mMenuLine.getWidth(), 0, 0, 500));
            mSelectedMenu = index;
            mMenus[mSelectedMenu].setTextColor(mColorOn);

            switch (mSelectedMenu) {
                case 0:
                    ((BaseActivity) getActivity()).changeFragment(
                            R.id.fragment_content,
                            new FragmentPesanMasuk(),
                            true,
                            R.anim.slide_in_left,
                            R.anim.slide_out_right
                    );
                    break;

                case 1:
                    ((BaseActivity) getActivity()).changeFragment(
                            R.id.fragment_content,
                            new FragmentPromo(),
                            true,
                            R.anim.slide_in_right,
                            R.anim.slide_out_left
                    );
                    break;
            }
        }
    }
}
