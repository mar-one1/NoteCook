package com.example.notecook.Model;

import java.net.URL;

public class image {

    private URL url;
    private int id_user_img_frk;
    private  int id_recipe_img_frk;

    public image() {
    }

    public image(URL url, int id_user_img_frk, int id_recipe_img_frk) {
        this.url = url;
        this.id_user_img_frk = id_user_img_frk;
        this.id_recipe_img_frk = id_recipe_img_frk;
    }

    public int getId_recipe_img_frk() {
        return id_recipe_img_frk;
    }

    public void setId_recipe_img_frk(int id_recipe_img_frk) {
        this.id_recipe_img_frk = id_recipe_img_frk;
    }

    public int getId_user_img_frk() {
        return id_user_img_frk;
    }

    public void setId_user_img_frk(int id_user_img_frk) {
        this.id_user_img_frk = id_user_img_frk;
    }

    public URL getUrl() {
        return url;
    }

    public void setUrl(URL url) {
        this.url = url;
    }
}
