package com.example.notecook.Data;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.example.notecook.Model.Step;

import java.util.ArrayList;
import java.util.List;

public class StepsDataSource {

    private static SQLiteDatabase database;
    private static String[] allColumns = {MySQLiteHelper.COLUMN_ID_STEP_RECIPE, MySQLiteHelper.COLUMN_DETAIL_STEP_RECIPE,
            MySQLiteHelper.COLUMN_IMAGE_STEP, MySQLiteHelper.COLUMN_TIME_STEP,
            MySQLiteHelper.COLUMN_FRK_STEP_RECIPE
    };


    private MySQLiteHelper dbHelper;


    public StepsDataSource(Context context) {
        dbHelper = new MySQLiteHelper(context);
    }

    /*
     * insert the value in the Image table
     */


    public Step Create_Step(String detail, int time, byte[] image, int rate, int frk_recipe) {
        open();
        ContentValues values = new ContentValues();
        values.put(MySQLiteHelper.COLUMN_DETAIL_STEP_RECIPE, detail);
        values.put(MySQLiteHelper.COLUMN_IMAGE_STEP, image);
        values.put(MySQLiteHelper.COLUMN_TIME_STEP, time);
        values.put(MySQLiteHelper.COLUMN_FRK_STEP_RECIPE, frk_recipe);

        long insertId = database.insert(MySQLiteHelper.TABLE_STEP_RECIPE, null,
                values);
        Cursor cursor = database.query(MySQLiteHelper.TABLE_STEP_RECIPE,
                allColumns, MySQLiteHelper.COLUMN_ID_STEP_RECIPE + " = " + insertId, null,
                null, null, null);
        cursor.moveToFirst();
        Step newDR = cursorToComment(cursor);
        cursor.close();
        close();
        return newDR;
    }

    public Step Create_Step(Step step) {
        open();
        ContentValues values = new ContentValues();
        values.put(MySQLiteHelper.COLUMN_DETAIL_STEP_RECIPE, step.getDetail_step());
        values.put(MySQLiteHelper.COLUMN_IMAGE_STEP, step.getImage_step());
        values.put(MySQLiteHelper.COLUMN_TIME_STEP, step.getTime_step());
        values.put(MySQLiteHelper.COLUMN_FRK_STEP_RECIPE, step.getFRK_recipe_step());

        long insertId = database.insert(MySQLiteHelper.TABLE_STEP_RECIPE, null,
                values);
        Cursor cursor = database.query(MySQLiteHelper.TABLE_STEP_RECIPE,
                allColumns, MySQLiteHelper.COLUMN_ID_STEP_RECIPE + " = " + insertId, null,
                null, null, null);
        cursor.moveToFirst();
        Step newDR = cursorToComment(cursor);
        cursor.close();
        close();
        return newDR;
    }

    public List<Step> insert_Steps(List<Step> steps, int id) {
        List<Step> insertedSteps = new ArrayList<>();

        open();
        for (Step step : steps) {
            ContentValues values = new ContentValues();
            values.put(MySQLiteHelper.COLUMN_DETAIL_STEP_RECIPE, step.getDetail_step());
            values.put(MySQLiteHelper.COLUMN_IMAGE_STEP, step.getImage_step());
            values.put(MySQLiteHelper.COLUMN_TIME_STEP, step.getTime_step());
            values.put(MySQLiteHelper.COLUMN_FRK_STEP_RECIPE, id);

            long insertId = database.insert(MySQLiteHelper.TABLE_STEP_RECIPE, null, values);

            Cursor cursor = database.query(MySQLiteHelper.TABLE_STEP_RECIPE,
                    allColumns, MySQLiteHelper.COLUMN_ID_STEP_RECIPE + " = " + insertId, null,
                    null, null, null);
            cursor.moveToFirst();
            Step insertedStep = cursorToComment(cursor);
            cursor.close();

            insertedSteps.add(insertedStep);
        }
        close();
        return insertedSteps;
    }


