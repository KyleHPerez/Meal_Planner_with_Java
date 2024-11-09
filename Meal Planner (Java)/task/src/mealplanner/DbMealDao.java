package mealplanner;

import org.postgresql.ds.PGSimpleDataSource;

import java.util.List;

public class DbMealDao implements MealDao {
    private static final String CONNECTION_URL = "jdbc:postgresql:meals_db";
    private static final String USER = "postgres";
    private static final String PASS = "1111";

    private static final String CREATE_TABLE = "CREATE TABLE IF NOT EXISTS meals (" +
            "category VARCHAR(9) NOT NULL," +
            "meal VARCHAR(1024) NOT NULL," +
            "meal_id INTEGER NOT NULL" +
            ")";

    private static final String MAX_ID = "SELECT MAX(meal_id) FROM meals;";
    private static final String SELECT_ALL = "SELECT * FROM meals";
    private static final String SELECT_BY_CATEGORY = "SELECT * FROM meals WHERE category = '%s'";
    private static final String SELECT_BY_CATEGORY_AND_NAME = "SELECT * FROM meals WHERE category = '%s' AND meal = '%s'";
    private static final String SELECT_BY_ID = "SELECT * FROM meals WHERE meal_id = %d";
    private static final String INSERT = "INSERT INTO meals (category, meal, meal_id) VALUES ('%s', '%s', %d)";
    private static final String UPDATE = "UPDATE meals SET category = '%s', name = %s WHERE meal_id = %d";
    private static final String DELETE = "DELETE FROM meals WHERE id = %d";
    private static final String DROP = "DROP TABLE IF EXISTS meals";

    private final DbClient dbClient;

    public DbMealDao() {
        PGSimpleDataSource dataSource = new PGSimpleDataSource();
        dataSource.setUrl(CONNECTION_URL);
        dataSource.setUser(USER);
        dataSource.setPassword(PASS);

        dbClient = new DbClient(dataSource);
        dbClient.run(DROP);
        dbClient.run(CREATE_TABLE);
    }

    @Override
    public void add(Meal meal) {
        dbClient.run(String.format(INSERT, meal.category, meal.mealName, meal.mealID));
    }

    @Override
    public List<Meal> findAll() {
        return dbClient.selectMealForList(SELECT_ALL);
    }

    @Override
    public List<Meal> findBy(String category) {
        return dbClient.selectMealForList(String.format(SELECT_BY_CATEGORY, category));
    }

    public Meal findBy(String category, String mealName) {
        return dbClient.selectMeal(String.format(SELECT_BY_CATEGORY_AND_NAME, category, mealName));
    }

    @Override
    public Meal findById(int id) {
        Meal meal = dbClient.selectMeal(String.format(SELECT_BY_ID, id));

        if (meal != null) {
            return meal;
        } else {
            System.out.println("Meal not found.");
            return null;
        }
    }

    @Override
    public void update(Meal meal) {
        dbClient.run(String.format(UPDATE, meal.category, meal.mealName, meal.mealID));

    }

    @Override
    public void delete(int id) {
        dbClient.run(String.format(DELETE, id));
    }

    @Override
    public int getMaxId() {
        return dbClient.get(MAX_ID);
    }
}
