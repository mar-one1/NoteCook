package com.example.notecook.Data;

import static com.example.notecook.Data.MySQLiteHelperTable.TABLE_USER;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.example.notecook.Model.User;

import java.util.ArrayList;
import java.util.Objects;

public class UserDatasource {

    private static SQLiteDatabase database;
    private static String[] allColumns = {MySQLiteHelper.COLUMN_ID_USER, MySQLiteHelper.COLUMN_USERNAME, MySQLiteHelper.COLUMN_ICON,
            MySQLiteHelper.COLUMN_FIRSTNAME_USER, MySQLiteHelper.COLUMN_LASTNAME_USER,
            MySQLiteHelper.COLUMN_BIRTHDAY_USER, MySQLiteHelper.COLUMN_EMAIL_USER,
            MySQLiteHelper.COLUMN_PHONENUMBER_USER,
            MySQLiteHelper.COLUMN_PASSWORD, MySQLiteHelper.COLUMN_GRADE, MySQLiteHelper.COLUMN_STATUS
    };
    private MySQLiteHelper dbHelper;


    public UserDatasource(Context context) {
        dbHelper = new MySQLiteHelper(context);
    }


    // Method to check if a record exists
    public static boolean isRecordExist (String tableName,String columnName, String value) {

        Cursor cursor = database.query(tableName, null, columnName + " = ?", new String[]{value}, null, null, null);
        boolean exists = (cursor.getCount() > 0);
        cursor.close();
        return exists;
    }
    /*
     * insert the value in the Image table
     */
    public static User insertUser(User user) {
        ContentValues values = new ContentValues();

        values.put(MySQLiteHelper.COLUMN_ICON, user.getIcon());
        values.put(MySQLiteHelper.COLUMN_USERNAME, user.getUsername());
        values.put(MySQLiteHelper.COLUMN_FIRSTNAME_USER, user.getFirstname());
        values.put(MySQLiteHelper.COLUMN_LASTNAME_USER, user.getLastname());
        values.put(MySQLiteHelper.COLUMN_BIRTHDAY_USER, user.getBirthday());
        values.put(MySQLiteHelper.COLUMN_EMAIL_USER, user.getEmail());
        values.put(MySQLiteHelper.COLUMN_PHONENUMBER_USER, user.getPhonenumber());
        values.put(MySQLiteHelper.COLUMN_PASSWORD, user.getPassWord());
        values.put(MySQLiteHelper.COLUMN_GRADE, user.getGrade());
        values.put(MySQLiteHelper.COLUMN_STATUS, user.getStatus());

        long insertId = database.insert(TABLE_USER, null,
                values);
        Cursor cursor = database.query(TABLE_USER,
                allColumns, MySQLiteHelper.COLUMN_ID_USER + " = " + insertId, null,
                null, null, null);
        cursor.moveToFirst();
        User newUser = cursorToComment(cursor);
        cursor.close();
        return newUser;
    }


    public static User createUserlogin(byte[] icon, String username, String firstname, String lastname, String birthday, String email, String tel, String Password, String grade, String status) {
        ContentValues values = new ContentValues();

        values.put(MySQLiteHelper.COLUMN_USERNAME, username);
        values.put(MySQLiteHelper.COLUMN_ICON, icon);
        values.put(MySQLiteHelper.COLUMN_FIRSTNAME_USER, firstname);
        values.put(MySQLiteHelper.COLUMN_LASTNAME_USER, lastname);
        values.put(MySQLiteHelper.COLUMN_BIRTHDAY_USER, birthday);
        values.put(MySQLiteHelper.COLUMN_EMAIL_USER, email);
        values.put(MySQLiteHelper.COLUMN_PHONENUMBER_USER, tel);
        values.put(MySQLiteHelper.COLUMN_PASSWORD, Password);
        values.put(MySQLiteHelper.COLUMN_GRADE, grade);
        values.put(MySQLiteHelper.COLUMN_STATUS, status);

        long insertId = database.insert(TABLE_USER, null,
                values);
        Cursor cursor = database.query(TABLE_USER,
                allColumns, MySQLiteHelper.COLUMN_ID_USER + " = " + insertId, null,
                null, null, null);
        cursor.moveToFirst();
        User newUser = cursorToComment(cursor);
        cursor.close();
        return newUser;
    }

