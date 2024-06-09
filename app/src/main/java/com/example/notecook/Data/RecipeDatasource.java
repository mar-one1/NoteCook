package com.example.notecook.Data;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Environment;
import android.util.Log;

import com.example.notecook.Model.Recipe;
import com.example.notecook.Utils.ImageHelper;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class RecipeDatasource {

    private static SQLiteDatabase database;
    private static String[] allColumns = {MySQLiteHelper.COLUMN_ID_RECIPE,
            MySQLiteHelper.COLUMN_ICON_RECIPE, MySQLiteHelper.COLUMN_ICON_RECIPE_PATH, MySQLiteHelper.COLUMN_FAV_RECIPE, MySQLiteHelper.COLUMN_NOM_RECIPE,
            MySQLiteHelper.COLUMN_ID_FRK_USER_RECIPE};
    private MySQLiteHelper dbHelper;

    public RecipeDatasource(Context context) {
        dbHelper = new MySQLiteHelper(context);
    }

    // Method to check if a record exists
    public boolean isRecordExist(String tableName, String columnName, String value) {
        open();
        Cursor cursor = database.query(tableName, null, columnName + " = ?", new String[]{value}, null, null, null);
        boolean exists = (cursor.getCount() > 0);
        cursor.close();
        close();
        return exists;
    }



    public boolean isRecordExistX(String tableName, String columnName, String value) {
        open();
        boolean exists = false;
        Cursor cursor = null;

        try {
            String[] projection = {columnName}; // Only select the column needed for checking existence
            cursor = database.query(tableName, projection, columnName + " = ?", new String[]{value}, null, null, null);
            exists = (cursor.getCount() > 0);
        } catch (Exception e) {
            // Handle any exceptions
            e.printStackTrace();
        } finally {
            if (cursor != null) {
                cursor.close();
            }
            close();
        }

        return exists;
    }

    /*
     * insert the value in the Image table
     */
    public Recipe createRecipe(byte[] ICONBYTE, String Pathicon, String nom_recipe, int fav, int frk) {
        open();
        ContentValues values = new ContentValues();
        values.put(MySQLiteHelper.COLUMN_ICON_RECIPE, ICONBYTE);
        values.put(MySQLiteHelper.COLUMN_ICON_RECIPE_PATH, Pathicon);
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
        values.put(MySQLiteHelper.COLUMN_ICON_RECIPE_PATH, recipe.getPathimagerecipe());
        values.put(MySQLiteHelper.COLUMN_FAV_RECIPE, recipe.getFav());
        values.put(MySQLiteHelper.COLUMN_NOM_RECIPE, recipe.getNom_recipe());
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
        recipe.setPathimagerecipe(cursor.getString(2));
        recipe.setFav(cursor.getInt(3));
        recipe.setNom_recipe(cursor.getString(4));
        recipe.setFrk_user(cursor.getInt(5));
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

    public ArrayList<String> getAllRecipesImagePath() {
        open();
        ArrayList<String> List = new ArrayList<>();
        Cursor cursor = database.query(MySQLiteHelper.TABLE_RECIPE,
                allColumns, null, null, null, null, null);

        cursor.moveToFirst();
        while (!cursor.isAfterLast()) {
            Recipe Recipe = cursorToComment(cursor);
            List.add(Recipe.getPathimagerecipe());
            cursor.moveToNext();
        }
        // assurez-vous de la fermeture du curseur
        cursor.close();
        close();
        return List;
    }


    public List<Recipe> getRecipeByIdUser(int id, int pageNumber, int itemsPerPage) {
        open();
        List<Recipe> ListRecipe = new ArrayList<>();
        int offset = (pageNumber - 1) * itemsPerPage; // Calculate the offset
        try {
            Cursor cursor = database.query(MySQLiteHelper.TABLE_RECIPE,
                    allColumns, MySQLiteHelper.COLUMN_ID_FRK_USER_RECIPE + " = ?",
                    new String[]{String.valueOf(id)}, null, null, null, offset + "," + itemsPerPage);
            cursor.moveToFirst();
            while (!cursor.isAfterLast()) {
                Recipe Recipe = cursorToComment(cursor);
                ListRecipe.add(Recipe);
                cursor.moveToNext();
            }
            cursor.close();
        } catch (Exception e) {
            // Handle any exceptions
            e.printStackTrace();
        }
        // Close the cursor and database connection
        close();
        return ListRecipe;
    }

    public List<Recipe> getRecipeByIdUser(int id) {
        open();
        List<Recipe> ListRecipe = new ArrayList<>();
        try {
            Cursor cursor = database.query(MySQLiteHelper.TABLE_RECIPE,
                    allColumns, MySQLiteHelper.COLUMN_ID_FRK_USER_RECIPE + " = ?",
                    new String[]{String.valueOf(id)}, null, null, null, null);
            cursor.moveToFirst();
            while (!cursor.isAfterLast()) {
                Recipe Recipe = cursorToComment(cursor);
                ListRecipe.add(Recipe);
                cursor.moveToNext();
            }
            cursor.close();
        } catch (Exception e) {
            // Handle any exceptions
            e.printStackTrace();
        }
        // Close the cursor and database connection
        close();
        return ListRecipe;
    }


    public Recipe getRecipe(int id) {
        open();
        Recipe recipe = new Recipe();

        Cursor cursor = database.query(MySQLiteHelper.TABLE_RECIPE,
                allColumns, MySQLiteHelper.COLUMN_ID_RECIPE + " = " + id, null, null, null, null);

        cursor.moveToFirst();
        while (!cursor.isAfterLast()) {
            recipe = cursorToComment(cursor);
            cursor.moveToNext();
        }
        cursor.close();
        close();
        return recipe;
    }

    public void UpdateRecipe(Recipe recipe, int id) {
        open();
        ContentValues values = new ContentValues();
        values.put(MySQLiteHelper.COLUMN_ICON_RECIPE, recipe.getIcon_recipe());
        values.put(MySQLiteHelper.COLUMN_ICON_RECIPE_PATH, recipe.getPathimagerecipe());
        values.put(MySQLiteHelper.COLUMN_FAV_RECIPE, recipe.getFav());
        values.put(MySQLiteHelper.COLUMN_NOM_RECIPE, recipe.getNom_recipe());
        database.update(MySQLiteHelper.TABLE_RECIPE, values, MySQLiteHelper.COLUMN_ID_RECIPE + " = " + id, null);
        close();
    }

    public int UpdateRecipe(Context context,Bitmap bitmap, int id) {
        open();
        String imagePath = ImageHelper.saveImageToInternalStorage(context,bitmap,"RecipeImages");
        ContentValues values = new ContentValues();
        values.put(MySQLiteHelper.COLUMN_ICON_RECIPE_PATH, imagePath);
        int updateid = database.update(MySQLiteHelper.TABLE_RECIPE, values, MySQLiteHelper.COLUMN_ID_RECIPE + " = " + id, null);
        close();
        return updateid;
    }



}