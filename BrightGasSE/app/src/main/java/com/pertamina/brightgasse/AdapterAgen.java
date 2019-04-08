package com.pertamina.brightgasse;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.RatingBar;
import android.widget.TextView;

import com.pertamina.brightgasse.model.Agen;

import java.util.ArrayList;

public class AdapterAgen extends ArrayAdapter<Agen> {

    private static final String TAG = "adapter_agen";

    private ArrayList<Agen> mDatas;

    private Context mContext;

    public AdapterAgen(Context context, ArrayList<Agen> datas) {
        super(context, R.layout.item_agen, datas);
        this.mDatas = datas;
        this.mContext = context;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        View rootView = LayoutInflater.from(mContext).inflate(R.layout.item_agen, parent, false);
        final Agen data = getItem(position);

        TextView name = (TextView) rootView.findViewById(R.id.name);
        name.setText(data.name);

        RatingBar ratingBar = (RatingBar) rootView.findViewById(R.id.rating_bar);
        ratingBar.setRating(data.rating);

        return rootView;
    }

    public void removeLast() {
        if (mDatas != null && mDatas.size() > 0) {
            mDatas.remove(mDatas.size() - 1);
            notifyDataSetChanged();
        }
    }

    @Override
    public void remove(Agen object) {
        mDatas.remove(object);
    }

    @Override
    public Agen getItem(int position) {
        return mDatas.get(position);
    }

    @Override
    public void clear() {
        mDatas.clear();
    }

    @Override
    public void add(Agen object) {
        mDatas.add(object);
    }

    @Override
    public int getCount() {
        return mDatas.size();
    }
}