package com.pertamina.brightgasse;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.pertamina.brightgasse.model.SimpleOrder;

import java.util.ArrayList;

public class AdapterOrderList extends ArrayAdapter<SimpleOrder> {

    private static final String TAG = "adapter_order_list";

    private ArrayList<SimpleOrder> mDatas;

    private Context mContext;

    public AdapterOrderList(Context context, ArrayList<SimpleOrder> datas) {
        super(context, R.layout.item_simple_order, datas);
        this.mDatas = datas;
        this.mContext = context;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        View rootView = LayoutInflater.from(mContext).inflate(R.layout.item_simple_order, parent, false);
        View topContainer = rootView.findViewById(R.id.top_container);
        TextView invoiceNo = (TextView) rootView.findViewById(R.id.invoice_no);
        TextView countdown = (TextView) rootView.findViewById(R.id.countdown);
        TextView date = (TextView) rootView.findViewById(R.id.my_date);
        TextView time = (TextView) rootView.findViewById(R.id.my_time);
        TextView distance = (TextView) rootView.findViewById(R.id.distance);
        TextView total = (TextView) rootView.findViewById(R.id.total);

        SimpleOrder data = getItem(position);

        Log.d(TAG, "data.invoiceNo = " + data.invoiceNo);
        Log.d(TAG, "data.countdown = " + data.countdown);
        Log.d(TAG, "data.date = " + data.date);
        Log.d(TAG, "data.time = " + data.time);
        Log.d(TAG, "data.distance = " + data.distance);
        Log.d(TAG, "data.total = " + data.total);

        invoiceNo.setText(data.invoiceNo);
        countdown.setText(data.countdown);
        date.setText(data.date);
        time.setText(data.time);
        distance.setText(data.distance);
        total.setText(data.total);

        switch (data.status) {
            case 0:
                topContainer.setBackgroundResource(R.drawable.ic_rectangle_red_solidtop);
                break;
            case 1:
                topContainer.setBackgroundResource(R.drawable.ic_rectangle_violet_solidtop);
                break;
            case 2:
                topContainer.setBackgroundResource(R.drawable.ic_rectangle_green_solidtop);
                break;
        }

        return rootView;
    }

    public void replace(ArrayList<SimpleOrder> datas) {
        this.mDatas = datas;
    }

    public void removeLast() {
        if (mDatas != null && mDatas.size() > 0) {
            mDatas.remove(mDatas.size() - 1);
            notifyDataSetChanged();
        }
    }

    @Override
    public void remove(SimpleOrder object) {
        mDatas.remove(object);
    }

    @Override
    public SimpleOrder getItem(int position) {
        return mDatas.get(position);
    }

    @Override
    public void clear() {
        mDatas.clear();
    }

    @Override
    public void add(SimpleOrder object) {
        mDatas.add(object);
    }

    @Override
    public int getCount() {
        return mDatas.size();
    }
}