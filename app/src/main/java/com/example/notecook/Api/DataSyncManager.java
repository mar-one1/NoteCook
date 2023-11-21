package com.example.notecook.Api;

import static com.example.notecook.Utils.Constants.Token;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.example.notecook.Data.MySQLiteHelper;
import com.example.notecook.Model.GenericModel;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class DataSyncManager<T> {
    private final String BASE_URL = "https://example.com/api/";
    private final Class<T> dataTypeClass;
    MySQLiteHelper dbHelper; // Initialize the database helper
    private Context context;


    public DataSyncManager(Class<T> dataTypeClass) {
        this.dataTypeClass = dataTypeClass;
    }

    private Retrofit createRetrofit() {
        return new Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();
    }

    public void fetchDataAndSyncToLocalDatabase() {
        Retrofit retrofit = createRetrofit();
        ApiService apiService = retrofit.create(ApiService.class);

        Call<T> call = (Call<T>) apiService.getData("Bearer " + Token);
        call.enqueue(new Callback<T>() {
            @Override
            public void onResponse(Call<T> call, Response<T> response) {
                if (response.isSuccessful()) {
                    GenericModel data = (GenericModel) response.body();
                    saveDataToLocalDatabase(data);
                } else {
                    // Handle API request failure
                    int statusCode = response.code();
                    // Handle the error based on the status code and response body
                }
            }

            @Override
            public void onFailure(Call<T> call, Throwable t) {
                // Handle network or execution error
                t.printStackTrace();
            }
        });
    }

    private void saveDataToLocalDatabase(GenericModel data) {
        // Check if the data already exists in the local database

        boolean dataExists = checkIfDataExistsLocally(data);

        if (dataExists) {
            // Update the existing data in the local database
            updateDataLocally(data);
        } else {
            // Insert the new data into the local database
            insertDataLocally(data);
        }
    }


    private boolean checkIfDataExistsLocally(GenericModel data) {
        SQLiteDatabase db = null;
        Cursor cursor = null;
        boolean dataExists = false;
        dbHelper = new MySQLiteHelper(context); // Initialize the database helper


        try {
            // Get a readable database instance
            db = dbHelper.getReadableDatabase(); // Get a readable database instance

            // Define a query to check if the data exists in the table
            String query = "SELECT COUNT(*) FROM " + data.toString() + " WHERE " + data.getUniqueIdentifier() + " = ?";

            // Execute the query with the unique identifier (e.g., ID or another unique field)
            cursor = db.rawQuery(query, new String[]{String.valueOf(data.getUniqueIdentifier())});

            // Check if the query returned a result
            if (cursor.moveToFirst()) {
                int count = cursor.getInt(0);
                dataExists = count > 0; // Data exists if the count is greater than 0
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (cursor != null) {
                cursor.close();
            }
            if (db != null) {
                db.close();
            }
        }

        return dataExists;
    }

    private void updateDataLocally(GenericModel data) {
        // Implement logic to update the existing data in the local database
        // You may need to perform an update operation based on data's unique identifier
    }

    private void insertDataLocally(GenericModel data) {
        // Implement logic to insert the new data into the local database
        // You may need to use an insert operation provided by your database library
    }


}
