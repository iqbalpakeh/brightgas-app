package com.pertamina.brightgas.model;

public class Order {

    public String id;
    public String type;
    public String name;
    public long quantity;
    public long price;
    public long orderPrice;
    public int drawableResId;
    public long total;
    public long extraPrice;

    public Order(String id,
                 String type,
                 String name,
                 long quantity,
                 long price,
                 long orderPrice,
                 int drawableResId,
                 long extraPrice) {

        this.id = id;
        this.type = type;
        this.drawableResId = drawableResId;
        this.name = name;
        this.quantity = quantity;
        this.price = price;
        this.orderPrice = orderPrice;
        this.extraPrice = extraPrice;
    }

    public Order(String id,
                 String type,
                 long price,
                 long quantity,
                 long total,
                 long ongkir) {

        this.id = id;
        this.type = type;
        this.price = price;
        this.quantity = quantity;
        this.total = total;
        this.orderPrice = ongkir;

        int idValue = Integer.parseInt(id);
        int typeValue = Integer.parseInt(type);
        String tempName = "";
        String tempType = "";

        switch (idValue) {
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

        switch (typeValue) {
            case 1:
                tempType = "TABUNG & ISI";
                break;
            case 2:
                tempType = "REFILL/ISI ULANG";
                break;
            case 3:
                tempType = "TRADE IN DENGAN 1 TABUNG ELPIJI 3 KG";
                break;
            case 4:
                tempType = "TRADE IN DENGAN 1 TABUNG EASE GAS 9 Kg";
                break;
            case 5:
                tempType = "TRADE IN DENGAN 1 TABUNG JOYCOOK";
                break;
            case 6:
                tempType = "TRADE IN DENGAN 2 TABUNG ELPIJI 6 Kg";
                break;
        }

        this.name = "BRIGHT GAS " + tempName + " " + tempType;
    }

    public void minus() {
        if (quantity > 0)
            this.quantity -= 1;
    }

    public void plus() {
        this.quantity += 1;
    }

    public long getTotal() {
        return quantity * price;
    }

    public String getCalculated(long input) {

        long thousand = input / 1000;
        String thousandString = "";
        if (thousand != 0) {
            thousandString = thousand + "";
        }

        long unit = input % 1000;
        String unitString = "";
        if (unit == 0) {
            unitString = "000";
        } else {
            unitString = unit + "";
        }

        if (thousandString.length() > 0) {
            return thousandString + "." + unitString;
        } else {
            return unitString;
        }
    }

    public String getPrice() {
        return getCalculated(price);
    }

    public String getOrderPrice() {
        return (quantity > 0) ? getCalculated(orderPrice) : getCalculated(0L);
    }

    public long getOrderPriceLong() {
        return (quantity > 0) ? orderPrice : 0L;
    }

}
