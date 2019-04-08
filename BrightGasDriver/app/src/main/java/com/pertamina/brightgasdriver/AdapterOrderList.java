package com.pertamina.brightgasdriver;

import android.content.Context;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.pertamina.brightgasdriver.model.OrderList;

import org.apache.http.message.BasicNameValuePair;
import org.json.JSONObject;

import java.util.ArrayList;

public class AdapterOrderList extends ArrayAdapter<OrderList> implements RequestLoaderInterface {

    public static final int TAKE_ORDER = 0;

    ArrayList<OrderList> datas;
    Context context;
    AlertDialog chooseDriverDialog;
    OrderList takenData;

    public AdapterOrderList(Context context, ArrayList<OrderList> datas) {
        super(context, R.layout.item_simple_order, datas);
        this.datas = datas;
        this.context = context;
    }


    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View rootView = LayoutInflater.from(context).inflate(R.layout.item_simple_order, parent, false);
        View topContainer = rootView.findViewById(R.id.topcontainer);
        TextView invoiceNo = (TextView) rootView.findViewById(R.id.invoice_no);
        TextView countdown = (TextView) rootView.findViewById(R.id.countdown);
        TextView date = (TextView) rootView.findViewById(R.id.mydate);
        TextView time = (TextView) rootView.findViewById(R.id.my_time);
        TextView distance = (TextView) rootView.findViewById(R.id.distance);
        TextView item = (TextView) rootView.findViewById(R.id.item);
        TextView total = (TextView) rootView.findViewById(R.id.total);

        OrderList data = getItem(position);

        invoiceNo.setText(data.invoiceNr);
        countdown.setText("1 menit yang lalu");
        date.setText(data.date);
        time.setText(data.startTime + " - " + data.endTime);
        distance.setText(data.distance);
        item.setText(data.datas.length + "");
        total.setText(((BaseActivity) context).getCalculatedPrice(data.total));
        switch (data.type) {
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

    private void takeOrder(OrderList data) {
        ((BaseActivity) context).showLoading(true, "Ambil order");
        takenData = data;
        new RequestLoader(this).loadRequest(TAKE_ORDER, new BasicNameValuePair[]{
                new BasicNameValuePair("id", data.id)
        }, "transaction", "delivery", true);
    }

    private void chooseDriver(final OrderList data) {
        ViewGroup rootView = (ViewGroup) LayoutInflater.from(getContext()).inflate(R.layout.fragment_choose_driver, null, false);
        for (int i = 0; i < 2; i++) {
            TextView textView = (TextView) LayoutInflater.from(getContext()).inflate(R.layout.item_choose_driver, rootView, false);
            textView.setText("driver " + i);
            textView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    chooseDriverDialog.dismiss();
                    remove(data);
                    notifyDataSetChanged();
                }
            });
            rootView.addView(textView);
        }
        chooseDriverDialog = ((BaseActivity) context).showDialog("Available Driver", rootView, "", "BATAL", null, null);
        chooseDriverDialog.getButton(AlertDialog.BUTTON_NEGATIVE).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                chooseDriverDialog.dismiss();
            }
        });
    }

    public String getCalculatedPrice(long input) {
        String millionString = "";
        String thousandString = "";
        String unitString = "";

        long million = input / 1000000;
        if (million > 0) {
            millionString = million + "";
        }
        long thousand = (input % 1000000) / 1000;
        if (thousand > 0) {
            if (million > 0 && thousand < 100) {
                thousandString = "0" + thousand;
            } else {
                thousandString = thousand + "";
            }

        } else {
            if (million != 0) {
                thousandString = "000";
            }
        }
        long unit = (input % 1000);
        if (unit > 0) {
            if (thousand > 0 || million > 0) {
                if (unit < 100) {
                    unitString = "0" + unit;
                } else {
                    unitString = unit + "";
                }
            } else {
                unitString = unit + "";
            }
        } else {
            if (thousand > 0 || million > 0) {
                unitString = "000";
            }
        }
        if (millionString.length() > 0) {
            return millionString + "." + thousandString + "." + unitString;
        } else if (thousandString.length() > 0) {
            return thousandString + "." + unitString;
        } else if (unitString.length() > 0) {
            return unitString;
        } else {
            return "0";
        }
    }

    public void removeLast() {
        if (datas != null && datas.size() > 0) {
            datas.remove(datas.size() - 1);
            notifyDataSetChanged();
        }
    }

    public void replaceAll(ArrayList<OrderList> datas) {
        this.datas = datas;
    }

    @Override
    public void remove(OrderList object) {
        datas.remove(object);
    }

    @Override
    public OrderList getItem(int position) {
        return datas.get(position);
    }

    @Override
    public void clear() {
        datas.clear();
    }

    @Override
    public void add(OrderList object) {
        datas.add(object);
    }

    @Override
    public int getCount() {
        return datas.size();
    }

    @Override
    public void setData(int index, String result, View[] impactedViews) {
        ((BaseActivity) context).showLoading(false);
        try {
            JSONObject jsonObject = new JSONObject(result);
            if (jsonObject.getInt("status") == 1) {
                ((BaseActivity) context).showDialog("", "Pesanan telah diinformasikan ke konsumen", "", "Ok");
//                datas.remove(takenData);
                takenData.setDeliveryDate(jsonObject.getString("date"));
                takenData.type = OrderList.TYPE_DRIVER;
                notifyDataSetChanged();
                takenData = null;
            } else {
                ((BaseActivity) context).showDialog("", jsonObject.getString("message"), "", "Ok");
            }
        } catch (Exception ex) {
            ((BaseActivity) context).showDialog("Error parsing", ex.toString(), "", "Ok");
        }
    }
}