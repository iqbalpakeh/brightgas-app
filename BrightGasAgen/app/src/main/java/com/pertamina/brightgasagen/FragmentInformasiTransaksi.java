package com.pertamina.brightgasagen;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.pertamina.brightgasagen.model.Order;

import java.util.ArrayList;

public class FragmentInformasiTransaksi extends Fragment {

    public static ArrayList<Order> datas;
    public static int counter = 300;
    private ViewGroup itemContainer;
    private TextView total,hargatotalbarang,ongkoskirim;
    private View rootView;

    public FragmentInformasiTransaksi(){

    }

    public void init(ArrayList<Order> datas){
        this.datas = datas;
    }

    @Override
    public void onResume() {
        super.onResume();
        ((BaseActivity) getActivity()).setTitle("Informasi Transaksi");
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        if(rootView==null){
            rootView = inflater.inflate(R.layout.fragment_informasi_transaksi,container,false);
            total = (TextView)rootView.findViewById(R.id.total);
            hargatotalbarang = (TextView)rootView.findViewById(R.id.hargatotalbarang);
            ongkoskirim = (TextView)rootView.findViewById(R.id.ongkoskirim);
            View agenPengirimContainer = rootView.findViewById(R.id.agenpengirimcontainer);
            View deliveryContainer = rootView.findViewById(R.id.deliverycontainer);
            View terimaKasih = rootView.findViewById(R.id.terimakasih);
            View topInfo = rootView.findViewById(R.id.topinfo);
            counter++;
            switch (counter){
                case 1:
                    agenPengirimContainer.setVisibility(View.GONE);
                    deliveryContainer.setVisibility(View.GONE);
                    break;
                case 2:
                    terimaKasih.setVisibility(View.GONE);
                    deliveryContainer.setVisibility(View.GONE);
                    TextView topText = (TextView)rootView.findViewById(R.id.toptext);
                    topText.setText("Pesanan sudah diteruskan ke agen");
                    break;
                case 3:
                    topInfo.setVisibility(View.GONE);
                    agenPengirimContainer.setVisibility(View.GONE);
                    View lacak = rootView.findViewById(R.id.lacak);
                    lacak.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            ((BaseActivity) getActivity()).changeFragment(new FragmentLacak(),false,true);
                        }
                    });
                    counter = 200;
                    break;
            }

            itemContainer = (ViewGroup)rootView.findViewById(R.id.itemcontainer);
            for(int i=0; i<datas.size(); i++){
                addOrder(datas.get(i));
            }

            recalculateTotal();
        }

        return rootView;
    }

    private void addOrder(final Order data){
        View rootView = LayoutInflater.from(getContext()).inflate(R.layout.item_informasi_transaksi,itemContainer,false);
        TextView name = (TextView)rootView.findViewById(R.id.name);
        name.setText(data.name);
        TextView quantity = (TextView)rootView.findViewById(R.id.quantity);
        quantity.setText(data.quantity+"");
        TextView price = (TextView)rootView.findViewById(R.id.price);
        price.setText(data.getPrice());
        itemContainer.addView(rootView);
    }

    public String getCalculatedPrice(long input){
        String millionString = "";
        String thousandString = "";
        String unitString = "";

        long million = input/1000000;
        if(million>0){
            millionString = million+"";
        }
        long thousand = (input%1000000)/1000;
        if(thousand>0){
            if(million>0 && thousand<100){
                thousandString = "0"+thousand;
            }else{
                thousandString = thousand+"";
            }

        }else{
            if(million != 0){
                thousandString = "000";
            }
        }
        long unit = (input%1000);
        if(unit>0){
            if(thousand>0 || million>0){
                if(unit<100){
                    unitString = "0"+unit;
                }else{
                    unitString = unit+"";
                }
            }else{
                unitString = unit+"";
            }
        }else{
            if(thousand>0 || million>0){
                unitString = "000";
            }
        }
        if(millionString.length()>0){
            return millionString+"."+thousandString+"."+unitString;
        }else if(thousandString.length()>0){
            return thousandString+"."+unitString;
        }else if(unitString.length()>0){
            return unitString;
        }else{
            return "0";
        }
    }

    public void recalculateTotal(){
        long totalHarga = 0;
        long totalOngkir = 0;
        long totalPrice = 0;
        for(int i=0; i<datas.size(); i++){
            totalHarga+=datas.get(i).getTotal();
            totalOngkir+=datas.get(i).orderPrice;
        }
        totalPrice = totalHarga+totalOngkir;
        total.setText(getCalculatedPrice(totalPrice));
        hargatotalbarang.setText(getCalculatedPrice(totalHarga));
        ongkoskirim.setText(getCalculatedPrice(totalOngkir));
    }

}
