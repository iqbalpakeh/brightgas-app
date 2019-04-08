package com.pertamina.brightgas.retrofit.listorder;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class ListOrderResponse {

    @SerializedName("status")
    private String status;

    @SerializedName("message")
    private String message;

    @SerializedName("data")
    List<Data> datas;

    public String getStatus() {
        return status;
    }

    public String getMessage() {
        return message;
    }

    public List<Data> getDatas() {
        return datas;
    }

    public static class Data {

        @SerializedName("id")
        private String id;

        @SerializedName("userAgentID")
        private String userAgentID;

        @SerializedName("customerID")
        private String customerID;

        @SerializedName("driverID")
        private String driverID;

        @SerializedName("addressID")
        private String addressID;

        @SerializedName("status")
        private String status;

        @SerializedName("deliveryAddress")
        private String deliveryAddress;

        @SerializedName("orderQty")
        private String orderQty;

        @SerializedName("grandTotalPrice")
        private String grandTotalPrice;

        @SerializedName("deliveryFee")
        private String deliveryFee;

        @SerializedName("hargaTotalBarang")
        private String hargaTotalBarang;

        @SerializedName("ongkosKirim")
        private String ongkosKirim;

        @SerializedName("totalBelanja")
        private String totalBelanja;

        @SerializedName("tanggalPengiriman")
        private String tanggalPengiriman;

        @SerializedName("waktuPengiriman")
        private String waktuPengiriman;

        @SerializedName("deliveryDistance")
        private String deliveryDistance;

        @SerializedName("orderDate")
        private String orderDate;

        @SerializedName("arriveDate")
        private String arriveDate;

        @SerializedName("createdAt")
        private String createdAt;

        public String getId() {
            return id;
        }

        public String getUserAgentID() {
            return userAgentID;
        }

        public String getCustomerID() {
            return customerID;
        }

        public String getDriverID() {
            return driverID;
        }

        public String getAddressID() {
            return addressID;
        }

        public String getStatus() {
            return status;
        }

        public String getDeliveryAddress() {
            return deliveryAddress;
        }

        public String getOrderQty() {
            return orderQty;
        }

        public String getGrandTotalPrice() {
            return grandTotalPrice;
        }

        public String getDeliveryFee() {
            return deliveryFee;
        }

        public String getHargaTotalBarang() {
            return hargaTotalBarang;
        }

        public String getOngkosKirim() {
            return ongkosKirim;
        }

        public String getTotalBelanja() {
            return totalBelanja;
        }

        public String getTanggalPengiriman() {
            return tanggalPengiriman;
        }

        public String getWaktuPengiriman() {
            return waktuPengiriman;
        }

        public String getDeliveryDistance() {
            return deliveryDistance;
        }

        public String getOrderDate() {
            return orderDate;
        }

        public String getArriveDate() {
            return arriveDate;
        }

        public String getCreatedAt() {
            return createdAt;
        }
    }

}
