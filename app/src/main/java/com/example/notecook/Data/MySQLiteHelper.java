package com.example.notecook.Data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import javax.annotation.Nullable;


public class MySQLiteHelper extends SQLiteOpenHelper implements MySQLiteHelperTable {


    /*
     * Commande sql pour la création de la base de données
     */

    private static final String DATABASE_NAME = "DB_Notebook.db";
    private static final int DATABASE_VERSION = 1;


    /*
     * Commande sql pour la création de la table USER
     */

    private static final String DATABASE_CREATE_USER = "create table "
            + TABLE_USER + "(" + COLUMN_ID_USER
            + " integer primary key autoincrement, " + COLUMN_USERNAME + " text, " + COLUMN_FIRSTNAME_USER + " text, " + COLUMN_LASTNAME_USER + " text, "
            + COLUMN_ICON + " BLOB, " + COLUMN_ICON_PATH + " text, " + COLUMN_PASSWORD + " text, "
            + COLUMN_BIRTHDAY_USER + " text, " + COLUMN_PHONENUMBER_USER + " text, " + COLUMN_EMAIL_USER + " integer ,"
            + COLUMN_STATUS + " text, " + COLUMN_GRADE + " text );";

    /*
     * Commande sql pour la création de la table RECIPE
     */

    private static final String DATABASE_CREATE_RECIPE = "create table "
            + TABLE_RECIPE + "(" + COLUMN_ID_RECIPE
            + " integer primary key autoincrement, " + COLUMN_ICON_RECIPE
             +" text, "+ COLUMN_ICON_RECIPE_PATH +" text, " + COLUMN_FAV_RECIPE + " integer, " + COLUMN_NOM_RECIPE + " text , "
            + COLUMN_ID_FRK_USER_RECIPE + " integer, " + COLUMN_ID_FRK_CATEGORIE_RECIPE + " integer, " + " FOREIGN KEY (" + COLUMN_ID_FRK_USER_RECIPE + ") REFERENCES " + TABLE_USER + "(" + COLUMN_ID_USER + ") ON DELETE CASCADE , "
            + " FOREIGN KEY (" + COLUMN_ID_FRK_CATEGORIE_RECIPE + ") REFERENCES " + TABLE_CATEGORIE_RECIPE + "(" + COLUMN_ID_CATEGORIE_RECIPE + ") ON DELETE CASCADE );";

    /*
     * Commande sql pour la création de la table Detail_Recipe
     */

    private static final String DATABASE_CREATE_DETAIL_RECIPE = "create table "
            + TABLE_DETAIL_RECIPE + "(" + COLUMN_ID_DETAIL_RECIPE
            + " integer primary key autoincrement, " + COLUMN_DETAIL
            + " text, " + COLUMN_LEVEL_DR + " integer , " + COLUMN_TIME_DR
            + " integer , " + COLUMN_RATE_DR + " float, "
            + COLUMN_CALORIES + " integer , "
            + COLUMN_FRK_RECIPE_DETAIL + " integer, "
            + " FOREIGN KEY (" + COLUMN_FRK_RECIPE_DETAIL + ") REFERENCES " + TABLE_RECIPE + "(" + COLUMN_ID_RECIPE + ") ON DELETE CASCADE );";

    //example : mailbox_id INTEGER REFERENCES mailboxes ON DELETE CASCADE

    /*
     * Commande sql pour la création de la table Ingredients_Recipe
     */

    private static final String DATABASE_CREATE_INGREDIENTS_RECIPE = "create table "
            + TABLE_INGREDIENT_RECIPE + "(" + COLUMN_ID_INGREDIENT_RECIPE
            + " integer primary key autoincrement, " + COLUMN_INGREDIENT_RECIPE
            + " text, " + COLUMN_POIDINGREDIENT_RECIPE + " double, " + COLUMN_UNITEINGREDIENT_RECIPE + " text, "
            + COLUMN_FRK_DETAIL_INGREDIENT_RECIPE + " integer, " + " FOREIGN KEY (" + COLUMN_FRK_DETAIL_INGREDIENT_RECIPE + ") REFERENCES " + TABLE_DETAIL_RECIPE + "(" + COLUMN_ID_DETAIL_RECIPE + ") ON DELETE CASCADE );";

