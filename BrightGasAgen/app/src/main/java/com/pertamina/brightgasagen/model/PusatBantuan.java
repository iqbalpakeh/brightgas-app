package com.pertamina.brightgasagen.model;

/**
 * Created by gumelartejasukma on 11/13/16.
 */
public class PusatBantuan {
    public static final int TYPE_TITLE = 0;
    public static final int TYPE_CONTENT = 1;
    public static final int TYPE_DETAIL = 2;

    public int type;
    public String value="";
    public String additional="";

    public PusatBantuan(int type, String value){
        this.type = type;
        this.value = value;
    }

    public PusatBantuan(String value,String additional){
        type = TYPE_CONTENT;
        this.value = value;
        this.additional = additional;
    }

    public PusatBantuan(PusatBantuan detailContent){
        type = TYPE_DETAIL;
        this.value = detailContent.value;
        this.additional = detailContent.additional;
    }

}
