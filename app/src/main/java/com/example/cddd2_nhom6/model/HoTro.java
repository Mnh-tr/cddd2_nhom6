package com.example.cddd2_nhom6.model;

public class HoTro {
    private long id;
    private String name;
    private String description;
    private String time;
    private String imageUrl;
    private String userId;

    public HoTro() {
    }
    public HoTro(String name, String description, String userId, String time, String imageUrl) {
        this.name = name;
        this.description = description;
        this.userId = userId;
        this.time = time;
        this.imageUrl = imageUrl;
    }
    public long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public String getImageUrl() {
        return imageUrl;
    }
    public String getTime() {
        return time;
    }
    public String getUserId() {
        return userId;
    }
    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

}
