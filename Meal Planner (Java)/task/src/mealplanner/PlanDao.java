package mealplanner;

import java.util.List;

public interface PlanDao {
    void add(Plan plan);
    List<Plan> findAll();
    List<Plan> findByDay(String day);
    List<Plan> findByCategory(String category);
    List<Plan> findByMealID(int mealID);
    Plan findById(int id);
    void update(Plan plan);
    void delete(int id);
}
