package com.pertamina.brightgas.retrofit.listaddress;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class ListAddressResponse {

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

        @SerializedName("customerID")
        private String customerID;

        @SerializedName("addressText")
        private String addressText;

        @SerializedName("latitude")
        private String latitude;

        @SerializedName("longitude")
        private String longitude;

        @SerializedName("postalCode")
        private String postalCode;

        @SerializedName("addressType")
        private String addressType;

        @SerializedName("regionID")
        private String regionID;

        @SerializedName("kelurahanID")
        private String kelurahanID;

        @SerializedName("cityID")
        private String cityID;

        @SerializedName("provinceID")
        private String provinceID;

        public String getId() {
            return id;
        }

        public String getCustomerID() {
            return customerID;
        }

        public String getAddressText() {
            return addressText;
        }

        public String getLatitude() {
            return latitude;
        }

        public String getLongitude() {
            return longitude;
        }

        public String getPostalCode() {
            return postalCode;
        }

        public String getAddressType() {
            return addressType;
        }

        public String getRegionID() {
            return regionID;
        }

        public String getKelurahanID() {
            return kelurahanID;
        }

        public String getCityID() {
            return cityID;
        }

        public String getProvinceID() {
            return provinceID;
        }
    }

}
