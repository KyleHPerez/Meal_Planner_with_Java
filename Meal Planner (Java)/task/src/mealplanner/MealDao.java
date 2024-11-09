package mealplanner;

import java.util.List;

public interface MealDao {
    void add(Meal meal);
    List<Meal> findAll();
    List<Meal> findBy(String category);
    Meal findById(int id);
    void update(Meal meal);
    void delete(int id);
    int getMaxId();
}
