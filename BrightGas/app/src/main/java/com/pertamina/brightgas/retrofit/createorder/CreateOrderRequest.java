package com.pertamina.brightgas.retrofit.createorder;

import java.util.List;

public class CreateOrderRequest {

    private String AddressID;
    private String DeliveryDistance;
    private String OrderDate;
    private String HargaTotalBarang;
    private String OngkosKirim;
    private String TotalBelanja;
    private String TanggalPengiriman;
    private String WaktuPengiriman;
    private List<Carts> Carts;

    public CreateOrderRequest(String addressID,
                              String deliveryDistance,
                              String orderDate,
                              String hargaTotalBarang,
                              String ongkosKirim,
                              String totalBelanja,
                              String tanggalPengiriman,
                              String waktuPengiriman,
                              List<CreateOrderRequest.Carts> carts) {
        AddressID = addressID;
        DeliveryDistance = deliveryDistance;
        OrderDate = orderDate;
        HargaTotalBarang = hargaTotalBarang;
        OngkosKirim = ongkosKirim;
        TotalBelanja = totalBelanja;
        TanggalPengiriman = tanggalPengiriman;
        WaktuPengiriman = waktuPengiriman;
        Carts = carts;
    }

    public String getAddressID() {
        return AddressID;
    }

    public String getDeliveryDistance() {
        return DeliveryDistance;
    }

    public String getOrderDate() {
        return OrderDate;
    }

    public String getHargaTotalBarang() {
        return HargaTotalBarang;
    }

    public String getOngkosKirim() {
        return OngkosKirim;
    }

    public String getTotalBelanja() {
        return TotalBelanja;
    }

    public String getTanggalPengiriman() {
        return TanggalPengiriman;
    }

    public String getWaktuPengiriman() {
        return WaktuPengiriman;
    }

    public List<CreateOrderRequest.Carts> getCarts() {
        return Carts;
    }

    public static class Carts {

        private String ProductID;
        private String Quantity;

        public Carts(String productID,
                     String quantity) {
            ProductID = productID;
            Quantity = quantity;
        }

        public String getProductID() {
            return ProductID;
        }

        public String getQuantity() {
            return Quantity;
        }
    }

}
