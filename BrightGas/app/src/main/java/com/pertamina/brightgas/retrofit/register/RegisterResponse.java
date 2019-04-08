package com.pertamina.brightgas.retrofit.register;

import com.google.gson.annotations.SerializedName;

public class RegisterResponse {

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

        @SerializedName("userID")
        private String userID;

        @SerializedName("customerID")
        private String customerID;

        @SerializedName("User")
        private User user;

        public String getUserID() {
            return userID;
        }

        public String getCustomerID() {
            return customerID;
        }

        public User getUser() {
            return user;
        }

        public Customer getCustomer() {
            return customer;
        }

        public static class User {

            @SerializedName("id")
            private String id;

            @SerializedName("roles")
            private String roles;

            @SerializedName("username")
            private String username;

            @SerializedName("name")
            private String name;

            public String getId() {
                return id;
            }

            public String getRoles() {
                return roles;
            }

            public String getUsername() {
                return username;
            }

            public String getName() {
                return name;
            }
        }

        @SerializedName("customer")
        private Customer customer;

        public static class Customer {

            @SerializedName("id")
            private String id;

            @SerializedName("name")
            private String name;

            @SerializedName("email")
            private String email;

            @SerializedName("dob")
            private String dob;

            @SerializedName("pob")
            private String pob;

            @SerializedName("ktpCode")
            private String ktpCode;

            @SerializedName("gender")
            private String gender;

            @SerializedName("phone")
            private String phone;

            @SerializedName("addresses")
            private Addresses addresses;

            public String getId() {
                return id;
            }

            public String getName() {
                return name;
            }

            public String getEmail() {
                return email;
            }

            public String getDob() {
                return dob;
            }

            public String getPob() {
                return pob;
            }

            public String getKtpCode() {
                return ktpCode;
            }

            public String getGender() {
                return gender;
            }

            public String getPhone() {
                return phone;
            }

            public Addresses getAddresses() {
                return addresses;
            }

            public static class Addresses {

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

                @SerializedName("addressType")
                private String addressType;

                @SerializedName("postalCode")
                private String postalCode;

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

                public String getAddressType() {
                    return addressType;
                }

                public String getPostalCode() {
                    return postalCode;
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
    }
}