    private static User cursorToComment(Cursor cursor) {

        User user = new User();
        user.setId_User(cursor.getInt(0));
        user.setUsername(cursor.getString(1));
        user.setIcon(cursor.getBlob(2));
        user.setFirstname(cursor.getString(3));
        user.setLastname(cursor.getString(4));
        user.setBirthday(cursor.getString(5));
        user.setEmail(cursor.getString(6));
        user.setPhonenumber(cursor.getString(7));
        user.setPassWord(cursor.getString(8));
        user.setGrade(cursor.getString(9));
        user.setStatus(cursor.getString(10));

        return user;
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
    public void deleteImage(User user) {
        long id = user.getId_User();
        Log.d("gps", "Image  deleted with id: " + id);
        database.delete(TABLE_USER, MySQLiteHelper.COLUMN_ID_USER
                + " = " + id, null);
    }

    public ArrayList<User> getAllUser() {
        ArrayList<User> ListUser = new ArrayList<>();
        Cursor cursor = database.query(TABLE_USER,
                allColumns, null, null, null, null, null);
        cursor.moveToFirst();
        while (!cursor.isAfterLast()) {
            User USER = cursorToComment(cursor);
            ListUser.add(USER);
            cursor.moveToNext();
        }
        // assurez-vous de la fermeture du curseur
        cursor.close();
        return ListUser;
    }

    public User select_User_BYid(int id) {
        User ListUserByid = new User();
        Cursor cursor = database.query(TABLE_USER,
                allColumns, MySQLiteHelper.COLUMN_ID_USER + " = " + id, null, null, null, null);

        cursor.moveToFirst();
        while (!cursor.isAfterLast()) {
            ListUserByid = cursorToComment(cursor);
            cursor.moveToNext();
        }
        cursor.close();
        return ListUserByid;
    }

    public User select_User_BYUsername(String username) {
        User user = new User();
        String[] selectionArgs = {username};
        Cursor cursor = database.query(TABLE_USER,
                allColumns, MySQLiteHelper.COLUMN_USERNAME + " = ?", selectionArgs, null, null, null);

        cursor.moveToFirst();
        while (!cursor.isAfterLast()) {
            user = cursorToComment(cursor);
            cursor.moveToNext();
        }
        cursor.close();
        return user;
    }

    public int  UpdateUser(User user, int id) {

        ContentValues data = new ContentValues();
        data.put(MySQLiteHelper.COLUMN_BIRTHDAY_USER, user.getBirthday());
        if (!Objects.equals(user.getUsername(), ""))
            data.put(MySQLiteHelper.COLUMN_USERNAME, user.getUsername());
        data.put(MySQLiteHelper.COLUMN_LASTNAME_USER, user.getLastname());
        data.put(MySQLiteHelper.COLUMN_FIRSTNAME_USER, user.getFirstname());
        data.put(MySQLiteHelper.COLUMN_EMAIL_USER, user.getEmail());
        data.put(MySQLiteHelper.COLUMN_PHONENUMBER_USER, user.getPhonenumber());
        if (!Objects.equals(user.getGrade(), ""))
            data.put(MySQLiteHelper.COLUMN_GRADE, user.getGrade());
        if (!Objects.equals(user.getStatus(), ""))
            data.put(MySQLiteHelper.COLUMN_STATUS, user.getStatus());
        data.put(MySQLiteHelper.COLUMN_ICON, user.getIcon());
        int value =database.update(TABLE_USER, data, MySQLiteHelper.COLUMN_ID_USER + " = " + id, null);
        return value;
    }

    public int UpdateUserByUsername(User user, String username) {
        String[] selectionArgs = {username};
        ContentValues data = new ContentValues();
        data.put(MySQLiteHelper.COLUMN_BIRTHDAY_USER, user.getBirthday());
        data.put(MySQLiteHelper.COLUMN_USERNAME, user.getUsername());
        data.put(MySQLiteHelper.COLUMN_LASTNAME_USER, user.getLastname());
        data.put(MySQLiteHelper.COLUMN_FIRSTNAME_USER, user.getFirstname());
        data.put(MySQLiteHelper.COLUMN_EMAIL_USER, user.getEmail());
        data.put(MySQLiteHelper.COLUMN_PHONENUMBER_USER, user.getPhonenumber());
        data.put(MySQLiteHelper.COLUMN_GRADE, user.getGrade());
        data.put(MySQLiteHelper.COLUMN_STATUS, user.getStatus());
        data.put(MySQLiteHelper.COLUMN_ICON, user.getIcon());
        int value =database.update(TABLE_USER, data, MySQLiteHelper.COLUMN_USERNAME +  "= ?", selectionArgs);
        return value;
    }

}













