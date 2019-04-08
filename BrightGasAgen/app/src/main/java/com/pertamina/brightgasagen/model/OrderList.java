package com.pertamina.brightgasagen.model;

import android.util.Log;

import org.json.JSONArray;
import org.json.JSONObject;

/**
 * Created by gumelartejasukma on 11/23/16.
 */
public class OrderList {

    public static final int TYPE_TAKE_ORDER = 0;
    public static final int TYPE_CHOOSE_DRIVER = 1;
    public static final int TYPE_DRIVER = 2;
    public static final int TYPE_ON_DELIVERY = 3;

    public int type;
    public Order[] datas;
    public String id;
    public String invoiceNr;
    public String date;
    public String startTime;
    public String endTime;
    public String name;
    public String address;
    public String driverName;
    public long subTotal;
    public long ongkir;
    public long total;
    public String distance;


    public OrderList(int type, String id, String invoiceNr, String date, String startTime, String endTime, String name, String address,String driverName, long subTotal, long ongkir, long total,String distance, JSONArray orders){
        this.type = type;
        this.id = id;
        this.invoiceNr = invoiceNr;
        this.date = date;
        this.startTime = startTime;
        this.endTime = endTime;
        this.name = name;
        this.address = address;
        this.subTotal = subTotal;
        this.ongkir = ongkir;
        this.total = total;
        this.driverName = driverName;
        this.distance = distance;
        try{
            if(orders.length()>0){
                JSONObject jsonObject;
                datas = new Order[orders.length()];
                for(int i=0; i<orders.length(); i++){
                    jsonObject = orders.getJSONObject(i);
                    datas[i] = new Order(jsonObject.getString("tri_item_id"),jsonObject.getString("tri_type_id"),jsonObject.getLong("tri_price"),jsonObject.getLong("tri_quantity"),jsonObject.getLong("tri_total"),jsonObject.getLong("tri_ongkir"));
                }
            }
        }catch (Exception ex){
            Log.d("gugum",ex.toString());
        }
    }

    public OrderList(int type, String id, String invoiceNr, String date, String startTime, String endTime, String name, String address, long subTotal, long ongkir, long total, String distance,JSONArray orders){
        this.type = type;
        this.id = id;
        this.invoiceNr = invoiceNr;
        this.date = date;
        this.startTime = startTime;
        this.endTime = endTime;
        this.name = name;
        this.address = address;
        this.subTotal = subTotal;
        this.ongkir = ongkir;
        this.total = total;
        this.distance = distance;
        try{
            if(orders.length()>0){
                JSONObject jsonObject;
                datas = new Order[orders.length()];
                for(int i=0; i<orders.length(); i++){
                    jsonObject = orders.getJSONObject(i);
                    datas[i] = new Order(jsonObject.getString("tri_item_id"),jsonObject.getString("tri_type_id"),jsonObject.getLong("tri_price"),jsonObject.getLong("tri_quantity"),jsonObject.getLong("tri_total"),jsonObject.getLong("tri_ongkir"));
                }
            }
        }catch (Exception ex){
            Log.d("gugum",ex.toString());
        }
    }

    public OrderList(int type, String id,String invoiceNr, String date, String startTime, String endTime, String name, String address, long subTotal, long ongkir, long total){
        this.type = type;
        this.id = id;
        this.invoiceNr = invoiceNr;
        this.date = date;
        this.startTime = startTime;
        this.endTime = endTime;
        this.name = name;
        this.address = address;
        this.subTotal = subTotal;
        this.ongkir = ongkir;
        this.total = total;
        datas = new Order[]{
//                new Order("BRIGHT GAS 5,5 Kg TABUNG & ISI",1,125000,5000,-1),
//                new Order("BRIGHT GAS 12 Kg REFILL/ISI ULANG",2,150000,15000,-1)
        };
    }

    public OrderList(int type){
        this.type = type;
        this.id = "";
        this.invoiceNr = "";
        this.date = "";
        this.startTime = "";
        this.endTime = "";
        this.name = "";
        this.address = "";
        this.subTotal = 0;
        this.ongkir = 0;
        this.total = 0;
        datas = new Order[]{
//                new Order("BRIGHT GAS 5,5 Kg TABUNG & ISI",1,125000,5000,-1),
//                new Order("BRIGHT GAS 12 Kg REFILL/ISI ULANG",2,150000,15000,-1)
        };
    }

}
