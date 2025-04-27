package com.example.notecook.Utils;

import android.util.Log;

import com.example.notecook.Model.Nutrition;

import org.json.JSONArray;
import org.json.JSONObject;

public class NutritionParser {
    public static Nutrition parseNutrition(JSONObject foodJson) {
        try {
            // Get the description (name) of the food
            Log.d("food parser",""+foodJson);
            String name = foodJson.getString("description");

            // Get the nutrients array (e.g., calories, protein, fat, carbs)
            JSONArray nutrients = foodJson.getJSONArray("foodNutrients");

            double calories = 0, protein = 0, fat = 0, carbs = 0;
            String caloriesUnit="" , proteinUnit="" , fatUnit="" , carbsUnit="";
            for (int i = 0; i < nutrients.length(); i++) {
                JSONObject nutrient = nutrients.getJSONObject(i);
                String nutrientName = nutrient.getString("nutrientName");
                String unitName = nutrient.getString("unitName");
                double value = nutrient.getDouble("value");

                // Parsing specific nutrients
                switch (nutrientName) {
                    case "Energy":
                        calories = value; // kcal
                        caloriesUnit = unitName;
                        break;
                    case "Protein":
                        protein = value; // g
                        proteinUnit = unitName;
                        break;
                    case "Total lipid (fat)":
                        fat = value; // g
                        fatUnit = unitName;
                        break;
                    case "Carbohydrate, by difference":
                        carbs = value; // g
                        carbsUnit = unitName;
                        break;
                }
            }

            // Extracting the serving size and unit (optional: may not be available in all responses)
            double servingSize = 0;
            String servingUnit = "";
            if (foodJson.has("servingSize")) {
                servingSize = foodJson.getDouble("servingSize");
            }
            if (foodJson.has("servingSizeUnit")) {
                servingUnit = foodJson.getString("servingSizeUnit");
            }

            // Return a new Nutrition object with all the parsed data
            //return new Nutrition(name, calories, protein, fat, carbs, servingSize, servingUnit);
            return new Nutrition(name, calories,caloriesUnit, protein,proteinUnit, fat,fatUnit, carbs,carbsUnit, servingSize, servingUnit);

        } catch (Exception e) {
            e.printStackTrace();
            return null; // Return null in case of an error
        }
    }
}
