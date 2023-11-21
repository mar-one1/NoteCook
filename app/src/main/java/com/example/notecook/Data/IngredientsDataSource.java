package com.example.notecook.Data;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.example.notecook.Model.Ingredients;

import java.util.ArrayList;

public class IngredientsDataSource {

    private static SQLiteDatabase database;
    private static String[] allColumns = {
            MySQLiteHelper.COLUMN_ID_INGREDIENT_RECIPE,
            MySQLiteHelper.COLUMN_INGREDIENT_RECIPE,
            MySQLiteHelper.COLUMN_POIDINGREDIENT_RECIPE
    };
    private MySQLiteHelper dbHelper;


    public IngredientsDataSource(Context context) {
        dbHelper = new MySQLiteHelper(context);
    }

    /*
     * insert the value in the Image table
     */


    public static Ingredients Create_Ingerdeients(String nome, double poid) {
        ContentValues values = new ContentValues();

        values.put(MySQLiteHelper.COLUMN_INGREDIENT_RECIPE, nome);
        values.put(MySQLiteHelper.COLUMN_POIDINGREDIENT_RECIPE, poid);


        long insertId = database.insert(MySQLiteHelper.TABLE_INGREDIENT_RECIPE, null,
                values);
        Cursor cursor = database.query(MySQLiteHelper.TABLE_INGREDIENT_RECIPE,
                allColumns, MySQLiteHelper.COLUMN_ID_INGREDIENT_RECIPE + " = " + insertId, null,
                null, null, null);
        cursor.moveToFirst();
        Ingredients ingerdeients = cursorToComment(cursor);
        cursor.close();
        return ingerdeients;
    }

    private static Ingredients cursorToComment(Cursor cursor) {

        Ingredients ingerdeients = new Ingredients();
        ingerdeients.setId(cursor.getInt(0));
        ingerdeients.setNome(cursor.getString(1));
        ingerdeients.setPoid_unite(cursor.getInt(2));

        return ingerdeients;
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
    public void deleteIngerdeient(Ingredients ingerdeients) {
        long id = ingerdeients.getId();
        Log.d("gps", "Image  deleted with id: " + id);
        database.delete(MySQLiteHelper.TABLE_INGREDIENT_RECIPE, MySQLiteHelper.COLUMN_ID_INGREDIENT_RECIPE
                + " = " + id, null);
    }

    public ArrayList<Ingredients> getAllIngerdeients() {
        ArrayList<Ingredients> ListIngerdeients = new ArrayList<>();

        Cursor cursor = database.query(MySQLiteHelper.TABLE_INGREDIENT_RECIPE,
                allColumns, null, null, null, null, null);

        cursor.moveToFirst();
        while (!cursor.isAfterLast()) {
            Ingredients DR = cursorToComment(cursor);
            ListIngerdeients.add(DR);
            cursor.moveToNext();
        }
        // assurez-vous de la fermeture du curseur
        cursor.close();
        return ListIngerdeients;
    }

    public Ingredients select_Ingerdeients_BYid(int id) {
        Ingredients ListingerdeientsByid = new Ingredients();
        Cursor cursor = database.query(MySQLiteHelper.TABLE_INGREDIENT_RECIPE,
                allColumns , MySQLiteHelper.COLUMN_ID_INGREDIENT_RECIPE + " = " + id , null, null, null, null);

        cursor.moveToFirst();
        while (!cursor.isAfterLast()) {
            ListingerdeientsByid = cursorToComment(cursor);
            cursor.moveToNext();
        }
        return ListingerdeientsByid;
    }

    public void Update_Ingerdeients(Ingredients ingerdeients,int id) {

        ContentValues data = new ContentValues();
        data.put(MySQLiteHelper.COLUMN_INGREDIENT_RECIPE , ingerdeients.getNome());
        data.put(MySQLiteHelper.COLUMN_POIDINGREDIENT_RECIPE, ingerdeients.getPoid_unite());

        database.update(MySQLiteHelper.TABLE_INGREDIENT_RECIPE, data, MySQLiteHelper.COLUMN_ID_INGREDIENT_RECIPE + " = " + id, null);

    }
}
