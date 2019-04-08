package com.pertamina.brightgas;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.pertamina.brightgas.model.RiwayatTransaksi;

import java.util.ArrayList;

public class AdapterRiwayatTransaksi extends ArrayAdapter<RiwayatTransaksi> {

    private static final String TAG = "adapter_riwayat_transaksi";

    private ArrayList<RiwayatTransaksi> mDatas;
    private Context mContext;

    public AdapterRiwayatTransaksi(Context context, ArrayList<RiwayatTransaksi> datas) {
        super(context, R.layout.item_riwayat_transaksi, datas);
        this.mDatas = datas;
        this.mContext = context;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        View rootView = LayoutInflater.from(mContext).inflate(R.layout.item_riwayat_transaksi, parent, false);
        RiwayatTransaksi data = getItem(position);

        TextView invoiceNr = (TextView) rootView.findViewById(R.id.invoicenr);
        invoiceNr.setText(data.invoiceNo);
        TextView myTime = (TextView) rootView.findViewById(R.id.mytime);
        myTime.setText(data.startDate + ", " + data.startTime + " - " + data.endTime);

        ImageView imageView = (ImageView) rootView.findViewById(R.id.image_view);
        View container = rootView.findViewById(R.id.container);
        container.setBackgroundResource(R.drawable.ic_rectangle_violet);

        switch (data.statusId) {
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
                container.setBackgroundResource(R.drawable.ic_rectangle_beligrey_solid);
                break;
        }
        return rootView;
    }

    public void removeLast() {
        if (mDatas != null && mDatas.size() > 0) {
            mDatas.remove(mDatas.size() - 1);
            notifyDataSetChanged();
        }
    }

    public void replace(ArrayList<RiwayatTransaksi> datas) {
        this.mDatas = datas;
    }

    @Override
    public void clear() {
        mDatas.clear();
    }

    @Override
    public void add(RiwayatTransaksi object) {
        mDatas.add(object);
    }

    @Override
    public int getCount() {
        return mDatas.size();
    }
}