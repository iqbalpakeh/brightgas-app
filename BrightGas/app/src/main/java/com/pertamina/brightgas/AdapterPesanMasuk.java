package com.pertamina.brightgas;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.pertamina.brightgas.model.PesanMasuk;

import java.util.ArrayList;

public class AdapterPesanMasuk extends ArrayAdapter<PesanMasuk> {

    private static final String TAG = "adapter_pesan_masuk";

    public static final int TYPE_PENGGUNA = R.drawable.ic_rectangle_pengguna;
    public static final int TYPE_AGEN = R.drawable.ic_rectangle_agen;
    public static final int TYPE_PERTAMINA = R.drawable.ic_rectangle_pertamina;

    private ArrayList<PesanMasuk> mDatas;
    private Context mCntext;

    public AdapterPesanMasuk(Context context, ArrayList<PesanMasuk> datas) {
        super(context, R.layout.item_pesan_masuk, datas);
        this.mDatas = datas;
        this.mCntext = context;
    }


    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        View rootView = LayoutInflater.from(mCntext).inflate(R.layout.item_pesan_masuk, parent, false);
        PesanMasuk data = getItem(position);

        TextView date = (TextView) rootView.findViewById(R.id.date);
        date.setText(data.date);
        TextView time = (TextView) rootView.findViewById(R.id.mytime);
        time.setText(data.time);
        TextView title = (TextView) rootView.findViewById(R.id.my_title);
        title.setText(data.title);
        TextView content = (TextView) rootView.findViewById(R.id.content);
        content.setText(data.content);

        return rootView;
    }

    public void removeLast() {
        if (mDatas != null && mDatas.size() > 0) {
            mDatas.remove(mDatas.size() - 1);
            notifyDataSetChanged();
        }
    }

    @Override
    public PesanMasuk getItem(int position) {
        return mDatas.get(position);
    }

    @Override
    public void clear() {
        mDatas.clear();
    }

    @Override
    public void add(PesanMasuk object) {
        mDatas.add(object);
    }

    @Override
    public int getCount() {
        return mDatas.size();
    }
}