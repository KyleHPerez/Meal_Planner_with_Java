package mealplanner;

import java.sql.Connection;
import java.sql.*;

public class Meal {
    static int mealIDCounter;

    String category;
    String mealName;
    Ingredient[] ingredients;
    int mealID;

    static {
        mealIDCounter = 1;
    }

    public Meal(String mealType, String mealName, Ingredient[] ingredients) throws SQLException {
        this.category = mealType;
        this.mealName = mealName;
        this.ingredients = ingredients;
        this.mealID = mealIDCounter;
        mealIDCounter++;
    }

    @Override
    public String toString() {
        return String.format("Meal{category=%s, mealName=%s, mealID=%d}", category, mealName, mealID);
    }

    public String getCategory() {
        return category;
    }

    public String getName() {
        return mealName;
    }

    public Ingredient[] getIngredients() {
        return ingredients;
    }
}
