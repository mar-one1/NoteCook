package com.example.notecook.Data;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.example.notecook.Model.Recipe;

import java.util.ArrayList;
import java.util.List;

public class RecipeDatasource {

    private static SQLiteDatabase database;
    private static String[] allColumns = {MySQLiteHelper.COLUMN_ID_RECIPE,
            MySQLiteHelper.COLUMN_ICON_RECIPE, MySQLiteHelper.COLUMN_FAV_RECIPE,MySQLiteHelper.COLUMN_NOM_RECIPE,
            MySQLiteHelper.COLUMN_ID_FRK_USER_RECIPE};
    private MySQLiteHelper dbHelper;

    public RecipeDatasource(Context context) {
        dbHelper = new MySQLiteHelper(context);
    }

    /*
     * insert the value in the Image table
     */
    public static Recipe createRecipe(byte[] ICONBYTE,String nom_recipe, int fav , int frk) {
        ContentValues values = new ContentValues();

        values.put(MySQLiteHelper.COLUMN_ICON_RECIPE, ICONBYTE);
        values.put(MySQLiteHelper.COLUMN_FAV_RECIPE, fav);
        values.put(MySQLiteHelper.COLUMN_NOM_RECIPE,nom_recipe );
        values.put(MySQLiteHelper.COLUMN_ID_FRK_USER_RECIPE,frk);

        long insertId = database.insert(MySQLiteHelper.TABLE_RECIPE, null,
                values);
        Cursor cursor = database.query(MySQLiteHelper.TABLE_RECIPE,
                allColumns, MySQLiteHelper.COLUMN_ID_RECIPE + " = " + insertId, null,
                null, null, null);
        cursor.moveToFirst();
        Recipe newRecipe = cursorToComment(cursor);
        cursor.close();
        return newRecipe;
    }

    public static long InsertRecipe(Recipe recipe,int id) {
        ContentValues values = new ContentValues();
        values.put(MySQLiteHelper.COLUMN_ICON_RECIPE, recipe.getIcon_recipe());
        values.put(MySQLiteHelper.COLUMN_NOM_RECIPE,recipe.getNom_recipe() );
        values.put(MySQLiteHelper.COLUMN_FAV_RECIPE, recipe.getFav());
        values.put(MySQLiteHelper.COLUMN_ID_FRK_USER_RECIPE,id);

       // long insertId = database.insert(MySQLiteHelper.TABLE_RECIPE, null, values);

        long insertId = database.insert(MySQLiteHelper.TABLE_RECIPE,
                null, values);

        return insertId;
    }

    private static Recipe cursorToComment(Cursor cursor) {
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
        long id = recipe.getId_recipe();
        Log.d("gps", "Image  deleted with id: " + id);
        database.delete(MySQLiteHelper.TABLE_RECIPE, MySQLiteHelper.COLUMN_ID_RECIPE
                + " = " + id, null);
    }

    public ArrayList<Recipe> getAllRecipes() {
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
        return ListRecipe;
    }
    public LiveData<List<Recipe>> getRecipeById(int id) {
        MutableLiveData<List<Recipe>> ListRecipe = new MutableLiveData<>();
        List<Recipe> list  = new ArrayList<>();

        Cursor cursor = database.query(MySQLiteHelper.TABLE_RECIPE,
                allColumns, MySQLiteHelper.COLUMN_ID_FRK_USER_RECIPE + " = " + id, null, null, null, null);

        cursor.moveToFirst();
        while (!cursor.isAfterLast()) {
            Recipe Recipe = cursorToComment(cursor);
            list.add(Recipe);
            cursor.moveToNext();
        }
        ListRecipe.setValue(list);
        // assurez-vous de la fermeture du curseur
        cursor.close();
        return ListRecipe;
    }

    public void UpdateRecipe(Recipe recipe,int id) {

        ContentValues values = new ContentValues();
        //values.put(MySQLiteHelper.COLUMN_ICON_RECIPE, recipe.getIcon_recipe());
        values.put(MySQLiteHelper.COLUMN_FAV_RECIPE, recipe.getFav());
        values.put(MySQLiteHelper.COLUMN_NOM_RECIPE,recipe.getNom_recipe());
        database.update(MySQLiteHelper.TABLE_RECIPE, values, MySQLiteHelper.COLUMN_ID_RECIPE + " = " + id, null);
    }


}
