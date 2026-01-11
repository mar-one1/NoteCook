package com.example.notecook.ViewModel;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.notecook.Dto.RecipeResponse;
import com.example.notecook.Dto.RecipesResponce;
import com.example.notecook.Dto.TokenResponse;
import com.example.notecook.Model.Category_Recipe;
import com.example.notecook.Model.Detail_Recipe;
import com.example.notecook.Model.Favorite_Recipe;
import com.example.notecook.Model.Ingredients;
import com.example.notecook.Model.Nutrition;
import com.example.notecook.Model.Recipe;
import com.example.notecook.Model.Review;
import com.example.notecook.Model.Step;
import com.example.notecook.Model.User;

import java.util.ArrayList;
import java.util.List;

public class SharedRecipeViewModel extends ViewModel {

    // Boolean flags
    private final MutableLiveData<Boolean> tagEditRecipe = new MutableLiveData<>(false);
    private final MutableLiveData<Boolean> tagMy = new MutableLiveData<>(false);
    private final MutableLiveData<Boolean> modeOnline = new MutableLiveData<>(false);
    private final MutableLiveData<Boolean> fingerprintId = new MutableLiveData<>(false);

    // Strings / token
    private final MutableLiveData<String> token = new MutableLiveData<>("");
    private final MutableLiveData<String> tagConnexionMessage = new MutableLiveData<>("");
    private final MutableLiveData<String> tagConnexionLocal = new MutableLiveData<>("");

    // Recipes / data
    private final MutableLiveData<List<Recipe>> listRecipe = new MutableLiveData<>(new ArrayList<>());
    private final MutableLiveData<List<Recipe>> remoteListRecipe = new MutableLiveData<>(new ArrayList<>());
    private final MutableLiveData<List<Recipe>> remoteListByIdUserRecipe = new MutableLiveData<>(new ArrayList<>());
    private final MutableLiveData<List<RecipeResponse>> remoteListFullRecipe = new MutableLiveData<>(new ArrayList<>());
    private final MutableLiveData<List<Ingredients>> ingredientsCurrentRecipe = new MutableLiveData<>(new ArrayList<>());
    private final MutableLiveData<Nutrition> remoteNutritions = new MutableLiveData<>();

    private final MutableLiveData<RecipesResponce> remoteRecipesByPages = new MutableLiveData<>();

    private final MutableLiveData<RecipesResponce> remoteSearchRecipesByPages = new MutableLiveData<>();

    private final MutableLiveData<Recipe> currentRecipe = new MutableLiveData<>();
    private final MutableLiveData<RecipeResponse> currentFullRecipe = new MutableLiveData<>();

    private final MutableLiveData<User> userCurrentRecipe = new MutableLiveData<>(new User());
    private final MutableLiveData<TokenResponse> userLogin = new MutableLiveData<>(new TokenResponse());
    private final MutableLiveData<TokenResponse> userLoginLocal = new MutableLiveData<>(new TokenResponse());
    private final MutableLiveData<Integer> tagConnexion = new MutableLiveData<>(-1);


    // Objects / Lists
    private List<Detail_Recipe> listDetailRecipe = new ArrayList<>();
    private Detail_Recipe detailCurrentRecipe;
    private List<Step> stepsCurrentRecipe = new ArrayList<>();
    private List<Review> reviewCurrentRecipe = new ArrayList<>();
    private List<Favorite_Recipe> favoriteCurrentRecipe = new ArrayList<>();
    private List<Ingredients> allIngredientsRecipe = new ArrayList<>();
    private List<Category_Recipe> allCategoriesRecipe = new ArrayList<>();
    private List<Recipe> searchList = new ArrayList<>();
    private List<Recipe> basketList = new ArrayList<>();
    private List<Recipe> recipesFavUser = new ArrayList<>();


    public void init() {
        // Reset LiveData values
        setToken("");
        setTagConnexion(-1);
        setTagConnexionMessage("");
        setListRecipe(new ArrayList<>());
        setStepsCurrentRecipe(new ArrayList<>());
        setReviewCurrentRecipe(new ArrayList<>());
        setIngredientsCurrentRecipe(new ArrayList<>());
        setAllIngredientsRecipe(new ArrayList<>());
        setSearchList(new ArrayList<>());
        setBasketList(new ArrayList<>());
        setUserCurrentRecipe(new User());
        setRecipesFavUser(new ArrayList<>());
        setRemoteListRecipe(new ArrayList<>());
        setRemoteListByIdUserRecipe(new ArrayList<>());
        setRemoteListFullRecipe(new ArrayList<>());
        setremoteRecipesByPages(new RecipesResponce());
        setRemoteSearchRecipesByPages(new RecipesResponce());
        // You can also reset other fields if needed
    }

    // Getters for LiveData
    public LiveData<Boolean> getTagEditRecipe() {
        return tagEditRecipe;
    }

    public LiveData<Boolean> getTagMy() {
        return tagMy;
    }

    public LiveData<Boolean> getModeOnline() {
        return modeOnline;
    }

    public LiveData<Boolean> getFingerprintId() {
        return fingerprintId;
    }

    public LiveData<String> getToken() {
        return token;
    }

    public LiveData<String> getTagConnexionMessage() {
        return tagConnexionMessage;
    }

    public LiveData<String> getTagConnexionLocal() {
        return tagConnexionLocal;
    }

    public LiveData<List<Recipe>> getListRecipe() {
        return listRecipe;
    }

    public LiveData<List<Recipe>> getRemoteListRecipe() {
        return remoteListRecipe;
    }

    public LiveData<List<Recipe>> getRemoteListByIdUserRecipe() {
        return remoteListByIdUserRecipe;
    }

