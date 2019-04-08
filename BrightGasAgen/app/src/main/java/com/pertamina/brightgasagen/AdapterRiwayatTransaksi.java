package com.pertamina.brightgasagen;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.pertamina.brightgasagen.model.RiwayatTransaksi;

import java.util.ArrayList;


public class AdapterRiwayatTransaksi extends ArrayAdapter<RiwayatTransaksi> {

    ArrayList<RiwayatTransaksi> datas;
    Context context;

    public AdapterRiwayatTransaksi(Context context, ArrayList<RiwayatTransaksi> datas) {
        super(context, R.layout.item_riwayat_transaksi,datas);
        this.datas = datas;
        this.context = context;
    }


    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View rootView = LayoutInflater.from(context).inflate(R.layout.item_riwayat_transaksi,parent,false);
        RiwayatTransaksi data = getItem(position);

        TextView invoiceNr = (TextView)rootView.findViewById(R.id.invoicenr);
        invoiceNr.setText(data.invoiceNo);
        TextView myTime = (TextView)rootView.findViewById(R.id.mytime);
        myTime.setText(data.startDate+", "+data.startTime+" - "+data.endTime);

        ImageView imageView = (ImageView)rootView.findViewById(R.id.imageview);

        switch (data.statusId){
            case 0:
                imageView.setImageResource(R.drawable.ic_loading_process);
                break;
            case 1:
                imageView.setImageResource(R.drawable.ic_hourglass);
                break;
            case 2:
                imageView.setImageResource(R.drawable.ic_delivery_truck_2);
                break;
            case 3:
                imageView.setImageResource(R.drawable.ic_check_mark_2);
                break;
        }

        return rootView;
    }

    public void removeLast(){
        if(datas!=null && datas.size()>0){
            datas.remove(datas.size()-1);
            notifyDataSetChanged();
        }
    }

    public void replaceAll(ArrayList<RiwayatTransaksi> datas){
        this.datas = datas;
    }

    @Override
    public void clear() {
        datas.clear();
    }

    @Override
    public void add(RiwayatTransaksi object) {
        datas.add(object);
    }

    @Override
    public int getCount() {
        return datas.size();
    }
}