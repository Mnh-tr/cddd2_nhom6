package com.example.cddd2_nhom6.model;

import java.util.Date;

public class YeuCau {
    public String idLichSuTT;
    public String idUser;
    public String content;
    public int amount;
    public String  paymentDate;
    public int idTrangThai;

    public YeuCau(String idLichSuTT, String idUser, String content, int amount, String  paymentDate, int idTrangThai) {
        this.idLichSuTT = idLichSuTT;
        this.idUser = idUser;
        this.content = content;
        this.amount = amount;
        this.paymentDate = paymentDate;
        this.idTrangThai = idTrangThai;
    }
    public YeuCau() {}
}
