package com.example.notecook.Data;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.example.notecook.Model.Detail_Recipe;

import java.util.ArrayList;

public class DetailRecipeDataSource {

    private static SQLiteDatabase database;
    private static String[] allColumns = {MySQLiteHelper.COLUMN_ID_DETAIL_RECIPE, MySQLiteHelper.COLUMN_DETAIL, MySQLiteHelper.COLUMN_LEVEL_DR,
            MySQLiteHelper.COLUMN_TIME_DR, MySQLiteHelper.COLUMN_RATE_DR,
            MySQLiteHelper.COLUMN_CALORIES, MySQLiteHelper.COLUMN_FRK_RECIPE_DETAIL
    };
    private MySQLiteHelper dbHelper;


    public DetailRecipeDataSource(Context context) {
        dbHelper = new MySQLiteHelper(context);
    }

    /*
     * insert the value in the Image table
     */
    public  Detail_Recipe insertDetail_recipe(Detail_Recipe detail_recipe,int id) {
        open();
        ContentValues values = new ContentValues();
        values.put(MySQLiteHelper.COLUMN_DETAIL, detail_recipe.getDt_recipe());
        values.put(MySQLiteHelper.COLUMN_LEVEL_DR, detail_recipe.getLevel());
        values.put(MySQLiteHelper.COLUMN_TIME_DR, detail_recipe.getTime());
        values.put(MySQLiteHelper.COLUMN_RATE_DR, detail_recipe.getRate());
        values.put(MySQLiteHelper.COLUMN_CALORIES, detail_recipe.getCal());
        values.put(MySQLiteHelper.COLUMN_FRK_RECIPE_DETAIL, id);

        long insertId = database.insert(MySQLiteHelper.TABLE_DETAIL_RECIPE, null,
                values);
        Cursor cursor = database.query(MySQLiteHelper.TABLE_DETAIL_RECIPE,
                allColumns, MySQLiteHelper.COLUMN_ID_DETAIL_RECIPE + " = " + insertId, null,
                null, null, null);
        cursor.moveToFirst();
        Detail_Recipe newDR = cursorToComment(cursor);
        cursor.close();
        close();
        return newDR;
    }

    public  Detail_Recipe Create_Detail_Recipe(String detail,int time, int rate, String level, int calories, int frk_recipe) {
        open();
        ContentValues values = new ContentValues();

        values.put(MySQLiteHelper.COLUMN_DETAIL, detail);
        values.put(MySQLiteHelper.COLUMN_LEVEL_DR, level);
        values.put(MySQLiteHelper.COLUMN_TIME_DR, time);
        values.put(MySQLiteHelper.COLUMN_RATE_DR, rate);
        values.put(MySQLiteHelper.COLUMN_CALORIES, calories);
        values.put(MySQLiteHelper.COLUMN_FRK_RECIPE_DETAIL, frk_recipe);

        long insertId = database.insert(MySQLiteHelper.TABLE_DETAIL_RECIPE, null,
                values);
        Cursor cursor = database.query(MySQLiteHelper.TABLE_DETAIL_RECIPE,
                allColumns, MySQLiteHelper.COLUMN_ID_DETAIL_RECIPE + " = " + insertId, null,
                null, null, null);
        cursor.moveToFirst();
        Detail_Recipe newDR = cursorToComment(cursor);
        cursor.close();
        close();
        return newDR;
    }

