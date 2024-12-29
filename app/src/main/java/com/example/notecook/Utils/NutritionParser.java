package com.example.notecook.Utils;

import com.example.notecook.Model.Nutrition;

import org.json.JSONArray;
import org.json.JSONObject;

public class NutritionParser {
    public static Nutrition parseNutrition(JSONObject foodJson) {
        try {
            // Get the description (name) of the food
            String name = foodJson.getString("description");

            // Get the nutrients array (e.g., calories, protein, fat, carbs)
            JSONArray nutrients = foodJson.getJSONArray("foodNutrients");

            double calories = 0, protein = 0, fat = 0, carbs = 0;
            for (int i = 0; i < nutrients.length(); i++) {
                JSONObject nutrient = nutrients.getJSONObject(i);
                String nutrientName = nutrient.getString("nutrientName");
                double value = nutrient.getDouble("value");

                // Parsing specific nutrients
                switch (nutrientName) {
                    case "Energy":
                        calories = value; // kcal
                        break;
                    case "Protein":
                        protein = value; // g
                        break;
                    case "Total lipid (fat)":
                        fat = value; // g
                        break;
                    case "Carbohydrate, by difference":
                        carbs = value; // g
                        break;
                }
            }

            // Extracting the serving size and unit (optional: may not be available in all responses)
            double servingSize = 0;
            String servingUnit = "";
            if (foodJson.has("servingSize")) {
                servingSize = foodJson.getDouble("servingSize");
            }
            if (foodJson.has("servingUnit")) {
                servingUnit = foodJson.getString("servingUnit");
            }

            // Return a new Nutrition object with all the parsed data
            return new Nutrition(name, calories, protein, fat, carbs, servingSize, servingUnit);

        } catch (Exception e) {
            e.printStackTrace();
            return null; // Return null in case of an error
        }
    }
}