    private Step cursorToComment(Cursor cursor) {

        Step Step = new Step();
        Step.setId_step(cursor.getInt(0));
        Step.setDetail_step(cursor.getString(1));
        Step.setImage_step(cursor.getBlob(2));
        Step.setTime_step(cursor.getInt(3));
        Step.setFRK_recipe_step(cursor.getInt(4));
        return Step;
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
    public void deleteStep(Step DR) {
        open();
        long id = DR.getId_step();
        Log.d("gps", "Image  deleted with id: " + id);
        database.delete(MySQLiteHelper.TABLE_STEP_RECIPE, MySQLiteHelper.COLUMN_ID_STEP_RECIPE
                + " = " + id, null);
        close();
    }

    public List<Step> getAllSTEP() {
        open();
        List<Step> ListDR = new ArrayList<>();

        Cursor cursor = database.query(MySQLiteHelper.TABLE_STEP_RECIPE,
                allColumns, null, null, null, null, null);

        cursor.moveToFirst();
        while (!cursor.isAfterLast()) {
            Step DR = cursorToComment(cursor);
            ListDR.add(DR);
            cursor.moveToNext();
        }
        // assurez-vous de la fermeture du curseur
        cursor.close();
        close();
        return ListDR;
    }

    public Step select_Step_BYidSTEP(int id) {
        open();
        Step ListDRByid = new Step();
        Cursor cursor = database.query(MySQLiteHelper.TABLE_STEP_RECIPE,
                allColumns, MySQLiteHelper.COLUMN_ID_STEP_RECIPE + " = " + id, null, null, null, null);

        cursor.moveToFirst();
        while (!cursor.isAfterLast()) {
            ListDRByid = cursorToComment(cursor);
            cursor.moveToNext();
        }
        close();
        return ListDRByid;
    }

    public ArrayList<Step> getStepByIdRecipe(int id) {
        open();
        ArrayList<Step> ListDRByidRecipe = new ArrayList<>();
        Cursor cursor = database.query(MySQLiteHelper.TABLE_STEP_RECIPE,
                allColumns, MySQLiteHelper.COLUMN_FRK_STEP_RECIPE + " = " + id, null, null, null, null);

        cursor.moveToFirst();
        while (!cursor.isAfterLast()) {
            Step dr = cursorToComment(cursor);
            ListDRByidRecipe.add(dr);
            cursor.moveToNext();
        }
        close();
        return ListDRByidRecipe;
    }

    public void Update_Step(Step step, int id) {
        open();
        ContentValues data = new ContentValues();
        data.put(MySQLiteHelper.COLUMN_DETAIL_STEP_RECIPE, step.getDetail_step());
        data.put(MySQLiteHelper.COLUMN_IMAGE_STEP, step.getImage_step());
        data.put(MySQLiteHelper.COLUMN_TIME_STEP, step.getTime_step());
        data.put(MySQLiteHelper.COLUMN_FRK_STEP_RECIPE, step.getFRK_recipe_step());
        database.update(MySQLiteHelper.TABLE_STEP_RECIPE, data, MySQLiteHelper.COLUMN_ID_STEP_RECIPE + " = " + id, null);
        close();
    }
    public List<Step> Update_Step(List<Step> steps, int id) {
        List<Step> updatedSteps = new ArrayList<>();
        open();
        for (Step step : steps) {
            ContentValues data = new ContentValues();
            data.put(MySQLiteHelper.COLUMN_DETAIL_STEP_RECIPE, step.getDetail_step());
            data.put(MySQLiteHelper.COLUMN_IMAGE_STEP, step.getImage_step());
            data.put(MySQLiteHelper.COLUMN_TIME_STEP, step.getTime_step());
            data.put(MySQLiteHelper.COLUMN_FRK_STEP_RECIPE, step.getFRK_recipe_step());
            database.update(MySQLiteHelper.TABLE_STEP_RECIPE, data, MySQLiteHelper.COLUMN_ID_STEP_RECIPE + " = " + id, null);
            updatedSteps.add(step);
        }
        close();

        return updatedSteps;
    }
}
