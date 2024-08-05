package com.example.notecook.Data;

public interface MySQLiteHelperTable extends AutoCloseable {
    /*
     * Table User
     */
    String TABLE_USER = "User";
    String COLUMN_ID_USER = "Id_user";
    String COLUMN_CODE_USER = "Code_user";
    String COLUMN_FIRSTNAME_USER = "Firstname_user";
    String COLUMN_USERNAME = "username";
    String COLUMN_LASTNAME_USER = "Lastname_user";
    String COLUMN_BIRTHDAY_USER = "Birthday_user";
    String COLUMN_EMAIL_USER = "Email_user";
    String COLUMN_PHONENUMBER_USER = "Phonenumber_user";
    String COLUMN_ICON = "Icon_user";
    String COLUMN_ICON_PATH = "Icon_user_path";
    String COLUMN_PASSWORD = "password";
    String COLUMN_GRADE = "Grade_user";
    String COLUMN_STATUS = "Status_user";
    /*
     * Table Recipe
     */
    String TABLE_RECIPE = "Recipe";
    String COLUMN_ID_RECIPE = "Id_recipe";
    String COLUMN_NOM_RECIPE = "Nom_Recipe";
    String COLUMN_ICON_RECIPE = "Icon_recipe";
    String COLUMN_ICON_RECIPE_PATH = "Icon_recipe_path";
    String COLUMN_FAV_RECIPE = "Fav_recipe";
    String COLUMN_CODE_RECIPE = "Code_recipe";
    String COLUMN_ID_FRK_USER_RECIPE = "Frk_user";
    String COLUMN_ID_FRK_CATEGORIE_RECIPE = "Frk_categorie";
    String COLUMN_UNIQUE_KEY = "unique_key_recipe";
    /*
     * Table Detail_Recipe
     */
    String TABLE_DETAIL_RECIPE = "Detail_recipe";
    String COLUMN_ID_DETAIL_RECIPE = "Id_detail_recipe";
    String COLUMN_DETAIL = "Dt_recipe";
    String COLUMN_TIME_DR = "Dt_recipe_time";
    String COLUMN_RATE_DR = "Rate_recipe";
    String COLUMN_LEVEL_DR = "Level_recipe";
    String COLUMN_CALORIES = "Calories_recipe";
    String COLUMN_FRK_RECIPE_DETAIL = "FRK_recipe";

    /*
     * Table Ingredients_Recipe
     */
    String TABLE_INGREDIENT_RECIPE = "Ingredient_recipe";
    String COLUMN_ID_INGREDIENT_RECIPE = "Id_Ingredient_recipe";
    String COLUMN_INGREDIENT_RECIPE = "Ingredient_recipe";
    String COLUMN_POIDINGREDIENT_RECIPE = "PoidIngredient_recipe";
    String COLUMN_UNITEINGREDIENT_RECIPE = "UnitIngredient_recipe";
    String COLUMN_FRK_DETAIL_INGREDIENT_RECIPE = "FRK_recipe";


    /*
     * Table Step_Recipe
     */
    String TABLE_STEP_RECIPE = "Step_recipe";
    String COLUMN_ID_STEP_RECIPE = "Id_Step_recipe";
    String COLUMN_DETAIL_STEP_RECIPE = "Detail_Step_recipe";
    String COLUMN_IMAGE_STEP = "Image_Step_recipe";
    String COLUMN_TIME_STEP = "Time_Step_recipe";
    String COLUMN_FRK_STEP_RECIPE = "FRK_recipe";
    /*
     * Table Review_Recipe
     */
    String TABLE_REVIEW_RECIPE = "Review_recipe";
    String COLUMN_ID_REVIEW_RECIPE = "Id_Review_recipe";
    String COLUMN_DETAIL_REVIEW_RECIPE = "Detail_Review_recipe";
    String COLUMN_RATE_REVIEW_RECIPE = "Rate_Review_recipe";
    String COLUMN_FRK_DETAIL_REVIEW_RECIPE = "FRK_recipe";


    /*
     * Table List Ingredient recipe
     */
    String TABLE_LIST_INGREDIENT_RECIPE ="List_ingredients_recipe";
    String COLUMN_ID_LIST_INGREDIENT_RECIPE = "Id_List_Ingredients_recipe";
    String COLUMN_FRK_ID_INGREDIENT_RECIPE = "Frk_Ingredient_recipe";
    String COLUMN_FRK_ID_RECIPE = "FRK_recipe";

    /*
     * Table List Categorie recipe
     */
    String TABLE_CATEGORIE_RECIPE = "Categorie_recipe";
    String COLUMN_ID_CATEGORIE_RECIPE = "Id_Categorie_recipe";
    String COLUMN_ICON_CATEGORIE_RECIPE = "Icon_Categorie_recipe";
    String COLUMN_ICON_PATH_CATEGORIE_RECIPE = "Icon_Path_Categorie_recipe";
    String COLUMN_DETAIL_CATEGORIE_RECIPE = "Detail_Categorie_recipe";

    /*
     * Table Produit
     */
    String TABLE_PRODUIT = "Produit";
    String COLUMN_ID_PRODUIT = "Id_Produit";
    String COLUMN_PRODUIT= "Produit";
    String COLUMN_POIDPRODUIT = "PoidProduit";


    /*
     * Table List Categorie recipe
     */
    String TABLE_RECIPEPRODIUT = "Recipe_produit";
    String COLUMN_ID_RECIPEPRODUIT = "Id_Recipe_produit";
    String COLUMN_FRK_RECIPE = "FRK_recipe";
    String COLUMN_FRK_PRODUIT = "FRK_produit";

    /*
     * Table List FavoriteUserRecipe
     */
    String TABLE_FAV_RECIPE_USER = "Favorite_User_Recipe";
    String COLUMN_ID_FAV_USER  = "favRecipe_id";
    String COLUMN_FRK_RECIPE_FAV= "FRK_recipe";
    String COLUMN_FRK_USER_FAV = "FRK_user";

    @Override
    void close() throws Exception;


}
