package mealplanner;

import java.util.List;

public interface IngredientDao {
        void add(Ingredient ingredient);
        List<Ingredient> findAll();
        List<Ingredient> findBy(int mealID);
        Ingredient findById(int id);
        void update(Ingredient ingredient);
        void delete(int id);
}
