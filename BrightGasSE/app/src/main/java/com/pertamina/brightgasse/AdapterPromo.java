package com.pertamina.brightgasse;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.pertamina.brightgasse.model.Promo;

import java.util.ArrayList;


public class AdapterPromo extends ArrayAdapter<Promo> {

    ArrayList<Promo> datas;
    Context context;

    public AdapterPromo(Context context, ArrayList<Promo> datas) {
        super(context, R.layout.item_promo,datas);
        this.datas = datas;
        this.context = context;
    }


    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View rootView = LayoutInflater.from(context).inflate(R.layout.item_promo,parent,false);
        Promo data = getItem(position);

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
    public Promo getItem(int position) {
        return datas.get(position);
    }

    @Override
    public void clear() {
        datas.clear();
    }

    @Override
    public void add(Promo object) {
        datas.add(object);
    }

    @Override
    public int getCount() {
        return datas.size();
    }
}