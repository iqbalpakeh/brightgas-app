package com.pertamina.brightgas.model;

public class Information {
    public String id;
    public String title;
    public String remark;
    public String imageUrl;
    public String date;
    public String createdBy;
    public String lastUpdate;
    public String creatorImage;

    public Information(String id, String title, String remark, String imageUrl, String date, String createdBy, String lastUpdate, String creatorImage) {
        this.id = id;
        this.title = title;
        this.remark = remark;
        this.imageUrl = imageUrl;
        this.date = date;
        this.createdBy = createdBy;
        this.lastUpdate = lastUpdate;
        this.creatorImage = creatorImage;
    }

    public boolean isImageValid() {
        if (imageUrl == null || imageUrl.equals("null"))
            return false;
        return !imageUrl.isEmpty();
    }

    public String getInformation() {
        if (remark == null)
            return "";
        return remark;
    }

}
