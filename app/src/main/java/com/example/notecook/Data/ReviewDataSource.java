package com.example.notecook.Data;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.example.notecook.Model.Review;

import java.util.ArrayList;
import java.util.List;

public class ReviewDataSource {

    private static SQLiteDatabase database;
    private static String[] allColumns = {MySQLiteHelper.COLUMN_ID_REVIEW_RECIPE, MySQLiteHelper.COLUMN_DETAIL_REVIEW_RECIPE,
            MySQLiteHelper.COLUMN_RATE_REVIEW_RECIPE, MySQLiteHelper.COLUMN_FRK_DETAIL_REVIEW_RECIPE
    };


    private MySQLiteHelper dbHelper;


    public ReviewDataSource(Context context) {
        dbHelper = new MySQLiteHelper(context);
    }

    /*
     * insert the value in the Image table
     */


    public Review postReview(String detail,float rate, int frk_recipe) {
        open();
        ContentValues values = new ContentValues();
        values.put(MySQLiteHelper.COLUMN_DETAIL_REVIEW_RECIPE, detail);
        values.put(MySQLiteHelper.COLUMN_RATE_REVIEW_RECIPE, rate);
        values.put(MySQLiteHelper.COLUMN_FRK_DETAIL_REVIEW_RECIPE, frk_recipe);

        long insertId = database.insert(MySQLiteHelper.TABLE_REVIEW_RECIPE, null,
                values);
        Cursor cursor = database.query(MySQLiteHelper.TABLE_REVIEW_RECIPE,
                allColumns, MySQLiteHelper.COLUMN_ID_REVIEW_RECIPE + " = " + insertId, null,
                null, null, null);
        cursor.moveToFirst();
        Review newDR = cursorToComment(cursor);
        cursor.close();
        close();
        return newDR;
    }

    private Review cursorToComment(Cursor cursor) {

        Review review = new Review();
        review.setDetail_Review_recipe(cursor.getString(0));
        review.setRate_Review_recipe(cursor.getFloat(1));
        review.setFRK_recipe(cursor.getInt(2));
        return review;
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
    public void deleteReview(Review RV) {
        open();
        long id = RV.getId_review();
        Log.d("gps", "Image  deleted with id: " + id);
        database.delete(MySQLiteHelper.TABLE_REVIEW_RECIPE, MySQLiteHelper.COLUMN_ID_REVIEW_RECIPE
                + " = " + id, null);
        close();
    }

    public List<Review> getAllReview() {
        open();
        List<Review> ListDR = new ArrayList<>();

        Cursor cursor = database.query(MySQLiteHelper.TABLE_REVIEW_RECIPE,
                allColumns, null, null, null, null, null);

        cursor.moveToFirst();
        while (!cursor.isAfterLast()) {
            Review DR = cursorToComment(cursor);
            ListDR.add(DR);
            cursor.moveToNext();
        }
        // assurez-vous de la fermeture du curseur
        cursor.close();
        close();
        return ListDR;
    }

    public Review getReviewById(int id) {
        open();
        Review ListDRByid = new Review();
        Cursor cursor = database.query(MySQLiteHelper.TABLE_REVIEW_RECIPE,
                allColumns, MySQLiteHelper.COLUMN_ID_REVIEW_RECIPE + " = " + id, null, null, null, null);

        cursor.moveToFirst();
        while (!cursor.isAfterLast()) {
            ListDRByid = cursorToComment(cursor);
            cursor.moveToNext();
        }
        close();
        return ListDRByid;
    }

    public ArrayList<Review> getReviewsByIdRecipe(int id) {
        open();
        ArrayList<Review> ListDRByidRecipe = new ArrayList<>();
        Cursor cursor = database.query(MySQLiteHelper.TABLE_REVIEW_RECIPE,
                allColumns, MySQLiteHelper.COLUMN_FRK_DETAIL_REVIEW_RECIPE + " = " + id, null, null, null, null);

        cursor.moveToFirst();
        while (!cursor.isAfterLast()) {
            Review dr = cursorToComment(cursor);
            ListDRByidRecipe.add(dr);
            cursor.moveToNext();
        }
        close();
        return ListDRByidRecipe;
    }


}


