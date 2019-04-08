package com.pertamina.brightgas;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.pertamina.brightgas.model.Promo;

import java.util.ArrayList;

public class AdapterPromo extends ArrayAdapter<Promo> {

    private static final String TAG = "adapter_promo";

    private ArrayList<Promo> mDatas;
    private Context mContext;

    public AdapterPromo(Context context, ArrayList<Promo> datas) {
        super(context, R.layout.item_promo, datas);
        this.mDatas = datas;
        this.mContext = context;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View rootView = LayoutInflater.from(mContext).inflate(R.layout.item_promo, parent, false);
        Promo data = getItem(position);

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
    public Promo getItem(int position) {
        return mDatas.get(position);
    }

    @Override
    public void clear() {
        mDatas.clear();
    }

    @Override
    public void add(Promo object) {
        mDatas.add(object);
    }

    @Override
    public int getCount() {
        return mDatas.size();
    }
}