package com.pertamina.brightgasagen;

import android.content.Context;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.pertamina.brightgasagen.model.OrderList;

import org.apache.http.message.BasicNameValuePair;
import org.json.JSONObject;

import java.util.ArrayList;


public class AdapterOrderList extends ArrayAdapter<OrderList> implements RequestLoaderInterface {

    public static final int TAKE_ORDER = 0;
//    public static final int GET_DRIVER_DATA = 1;
//    public static final int PICK_DRIVER = 2;

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
        TextView invoiceNo = (TextView) rootView.findViewById(R.id.invoiceno);
        TextView countdown = (TextView) rootView.findViewById(R.id.countdown);
        TextView date = (TextView) rootView.findViewById(R.id.mydate);
        TextView time = (TextView) rootView.findViewById(R.id.mytime);
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
        total.setText(((BaseActivity)context).getCalculatedPrice(data.total));
        switch (data.type) {
            case 0:
                topContainer.setBackgroundResource(R.drawable.ic_rectangle_red_solidtop);
                break;
            case 1:
                topContainer.setBackgroundResource(R.drawable.ic_rectangle_violet_solidtop);
                if (((BaseActivity)context).isValidString(data.driverName)) {
                    View driverLayout = rootView.findViewById(R.id.driverlayout);
                    driverLayout.setVisibility(View.VISIBLE);
                    TextView driverName = (TextView) rootView.findViewById(R.id.drivername);
                    driverName.setText(data.driverName);
                }
                break;
            case 2:
                topContainer.setBackgroundResource(R.drawable.ic_rectangle_green_solidtop);
                if (((BaseActivity)context).isValidString(data.driverName)) {
                    View driverLayout = rootView.findViewById(R.id.driverlayout);
                    driverLayout.setVisibility(View.VISIBLE);
                    TextView driverName = (TextView) rootView.findViewById(R.id.drivername);
                    driverName.setText(data.driverName);
                }
                break;
        }


//        TextView invoiceNr = (TextView)rootView.findViewById(R.id.invoiceno);
//        TextView headerTime = (TextView)rootView.findViewById(R.id.headertime);
//        TextView headerDate = (TextView)rootView.findViewById(R.id.headerdate);
//        TextView customerName = (TextView)rootView.findViewById(R.id.name);
//        TextView date = (TextView)rootView.findViewById(R.id.date);
//        TextView time = (TextView)rootView.findViewById(R.id.mytime);
//        TextView address = (TextView)rootView.findViewById(R.id.address);
//        TextView distance = (TextView)rootView.findViewById(R.id.distance);
//
//        ViewGroup itemContainer = (ViewGroup) rootView.findViewById(R.id.itemcontainer);
//        final OrderList data = getItem(position);
//
//        invoiceNr.setText(data.invoiceNr);
//        headerTime.setText(data.startTime);
//        headerDate.setText(data.date);
//        customerName.setText(data.name);
//        date.setText(data.date);
//        time.setText(data.startTime+" - "+data.endTime);
//        address.setText(data.address);
//        distance.setText(data.distance);
//
//        Order[] orders = data.datas;
//        for(int i=0; i<orders.length; i++){
//            View rv = LayoutInflater.from(getContext()).inflate(R.layout.item_informasi_transaksi,itemContainer,false);
//            TextView name = (TextView)rv.findViewById(R.id.name);
//            name.setText(orders[i].name);
//            TextView quantity = (TextView)rv.findViewById(R.id.quantity);
//            quantity.setText(orders[i].quantity+"");
//            TextView price = (TextView)rv.findViewById(R.id.price);
//            price.setText(orders[i].getPrice());
//            itemContainer.addView(rv);
//        }
//        TextView total = (TextView)rootView.findViewById(R.id.total);
//        TextView hargatotalbarang = (TextView)rootView.findViewById(R.id.hargatotalbarang);
//        TextView ongkoskirim = (TextView)rootView.findViewById(R.id.ongkoskirim);
////        long totalHarga = 0;
////        long totalOngkir = 0;
////        long totalPrice = 0;
////        for(int i=0; i<orders.length; i++){
////            totalHarga+=orders[i].getTotal();
////            totalOngkir+=orders[i].orderPrice;
////        }
////        totalPrice = totalHarga+totalOngkir;
//        total.setText(getCalculatedPrice(data.total));
//        hargatotalbarang.setText(getCalculatedPrice(data.subTotal));
//        ongkoskirim.setText(getCalculatedPrice(data.ongkir));
//
//        TextView action = (TextView)rootView.findViewById(R.id.action);
//        switch (data.type){
//            case OrderList.TYPE_CHOOSE_DRIVER:
//                action.setTextColor(ContextCompat.getColor(getContext(),R.color.colorWhite));
//                action.setBackgroundResource(R.drawable.ic_rectangle_orderlistactionorange_solid);
//                action.setText("pilih driver");
//                action.setOnClickListener(new View.OnClickListener() {
//                    @Override
//                    public void onClick(View v) {
//                        chooseDriver(data);
//                    }
//                });
//                break;
//            case OrderList.TYPE_DRIVER:
//                action.setTextColor(ContextCompat.getColor(getContext(),R.color.orderlistline));
//                action.setBackgroundColor(ContextCompat.getColor(getContext(),android.R.color.transparent));
//                action.setText(data.driverName);
//                TextView additionalText = (TextView)rootView.findViewById(R.id.additionaltext);
//                additionalText.setVisibility(View.VISIBLE);
//                break;
//            case OrderList.TYPE_ON_DELIVERY:
//                action.setTextColor(ContextCompat.getColor(getContext(),R.color.orderlistactionsendgreen));
//                action.setBackgroundResource(R.drawable.ic_rectangle_orderlistactionsend);
//                action.setText("driver sedang mengirim pesanan");
//                action.setOnClickListener(new View.OnClickListener() {
//                    @Override
//                    public void onClick(View v) {
//                        FragmentDelivery fragment = new FragmentDelivery();
//                        fragment.setData(data);
//                        BaseActivity.self.changeFragment(fragment,false,true);
//                    }
//                });
//                break;
//            case OrderList.TYPE_TAKE_ORDER:
//                action.setOnClickListener(new View.OnClickListener() {
//                    @Override
//                    public void onClick(View v) {
//                        takeOrder(data);
//                    }
//                });
//                break;
//        }