    // SQL statement to create the table Produit
    private static final String DATABASE_CREATE_PRODUIT =
            "create table " + TABLE_PRODUIT + " (" +
                    COLUMN_ID_PRODUIT + "  integer primary key autoincrement, " +
                    COLUMN_PRODUIT + " text, " +
                    COLUMN_POIDPRODUIT + " text)";
    /*
     * Commande sql pour la création de la table Recipe_produit
     */
    private static final String DATABASE_CREATE_RECIPE_PRODUIT =
            "CREATE TABLE " + TABLE_RECIPEPRODIUT + "(" +
                    COLUMN_ID_RECIPEPRODUIT + " integer primary key autoincrement, "
                    + COLUMN_FRK_RECIPE + " integer, " + COLUMN_FRK_PRODUIT + " integer ,"
                    + " FOREIGN KEY (" + COLUMN_FRK_RECIPE + ") REFERENCES " + TABLE_RECIPE + "(" + COLUMN_ID_RECIPE + ") ON DELETE CASCADE , "
                    + " FOREIGN KEY (" + COLUMN_FRK_PRODUIT + ") REFERENCES " + TABLE_PRODUIT + "(" + COLUMN_ID_PRODUIT + ") ON DELETE CASCADE );";
    ;


    /*
     * Commande sql pour la création de la table Step_Recipe
     */
    private static final String DATABASE_CREATE_STEP_RECIPE = "create table "
            + TABLE_STEP_RECIPE + "(" + COLUMN_ID_STEP_RECIPE
            + " integer primary key autoincrement, " + COLUMN_DETAIL_STEP_RECIPE
            + " text, " + COLUMN_IMAGE_STEP + " integer , " + COLUMN_TIME_STEP + " text, "
            + COLUMN_FRK_STEP_RECIPE + " integer, " + " FOREIGN KEY (" + COLUMN_FRK_STEP_RECIPE + ") REFERENCES " + TABLE_RECIPE + "(" + COLUMN_ID_RECIPE + ") ON DELETE CASCADE );";

    /*
     * Commande sql pour la création de la table Review_Recipe
     */
    private static final String DATABASE_CREATE_REVIEW_RECIPE = "create table "
            + TABLE_REVIEW_RECIPE + "(" + COLUMN_ID_REVIEW_RECIPE
            + " integer primary key autoincrement, " + COLUMN_DETAIL_REVIEW_RECIPE
            + " text, " + COLUMN_RATE_REVIEW_RECIPE + " double , "
            + COLUMN_FRK_DETAIL_REVIEW_RECIPE + " integer, " + " FOREIGN KEY (" + COLUMN_FRK_DETAIL_REVIEW_RECIPE + ") REFERENCES " + TABLE_DETAIL_RECIPE + "(" + COLUMN_ID_DETAIL_RECIPE + ") ON DELETE CASCADE );";

    /*
     * Commande sql pour la création de la table Categorie
     */
    private static final String DATABASE_CREATE_CATEGORIE_RECIPE = "create table "
            + TABLE_CATEGORIE_RECIPE + "(" + COLUMN_ID_CATEGORIE_RECIPE
            + " integer primary key autoincrement, " + COLUMN_ICON_CATEGORIE_RECIPE
            + " text, " + COLUMN_DETAIL_CATEGORIE_RECIPE + " text );";

    /*
     * Commande sql pour la création de la table list_Ingredients_recipe
     */
    private static final String DATABASE_CREATE_LIST_INGREDIENT_RECIPE = "create table "
            + TABLE_LIST_INGREDIENT_RECIPE + "(" + COLUMN_ID_LIST_INGREDIENT_RECIPE
            + " integer primary key autoincrement, " + COLUMN_FRK_ID_INGREDIENT_RECIPE + " integer , " + COLUMN_FRK_ID_RECIPE + " integer );";

    /*
     * Commande sql pour la création de la table FAV_RECIPE_USER
     */
        private static final String DATABASE_CREATE_FAV_RECIPE_USER = "create table "
            + TABLE_FAV_RECIPE_USER + "(" + COLUMN_ID_FAV_USER + " integer primary key autoincrement, "
            + COLUMN_FRK_RECIPE_FAV + " integer , "
            + COLUMN_FRK_USER_FAV + " integer , "
            + " FOREIGN KEY (" + COLUMN_FRK_USER_FAV + ") REFERENCES " + TABLE_USER + "(" + COLUMN_ID_USER + ") ON DELETE CASCADE , "
            +  "FOREIGN KEY (" + COLUMN_FRK_RECIPE_FAV + ") REFERENCES " + TABLE_RECIPE + "(" + COLUMN_ID_RECIPE + ") ON DELETE CASCADE )";

    /*
     * Commande sql pour la création des champs dans les table intial
     */
    private static final String InsertDataUser = "INSERT INTO " + TABLE_USER +
            " (\"" + COLUMN_USERNAME + "\",\"" + COLUMN_PASSWORD + "\") values ('R_','$2a$10$s0LOsngxGXQpiDywyvF6ceCBGN238klsEprYMtVWdpnlXruSNvjnO'); ";

