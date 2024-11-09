package mealplanner;

import javax.sql.*;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class DbClient {
    private final DataSource dataSource;

    public DbClient(DataSource ds) {
        this.dataSource = ds;
    }

    public void run(String query) {
        try (Connection conn = dataSource.getConnection();
        Statement stmt = conn.createStatement()) {
            stmt.executeUpdate(query);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public Meal selectMeal(String query) {
        List<Meal> meals = selectMealForList(query);
        if (meals.size() == 1) {
            return meals.getFirst();
        } else if (meals.isEmpty()) {
            return null;
        } else {
            throw new IllegalStateException("Query returned more than one meal.");
        }
    }

    public Ingredient selectIngredient(String query) {
        List<Ingredient> ingredients = selectIngredientForList(query);
        if (ingredients.size() == 1) {
            return ingredients.getFirst();
        } else if (ingredients.isEmpty()) {
            return null;
        } else {
            throw new IllegalStateException("Query returned more than one ingredient.");
        }
    }

    public Plan selectPlan(String query) {
        List<Plan> plans = selectPlanForList(query);
        if (plans.size() == 1) {
            return plans.getFirst();
        } else if (plans.isEmpty()) {
            return null;
        } else {
            throw new IllegalStateException("Query returned more than one plan.");
        }
    }

    public List<Meal> selectMealForList(String query) {
        List<Meal> meals = new ArrayList<>();
        try (Connection conn = dataSource.getConnection();
            Statement statement = conn.createStatement();
            ResultSet resultSetM = statement.executeQuery(query)
        ) {
            while (resultSetM.next()) {
                String category = resultSetM.getString("category");
                String meal_name = resultSetM.getString("meal");
                int meal_id = resultSetM.getInt("meal_id");

                String queryI = String.format("SELECT * FROM ingredients WHERE meal_id = %d", meal_id);
                List<Ingredient> ingredients = selectIngredientForList(queryI);
                Ingredient[] ingredientArray = new Ingredient[ingredients.size()];
                ingredients.toArray(ingredientArray);

                meals.add(new Meal(category, meal_name, ingredientArray));
            }
            return meals;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return meals; // empty list
    }

    public List<Ingredient> selectIngredientForList(String query) {
        List<Ingredient> ingredients = new ArrayList<>();
        try (Connection conn = dataSource.getConnection();
            PreparedStatement preparedStatement = conn.prepareStatement(query);
            ) {
                try (ResultSet resultSet = preparedStatement.executeQuery()) {
                    while (resultSet.next()) {
                        String ingredient = resultSet.getString("ingredient");
                        int ingredientID = resultSet.getInt("ingredient_id");
                        int mealID = resultSet.getInt("meal_id");
                        ingredients.add(new Ingredient(ingredient, ingredientID, mealID));
                    }
                }
            return ingredients;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return ingredients;
    }

    public List<Plan> selectPlanForList(String query) {
        List<Plan> plans = new ArrayList<>();
        try (Connection conn = dataSource.getConnection();
        Statement statement = conn.createStatement();
        ResultSet resultSet = statement.executeQuery(query)
        ){
            while (resultSet.next()) {
                String day = resultSet.getString("day");
                String mealCategory = resultSet.getString("meal_category");
                int mealID = resultSet.getInt("meal_id");
                int planID = resultSet.getInt("plan_id");

                plans.add(new Plan(day, mealCategory, mealID, planID));
            }
            return plans;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return plans; //empty
    }

    public int get(String query) {
        try (Connection conn = dataSource.getConnection();
        Statement statement = conn.createStatement();
        ResultSet resultSet = statement.executeQuery(query)
        ){
            while (resultSet.next()) {
                return resultSet.getInt(1);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 0;
    }

}
