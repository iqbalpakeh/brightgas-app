package com.pertamina.brightgasagen;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.pertamina.brightgasagen.model.Message;

import java.util.ArrayList;


public class AdapterMessage extends ArrayAdapter<Message> {

    String leftName = "";
    String rightName = "";

    ArrayList<Message> datas;
    Context context;

    public AdapterMessage(Context context, ArrayList<Message> datas, String leftName, String rightName) {
        super(context, R.layout.item_chat_left,datas);
        this.datas = datas;
        this.context = context;
        this.leftName = leftName;
        this.rightName = rightName;
    }


    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        Message data = getItem(position);
        View rootView;
        String nameVal;
        if(data.type == Message.TYPE_LEFT){
            rootView = LayoutInflater.from(context).inflate(R.layout.item_chat_left,parent,false);
            nameVal = leftName;
        }else{
            rootView = LayoutInflater.from(context).inflate(R.layout.item_chat_right,parent,false);
            nameVal = rightName;
        }

        TextView name = (TextView)rootView.findViewById(R.id.name);
        name.setText(nameVal);
        TextView time = (TextView)rootView.findViewById(R.id.mytime);
        time.setText(data.time);
        TextView content = (TextView)rootView.findViewById(R.id.content);
        content.setText(data.message);

        return rootView;
    }

    public void removeLast(){
        if(datas!=null && datas.size()>0){
            datas.remove(datas.size()-1);
            notifyDataSetChanged();
        }
    }

    @Override
    public Message getItem(int position) {
        return datas.get(position);
    }

    @Override
    public void clear() {
        datas.clear();
    }

    @Override
    public void add(Message object) {
        datas.add(object);
        notifyDataSetChanged();
    }

    @Override
    public int getCount() {
        return datas.size();
    }
}