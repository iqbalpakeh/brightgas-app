package com.pertamina.brightgas.retrofit.addaddress;

public class AddAddressRequest {

    private String AddressText;
    private String Latitude;
    private String Longitude;
    private String PostalCode;
    private String ProvinceID;
    private String CityID;
    private String RegionID;
    private String KelurahanID;

    public AddAddressRequest(String addressText,
                             String latitude,
                             String longitude,
                             String postalCode,
                             String provinceID,
                             String cityID,
                             String regionID,
                             String kelurahanID) {
        AddressText = addressText;
        Latitude = latitude;
        Longitude = longitude;
        PostalCode = postalCode;
        ProvinceID = provinceID;
        CityID = cityID;
        RegionID = regionID;
        KelurahanID = kelurahanID;
    }
}
