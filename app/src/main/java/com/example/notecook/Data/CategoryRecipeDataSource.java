package com.example.notecook.Data;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.example.notecook.Model.Category_Recipe;
import com.example.notecook.Model.Recipe;

import java.util.ArrayList;
import java.util.List;

public class CategoryRecipeDataSource {
    private static SQLiteDatabase database;
    private static String[] allColumns = {MySQLiteHelper.COLUMN_ID_CATEGORIE_RECIPE,
            MySQLiteHelper.COLUMN_ICON_CATEGORIE_RECIPE,MySQLiteHelper.COLUMN_ICON_PATH_CATEGORIE_RECIPE, MySQLiteHelper.COLUMN_DETAIL_CATEGORIE_RECIPE};
    private MySQLiteHelper dbHelper;

    public CategoryRecipeDataSource(Context context) {
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

    public void open() throws SQLException {
        database = dbHelper.getWritableDatabase();
    }

    public void close() {
        dbHelper.close();
    }

//    public Category_Recipe createCategoryRecipe(byte[] icon, String detail) {
//        open();
//        ContentValues values = new ContentValues();
//        values.put(MySQLiteHelper.COLUMN_ICON_CATEGORIE_RECIPE, icon);
//        values.put(MySQLiteHelper.COLUMN_DETAIL_CATEGORIE_RECIPE, detail);
//
//        long insertId = database.insert(MySQLiteHelper.TABLE_CATEGORIE_RECIPE, null, values);
//        Cursor cursor = database.query(MySQLiteHelper.TABLE_CATEGORIE_RECIPE,
//                allColumns, MySQLiteHelper.COLUMN_ID_CATEGORIE_RECIPE + " = " + insertId, null,
//                null, null, null);
//        cursor.moveToFirst();
//        Category_Recipe newCategory_Recipe = cursorToCategoryRecipe(cursor);
//        cursor.close();
//        close();
//        return newCategory_Recipe;
//    }

//    public long insertCategoryRecipe(Category_Recipe categoryRecipe) {
//        open();
//        ContentValues values = new ContentValues();
//        values.put(MySQLiteHelper.COLUMN_ICON_CATEGORIE_RECIPE, categoryRecipe.getIcon_category());
//        values.put(MySQLiteHelper.COLUMN_ICON_CATEGORIE_RECIPE, categoryRecipe.getIcon_category());
//        values.put(MySQLiteHelper.COLUMN_ICON_PATH_CATEGORIE_RECIPE, categoryRecipe.getIcon_path_category());
//        values.put(MySQLiteHelper.COLUMN_DETAIL_CATEGORIE_RECIPE, categoryRecipe.getDetail_category());
//
//        long insertId = database.insert(MySQLiteHelper.TABLE_CATEGORIE_RECIPE, null, values);
//        close();
//        return insertId;
//    }

    public void deleteCategoryRecipe(Category_Recipe categoryRecipe) {
        open();
        long id = categoryRecipe.getId_category();
        Log.d("DB", "Category recipe deleted with id: " + id);
        database.delete(MySQLiteHelper.TABLE_CATEGORIE_RECIPE, MySQLiteHelper.COLUMN_ID_CATEGORIE_RECIPE + " = " + id, null);
        close();
    }

//    public ArrayList<Category_Recipe> getAllCategoryRecipes() {
//        open();
//        ArrayList<Category_Recipe> categoryRecipes = new ArrayList<>();
//        Cursor cursor = database.query(MySQLiteHelper.TABLE_CATEGORIE_RECIPE, allColumns, null, null, null, null, null);
//
//        cursor.moveToFirst();
//        while (!cursor.isAfterLast()) {
//            Category_Recipe category_Recipe = cursorToCategoryRecipe(cursor);
//            categoryRecipes.add(category_Recipe);
//            cursor.moveToNext();
//        }
//        cursor.close();
//        close();
//        return categoryRecipes;
//    }

//    public List<Category_Recipe> getCategoryRecipesByDetail(String detail, int pageNumber, int itemsPerPage) {
//        open();
//        List<Category_Recipe> categoryRecipes = new ArrayList<>();
//        int offset = (pageNumber - 1) * itemsPerPage;
//
//        try {
//            Cursor cursor = database.query(MySQLiteHelper.TABLE_CATEGORIE_RECIPE,
//                    allColumns, MySQLiteHelper.COLUMN_DETAIL_CATEGORIE_RECIPE + " = ?",
//                    new String[]{detail}, null, null, null, offset + "," + itemsPerPage);
//            cursor.moveToFirst();
//            while (!cursor.isAfterLast()) {
//                Category_Recipe category_Recipe = cursorToCategoryRecipe(cursor);
//                categoryRecipes.add(category_Recipe);
//                cursor.moveToNext();
//            }
//            cursor.close();
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//        close();
//        return categoryRecipes;
//    }

//    public Category_Recipe getCategoryRecipe(int id) {
//        open();
//        Category_Recipe category_Recipe = new Category_Recipe();
//        Cursor cursor = database.query(MySQLiteHelper.TABLE_CATEGORIE_RECIPE, allColumns,
//                MySQLiteHelper.COLUMN_ID_CATEGORIE_RECIPE + " = " + id, null, null, null, null);
//
//        cursor.moveToFirst();
//        while (!cursor.isAfterLast()) {
//            category_Recipe = cursorToCategoryRecipe(cursor);
//            cursor.moveToNext();
//        }
//        cursor.close();
//        close();
//        return category_Recipe;
//    }

//    public void updateCategoryRecipe(Category_Recipe categoryRecipe) {
//        open();
//        ContentValues values = new ContentValues();
//        values.put(MySQLiteHelper.COLUMN_ICON_CATEGORIE_RECIPE, categoryRecipe.getIcon_category());
//        values.put(MySQLiteHelper.COLUMN_ICON_CATEGORIE_RECIPE, categoryRecipe.getIcon_category());
//        values.put(MySQLiteHelper.COLUMN_ICON_PATH_CATEGORIE_RECIPE, categoryRecipe.getIcon_path_category());
//        values.put(MySQLiteHelper.COLUMN_DETAIL_CATEGORIE_RECIPE, categoryRecipe.getDetail_category());
//        close();
//    }

//    private Category_Recipe cursorToCategoryRecipe(Cursor cursor) {
//        Category_Recipe category_Recipe = new Category_Recipe();
//        category_Recipe.setId_category(cursor.getInt(0));
//        category_Recipe.setIcon_category(cursor.getBlob(1));
//        category_Recipe.setIcon_path_category(cursor.getString(1));
//        category_Recipe.setDetail_category(cursor.getString(2));
//        return category_Recipe;
//    }
}
