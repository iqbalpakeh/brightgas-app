package com.pertamina.brightgasse;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.pertamina.brightgasse.model.PesanMasuk;

import java.util.ArrayList;


public class AdapterPesanMasuk extends ArrayAdapter<PesanMasuk> {

    public static final int TYPE_PENGGUNA = R.drawable.ic_rectangle_pengguna;
    public static final int TYPE_AGEN = R.drawable.ic_rectangle_agen;
    public static final int TYPE_PERTAMINA = R.drawable.ic_rectangle_pertamina;

    ArrayList<PesanMasuk> datas;
    Context context;

    public AdapterPesanMasuk(Context context, ArrayList<PesanMasuk> datas) {
        super(context, R.layout.item_pesan_masuk,datas);
        this.datas = datas;
        this.context = context;
    }


    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View rootView = LayoutInflater.from(context).inflate(R.layout.item_pesan_masuk,parent,false);
        PesanMasuk data = getItem(position);

        TextView name = (TextView)rootView.findViewById(R.id.name);

        switch (data.type){
            case TYPE_PENGGUNA:
                name.setText(data.name);
                name.setBackgroundResource(TYPE_PENGGUNA);
                break;
            case TYPE_AGEN:
                name.setText(data.name);
                name.setBackgroundResource(TYPE_AGEN);
                break;
            case TYPE_PERTAMINA:
                name.setText("PERTAMINA");
                name.setBackgroundResource(TYPE_PERTAMINA);
                break;
        }

        TextView date = (TextView)rootView.findViewById(R.id.date);
        date.setText(data.date);
        TextView time = (TextView)rootView.findViewById(R.id.my_time);
        time.setText(data.time);
        TextView title = (TextView)rootView.findViewById(R.id.mytitle);
        title.setText(data.title);
        TextView content = (TextView)rootView.findViewById(R.id.content);
        content.setText(data.content);

        return rootView;
    }

    public void removeLast(){
        if(datas!=null && datas.size()>0){
            datas.remove(datas.size()-1);
            notifyDataSetChanged();
        }
    }

    @Override
    public PesanMasuk getItem(int position) {
        return datas.get(position);
    }

    @Override
    public void clear() {
        datas.clear();
    }

    @Override
    public void add(PesanMasuk object) {
        datas.add(object);
    }

    @Override
    public int getCount() {
        return datas.size();
    }
}