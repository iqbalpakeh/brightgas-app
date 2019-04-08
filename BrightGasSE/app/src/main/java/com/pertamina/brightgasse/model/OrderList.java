package com.pertamina.brightgasse.model;

public class OrderList {

    public static final int TYPE_NEW_ORDER = 0;
    public static final int TYPE_ON_PROCESS = 1;
    public static final int TYPE_PENDING_ORDER = 2;
    public static final int TYPE_DELAY = 3;

    public int type;
    public Order[] datas;

    public OrderList(int type){
        this.type = type;
        datas = new Order[]{
//                new Order("BRIGHT GAS 5,5 Kg TABUNG & ISI",1,125000,5000,-1),
//                new Order("BRIGHT GAS 12 Kg REFILL/ISI ULANG",2,150000,15000,-1)
        };
    }

}
