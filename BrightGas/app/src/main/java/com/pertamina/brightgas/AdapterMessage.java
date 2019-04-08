package com.pertamina.brightgas;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.pertamina.brightgas.model.Message;

import java.util.ArrayList;


public class AdapterMessage extends ArrayAdapter<Message> {

    private String mLeftName;
    private String mRightName;

    private ArrayList<Message> mDatas;
    private Context mContext;

    public AdapterMessage(Context context, ArrayList<Message> datas, String leftName, String rightName) {
        super(context, R.layout.item_chat_left, datas);
        this.mDatas = datas;
        this.mContext = context;
        this.mLeftName = leftName;
        this.mRightName = rightName;
    }


    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        Message data = getItem(position);
        View rootView;
        String nameVal;
        if (data.type == Message.TYPE_LEFT) {
            rootView = LayoutInflater.from(mContext).inflate(R.layout.item_chat_left, parent, false);
            nameVal = mLeftName;
        } else {
            rootView = LayoutInflater.from(mContext).inflate(R.layout.item_chat_right, parent, false);
            nameVal = mRightName;
        }

        TextView name = (TextView) rootView.findViewById(R.id.name);
        name.setText(nameVal);
        TextView time = (TextView) rootView.findViewById(R.id.mytime);
        time.setText(data.time);
        TextView content = (TextView) rootView.findViewById(R.id.content);
        content.setText(data.message);

        return rootView;
    }

    public void removeLast() {
        if (mDatas != null && mDatas.size() > 0) {
            mDatas.remove(mDatas.size() - 1);
            notifyDataSetChanged();
        }
    }

    @Override
    public Message getItem(int position) {
        return mDatas.get(position);
    }

    @Override
    public void clear() {
        mDatas.clear();
    }

    @Override
    public void add(Message object) {
        mDatas.add(object);
        notifyDataSetChanged();
    }

    @Override
    public int getCount() {
        return mDatas.size();
    }
}