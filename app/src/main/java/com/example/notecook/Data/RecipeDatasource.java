package com.example.notecook.Data;

import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.example.notecook.Dto.RecipeResponse;
import com.example.notecook.Model.Detail_Recipe;
import com.example.notecook.Model.Ingredients;
import com.example.notecook.Model.Recipe;
import com.example.notecook.Model.Review;
import com.example.notecook.Model.Step;
import com.example.notecook.Model.User;

import java.sql.NClob;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

public class RecipeDatasource {

    private static SQLiteDatabase database;
    private static String[] allColumns = {MySQLiteHelper.COLUMN_ID_RECIPE,
            MySQLiteHelper.COLUMN_ICON_RECIPE, MySQLiteHelper.COLUMN_FAV_RECIPE, MySQLiteHelper.COLUMN_NOM_RECIPE,
            MySQLiteHelper.COLUMN_ID_FRK_USER_RECIPE};
    private MySQLiteHelper dbHelper;

    public RecipeDatasource(Context context) {
        dbHelper = new MySQLiteHelper(context);
    }

    /*
     * insert the value in the Image table
     */
    public Recipe createRecipe(byte[] ICONBYTE, String nom_recipe, int fav, int frk) {
        open();
        ContentValues values = new ContentValues();
        values.put(MySQLiteHelper.COLUMN_ICON_RECIPE, ICONBYTE);
        values.put(MySQLiteHelper.COLUMN_FAV_RECIPE, fav);
        values.put(MySQLiteHelper.COLUMN_NOM_RECIPE, nom_recipe);
        values.put(MySQLiteHelper.COLUMN_ID_FRK_USER_RECIPE, frk);

        long insertId = database.insert(MySQLiteHelper.TABLE_RECIPE, null,
                values);
        Cursor cursor = database.query(MySQLiteHelper.TABLE_RECIPE,
                allColumns, MySQLiteHelper.COLUMN_ID_RECIPE + " = " + insertId, null,
                null, null, null);
        cursor.moveToFirst();
        Recipe newRecipe = cursorToComment(cursor);
        cursor.close();
        close();
        return newRecipe;
    }

    public long InsertRecipe(Recipe recipe, int id) {
        open();
        ContentValues values = new ContentValues();
        values.put(MySQLiteHelper.COLUMN_ICON_RECIPE, recipe.getIcon_recipe());
        values.put(MySQLiteHelper.COLUMN_NOM_RECIPE, recipe.getNom_recipe());
        values.put(MySQLiteHelper.COLUMN_FAV_RECIPE, recipe.getFav());
        values.put(MySQLiteHelper.COLUMN_ID_FRK_USER_RECIPE, id);

        // long insertId = database.insert(MySQLiteHelper.TABLE_RECIPE, null, values);

        long insertId = database.insert(MySQLiteHelper.TABLE_RECIPE,
                null, values);
        close();
        return insertId;
    }

    private Recipe cursorToComment(Cursor cursor) {
        Recipe recipe = new Recipe();
        recipe.setId_recipe(cursor.getInt(0));
        recipe.setIcon_recipe(cursor.getBlob(1));
        recipe.setFav(cursor.getInt(2));
        recipe.setNom_recipe(cursor.getString(3));
        recipe.setFrk_user(cursor.getInt(4));
        return recipe;
    }

    /*
     * Open DataBase
     */
    public void open() throws SQLException {
        database = dbHelper.getWritableDatabase();
    }

    /*
     * close DataBase
     */
    public void close() {
        dbHelper.close();
    }

    /*
     * delete item from the table of images
     */
    public void deleteRecipe(Recipe recipe) {
        open();
        long id = recipe.getId_recipe();
        Log.d("gps", "Image  deleted with id: " + id);
        database.delete(MySQLiteHelper.TABLE_RECIPE, MySQLiteHelper.COLUMN_ID_RECIPE
                + " = " + id, null);
        close();
    }

    public ArrayList<Recipe> getAllRecipes() {
        open();
        ArrayList<Recipe> ListRecipe = new ArrayList<>();
        Cursor cursor = database.query(MySQLiteHelper.TABLE_RECIPE,
                allColumns, null, null, null, null, null);

        cursor.moveToFirst();
        while (!cursor.isAfterLast()) {
            Recipe Recipe = cursorToComment(cursor);
            ListRecipe.add(Recipe);
            cursor.moveToNext();
        }
        // assurez-vous de la fermeture du curseur
        cursor.close();
        close();
        return ListRecipe;
    }

    public List<Recipe> getRecipeByIdUser(int id) {
        List<Recipe> ListRecipe = new ArrayList<>();

        Cursor cursor = database.query(MySQLiteHelper.TABLE_RECIPE,
                allColumns, MySQLiteHelper.COLUMN_ID_FRK_USER_RECIPE + " = " + id, null, null, null, null);

        cursor.moveToFirst();
        while (!cursor.isAfterLast()) {
            Recipe Recipe = cursorToComment(cursor);
            ListRecipe.add(Recipe);
            cursor.moveToNext();
        }

        // assurez-vous de la fermeture du curseur
        cursor.close();
        return ListRecipe;
    }

    public Recipe getRecipe(int id) {
        Recipe recipe = new Recipe();

        Cursor cursor = database.query(MySQLiteHelper.TABLE_RECIPE,
                allColumns, MySQLiteHelper.COLUMN_ID_RECIPE + " = " + id, null, null, null, null);

        cursor.moveToFirst();
        while (!cursor.isAfterLast()) {
            recipe = cursorToComment(cursor);
            cursor.moveToNext();
        }
        cursor.close();
        return recipe;
    }

