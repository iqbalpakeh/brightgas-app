package com.pertamina.brightgasagen.model;

/**
 * Created by gumelartejasukma on 11/10/16.
 */
public class RiwayatTransaksi {

    public static final int STATUS_PENDING = 0;
    public static final int STATUS_DIPROSES = 1;
    public static final int STATUS_DIKIRIM = 2;
    public static final int STATUS_DITERIMA = 3;

    public String id;
    public String startDate;
    public String startTime;
    public String endTime;
    public String invoiceNo;
    public int statusId;

    public RiwayatTransaksi(String id, String startDate, String startTime, String endTime, String invoiceNo, int statusId) {
        this.id = id;
        this.startDate  =startDate;
        this.startTime = startTime;
        this.endTime = endTime;
        this.invoiceNo = invoiceNo;
        this.statusId = statusId;
    }

}