    public  Detail_Recipe cursorToComment(Cursor cursor) {

        Detail_Recipe detail_recipe = new Detail_Recipe();
        detail_recipe.setId_detail_recipe(cursor.getInt(0));
        detail_recipe.setDt_recipe(cursor.getString(1));
        detail_recipe.setLevel(cursor.getString(2));
        detail_recipe.setTime(cursor.getInt(3));
        detail_recipe.setRate(cursor.getInt(4));
        detail_recipe.setCal(cursor.getInt(5));
        detail_recipe.setFrk_recipe(cursor.getInt(6));
        return detail_recipe;
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
    public void deleteDR(Detail_Recipe DR) {
        open();
        long id = DR.getId_detail_recipe();
        Log.d("gps", "Image  deleted with id: " + id);
        database.delete(MySQLiteHelper.TABLE_DETAIL_RECIPE, MySQLiteHelper.COLUMN_ID_DETAIL_RECIPE
                + " = " + id, null);
        close();
    }

    public ArrayList<Detail_Recipe> getAllDR() {
        open();
        ArrayList<Detail_Recipe> ListDR = new ArrayList<>();

        Cursor cursor = database.query(MySQLiteHelper.TABLE_DETAIL_RECIPE,
                allColumns, null, null, null, null, null);

        cursor.moveToFirst();
        while (!cursor.isAfterLast()) {
            Detail_Recipe DR = cursorToComment(cursor);
            ListDR.add(DR);
            cursor.moveToNext();
        }
        // assurez-vous de la fermeture du curseur
        cursor.close();
        close();
        return ListDR;
    }

    public Detail_Recipe getDrById(int id) {
        open();
        Detail_Recipe DRByid = new Detail_Recipe();
        Cursor cursor = database.query(MySQLiteHelper.TABLE_DETAIL_RECIPE,
                allColumns , MySQLiteHelper.COLUMN_ID_DETAIL_RECIPE + " = " + id , null, null, null, null);

        cursor.moveToFirst();
        while (!cursor.isAfterLast()) {
            DRByid = cursorToComment(cursor);
            cursor.moveToNext();
        }
        close();
        return DRByid;
    }

    public Detail_Recipe getDrByIdRecipe(int id_recipe) {
        open();
        Detail_Recipe DRByIdRecipe = new Detail_Recipe();
        Cursor cursor = database.query(MySQLiteHelper.TABLE_DETAIL_RECIPE,
                allColumns , MySQLiteHelper.COLUMN_FRK_RECIPE_DETAIL + " = " + id_recipe , null, null, null, null);

        cursor.moveToFirst();
        while (!cursor.isAfterLast()) {
            DRByIdRecipe = cursorToComment(cursor);
            cursor.moveToNext();
        }
        close();
        return DRByIdRecipe;
    }

    public ArrayList<Detail_Recipe> select_DR_BYidRecipe(int id) {
        open();
        ArrayList<Detail_Recipe> ListDRByidRecipe = new ArrayList<>();
        Cursor cursor = database.query(MySQLiteHelper.TABLE_DETAIL_RECIPE,
                allColumns , MySQLiteHelper.COLUMN_FRK_RECIPE_DETAIL + " = " + id , null, null, null, null);

        cursor.moveToFirst();
        while (!cursor.isAfterLast()) {
            Detail_Recipe dr = cursorToComment(cursor);
            ListDRByidRecipe.add(dr);
            cursor.moveToNext();
        }
        close();
        return ListDRByidRecipe;
    }

    public void Update_Detail_Recipe(Detail_Recipe DR,int id) {
        open();
        ContentValues data = new ContentValues();

        data.put(MySQLiteHelper.COLUMN_DETAIL, DR.getDt_recipe());
        data.put(MySQLiteHelper.COLUMN_LEVEL_DR, DR.getLevel());
        data.put(MySQLiteHelper.COLUMN_TIME_DR , DR.getTime());
        data.put(MySQLiteHelper.COLUMN_RATE_DR, DR.getRate());
        data.put(MySQLiteHelper.COLUMN_CALORIES, DR.getCal());
        data.put(MySQLiteHelper.COLUMN_FRK_RECIPE_DETAIL, DR.getFrk_recipe());

        database.update(MySQLiteHelper.TABLE_DETAIL_RECIPE, data, MySQLiteHelper.COLUMN_ID_DETAIL_RECIPE + " = " + id, null);
        close();
    }
    public void Update_Detail_RecipeByIdRecipe(Detail_Recipe DR,int id) {
    open();
        ContentValues data = new ContentValues();
        data.put(MySQLiteHelper.COLUMN_DETAIL, DR.getDt_recipe());
        data.put(MySQLiteHelper.COLUMN_LEVEL_DR, DR.getLevel());
        data.put(MySQLiteHelper.COLUMN_TIME_DR , DR.getTime());
        data.put(MySQLiteHelper.COLUMN_RATE_DR, DR.getRate());
        data.put(MySQLiteHelper.COLUMN_CALORIES, DR.getCal());
        data.put(MySQLiteHelper.COLUMN_FRK_RECIPE_DETAIL, DR.getFrk_recipe());
        Log.d("found","update"+id);
        database.update(MySQLiteHelper.TABLE_DETAIL_RECIPE, data, MySQLiteHelper.COLUMN_FRK_RECIPE + " = " + id, null);
        close();
    }
}
