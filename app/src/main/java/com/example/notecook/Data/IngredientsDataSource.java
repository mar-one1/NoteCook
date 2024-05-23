package com.example.notecook.Data;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.example.notecook.Model.Ingredients;

import java.util.ArrayList;
import java.util.List;

public class IngredientsDataSource {

    private static SQLiteDatabase database;
    private static String[] allColumns = {
            MySQLiteHelper.COLUMN_ID_INGREDIENT_RECIPE,
            MySQLiteHelper.COLUMN_INGREDIENT_RECIPE,
            MySQLiteHelper.COLUMN_POIDINGREDIENT_RECIPE,
            MySQLiteHelper.COLUMN_UNITEINGREDIENT_RECIPE,
            MySQLiteHelper.COLUMN_FRK_DETAIL_INGREDIENT_RECIPE
    };
    private MySQLiteHelper dbHelper;


    public IngredientsDataSource(Context context) {
        dbHelper = new MySQLiteHelper(context);
    }

    /*
     * insert the value in the Image table
     */


    public Ingredients insert_Ingerdeient(Ingredients ingredients,int id_recipe) {
        open();
        ContentValues values = new ContentValues();
        values.put(MySQLiteHelper.COLUMN_INGREDIENT_RECIPE, ingredients.getNome());
        values.put(MySQLiteHelper.COLUMN_POIDINGREDIENT_RECIPE, ingredients.getPoid_unite());
        values.put(MySQLiteHelper.COLUMN_UNITEINGREDIENT_RECIPE, ingredients.getUnit());
        values.put(MySQLiteHelper.COLUMN_FRK_DETAIL_INGREDIENT_RECIPE, id_recipe);

        long insertId = database.insert(MySQLiteHelper.TABLE_INGREDIENT_RECIPE, null,
                values);
        Cursor cursor = database.query(MySQLiteHelper.TABLE_INGREDIENT_RECIPE,
                allColumns, MySQLiteHelper.COLUMN_ID_INGREDIENT_RECIPE + " = " + insertId, null,
                null, null, null);
        cursor.moveToFirst();
        Ingredients ingerdeients = cursorToComment(cursor);
        cursor.close();
        close();
        return ingerdeients;
    }

    public List<Ingredients> insertIngredients(List<Ingredients> ingredientsList,int id_recipe) {
        List<Ingredients> insertedIngredients = new ArrayList<>();

        open();
        for (Ingredients ingredients : ingredientsList) {
            ContentValues values = new ContentValues();
            values.put(MySQLiteHelper.COLUMN_INGREDIENT_RECIPE, ingredients.getNome());
            values.put(MySQLiteHelper.COLUMN_POIDINGREDIENT_RECIPE, ingredients.getPoid_unite());
            values.put(MySQLiteHelper.COLUMN_UNITEINGREDIENT_RECIPE, ingredients.getUnit());
            values.put(MySQLiteHelper.COLUMN_FRK_DETAIL_INGREDIENT_RECIPE, id_recipe);

            long insertId = database.insert(MySQLiteHelper.TABLE_INGREDIENT_RECIPE, null, values);

            Cursor cursor = database.query(MySQLiteHelper.TABLE_INGREDIENT_RECIPE,
                    allColumns, MySQLiteHelper.COLUMN_ID_INGREDIENT_RECIPE + " = " + insertId, null,
                    null, null, null);
            cursor.moveToFirst();
            Ingredients insertedIngredient = cursorToComment(cursor);
            cursor.close();

            insertedIngredients.add(insertedIngredient);
        }
        close();
        return insertedIngredients;
    }


    private Ingredients cursorToComment(Cursor cursor) {

        Ingredients ingredients = new Ingredients();
        ingredients.setId(cursor.getInt(0));
        ingredients.setNome(cursor.getString(1));
        ingredients.setPoid_unite(cursor.getDouble(2));
        ingredients.setUnit(cursor.getString(3));
        ingredients.setFrk_recipe(cursor.getInt(4));

        return ingredients;
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
    public void deleteIngredient(Ingredients ingerdeients) {
        open();
        long id = ingerdeients.getId();
        Log.d("gps", "Image  deleted with id: " + id);
        database.delete(MySQLiteHelper.TABLE_INGREDIENT_RECIPE, MySQLiteHelper.COLUMN_ID_INGREDIENT_RECIPE
                + " = " + id, null);
        close();
    }

    public ArrayList<Ingredients> getAllIngredeients() {
        open();
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
        close();
        return ListIngerdeients;
    }

    public ArrayList<Ingredients> getListIngerdientsByidRecipe(int id_recipe) {
        open();
        ArrayList<Ingredients> ListingerdeientsByid = new ArrayList<>();
        Cursor cursor = database.query(MySQLiteHelper.TABLE_INGREDIENT_RECIPE,
                allColumns, MySQLiteHelper.COLUMN_FRK_DETAIL_INGREDIENT_RECIPE + " = " + id_recipe, null, null, null, null);

        cursor.moveToFirst();
        while (!cursor.isAfterLast()) {
            Ingredients DR = cursorToComment(cursor);
            ListingerdeientsByid.add(DR);
            cursor.moveToNext();
        }
        close();
        return ListingerdeientsByid;
    }

    public Ingredients getIngerdientsByidRecipe(int id_recipe) {
        open();
        Ingredients ingerdeientsByid = new Ingredients();
        Cursor cursor = database.query(MySQLiteHelper.TABLE_INGREDIENT_RECIPE,
                allColumns, MySQLiteHelper.COLUMN_FRK_DETAIL_INGREDIENT_RECIPE + " = " + id_recipe, null, null, null, null);

        cursor.moveToFirst();
        while (!cursor.isAfterLast()) {
            ingerdeientsByid = cursorToComment(cursor);
            cursor.moveToNext();
        }
        close();
        return ingerdeientsByid;
    }

    public void Update_Ingerdeients(Ingredients ingerdeients,int id) {
        open();
        ContentValues data = new ContentValues();
        data.put(MySQLiteHelper.COLUMN_INGREDIENT_RECIPE , ingerdeients.getNome());
        data.put(MySQLiteHelper.COLUMN_POIDINGREDIENT_RECIPE, ingerdeients.getPoid_unite());
        data.put(MySQLiteHelper.COLUMN_UNITEINGREDIENT_RECIPE, ingerdeients.getUnit());

        database.update(MySQLiteHelper.TABLE_INGREDIENT_RECIPE, data, MySQLiteHelper.COLUMN_ID_INGREDIENT_RECIPE + " = " + id, null);
        close();
    }
}
