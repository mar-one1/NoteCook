package com.example.notecook.Utils;

import android.util.Log;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;

import java.lang.reflect.Type;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class CustomDateDeserializer implements JsonDeserializer<Date> {
    private static final SimpleDateFormat dateFormat =
            new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSX", Locale.getDefault());

    @Override
    public Date deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context)
            throws JsonParseException {
        try {
            return dateFormat.parse(json.getAsString());
        } catch (ParseException e) {
            Log.e("DateParse", "Failed to parse date: " + json.getAsString());
            return null;
        }
    }
}

