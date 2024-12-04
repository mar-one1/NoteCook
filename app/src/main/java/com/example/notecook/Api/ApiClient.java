package com.example.notecook.Api;

import com.example.notecook.Utils.CustomDateDeserializer;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.util.Date;
import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class ApiClient {
//    public static final String BASE_URL = "http://192.168.60.34:3000/";
//     public static final String BASE_URL = "https://9254-196-64-117-33.ngrok-free.app/";
    private static Retrofit retrofit = null;


    public static Retrofit getClient() {
        if (retrofit == null) {
            HttpLoggingInterceptor logging = new HttpLoggingInterceptor();
            logging.setLevel(HttpLoggingInterceptor.Level.BODY);

            OkHttpClient okHttpClient = new OkHttpClient.Builder()
                    .connectTimeout(15, TimeUnit.SECONDS) // Connection timeout
                    .readTimeout(15, TimeUnit.SECONDS)    // Read timeout
                    .writeTimeout(30, TimeUnit.SECONDS)   // Write timeout
                    .addInterceptor(logging)
                    .callTimeout(10, TimeUnit.SECONDS)
                    .build();
            Gson gson = new GsonBuilder()
                    .registerTypeAdapter(Date.class, new CustomDateDeserializer())
                    .create();
            retrofit = new Retrofit.Builder()
                    .client(okHttpClient)
                    .baseUrl(env.BASE_URL)
                    .addConverterFactory(GsonConverterFactory.create(gson))
                    .build();
        }
        return retrofit;
    }
}
