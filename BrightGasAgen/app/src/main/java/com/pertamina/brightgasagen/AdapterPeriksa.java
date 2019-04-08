package com.pertamina.brightgasagen;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.pertamina.brightgasagen.model.Order;

import java.util.ArrayList;


public class AdapterPeriksa extends ArrayAdapter<Order> {

    ArrayList<Order> datas;
    Context context;
    InterfaceAdapterPeriksa interfaceAdapterPeriksa;

    public AdapterPeriksa(Context context, ArrayList<Order> datas, InterfaceAdapterPeriksa interfaceAdapterPeriksa) {
        super(context, R.layout.item_periksa,datas);
        this.interfaceAdapterPeriksa = interfaceAdapterPeriksa;
        this.datas = datas;
        this.context = context;
    }


    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View rootView = LayoutInflater.from(context).inflate(R.layout.item_periksa,parent,false);
        final Order data = getItem(position);
        ImageView imageView = (ImageView)rootView.findViewById(R.id.imageview);
        imageView.setImageResource(data.drawableResId);
        TextView name = (TextView)rootView.findViewById(R.id.name);
        name.setText(data.name);
        TextView quantity = (TextView)rootView.findViewById(R.id.quantity);
        quantity.setText(data.quantity+"");
        TextView price = (TextView)rootView.findViewById(R.id.price);
        price.setText(data.getPrice());
        View plusAction = rootView.findViewById(R.id.plusaction);
        plusAction.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                data.plus();
                recalculateTotal();
            }
        });
        View minusAction = rootView.findViewById(R.id.minusaction);
        minusAction.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                data.minus();
                recalculateTotal();
            }
        });
        return rootView;
    }

    public void recalculateTotal(){
        long total = 0;
        for(int i=0; i<datas.size(); i++){
            total+=datas.get(i).getTotal();
        }
        String millionString = "";
        String thousandString = "";
        String unitString = "";

        long million = total/1000000;
        if(million>0){
            millionString = million+"";
        }
        long thousand = (total%1000000)/1000;
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
        long unit = (total%1000);
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
            interfaceAdapterPeriksa.setTotal(millionString+"."+thousandString+"."+unitString);
        }else if(thousandString.length()>0){
            interfaceAdapterPeriksa.setTotal(thousandString+"."+unitString);
        }else if(unitString.length()>0){
            interfaceAdapterPeriksa.setTotal(unitString);
        }else{
            interfaceAdapterPeriksa.setTotal("0");
        }
        notifyDataSetChanged();
    }

    public void removeLast(){
        if(datas!=null && datas.size()>0){
            datas.remove(datas.size()-1);
            notifyDataSetChanged();
        }
    }

    @Override
    public void clear() {
        datas.clear();
    }

    @Override
    public void add(Order object) {
        datas.add(object);
    }

    @Override
    public int getCount() {
        return datas.size();
    }
}