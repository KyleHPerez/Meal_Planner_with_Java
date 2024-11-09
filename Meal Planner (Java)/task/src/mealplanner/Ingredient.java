package mealplanner;

public class Ingredient {
    private final String name;
    private final int iD;
    private final int mealID;

    public Ingredient(String name, int iD, int mealID) {
        this.name = name;
        this.iD = iD;
        this.mealID = mealID;
    }

    @Override
    public String toString() {
        return String.format("Ingredient{ingredient=%s,ingredient_id=%d,meal_id=%d}", name, iD, mealID);
    }

    public String getName() {
        return name;
    }

    public int getID() {
        return iD;
    }

    public int getMealID() {
        return mealID;
    }
}
