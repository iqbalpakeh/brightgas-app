package com.pertamina.brightgasagen;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.pertamina.brightgasagen.model.PusatBantuan;

import java.util.ArrayList;


public class AdapterPusatBantuan extends ArrayAdapter<PusatBantuan> {


    ArrayList<PusatBantuan> datas;
    Context context;

    public AdapterPusatBantuan(Context context, ArrayList<PusatBantuan> datas) {
        super(context, R.layout.item_pusat_bantuan,datas);
        this.datas = datas;
        this.context = context;
    }


    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        PusatBantuan data = getItem(position);
        View rootView = null;
        TextView textView = null;
        switch (data.type){
            case PusatBantuan.TYPE_TITLE:
                rootView = LayoutInflater.from(context).inflate(R.layout.item_pusat_bantuan_title,parent,false);
                textView = (TextView)rootView;
                break;
            case PusatBantuan.TYPE_CONTENT:
                rootView = LayoutInflater.from(context).inflate(R.layout.item_pusat_bantuan,parent,false);
                textView = (TextView)rootView.findViewById(R.id.textview);
                break;
            case PusatBantuan.TYPE_DETAIL:
                rootView = LayoutInflater.from(context).inflate(R.layout.item_pusat_bantuan_content,parent,false);
                textView = (TextView)rootView.findViewById(R.id.mytitle);
                TextView answer = (TextView)rootView.findViewById(R.id.myanswer);
                answer.setText(data.additional);
                break;
        }
        textView.setText(data.value);
        return rootView;
    }

    public void removeLast(){
        if(datas!=null && datas.size()>0){
            datas.remove(datas.size()-1);
            notifyDataSetChanged();
        }
    }

    @Override
    public PusatBantuan getItem(int position) {
        return datas.get(position);
    }

    @Override
    public void clear() {
        datas.clear();
    }

    public void addDetail(PusatBantuan object){
        if(datas.get(0).type == PusatBantuan.TYPE_DETAIL){
            datas.remove(0);
        }
        datas.add(0,new PusatBantuan(object));
        notifyDataSetChanged();
    }

    @Override
    public void add(PusatBantuan object) {
        datas.add(object);
        notifyDataSetChanged();
    }

    @Override
    public int getCount() {
        return datas.size();
    }
}