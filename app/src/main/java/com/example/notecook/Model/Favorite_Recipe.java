package com.example.notecook.Model;

public class Favorite_Recipe {
    private int id_fav;
    private int id_recipe;
    private int id_user;

    public Favorite_Recipe() {
    }

    public Favorite_Recipe( int id_recipe, int id_user) {
        this.id_recipe = id_recipe;
        this.id_user = id_user;
    }

    public int getId_fav() {
        return id_fav;
    }

    public void setId_fav(int id_fav) {
        this.id_fav = id_fav;
    }

    public int getId_recipe() {
        return id_recipe;
    }

    public void setId_recipe(int id_recipe) {
        this.id_recipe = id_recipe;
    }

    public int getId_user() {
        return id_user;
    }

    public void setId_user(int id_user) {
        this.id_user = id_user;
    }
}
