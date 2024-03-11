package com.example.notecook.Data;

import static com.example.notecook.Data.MySQLiteHelperTable.*;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.example.notecook.Fragement.Favorite_User_Recipe;
import com.example.notecook.Fragement.Favorite_User_Recipe;

import java.util.ArrayList;
import java.util.List;

public class FavRecipeDataSource {
    private static SQLiteDatabase database;
    private static String[] allColumns = {COLUMN_ID_FAV_USER, COLUMN_FRK_RECIPE_FAV,
            COLUMN_FRK_USER_FAV};
    private MySQLiteHelper dbHelper;


    public FavRecipeDataSource(Context context) {
        dbHelper = new MySQLiteHelper(context);
    }


    private static Favorite_User_Recipe cursorToComment(Cursor cursor) {

        Favorite_User_Recipe fav = new Favorite_User_Recipe();
        fav.setId(cursor.getInt(0));
        fav.setUserId(cursor.getInt(1));
        fav.setRecipeId(cursor.getInt(2));
        return fav;
    }

    // Method to check if a record exists
    public boolean isRecordExist(String tableName, String columnName, String value) {

        Cursor cursor = database.query(tableName, null, columnName + "=?", new String[]{value}, null, null, null);
        boolean exists = (cursor.getCount() > 0);
        cursor.close();
        return exists;
    }
    // Method to insert data
    public long insertData(String tableName, ContentValues values) {
        return database.insert(tableName, null, values);
    }

    /*
     * insert the value in the Image table
     */
    public Favorite_User_Recipe insertFav_recipe(Favorite_User_Recipe favorite_user_recipe) {
        ContentValues values = new ContentValues();

        values.put(MySQLiteHelper.COLUMN_ID_FAV_USER  , favorite_user_recipe.getId());
        values.put(MySQLiteHelper.COLUMN_FRK_USER_FAV , favorite_user_recipe.getUserId());
        values.put(MySQLiteHelper.COLUMN_FRK_RECIPE_FAV, favorite_user_recipe.getRecipeId());

        long insertId = database.insert(TABLE_FAV_RECIPE_USER, null,
                values);
        Cursor cursor = database.query(MySQLiteHelper.TABLE_FAV_RECIPE_USER,
                allColumns, COLUMN_ID_FAV_USER + " = " + insertId, null,
                null, null, null);
        cursor.moveToFirst();
        Favorite_User_Recipe newFav = cursorToComment(cursor);
        cursor.close();
        return newFav;
    }

    /*
     * Open DataBase
     */
    public void open() throws SQLException {
        database = dbHelper.getWritableDatabase();
        if (!database.isReadOnly()) {
            // Enable foreign key constraints
            database.execSQL("PRAGMA foreign_keys=ON;");
        }
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
    public void deleteDR(Favorite_User_Recipe fav) {
        long id = fav.getId();
        Log.d("gps", "Image  deleted with id: " + id);
        database.delete(TABLE_FAV_RECIPE_USER, COLUMN_ID_FAV_USER
                + " = " + id, null);
    }

    public List<Favorite_User_Recipe> getAlFav() {
        List<Favorite_User_Recipe> ListFav = new ArrayList<>();

        Cursor cursor = database.query(TABLE_FAV_RECIPE_USER,
                allColumns, null, null, null, null, null);

        cursor.moveToFirst();
        while (!cursor.isAfterLast()) {
            Favorite_User_Recipe fav = cursorToComment(cursor);
            ListFav.add(fav);
            cursor.moveToNext();
        }
        // assurez-vous de la fermeture du curseur
        cursor.close();
        return ListFav;
    }

    public Favorite_User_Recipe select_DR_BYid(int id) {
        Favorite_User_Recipe ListFavById = new Favorite_User_Recipe();
        Cursor cursor = database.query(TABLE_FAV_RECIPE_USER,
                allColumns, COLUMN_ID_FAV_USER + " = " + id, null, null, null, null);

        cursor.moveToFirst();
        while (!cursor.isAfterLast()) {
            ListFavById = cursorToComment(cursor);
            cursor.moveToNext();
        }
        return ListFavById;
    }

    public List<Favorite_User_Recipe> select_DR_BYidRecipe(int id) {
        List<Favorite_User_Recipe> ListDRByidRecipe = new ArrayList<>();
        Cursor cursor = database.query(TABLE_FAV_RECIPE_USER,
                allColumns, MySQLiteHelper.COLUMN_FRK_RECIPE_DETAIL + " = " + id, null, null, null, null);

        cursor.moveToFirst();
        while (!cursor.isAfterLast()) {
            Favorite_User_Recipe dr = cursorToComment(cursor);
            ListDRByidRecipe.add(dr);
            cursor.moveToNext();
        }
        return ListDRByidRecipe;
    }

    public void Update_Fav(Favorite_User_Recipe DR, int id) {

        ContentValues data = new ContentValues();
        data.put(COLUMN_FRK_USER_FAV, DR.getUserId());
        data.put(COLUMN_FRK_RECIPE_FAV, DR.getRecipeId());

        database.update(TABLE_FAV_RECIPE_USER, data, COLUMN_ID_FAV_USER + " = " + id, null);

    }
}