    public LiveData<List<RecipeResponse>> getRemoteListFullRecipe() {
        return remoteListFullRecipe;
    }

    public LiveData<List<Ingredients>> getIngredientsCurrentRecipe() {
        return ingredientsCurrentRecipe;
    }

    public LiveData<Nutrition> getRemoteNutritions() {
        return remoteNutritions;
    }

    public LiveData<Recipe> getCurrentRecipe() {
        return currentRecipe;
    }

    public LiveData<RecipeResponse> getCurrentFullRecipe() {
        return currentFullRecipe;
    }

    public LiveData<User> getUserCurrentRecipe() {
        return userCurrentRecipe;
    }

    public LiveData<TokenResponse> getUserLogin() {
        return userLogin;
    }

    public LiveData<TokenResponse> getUserLoginLocal() {
        return userLoginLocal;
    }

    // Setters for LiveData
    public void setTagEditRecipe(boolean value) {
        tagEditRecipe.setValue(value);
    }

    public void setTagMy(boolean value) {
        tagMy.setValue(value);
    }

    public void setModeOnline(boolean value) {
        modeOnline.setValue(value);
    }

    public void setFingerprintId(boolean value) {
        fingerprintId.setValue(value);
    }

    public void setToken(String value) {
        token.setValue(value);
    }

    public void setTagConnexionMessage(String value) {
        tagConnexionMessage.setValue(value);
    }

    public void setTagConnexionLocal(String value) {
        tagConnexionLocal.setValue(value);
    }

    public void setListRecipe(List<Recipe> list) {
        listRecipe.setValue(list);
    }

    public void setRemoteListRecipe(List<Recipe> list) {
        remoteListRecipe.setValue(list);
    }

    public void setRemoteListByIdUserRecipe(List<Recipe> list) {
        remoteListByIdUserRecipe.setValue(list);
    }

    public void setRemoteListFullRecipe(List<RecipeResponse> list) {
        remoteListFullRecipe.setValue(list);
    }

    public void setIngredientsCurrentRecipe(List<Ingredients> list) {
        ingredientsCurrentRecipe.setValue(list);
    }

    public void setRemoteNutritions(Nutrition nutrition) {
        remoteNutritions.setValue(nutrition);
    }

    public void setCurrentRecipe(Recipe recipe) {
        currentRecipe.setValue(recipe);
    }

    public void setCurrentFullRecipe(RecipeResponse recipeResponse) {
        currentFullRecipe.setValue(recipeResponse);
    }

    public void setUserCurrentRecipe(User user) {
        userCurrentRecipe.setValue(user);
    }

    public void setUserLogin(TokenResponse login) {
        userLogin.setValue(login);
    }

    public void setUserLoginLocal(TokenResponse login) {
        userLoginLocal.setValue(login);
    }

    // Getters & setters for non-LiveData fields
    public List<Detail_Recipe> getListDetailRecipe() {
        return listDetailRecipe;
    }

    public void setListDetailRecipe(List<Detail_Recipe> list) {
        this.listDetailRecipe = list;
    }

    public Detail_Recipe getDetailCurrentRecipe() {
        return detailCurrentRecipe;
    }

    public void setDetailCurrentRecipe(Detail_Recipe detail) {
        this.detailCurrentRecipe = detail;
    }

    public List<Step> getStepsCurrentRecipe() {
        return stepsCurrentRecipe;
    }

    public void setStepsCurrentRecipe(List<Step> steps) {
        this.stepsCurrentRecipe = steps;
    }

    public List<Review> getReviewCurrentRecipe() {
        return reviewCurrentRecipe;
    }

    public void setReviewCurrentRecipe(List<Review> review) {
        this.reviewCurrentRecipe = review;
    }

    public List<Favorite_Recipe> getFavoriteCurrentRecipe() {
        return favoriteCurrentRecipe;
    }

    public void setFavoriteCurrentRecipe(List<Favorite_Recipe> list) {
        this.favoriteCurrentRecipe = list;
    }

    public List<Ingredients> getAllIngredientsRecipe() {
        return allIngredientsRecipe;
    }

    public void setAllIngredientsRecipe(List<Ingredients> list) {
        this.allIngredientsRecipe = list;
    }

    public List<Category_Recipe> getAllCategoriesRecipe() {
        return allCategoriesRecipe;
    }

    public void setAllCategoriesRecipe(List<Category_Recipe> list) {
        this.allCategoriesRecipe = list;
    }

    public List<Recipe> getSearchList() {
        return searchList;
    }

    public void setSearchList(List<Recipe> list) {
        this.searchList = list;
    }

    public List<Recipe> getBasketList() {
        return basketList;
    }

    public void setBasketList(List<Recipe> list) {
        this.basketList = list;
    }

    public List<Recipe> getRecipesFavUser() {
        return recipesFavUser;
    }

    public void setRecipesFavUser(List<Recipe> list) {
        this.recipesFavUser = list;
    }
    public LiveData<Integer> getTagConnexion() {
        return tagConnexion;
    }

    public void setTagConnexion(int value) {
        tagConnexion.setValue(value);
    }

    public MutableLiveData<RecipesResponce> getRemoteRecipesByPages() {return remoteRecipesByPages;}

    public void setremoteRecipesByPages(RecipesResponce recipeResponse) { remoteRecipesByPages.setValue(recipeResponse);}

    public MutableLiveData<RecipesResponce> getRemoteSearchRecipesByPages() {return remoteSearchRecipesByPages;}

    public void setRemoteSearchRecipesByPages(RecipesResponce recipeResponse) { remoteSearchRecipesByPages.setValue(recipeResponse);}

}