    private static final String InsertDataRecipe = "INSERT INTO " + TABLE_RECIPE + "" +
            " (\"" + COLUMN_NOM_RECIPE + "\",\"" + COLUMN_ID_FRK_USER_RECIPE + "\") values " +
            "('Pancakes',1)," +
            "('Chicken Curry',1)," +
            "('Salad with Vinaigrette',1)," +
            "('Vegetable Curry',1)," +
            "('Mushroom Risotto',1)," +
            "('Spaghetti Bolognese',1)," +
            "('Vegetable Soup',1)," +
            "('Grilled Salmon',1)," +
            "('Pasta', '200g', 1)," +
            "('Chocolate Cake', '200g', 1);";

    private static final String InsertDataDetailRecipe = "INSERT INTO " + TABLE_DETAIL_RECIPE + "" +
            " (\"" + COLUMN_DETAIL + "\",\"" + COLUMN_TIME_DR + "\",\"" + COLUMN_RATE_DR
            + "\",\"" + COLUMN_LEVEL_DR + "\",\"" + COLUMN_CALORIES + "\",\"" + COLUMN_FRK_RECIPE_DETAIL + "\") values " +
            "('Mix flour, milk, and eggs to make the batter.', 15, 4, 'easy', 250, 1)," +
            "('Simmer chicken with spices and coconut milk.', 30, 5, 'medium', 450, 2)," +
            "('Chop lettuce, tomatoes, and cucumbers; drizzle with vinaigrette.', 10, 3, 'low', 120, 3) ," +
            "('Simmer chicken with spices and coconut milk.', 30, 5, 'medium', 450, 4), " +
            "('Chop lettuce, tomatoes, and cucumbers; drizzle with vinaigrette.', 10, 3, 'low', 120, 5)," +
            "('Brown ground beef and onions, then add tomato sauce and simmer.', 45, 4, 'medium', 380, 6)," +
            "('Boil vegetables in broth, then blend until smooth.', 20, 3, 'easy', 180, 7)," +
            "('Boil water for pasta', '5', '3', 'easy', 9)," +
            "('Preheat oven to 350°F', 10, 5,'medium', 10);";

    private static final String InsertDataIngredient = "INSERT INTO " + TABLE_INGREDIENT_RECIPE + "" +
            " (\"" + COLUMN_INGREDIENT_RECIPE + "\",\"" + COLUMN_POIDINGREDIENT_RECIPE + "\",\"" + COLUMN_FRK_DETAIL_INGREDIENT_RECIPE + "\") values  " +
            "('2 cups of all-purpose flour', 200, 1)," +
            "  ('1 cup of milk', 250, 1)," +
            "('Salt', 5,1), " +
            "('Black Pepper', 2,1), " +
            "('Olive Oil', 30,1), " +
            "('Garlic', 10,1)," +
            "  ('2 eggs', 100, 1)," +
            "  ('500g of boneless chicken', 500, 2)," +
            "  ('400ml of coconut milk', 400, 2)," +
            "  ('Lettuce, tomatoes, cucumbers', 300, 3)," +
            "  ('Olive oil, vinegar, salt, pepper', 50, 3)," +
            "('Ground Beef', 300, 4)," +
            "('Onions', 50, 6)," +
            "('Tomato Sauce', 300, 6)," +
            "('Mixed Vegetables', 250, 7)," +
            "('Vegetable Broth', 500, 7), " +
            "('Pasta', '200g', 9)," +
            "('Olive Oil', '2 tbsp', 2);";


    private static final String InsertDataListIngredient = "INSERT INTO " + TABLE_LIST_INGREDIENT_RECIPE + "" +
            " (\"" + COLUMN_FRK_ID_INGREDIENT_RECIPE + "\",\"" + COLUMN_FRK_ID_RECIPE + "\") values" +
            "  (1, 1)," +
            "  (2, 1)," +
            "  (3, 1)," +
            "  (4, 2)," +
            "  (5, 2)," +
            "  (6, 3)," +
            "  (7, 3); ";

