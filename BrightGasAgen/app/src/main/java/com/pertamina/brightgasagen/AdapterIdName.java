package com.pertamina.brightgasagen;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Filter;
import android.widget.TextView;

import com.pertamina.brightgasagen.model.IdName;

import java.util.ArrayList;


public class AdapterIdName extends ArrayAdapter<IdName> {

    ArrayList<IdName> datas;
    ArrayList<IdName> filteredDatas;
    Context context;

    Filter filter = new Filter() {
        @Override
        protected FilterResults performFiltering(CharSequence constraint) {
            FilterResults filterResults = new FilterResults();
            ArrayList<IdName> tempList=new ArrayList<IdName>();
            //constraint is the result from text you want to filter against.
            //objects is your data set you will filter from
            if(constraint != null && datas!=null) {
                int length=datas.size();
                int i=0;
                while(i<length){
                    IdName item=datas.get(i);
                    if(item.name.toLowerCase().contains(constraint)){
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
            filteredDatas = (ArrayList<IdName>) results.values;
            if (results.count > 0) {
                notifyDataSetChanged();
            } else {
                notifyDataSetInvalidated();
            }
        }
    };

    public AdapterIdName(Context context, ArrayList<IdName> datas) {
        super(context, R.layout.item_company,datas);
        this.datas = datas;
        this.filteredDatas = this.datas;
        this.context = context;
    }

    @Override
    public View getDropDownView(int position, View convertView, ViewGroup parent) {
        int resId = R.layout.item_company;
        View rootView = LayoutInflater.from(context).inflate(resId,parent,false);
        TextView name = (TextView)rootView;
        name.setText(filteredDatas.get(position).name);
        return rootView;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        int resId = R.layout.item_company;
        View rootView = LayoutInflater.from(context).inflate(resId,parent,false);
        TextView name = (TextView)rootView;
        name.setText(filteredDatas.get(position).name);
        return rootView;
    }

    public void removeLast(){
        if(datas!=null && datas.size()>0){
            datas.remove(datas.size()-1);
            notifyDataSetChanged();
        }
    }

    @Override
    public IdName getItem(int position) {
        if(filteredDatas == null){
            filteredDatas = datas;
        }
        return filteredDatas.get(position);
    }

    @Override
    public Filter getFilter() {
        return filter;
    }

    @Override
    public void clear() {
        datas.clear();
        filteredDatas.clear();
    }

    @Override
    public void add(IdName object) {
        datas.add(object);
        filteredDatas = datas;
    }

    @Override
    public int getCount() {
        if(filteredDatas==null){
            if(datas==null)
                return 0;
            else {
                filteredDatas = datas;
            }
        }
        return filteredDatas.size();
    }
}