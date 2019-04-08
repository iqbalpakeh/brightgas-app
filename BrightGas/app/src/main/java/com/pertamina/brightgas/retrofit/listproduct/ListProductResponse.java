package com.pertamina.brightgas.retrofit.listproduct;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class ListProductResponse {

    @SerializedName("status")
    private String status;

    @SerializedName("message")
    private String message;

    @SerializedName("data")
    private List<Data> data;

    @SerializedName("length")
    private String length;

    @SerializedName("totalPage")
    private String totalPage;

    @SerializedName("page")
    private String page;

    @SerializedName("limit")
    private String limit;

    public String getStatus() {
        return status;
    }

    public String getMessage() {
        return message;
    }

    public List<Data> getData() {
        return data;
    }

    public String getLength() {
        return length;
    }

    public String getTotalPage() {
        return totalPage;
    }

    public String getPage() {
        return page;
    }

    public String getLimit() {
        return limit;
    }

    public static class Data {

        @SerializedName("id")
        private String id;

        @SerializedName("name")
        private String name;

        @SerializedName("categoryID")
        private String categoryID;

        @SerializedName("itemID")
        private String itemID;

        @SerializedName("description")
        private String description;

        @SerializedName("price")
        private String price;

        @SerializedName("deliveryFee")
        private String deliveryFee;

        public String getId() {
            return id;
        }

        public String getName() {
            return name;
        }

        public String getCategoryID() {
            return categoryID;
        }

        public String getItemID() {
            return itemID;
        }

        public String getDescription() {
            return description;
        }

        public String getPrice() {
            return price;
        }

        public String getDeliveryFee() {
            return deliveryFee;
        }
    }

}


