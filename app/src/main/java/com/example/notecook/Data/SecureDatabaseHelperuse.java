package com.example.notecook.Data;

import net.sqlcipher.database.SQLiteDatabase;

public interface SecureDatabaseHelperuse {
    SQLiteDatabase getWritableDatabase();

    SQLiteDatabase getReadableDatabase();
}
