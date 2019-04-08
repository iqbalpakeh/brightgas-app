package com.pertamina.brightgasagen.model;

public class IdName {

    public String id;
    public String name;
    public String additional;

    public IdName(String id, String name){
        this.id = id;
        this.name = name;
    }

    public IdName(String id, String name, String additional){
        this.id = id;
        this.name = name;
        this.additional = additional;
    }

}
