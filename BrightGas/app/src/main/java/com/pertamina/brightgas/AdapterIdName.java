package com.pertamina.brightgas;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Filter;
import android.widget.TextView;

import com.pertamina.brightgas.model.IdName;

import java.util.ArrayList;

public class AdapterIdName extends ArrayAdapter<IdName> {

    private static final String TAG = "adapter_id_name";

    ArrayList<IdName> mDatas;
    ArrayList<IdName> mFilteredDatas;
    Context mContext;

    Filter filter = new Filter() {
        @Override
        protected FilterResults performFiltering(CharSequence constraint) {
            FilterResults filterResults = new FilterResults();
            ArrayList<IdName> tempList = new ArrayList<IdName>();
            //constraint is the result from text you want to filter against.
            //objects is your data set you will filter from
            if (constraint != null && mDatas != null) {
                int length = mDatas.size();
                int i = 0;
                while (i < length) {
                    IdName item = mDatas.get(i);
                    if (item.name.toLowerCase().contains(constraint)) {
                        tempList.add(item);
                    }
                    i++;
                }
                //following two lines is very important
                //as publish result can only take FilterResults objects
                filterResults.values = tempList;
                filterResults.count = tempList.size();
            }
            return filterResults;
        }

        @Override
        protected void publishResults(CharSequence constraint, FilterResults results) {
            mFilteredDatas = (ArrayList<IdName>) results.values;
            if (results.count > 0) {
                notifyDataSetChanged();
            } else {
                notifyDataSetInvalidated();
            }
        }
    };

    public AdapterIdName(Context context, ArrayList<IdName> datas) {
        super(context, R.layout.item_company, datas);
        this.mDatas = datas;
        this.mFilteredDatas = this.mDatas;
        this.mContext = context;
    }

    @Override
    public View getDropDownView(int position, View convertView, ViewGroup parent) {
        int resId = R.layout.item_company;
        View rootView = LayoutInflater.from(mContext).inflate(resId, parent, false);
        TextView name = (TextView) rootView;
        name.setText(mFilteredDatas.get(position).name);
        return rootView;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        int resId = R.layout.item_company;
        View rootView = LayoutInflater.from(mContext).inflate(resId, parent, false);
        TextView name = (TextView) rootView;
        name.setText(mFilteredDatas.get(position).name);
        return rootView;
    }

    public void removeLast() {
        if (mDatas != null && mDatas.size() > 0) {
            mDatas.remove(mDatas.size() - 1);
            notifyDataSetChanged();
        }
    }

    @Override
    public IdName getItem(int position) {
        if (mFilteredDatas == null) {
            mFilteredDatas = new ArrayList<>(mDatas);
        }
        return mFilteredDatas.get(position);
    }

    @Override
    public Filter getFilter() {
        return filter;
    }

    @Override
    public void clear() {
        mDatas.clear();
        mFilteredDatas.clear();
    }

    @Override
    public void add(IdName object) {
        mDatas.add(object);
        mFilteredDatas = new ArrayList<>(mDatas);
    }

    @Override
    public int getCount() {
        if (mFilteredDatas == null) {
            if (mDatas == null)
                return 0;
            else {
                mFilteredDatas = new ArrayList<>(mDatas);
            }
        }
        return mFilteredDatas.size();
    }

    public void query(String query) {
        Log.d(TAG, "QUERY : " + query);
        if (query.equals(null) || query.length() == 0) {
            mFilteredDatas = new ArrayList<>(mDatas);
        } else {
            mFilteredDatas.clear();
            for (IdName data : mDatas) {
                if (data.contain(query)) {
                    mFilteredDatas.add(data);
                    Log.d(TAG, "CONTAIN : " + query);
                } else {
                    Log.d(TAG, "NOT CONTAIN : " + query);
                }

            }
        }
        Log.d(TAG, "LENGTH : " + mFilteredDatas.size());
        notifyDataSetChanged();
    }

}