package com.example.notecook.Api;

import com.example.notecook.BuildConfig;
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
    private static Retrofit retrofit = null;


    public static Retrofit getClient() {
        if (retrofit == null) {

            OkHttpClient.Builder okHttpBuilder = new OkHttpClient.Builder()
                    //.retryOnConnectionFailure(false) // Disable retry on failure
                    .connectTimeout(15, TimeUnit.SECONDS) // Connection timeout
                    .readTimeout(30, TimeUnit.SECONDS)    // Read timeout
                    .writeTimeout(40, TimeUnit.SECONDS)   // Write timeout
                    .callTimeout(15, TimeUnit.SECONDS);

            // Debug logging interceptor
            if (BuildConfig.DEBUG) {
                HttpLoggingInterceptor logging = new HttpLoggingInterceptor();
                logging.setLevel(HttpLoggingInterceptor.Level.BODY);
                okHttpBuilder.addInterceptor(logging);
            }

            OkHttpClient okHttpClient = okHttpBuilder.build();

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