        return rootView;
    }

    private void takeOrder(OrderList data) {
        ((BaseActivity)context).showLoading(true, "Ambil order");
        takenData = data;
        new RequestLoader(this).loadRequest(TAKE_ORDER, new BasicNameValuePair[]{
                new BasicNameValuePair("id", data.id),
                new BasicNameValuePair("agen_id", User.id)
        }, "transaction", "take_transaction", true);
    }

    private void chooseDriver(final OrderList data) {
        FragmentDriverPicker fragment = new FragmentDriverPicker();
        fragment.setData(data);
        ((BaseActivity)context).changeFragment(fragment, false, true);
//        takenData = data;
//        BaseActivity.self.showLoading(true,"Ambil data driver");
//        new RequestLoader(this).loadRequest(GET_DRIVER_DATA,new BasicNameValuePair[]{
//                new BasicNameValuePair("agen_id",User.id)
//        },"agen","get_driver",false,true);
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

    public void replaceAll(ArrayList<OrderList> datas) {
        this.datas = datas;
    }

    private void parseTakeOrder(String result) {
        try {
            JSONObject jsonObject = new JSONObject(result);
            if (jsonObject.getInt("status") == 1) {
                ((BaseActivity)context).showDialog("", "Order terambil", "", "Ok");
                datas.remove(takenData);
                notifyDataSetChanged();
                takenData = null;
            } else {
                ((BaseActivity)context).showDialog("", jsonObject.getString("message"), "", "Ok");
            }
        } catch (Exception ex) {
            ((BaseActivity)context).showDialog("Error parsing", ex.toString(), "", "Ok");
        }
    }

//    private void parsePickDriver(String result){
//        try{
//            JSONObject jsonObject = new JSONObject(result);
//            if(jsonObject.getInt("status") == 1){
//                BaseActivity.self.showDialog("","Driver telah dipilih","","Ok");
//                datas.remove(takenData);
//                notifyDataSetChanged();
//                takenData = null;
//            }else{
//                BaseActivity.self.showDialog("",jsonObject.getString("message"),"","Ok");
//            }
//        }catch(Exception ex){
//            BaseActivity.self.showDialog("Error parsing",ex.toString(),"","Ok");
//        }
//    }

//    private void parseGetDriverData(String result){
//        try{
//            JSONObject jsonObject = new JSONObject(result);
//            if(jsonObject.getInt("status") == 1){
//                JSONArray jsonArray = jsonObject.getJSONArray("customer_id");
//                if(jsonArray.length()>0){
//
//                    ViewGroup rootView = (ViewGroup) LayoutInflater.from(getContext()).inflate(R.layout.fragment_choose_driver,null,false);
//                    for(int i=0; i<jsonArray.length(); i++){
//                        jsonObject = jsonArray.getJSONObject(i);
//                        final String id = jsonObject.getString("agd_id");
//                        TextView textView = (TextView)LayoutInflater.from(getContext()).inflate(R.layout.item_choose_driver,rootView,false);
//                        textView.setText(jsonObject.getString("agd_name"));
//                        textView.setOnClickListener(new View.OnClickListener() {
//                            @Override
//                            public void onClick(View v) {
//                                chooseDriverDialog.dismiss();
//                                pickDriver(id);
//                            }
//                        });
//                        rootView.addView(textView);
//                    }
//                    chooseDriverDialog = BaseActivity.self.showDialog("Available Driver",rootView,"","BATAL",null,null);
//                    chooseDriverDialog.getButton(AlertDialog.BUTTON_NEGATIVE).setOnClickListener(new View.OnClickListener() {
//                        @Override
//                        public void onClick(View v) {
//                            chooseDriverDialog.dismiss();
//                        }
//                    });
//
//
//                }
//            }else{
//                BaseActivity.self.showDialog("",jsonObject.getString("message"),"","Ok");
//            }
//        }catch(Exception ex){
//            BaseActivity.self.showDialog("Error parsing",ex.toString(),"","Ok");
//        }
//    }

//    private void pickDriver(String id){
//        BaseActivity.self.showLoading(true);
//        new RequestLoader(this).loadRequest(PICK_DRIVER,new BasicNameValuePair[]{
//                new BasicNameValuePair("id",takenData.id),
//                new BasicNameValuePair("driver_id",id)
//        },"transaction","send_to_driver",true);
//    }

    @Override
    public void setData(int index, String result, View[] impactedViews) {
        ((BaseActivity)context).showLoading(false);
        switch (index) {
            case TAKE_ORDER:
                parseTakeOrder(result);
                break;
//            case GET_DRIVER_DATA:
//                parseGetDriverData(result);
//                break;
//            case PICK_DRIVER:
//                parsePickDriver(result);
//                break;
        }
    }
}