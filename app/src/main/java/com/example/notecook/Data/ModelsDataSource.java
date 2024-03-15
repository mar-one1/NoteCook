package com.example.notecook.Data;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.example.notecook.Model.GenericModel;
import com.example.notecook.Model.Recipe;
import com.example.notecook.Model.User;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

public class ModelsDataSource<T> {

    private SQLiteDatabase database;
    private MySQLiteHelper dbHelper;
    private Class<T> clazz; // Add a field to store the class type

    public ModelsDataSource(Context context, Class<T> clazz) {
        dbHelper = new MySQLiteHelper(context);
        this.clazz = clazz;
    }

    public void open() throws SQLException {
        database = dbHelper.getWritableDatabase();
    }

    public void close() {
        dbHelper.close();
    }

    public boolean isRecordExist(String tableName, String columnName, String value) {
        open();
        Cursor cursor = database.query(tableName, null, columnName + " = ?", new String[]{value}, null, null, null);
        boolean exists = cursor.getCount() > 0;
        cursor.close();
        close();
        return exists;
    }

    public long insertRecord(String tableName, ContentValues values) {
        open();
        try {
            long insertedRowId = database.insertOrThrow(tableName, null, values);
            return insertedRowId;
        } catch (SQLException e) {
            Log.e("ModelsDataSource", "Error inserting record into table: " + e.getMessage());
            return -1; // Return a negative value to indicate failure
        } finally {
            close();
        }
    }

    public int updateRecord(String tableName, String columnName, String value, ContentValues values) {
        try {
            open();
            int rowsUpdated = database.update(tableName, values, columnName + " = ?", new String[]{value});
            return rowsUpdated;
        } catch (SQLException e) {
            Log.e("ModelsDataSource", "Error updating record in table: " + e.getMessage());
            return -1; // Return a negative value to indicate failure
        } finally {
            close();
        }
    }

    public int deleteRecord(String tableName, String columnName, String value) {
        try {
            open();
            int rowsDeleted = database.delete(tableName, columnName + " = ?", new String[]{value});
            return rowsDeleted;
        } catch (SQLException e) {
            Log.e("ModelsDataSource", "Error deleting record from table: " + e.getMessage());
            return -1; // Return a negative value to indicate failure
        } finally {
            close();
        }
    }

    private T cursorToObject(Cursor cursor) {
        try {
            T obj = clazz.newInstance(); // Create an instance of the specified class
            // Assuming that the cursor columns align with the fields of the class
            for (int i = 0; i < cursor.getColumnCount(); i++) {
                String columnName = cursor.getColumnName(i);
                Field field = clazz.getDeclaredField(columnName); // Get the field corresponding to the column name
                field.setAccessible(true); // Make the field accessible (in case it's private)
                Class<?> fieldType = field.getType(); // Get the type of the field
                // Depending on the type, retrieve data from the cursor and set the field value
                if (fieldType == int.class || fieldType == Integer.class) {
                    field.setInt(obj, cursor.getInt(i));
                } else if (fieldType == long.class || fieldType == Long.class) {
                    field.setLong(obj, cursor.getLong(i));
                } else if (fieldType == float.class || fieldType == Float.class) {
                    field.setFloat(obj, cursor.getFloat(i));
                } else if (fieldType == double.class || fieldType == Double.class) {
                    field.setDouble(obj, cursor.getDouble(i));
                } else if (fieldType == String.class) {
                    field.set(obj, cursor.getString(i));
                } else if (fieldType == byte[].class) {
                    field.set(obj, cursor.getBlob(i));
                }
                // Add more conditions for other data types if needed
            }
            return obj;
        } catch (InstantiationException | IllegalAccessException | NoSuchFieldException | SecurityException | IllegalArgumentException e) {
            e.printStackTrace(); // Handle the exceptions appropriately
            return null;
        }
    }


    public List<T> getAllRecords(String tableName) {
        open();
        List<T> recordList = new ArrayList<>();
        Cursor cursor = database.query(tableName, null, null, null, null, null, null);
        if (cursor.moveToFirst()) {
            do {
                T object = cursorToObject(cursor);
                recordList.add(object);
            } while (cursor.moveToNext());
        }
        cursor.close();
        close();
        return recordList;
    }
    public List<T> getAllRecordsByIdUser(String tableName, String columnName, int value) {
        open();
        List<T> recordList = new ArrayList<>();
        Cursor cursor = database.query(tableName, null, columnName + " = " + value, null, null, null, null, null);
        if (cursor.moveToFirst()) {
            do {
                T object = cursorToObject(cursor);
                recordList.add(object);
            } while (cursor.moveToNext());
        }
        cursor.close();
        close();
        return recordList;
    }

    // Method to query a specific record from the database
    public T getRecord(String tableName, String columnName, String value) {
        open();
        Cursor cursor = database.query(tableName, null, columnName + " = ?", new String[]{value}, null, null, null);
        T object = null;
        if (cursor.moveToFirst()) {
            object = cursorToObject(cursor);
        }
        cursor.close();
        close();
        return object;
    }

}
