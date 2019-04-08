package com.pertamina.brightgas.model;

public class PesanMasuk {

    public String name;
    public int type;
    public String date;
    public String time;
    public String title;
    public String content;

    public PesanMasuk(String name, int type, String date, String time, String title, String content) {
        this.name = name;
        this.type = type;
        this.date = date;
        this.time = time;
        this.title = title;
        this.content = content;
    }

}
