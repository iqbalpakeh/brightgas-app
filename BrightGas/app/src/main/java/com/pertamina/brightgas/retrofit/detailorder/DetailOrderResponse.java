package com.pertamina.brightgas.retrofit.detailorder;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class DetailOrderResponse {

    @SerializedName("status")
    private String status;

    @SerializedName("message")
    private String message;

    @SerializedName("data")
    private Data data;

    public String getStatus() {
        return status;
    }

    public String getMessage() {
        return message;
    }

    public Data getData() {
        return data;
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

        @SerializedName("verificationCode")
        private String verificationCode;

        @SerializedName("orderDate")
        private String orderDate;

        @SerializedName("arriveDate")
        private String arriveDate;

        @SerializedName("createdAt")
        private String createdAt;

        @SerializedName("carts")
        private List<Carts> cartsList;

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
            return grandTotalPrice.split("\\.")[0];
        }

        public String getDeliveryFee() {
            return deliveryFee;
        }

        public String getHargaTotalBarang() {
            return hargaTotalBarang;
        }

        public String getOngkosKirim() {
            return ongkosKirim.split("\\.")[0];
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

        public String getVerificationCode() {
            return verificationCode;
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

        public List<Carts> getCartsList() {
            return cartsList;
        }

        public static class Carts {

            @SerializedName("id")
            private String id;

            @SerializedName("orderID")
            private String orderID;

            @SerializedName("productID")
            private String productID;

            @SerializedName("quantity")
            private String quantity;

            @SerializedName("price")
            private String price;

            @SerializedName("deliveryFee")
            private String deliveryFee;

            @SerializedName("totalPrice")
            private String totalPrice;

            @SerializedName("createdAt")
            private String createdAt;

            public String getId() {
                return id;
            }

            public String getOrderID() {
                return orderID;
            }

            public String getProductID() {
                return productID;
            }

            public String getQuantity() {
                return quantity;
            }

            public String getPrice() {
                return price;
            }

            public String getDeliveryFee() {
                return deliveryFee;
            }

            public String getTotalPrice() {
                return totalPrice.split("\\.")[0];
            }

            public String getCreatedAt() {
                return createdAt;
            }
        }

    }

}
