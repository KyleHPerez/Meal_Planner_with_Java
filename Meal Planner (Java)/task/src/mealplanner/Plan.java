package mealplanner;

public class Plan {
    private final String day;
    private final String mealCategory;
    private final int mealID;
    private final int planID;

    public Plan(String day, String mealCategory, int mealID, int planID) {
        this.day = day;
        this.mealCategory = mealCategory;
        this.mealID = mealID;
        this.planID = planID;
    }

    @Override
    public String toString() {
        return String.format("Plan{day=%s,mealCategory=%s,mealID=%d}", day, mealCategory, mealID);
    }

    public String getMealCategory() {
        return this.mealCategory;
    }

    public String getDay() {
        return this.day;
    }

    public int getMealID() {
        return this.mealID;
    }

    public int getPlanID() {
        return this.planID;
    }
}