    private static final String InsertDataStep = "INSERT INTO " + TABLE_STEP_RECIPE + "" +
            " (\"" + COLUMN_DETAIL_STEP_RECIPE + "\",\"" + COLUMN_TIME_STEP + "\",\"" + COLUMN_FRK_STEP_RECIPE + "\") values" +
            " ('Step 1: Mix flour and milk in a bowl.', 5, 1)," +
            "  ('Step 2: Add eggs and whisk until smooth.', 3, 1)," +
            "  ('Step 3: Pour batter onto a hot griddle.', 2, 1)," +
            "  ('Step 1: Heat oil and sauté chicken pieces.', 10, 2)," +
            "  ('Step 2: Add spices and coconut milk.',  8, 2)," +
            "  ('Step 3: Simmer until chicken is cooked.', 12, 2)," +
            "  ('Step 1: Chop vegetables and place them in a bowl.', 5, 3)," +
            "  ('Step 2: Prepare vinaigrette dressing.',  2, 3)," +
            "  ('Step 3: Drizzle dressing over the salad.', 1, 3)," +
            "  ('Step 1: Brown ground beef and onions in a skillet.', 10, 6)," +
            "  ('Step 2: Add tomato sauce and simmer for 30 minutes.', 30, 6), " +
            "  ('Step 1: Boil mixed vegetables in vegetable broth.', 10, 7)," +
            "  ('Step 2: Blend the mixture until smooth.', 10, 7) ," +
            "  ('Step 1: Season the salmon fillet with salt and pepper.', 5, 8)," +
            "  ('Step 2: Preheat the grill to medium-high heat.', 10, 8)," +
            "  ('Step 3: Place the salmon fillet on the grill and cook for 5-7 minutes on each side.', 15, 8)," +
            "  ('Step 4: Remove from the grill and drizzle with olive oil and lemon juice.', 5, 8)," +
            "('Boil water for pasta', 5, 9)," +
            "('Grease baking pan', 3, 10);";

    private static final String InsertDataReview = "INSERT INTO " + TABLE_REVIEW_RECIPE + "" +
            " (\"" + COLUMN_DETAIL_REVIEW_RECIPE + "\",\"" + COLUMN_RATE_REVIEW_RECIPE + "\",\"" + COLUMN_FRK_DETAIL_REVIEW_RECIPE + "\") values " +
            "('Best pancakes ever!', 5, 1)," +
            "  ('Delicious and creamy curry.', 5, 2)," +
            "  ('Simple and tasty salad.', 4, 3)," +
            "  ('Good recipe for beginners.', 4, 1)," +
            "  ('Loved the coconut flavor!', 5, 2)," +
            "  ('Healthy and refreshing salad.', 5, 3)," +
            "  ('Fantastic spaghetti bolognese!', 5, 6)," +
            "  ('Delicious vegetable soup!', 4, 7)," +
            "  ('Delicious grilled salmon!', 5, 7)," +
            "  ('Perfectly cooked salmon.', 4, 7)," +
            "  ('Easy and quick recipe.', 5, 7), " +
            "('Delicious! Will make it again.', 5, 9)," +
            "('Perfect cake, my family loved it.', 5, 2);";


    public MySQLiteHelper(@Nullable Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        sqLiteDatabase.execSQL(DATABASE_CREATE_USER);
        sqLiteDatabase.execSQL(DATABASE_CREATE_RECIPE);
        sqLiteDatabase.execSQL(DATABASE_CREATE_DETAIL_RECIPE);
        sqLiteDatabase.execSQL(DATABASE_CREATE_INGREDIENTS_RECIPE);
        sqLiteDatabase.execSQL(DATABASE_CREATE_STEP_RECIPE);
        sqLiteDatabase.execSQL(DATABASE_CREATE_REVIEW_RECIPE);
        sqLiteDatabase.execSQL(DATABASE_CREATE_LIST_INGREDIENT_RECIPE);
        sqLiteDatabase.execSQL(DATABASE_CREATE_CATEGORIE_RECIPE);
        sqLiteDatabase.execSQL(DATABASE_CREATE_PRODUIT);
        sqLiteDatabase.execSQL(DATABASE_CREATE_RECIPE_PRODUIT);
        sqLiteDatabase.execSQL(DATABASE_CREATE_FAV_RECIPE_USER);
        sqLiteDatabase.execSQL(InsertDataUser);
//        sqLiteDatabase.execSQL(InsertDataRecipe);
//        sqLiteDatabase.execSQL(InsertDataDetailRecipe);
//        sqLiteDatabase.execSQL(InsertDataIngredient);
//        sqLiteDatabase.execSQL(InsertDataListIngredient);
//        sqLiteDatabase.execSQL(InsertDataStep);
//        sqLiteDatabase.execSQL(InsertDataReview);
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + TABLE_USER);
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + TABLE_RECIPE);
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + TABLE_DETAIL_RECIPE);
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + TABLE_STEP_RECIPE);
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + TABLE_REVIEW_RECIPE);
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + TABLE_INGREDIENT_RECIPE);
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + TABLE_LIST_INGREDIENT_RECIPE);
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + TABLE_PRODUIT);
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + TABLE_RECIPEPRODIUT);
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + TABLE_FAV_RECIPE_USER);

        onCreate(sqLiteDatabase);
    }

    @Override
    public void onConfigure(SQLiteDatabase db) {
        db.setForeignKeyConstraintsEnabled(true);
    }
}
