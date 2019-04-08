package com.pertamina.brightgasse.model;

public class Order {

    public String id;
    public String type;
    public String name;
    public long quantity;
    public long price;
    public long orderPrice;
    public int drawableResId;
    public long total;

    public Order(String id, String type,String name, long quantity, long price, long orderPrice, int drawableResId){
        this.id = id;
        this.type = type;
        this.drawableResId = drawableResId;
        this.name = name;
        this.quantity = quantity;
        this.price = price;
        this.orderPrice = orderPrice;
    }

    public Order(String id, String type, long price, long quantity, long total, long ongkir){
        this.id = id;
        this.type = type;
        this.price = price;
        this.quantity = quantity;
        this.total = total;
        this.orderPrice = ongkir;
        int idValue = Integer.parseInt(id);
        int typeValue = Integer.parseInt(type);
        String tempName="";
        String tempType="";
        switch (idValue){
            case 1:
                tempName = "5.5 Kg";
                break;
            case 2:
                tempName = "12 Kg";
                break;
            case 3:
                tempName = "220 gram";
                break;
        }
        switch (typeValue){
            case 1:
                tempType = "TABUNG & ISI";
                break;
            case 2:
                tempType = "REFILL/ISI ULANG";
                break;
            case 3:
                tempType = "TRADE IN";
                break;
        }
        this.name = "BRIGHT GAS "+tempName+" "+tempType;
    }

    public void minus(){
        if(quantity>0)
            this.quantity-=1;
    }

    public void plus(){
        this.quantity+=1;
    }

    public long getTotal(){
        return quantity*price;
    }

    public String getCalculated(long input){
        long thousand = input/1000;
        String thousandString = "";
        if(thousand != 0){
            thousandString = thousand+"";
        }

        long unit = input%1000;
        String unitString = "";
        if(unit==0){
            unitString = "000";
        }else{
            unitString = unit+"";
        }

        if(thousandString.length()>0){
            return thousandString+"."+unitString;
        }else{
            return unitString;
        }
    }

    public String getPrice(){
        return getCalculated(price);
    }

    public String getOrderPrice(){
        return getCalculated(orderPrice);
    }

}
