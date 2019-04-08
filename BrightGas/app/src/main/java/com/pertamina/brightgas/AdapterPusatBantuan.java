package com.pertamina.brightgas;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.pertamina.brightgas.model.PusatBantuan;

import java.util.ArrayList;

public class AdapterPusatBantuan extends ArrayAdapter<PusatBantuan> {

    private static final String TAG = "adapter_pusat_bantuan";

    private ArrayList<PusatBantuan> mDatas;
    private Context mContext;

    public AdapterPusatBantuan(Context context, ArrayList<PusatBantuan> datas) {
        super(context, R.layout.item_pusat_bantuan, datas);
        this.mDatas = datas;
        this.mContext = context;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        PusatBantuan data = getItem(position);
        View rootView = null;
        TextView textView = null;

        switch (data.type) {
            case PusatBantuan.TYPE_TITLE:
                rootView = LayoutInflater.from(mContext).inflate(R.layout.item_pusat_bantuan_title, parent, false);
                textView = (TextView) rootView;
                break;

            case PusatBantuan.TYPE_CONTENT:
                rootView = LayoutInflater.from(mContext).inflate(R.layout.item_pusat_bantuan, parent, false);
                textView = (TextView) rootView.findViewById(R.id.text_view);
                break;

            case PusatBantuan.TYPE_DETAIL:
                rootView = LayoutInflater.from(mContext).inflate(R.layout.item_pusat_bantuan_content, parent, false);
                textView = (TextView) rootView.findViewById(R.id.my_title);
                TextView answer = (TextView) rootView.findViewById(R.id.myanswer);
                answer.setText(data.additional);
                break;
        }

        textView.setText(data.value);

        return rootView;
    }

    public void removeLast() {
        if (mDatas != null && mDatas.size() > 0) {
            mDatas.remove(mDatas.size() - 1);
            notifyDataSetChanged();
        }
    }

    @Override
    public PusatBantuan getItem(int position) {
        return mDatas.get(position);
    }

    @Override
    public void clear() {
        mDatas.clear();
    }

    public void addDetail(PusatBantuan object) {
        if (mDatas.get(0).type == PusatBantuan.TYPE_DETAIL) {
            mDatas.remove(0);
        }
        mDatas.add(0, new PusatBantuan(object));
        notifyDataSetChanged();
    }

    @Override
    public void add(PusatBantuan object) {
        mDatas.add(object);
        notifyDataSetChanged();
    }

    @Override
    public int getCount() {
        return mDatas.size();
    }
}