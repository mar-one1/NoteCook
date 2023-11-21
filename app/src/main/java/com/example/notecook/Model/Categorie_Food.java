package com.example.notecook.Model;

import android.graphics.drawable.Drawable;
import android.media.Image;

public class Categorie_Food {

    private int id_Food;
    private Drawable icon_food;
    private String Detail_Cat_food;

    public Categorie_Food(Drawable icon_food, String detail_Cat_food) {
        this.icon_food = icon_food;
        Detail_Cat_food = detail_Cat_food;
    }

    public int getId_Food() {
        return id_Food;
    }

    public Drawable getIcon_food() {
        return icon_food;
    }

    public void setIcon_food(Drawable icon_food) {
        this.icon_food = icon_food;
    }

    public String getDetail_Cat_food() {
        return Detail_Cat_food;
    }

    public void setDetail_Cat_food(String detail_Cat_food) {
        Detail_Cat_food = detail_Cat_food;
    }
}
