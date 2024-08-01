package com.example.notecook.Data;


import android.content.ContentValues;
import android.content.Context;
import net.sqlcipher.database.SQLiteDatabase;
import net.sqlcipher.database.SQLiteOpenHelper;

public class SecureDatabaseHelper extends SQLiteOpenHelper implements SecureDatabaseHelperuse {
    private static final String DATABASE_NAME = "secure.db";
    private static final int DATABASE_VERSION = 1;
    private static final String DATABASE_PASSWORD = "123456";

    public SecureDatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE secure_table (id INTEGER PRIMARY KEY, data TEXT)");
    }


    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS secure_table");
        onCreate(db);
    }

    @Override
    public void onOpen(SQLiteDatabase db) {
        super.onOpen(db);
        db.execSQL("PRAGMA cipher_memory_security = ON");
    }

    @Override
    public SQLiteDatabase getWritableDatabase() {
        return super.getWritableDatabase(DATABASE_PASSWORD);
    }

    @Override
    public SQLiteDatabase getReadableDatabase() {
        return super.getReadableDatabase(DATABASE_PASSWORD);
    }

    // Method to insert data into the table
    public long insertData(String data) {
        SQLiteDatabase db = getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("data", data);
        return db.insert("secure_table", null, values);
    }
}
