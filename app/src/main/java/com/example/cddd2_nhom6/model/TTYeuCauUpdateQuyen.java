package com.example.cddd2_nhom6.model;

public class TTYeuCauUpdateQuyen {
    private String id_TTYeuCauQuyen;
    private String id_userCanUpdate;
    private String id_userYeuCau;
    private String ngayUpdater;
    public String noiDung;
    private Long id_loaiNDUpdate;
    private Long id_loaiNDCu;
    public int idTrangThai;
    public TTYeuCauUpdateQuyen() {
    }
    public TTYeuCauUpdateQuyen(String id_TTYeuCauQuyen, String id_userCanUpdate, String id_userYeuCau, String ngayUpdater, String noiDung, Long id_loaiNDUpdate,Long id_loaiNDCu, int idTrangThai) {
        this.id_TTYeuCauQuyen = id_TTYeuCauQuyen;
        this.id_userCanUpdate = id_userCanUpdate;
        this.id_userYeuCau = id_userYeuCau;
        this.ngayUpdater = ngayUpdater;
        this.noiDung = noiDung;
        this.id_loaiNDUpdate = id_loaiNDUpdate;
        this.id_loaiNDCu = id_loaiNDCu;
        this.idTrangThai = idTrangThai;
    }

    public Long getId_loaiNDCu() {
        return id_loaiNDCu;
    }

    public void setId_loaiNDCu(Long id_loaiNDCu) {
        this.id_loaiNDCu = id_loaiNDCu;
    }

    public int getIdTrangThai() {
        return idTrangThai;
    }

    public void setIdTrangThai(int idTrangThai) {
        this.idTrangThai = idTrangThai;
    }

    public String getId_TTYeuCauQuyen() {
        return id_TTYeuCauQuyen;
    }

    public void setId_TTYeuCauQuyen(String id_TTYeuCauQuyen) {
        this.id_TTYeuCauQuyen = id_TTYeuCauQuyen;
    }

    public String getId_userCanUpdate() {
        return id_userCanUpdate;
    }

    public void setId_userCanUpdate(String id_userCanUpdate) {
        this.id_userCanUpdate = id_userCanUpdate;
    }

    public String getId_userYeuCau() {
        return id_userYeuCau;
    }

    public void setId_userYeuCau(String id_userYeuCau) {
        this.id_userYeuCau = id_userYeuCau;
    }

    public String getNgayUpdater() {
        return ngayUpdater;
    }

    public void setNgayUpdater(String ngayUpdater) {
        this.ngayUpdater = ngayUpdater;
    }

    public String getNoiDung() {
        return noiDung;
    }

    public void setNoiDung(String noiDung) {
        this.noiDung = noiDung;
    }

    public Long getId_loaiNDUpdate() {
        return id_loaiNDUpdate;
    }

    public void setId_loaiNDUpdate(Long id_loaiNDUpdate) {
        this.id_loaiNDUpdate = id_loaiNDUpdate;
    }
}
