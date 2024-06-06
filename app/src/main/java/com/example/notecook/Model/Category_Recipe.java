package com.example.notecook.Model;

import android.graphics.drawable.Drawable;
import android.media.Image;

import com.google.gson.annotations.SerializedName;

public class Category_Recipe {

    @SerializedName("id")
    private int id_category;
    @SerializedName("icon")
    private byte[] icon_category;
    private String icon_path_category;
    @SerializedName("detail_ct")
    private String detail_category;

    public Category_Recipe(byte[] icon_category, String icon_path_category, String detail_category) {
        this.icon_category = icon_category;
        this.icon_path_category = icon_path_category;
        this.detail_category = detail_category;
    }

    public Category_Recipe(String detail_category,byte[] icon_category) {
        this.icon_category = icon_category;
        this.detail_category = detail_category;
    }

    public Category_Recipe() {
    }

    public int getId_category() {
        return id_category;
    }

    public void setId_category(int id_category) {
        this.id_category = id_category;
    }

    public byte[] getIcon_category() {
        return icon_category;
    }

    public void setIcon_category(byte[] icon_category) {
        this.icon_category = icon_category;
    }

    public String getIcon_path_category() {
        return icon_path_category;
    }

    public void setIcon_path_category(String icon_path_category) {
        this.icon_path_category = icon_path_category;
    }

    public String getDetail_category() {
        return detail_category;
    }

    public void setDetail_category(String detail_category) {
        this.detail_category = detail_category;
    }
}
