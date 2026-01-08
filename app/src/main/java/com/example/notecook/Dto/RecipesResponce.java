package com.example.notecook.Dto;

import com.example.notecook.Model.Recipe;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class RecipesResponce {

    @SerializedName("page")
    private int page;
    @SerializedName("limit")
    private int limit;
    @SerializedName("totalRecipes")
    private int totalRecipes;
    @SerializedName("totalPages")
    private int totalPages;

    @SerializedName("recipes")
    private List<Recipe> recipes;

    public int getPage() {return page;}

    public void setPage(int page) {this.page = page;}

    public int getLimit() {return limit;}

    public void setLimit(int limit) {this.limit = limit;}

    public int getTotalRecipes() {return totalRecipes;}

    public void setTotalRecipes(int totalRecipes) {this.totalRecipes = totalRecipes;}

    public int getTotalPages() {return totalPages;}

    public void setTotalPages(int totalPages) {this.totalPages = totalPages;}

    public List<Recipe> getRecipes() {return recipes;}

    public void setRecipes(List<Recipe> recipes) {this.recipes = recipes;}
}
