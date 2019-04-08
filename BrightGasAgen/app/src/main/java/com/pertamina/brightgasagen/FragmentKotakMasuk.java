package com.pertamina.brightgasagen;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

public class FragmentKotakMasuk extends Fragment {

//    private TextView[] menus = new TextView[2];
//    private View menuLine;
//    private int colorOn = ContextCompat.getColor(BaseActivity.self,R.color.colorPrimary);
//    private int colorOff = ContextCompat.getColor(BaseActivity.self,R.color.beligrey);
//    private int selectedMenu = 0;
    public FragmentKotakMasuk(){

    }

    @Override
    public void onResume() {
        super.onResume();
        ((BaseActivity) getActivity()).setTitle("Kotak Masuk");
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_kotak_masuk,container,false);
//        menus[0] = (TextView)rootView.findViewById(R.id.menu1);
//        menus[1] = (TextView)rootView.findViewById(R.id.menu2);
//        menus[0].post(new Runnable() {
//            @Override
//            public void run() {
//                ViewGroup.LayoutParams params = menuLine.getLayoutParams();
//                params.width = menus[0].getWidth();
//                menuLine.setLayoutParams(params);
//            }
//        });
//        for(int i=0; i<menus.length; i++){
//            final int menusIndex = i;
//            menus[i].setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View v) {
//                    setSelectedMenu(menusIndex);
//                }
//            });
//        }
//        menuLine = rootView.findViewById(R.id.menuline);
        ((BaseActivity) getActivity()).changeFragment(R.id.fragmentcontent,new FragmentPesanMasuk(),true,false);
        return rootView;
    }

//    private void setSelectedMenu(int index){
//        if(selectedMenu!=index){
//            menus[selectedMenu].setTextColor(colorOff);
//            menuLine.startAnimation(BaseActivity.self.createTranslateAnimation(selectedMenu*menuLine.getWidth(),index*menuLine.getWidth(),0,0,500));
//            selectedMenu = index;
//            menus[selectedMenu].setTextColor(colorOn);
//            switch (selectedMenu){
//                case 0:
//                    BaseActivity.self.changeFragment(R.id.fragmentcontent,new FragmentPesanMasuk(),true,R.anim.slide_in_left,R.anim.slide_out_right);
//                    break;
//                case 1:
//                    BaseActivity.self.changeFragment(R.id.fragmentcontent,new FragmentPromo(),true,R.anim.slide_in_right,R.anim.slide_out_left);
//                    break;
//            }
//        }
//    }
}
