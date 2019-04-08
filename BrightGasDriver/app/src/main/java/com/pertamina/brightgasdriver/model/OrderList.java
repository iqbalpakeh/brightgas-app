package com.pertamina.brightgasdriver.model;

import android.util.Log;

import com.google.android.gms.maps.model.LatLng;

import org.json.JSONArray;
import org.json.JSONObject;

public class OrderList {

    private static final String TAG = "order_list";

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
    public String deliveryDate;
    public String customerId;

    public LatLng driverLocation;
    public LatLng destinationLocation;

    public void setDeliveryDate(String deliveryDate) {
        this.deliveryDate = deliveryDate;
    }

    public void setLocation(Double driverLat,
                            Double driverLong,
                            Double destinationLat,
                            Double destinationLong) {

        try {
            driverLocation = new LatLng(driverLat, driverLong);
            destinationLocation = new LatLng(destinationLat, destinationLong);
        } catch (Exception ex) {
            Log.d(TAG, ex.toString());
        }

    }

    public OrderList(int type, String id, String invoiceNr, String date, String startTime,
                     String endTime, String name, String address, long subTotal, long ongkir,
                     long total, String distance, String customerId, JSONArray orders) {

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
        this.customerId = customerId;

        try {
            if (orders.length() > 0) {
                JSONObject jsonObject;
                datas = new Order[orders.length()];
                for (int i = 0; i < orders.length(); i++) {
                    jsonObject = orders.getJSONObject(i);
                    datas[i] = new Order(
                            jsonObject.getString("tri_item_id"),
                            jsonObject.getString("tri_type_id"),
                            jsonObject.getLong("tri_price"),
                            jsonObject.getLong("tri_quantity"),
                            jsonObject.getLong("tri_total"),
                            jsonObject.getLong("tri_ongkir")
                    );
                }
            }
            Log.d(TAG, "datas: " + datas.length);
        } catch (Exception ex) {
            Log.d(TAG, "error: " + ex.toString());
        }
    }

    public OrderList(int type) {
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
        this.customerId = "";
        datas = new Order[]{};
    }

}