    @SuppressLint("Range")
    public RecipeResponse getFullRecipe(int recipeId) {
        RecipeResponse recipeResponse = new RecipeResponse();
        Cursor cursor = null;
        try {
            cursor = database.query(
                    MySQLiteHelper.TABLE_RECIPE + " LEFT JOIN " +
                            MySQLiteHelper.TABLE_USER + " ON " +
                            MySQLiteHelper.TABLE_RECIPE + "." + MySQLiteHelper.COLUMN_ID_FRK_USER_RECIPE + " = " +
                            MySQLiteHelper.TABLE_USER + "." + MySQLiteHelper.COLUMN_ID_USER +
                            " LEFT JOIN " +
                            MySQLiteHelper.TABLE_DETAIL_RECIPE + " ON " +
                            MySQLiteHelper.TABLE_RECIPE + "." + MySQLiteHelper.COLUMN_ID_RECIPE + " = " +
                            MySQLiteHelper.TABLE_DETAIL_RECIPE + "." + MySQLiteHelper.COLUMN_FRK_RECIPE +
                            " LEFT JOIN " +
                            MySQLiteHelper.TABLE_INGREDIENT_RECIPE + " ON " +
                            MySQLiteHelper.TABLE_RECIPE + "." + MySQLiteHelper.COLUMN_ID_RECIPE + " = " +
                            MySQLiteHelper.TABLE_INGREDIENT_RECIPE + "." + MySQLiteHelper.COLUMN_FRK_RECIPE +
                            " LEFT JOIN " +
                            MySQLiteHelper.TABLE_STEP_RECIPE + " ON " +
                            MySQLiteHelper.TABLE_RECIPE + "." + MySQLiteHelper.COLUMN_ID_RECIPE + " = " +
                            MySQLiteHelper.TABLE_STEP_RECIPE + "." + MySQLiteHelper.COLUMN_FRK_RECIPE +
                            " LEFT JOIN " +
                            MySQLiteHelper.TABLE_REVIEW_RECIPE + " ON " +
                            MySQLiteHelper.TABLE_RECIPE + "." + MySQLiteHelper.COLUMN_ID_RECIPE + " = " +
                            MySQLiteHelper.TABLE_REVIEW_RECIPE + "." + MySQLiteHelper.COLUMN_FRK_RECIPE,
                    allColumns,
                    MySQLiteHelper.TABLE_RECIPE + "." + MySQLiteHelper.COLUMN_ID_RECIPE + " = ?",
                    new String[]{String.valueOf(recipeId)},
                    null, null, null
            );


            try {
                    // SQL query to fetch recipe data

                    // Populate RecipeResponse object
                    if (cursor != null && cursor.moveToFirst()) {
                        // Populate Recipe object
                        Recipe recipe = new Recipe();
                        recipe.setId_recipe(Integer.parseInt(cursor.getString(cursor.getColumnIndex("Id_recipe"))));
                        recipe.setNom_recipe(cursor.getString(cursor.getColumnIndex("Nom_Recipe")));
                        // Populate other properties of Recipe object

                        // Populate User object
                        User user = new User();
                        user.setId_User(Integer.parseInt(cursor.getString(cursor.getColumnIndex("Id_user"))));
                        user.setUsername(cursor.getString(cursor.getColumnIndex("username")));
                        // Populate other properties of User object

                        // Populate Detail_Recipe object
                        Detail_Recipe detailRecipe = new Detail_Recipe();
                        detailRecipe.setId_detail_recipe(cursor.getInt(cursor.getColumnIndex("Id_detail_recipe")));
                        detailRecipe.setDt_recipe(cursor.getString(cursor.getColumnIndex("description")));
                        // Populate other properties of Detail_Recipe object

                        // Populate Ingredients list
                        List<Ingredients> ingredientsList = new ArrayList<>();
                        // Populate ingredientsList from resultSet

                        // Populate Reviews list
                        List<Review> reviewsList = new ArrayList<>();
                        // Populate reviewsList from resultSet

                        // Populate Steps list
                        List<Step> stepsList = new ArrayList<>();
                        // Populate stepsList from resultSet

                        // Set populated objects in RecipeResponse
                        recipeResponse.setRecipe(recipe);
                        recipeResponse.setUser(user);
                        recipeResponse.setDetail_recipe(detailRecipe);
                        recipeResponse.setIngredients(ingredientsList);
                        recipeResponse.setReviews(reviewsList);
                        recipeResponse.setSteps(stepsList);
                    }
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            } finally {
                if (cursor != null) {
                    cursor.close();
                }
                database.close(); // Close the database connection
            }

            return recipeResponse;
    }

    public void UpdateRecipe(Recipe recipe, int id) {
        open();
        ContentValues values = new ContentValues();
        //values.put(MySQLiteHelper.COLUMN_ICON_RECIPE, recipe.getIcon_recipe());
        values.put(MySQLiteHelper.COLUMN_FAV_RECIPE, recipe.getFav());
        values.put(MySQLiteHelper.COLUMN_NOM_RECIPE, recipe.getNom_recipe());
        database.update(MySQLiteHelper.TABLE_RECIPE, values, MySQLiteHelper.COLUMN_ID_RECIPE + " = " + id, null);
        close();
    }


}
