package com.example.cddd2_nhom6.model;

public class BinhLuanPhim {
    public String id;
    public String userId;
    public String commentText;
    public long timestamp;
    private String userName; // Thêm trường tên người dùng
    private String formattedDate; // Thêm trường ngày giờ định dạng
    private String commentId;
    private boolean isGif;
    private String avatarUrl;

    // Constructor không đối số và có đối số
    public BinhLuanPhim() {}

    // Constructor

    public BinhLuanPhim(String userId, String commentText, long timestamp, String userName, String formattedDate) {
        this.userId = userId;
        this.commentText = commentText;
        this.timestamp = timestamp;
        this.userName = userName;
        this.formattedDate = formattedDate;
        this.isGif = false;
    }

    // Các getter và setter cho các trường dữ liệu
    public String getUserName() {
        return userName;
    }

    public String getUserId() {
        return userId;
    }

    public String getCommentText() {
        return commentText;
    }

    public String getCommentId() {
        return commentId;
    }

    public String getId() {
        return id;
    }

    public boolean isGif() {
        return isGif;
    }

    public void setGif(boolean isGif) {
        this.isGif = isGif;
    }
}
