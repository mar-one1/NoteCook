package com.example.notecook.Utils;

import android.os.AsyncTask;
import android.util.Log;

import com.example.notecook.Model.Nutrition;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class FetchNutritionTask extends AsyncTask<String, Void, Nutrition> {

    private OnNutritionFetchedListener listener;
    private double customServingSize;
    private String customgetServingUnit;

    public FetchNutritionTask(OnNutritionFetchedListener listener, double customServingSize) {
        this.listener = listener;
        this.customServingSize = customServingSize;
    }

    public FetchNutritionTask(OnNutritionFetchedListener listener, double customServingSize, String customgetServingUnit) {
        this.listener = listener;
        this.customServingSize = customServingSize;
        this.customgetServingUnit = customgetServingUnit;
    }

    @Override
    protected Nutrition doInBackground(String... params) {
        String foodQuery = params[0];
        JSONObject foodJson = fetchFoodData(foodQuery);
        Log.d("food",""+foodJson);

        // Parse the nutrition data from the JSON response
        Nutrition nutrition = NutritionParser.parseNutrition(foodJson);

        if (nutrition != null) {
            // Set the custom serving size
            nutrition.setServingSize(customServingSize);
            nutrition.setServingSizeUnit(customgetServingUnit);
        }

        return nutrition;
    }

    @Override
    protected void onPostExecute(Nutrition nutrition) {
        if (listener != null) {
            listener.onNutritionFetched(nutrition);
        }
    }

    // Method to fetch food data from USDA API or other food APIs
    private JSONObject fetchFoodData(String foodQuery) {
        try {
            // The base URL of the USDA API endpoint
            String apiKey = "wc3calHf2GFSWdfw0tVKKbuiCeZv6WNYcFXMQoQA";  // Replace with your actual USDA API key
            String apiUrl = "https://api.nal.usda.gov/fdc/v1/foods/search?query=" + foodQuery + "&api_key=" + apiKey;

            // Make the HTTP request
            URL url = new URL(apiUrl);
            HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.setRequestMethod("GET");
            urlConnection.connect();

            // Read the response from the API
            BufferedReader reader = new BufferedReader(new InputStreamReader(urlConnection.getInputStream()));
            StringBuilder response = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                response.append(line);
            }
            reader.close();

            // Parse the response to JSON
            JSONObject jsonResponse = new JSONObject(response.toString());

            // Get the first food item from the search results
            return jsonResponse.getJSONArray("foods").getJSONObject(0);  // You can adjust this if you want to handle multiple results

        } catch (Exception e) {
            Log.e("FetchNutritionTask", "Error fetching food data: " + e.getMessage());
            return null;
        }
    }

    // Interface for callback
    public interface OnNutritionFetchedListener {
        void onNutritionFetched(Nutrition nutrition);
    }
}
