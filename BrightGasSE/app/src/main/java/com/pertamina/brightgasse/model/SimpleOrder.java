package com.pertamina.brightgasse.model;

public class SimpleOrder {

    public String id;
    public String invoiceNo;
    public String countdown;
    public String date;
    public String time;
    public String distance;
    public String item;
    public String total;
    public int status;
    public String agenId;
    public String driverId;
    public String customerId;

    public SimpleOrder(String id,
                       String invoiceNo,
                       String countdown,
                       String date,
                       String time,
                       String distance,
                       String item,
                       String total,
                       int status,
                       String agenId,
                       String driverId,
                       String customerId) {

        this.id = id;
        this.invoiceNo = invoiceNo;
        this.countdown = countdown;
        this.date = date;
        this.time = time;
        this.distance = distance;
        this.item = item;
        this.total = total;
        this.status = status;
        this.agenId = agenId;
        this.driverId = driverId;
        this.customerId = customerId;
    }

}
