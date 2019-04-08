package com.pertamina.brightgasagen;

import android.content.Context;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.pertamina.brightgasagen.model.Driver;

import java.util.ArrayList;


public class AdapterDriver extends ArrayAdapter<Driver> {

    ArrayList<Driver> datas;
    Context context;
    AlertDialog chooseDriverDialog;

    public AdapterDriver(Context context, ArrayList<Driver> datas) {
        super(context, R.layout.item_driver,datas);
        this.datas = datas;
        this.context = context;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View rootView = LayoutInflater.from(context).inflate(R.layout.item_driver,parent,false);
        final Driver data = getItem(position);
        TextView name = (TextView)rootView.findViewById(R.id.name);
        name.setText(data.name);
        TextView phoneNumber = (TextView)rootView.findViewById(R.id.phonenumber);
        phoneNumber.setText(data.phoneNumber);
//        View delete = rootView.findViewById(R.id.delete);
//        delete.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                remove(data);
//                notifyDataSetChanged();
//            }
//        });
        return rootView;
    }

    public void removeLast(){
        if(datas!=null && datas.size()>0){
            datas.remove(datas.size()-1);
            notifyDataSetChanged();
        }
    }

    @Override
    public void remove(Driver object) {
        datas.remove(object);
    }

    @Override
    public Driver getItem(int position) {
        return datas.get(position);
    }

    @Override
    public void clear() {
        datas.clear();
    }

    @Override
    public void add(Driver object) {
        datas.add(object);
    }

    @Override
    public int getCount() {
        return datas.size();
    }
}