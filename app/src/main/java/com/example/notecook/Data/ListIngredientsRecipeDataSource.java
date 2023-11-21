package com.example.notecook.Data;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.example.notecook.Model.Ingredient_recipe;

import java.util.ArrayList;

public class ListIngredientsRecipeDataSource {
    private static SQLiteDatabase database;
    private static String[] allColumns = {MySQLiteHelper.COLUMN_ID_LIST_INGREDIENT_RECIPE, MySQLiteHelper.COLUMN_FRK_ID_INGREDIENT_RECIPE,
            MySQLiteHelper.COLUMN_FRK_ID_RECIPE
    };
    private MySQLiteHelper dbHelper;


    public ListIngredientsRecipeDataSource(Context context) {
        dbHelper = new MySQLiteHelper(context);
    }

    /*
     * insert the value in the Image table
     */


    public static Ingredient_recipe Create_List_ingredient_recipe( int IdIngredient,int IdRecipe ) {
        ContentValues values = new ContentValues();

        values.put(MySQLiteHelper.COLUMN_FRK_ID_INGREDIENT_RECIPE, IdIngredient);
        values.put(MySQLiteHelper.COLUMN_FRK_ID_RECIPE, IdRecipe);

        long insertId = database.insert(MySQLiteHelper.TABLE_LIST_INGREDIENT_RECIPE, null,
                values);
        Cursor cursor = database.query(MySQLiteHelper.TABLE_LIST_INGREDIENT_RECIPE,
                allColumns, MySQLiteHelper.COLUMN_ID_LIST_INGREDIENT_RECIPE + " = " + insertId, null,
                null, null, null);
        cursor.moveToFirst();
        Ingredient_recipe newDR = cursorToComment(cursor);
        cursor.close();
        return newDR;
    }

    private static Ingredient_recipe cursorToComment(Cursor cursor) {

        Ingredient_recipe listIngredient = new Ingredient_recipe();
        listIngredient.setId_Ingeredient_recipe(cursor.getInt(0));
        listIngredient.setFrk_idIngredient(cursor.getInt(1));
        listIngredient.setFrk_idRecipe(cursor.getInt(2));

        return listIngredient;
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
    public void deleteListIgredient(Ingredient_recipe DR) {
        long id = DR.getId_Ingeredient_recipe();
        Log.d("gps", "Image  deleted with id: " + id);
        database.delete(MySQLiteHelper.TABLE_LIST_INGREDIENT_RECIPE, MySQLiteHelper.COLUMN_ID_LIST_INGREDIENT_RECIPE
                + " = " + id, null);
    }

    public ArrayList<Ingredient_recipe> getAllDR() {
        ArrayList<Ingredient_recipe> ListDR = new ArrayList<>();

        Cursor cursor = database.query(MySQLiteHelper.TABLE_LIST_INGREDIENT_RECIPE,
                allColumns, null, null, null, null, null);

        cursor.moveToFirst();
        while (!cursor.isAfterLast()) {
            Ingredient_recipe DR = cursorToComment(cursor);
            ListDR.add(DR);
            cursor.moveToNext();
        }
        // assurez-vous de la fermeture du curseur
        cursor.close();
        return ListDR;
    }

    public Ingredient_recipe select_ListIngredients_BYid(int id) {
        Ingredient_recipe ListDRByid = new Ingredient_recipe();
        Cursor cursor = database.query(MySQLiteHelper.TABLE_LIST_INGREDIENT_RECIPE,
                allColumns , MySQLiteHelper.COLUMN_ID_LIST_INGREDIENT_RECIPE + " = " + id , null, null, null, null);

        cursor.moveToFirst();
        while (!cursor.isAfterLast()) {
            Ingredient_recipe dr = cursorToComment(cursor);
            ListDRByid =dr;
            cursor.moveToNext();
        }
        return ListDRByid;
    }
    public Ingredient_recipe select_BYidListIngredient(int id) {
        Ingredient_recipe ListDRByidListIngredient = new Ingredient_recipe();
        Cursor cursor = database.query(MySQLiteHelper.TABLE_LIST_INGREDIENT_RECIPE,
                allColumns , MySQLiteHelper.COLUMN_ID_LIST_INGREDIENT_RECIPE + " = " + id , null, null, null, null);

        cursor.moveToFirst();
        while (!cursor.isAfterLast()) {
            Ingredient_recipe dr = cursorToComment(cursor);
            ListDRByidListIngredient =dr;
            cursor.moveToNext();
        }
        return ListDRByidListIngredient;
    }

    public void Update_Detail_Recipe(Ingredient_recipe DR,int id) {

        ContentValues data = new ContentValues();
        data.put(MySQLiteHelper.COLUMN_FRK_ID_INGREDIENT_RECIPE , DR.getFrk_idIngredient());
        data.put(MySQLiteHelper.COLUMN_FRK_ID_RECIPE, DR.getFrk_idRecipe());

        database.update(MySQLiteHelper.TABLE_LIST_INGREDIENT_RECIPE, data, MySQLiteHelper.COLUMN_ID_LIST_INGREDIENT_RECIPE + " = " + id, null);

    }
}
