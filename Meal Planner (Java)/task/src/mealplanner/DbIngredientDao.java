package mealplanner;

import org.postgresql.ds.PGSimpleDataSource;

import java.util.List;

public class DbIngredientDao implements IngredientDao {
    private static final String CONNECTION_URL = "jdbc:postgresql:meals_db";
    private static final String USER = "postgres";
    private static final String PASS = "1111";

    private static final String CREATE_TABLE = "CREATE TABLE IF NOT EXISTS ingredients (" +
            "ingredient VARCHAR(1024) NOT NULL," +
            "ingredient_id INTEGER NOT NULL," +
            "meal_id INTEGER NOT NULL" +
            ")";

    private static final String MAX_ID = "SELECT MAX(ingredient_id) FROM ingredients";
    private static final String SELECT_ALL = "SELECT * FROM ingredients";
    private static final String SELECT_BY_FK = "SELECT * FROM ingredients WHERE meal_id = %d";
    private static final String SELECT_BY_ID = "SELECT * FROM ingredients WHERE ingredient_id = %d";
    private static final String INSERT = "INSERT INTO ingredients (ingredient, ingredient_id, meal_id) VALUES ('%s', %d, %d)";
    private static final String UPDATE = "UPDATE ingredients SET ingredient = '%s', meal_id = %d WHERE ingredient_id = %d";
    private static final String DELETE = "DELETE FROM ingredients WHERE ingredient_id = %d";
    private static final String DROP = "DROP TABLE IF EXISTS ingredients";

    private final DbClient dbClient;

    public DbIngredientDao() {
        PGSimpleDataSource dataSource = new PGSimpleDataSource();
        dataSource.setUrl(CONNECTION_URL);
        dataSource.setUser(USER);
        dataSource.setPassword(PASS);

        dbClient = new DbClient(dataSource);
        dbClient.run(DROP);
        dbClient.run(CREATE_TABLE);
    }

    @Override
    public void add(Ingredient ingredient) {
        dbClient.run(String.format(INSERT, ingredient.getName(), ingredient.getID(), ingredient.getMealID()));
    }

    @Override
    public List<Ingredient> findAll() {
        return dbClient.selectIngredientForList(SELECT_ALL);
    }

    @Override
    public List<Ingredient> findBy(int mealID) {
        return dbClient.selectIngredientForList(String.format(SELECT_BY_FK, mealID));
    }

    @Override
    public Ingredient findById(int ingredientID) {
        Ingredient ingredient = dbClient.selectIngredient(String.format(SELECT_BY_ID, ingredientID));

        if (ingredient != null) {
            return ingredient;
        } else {
            System.out.println("Ingredient not found.");
            return null;
        }
    }

    @Override
    public void update(Ingredient ingredient) {
        dbClient.run(String.format(UPDATE, ingredient.getName(), ingredient.getMealID(), ingredient.getID()));

    }

    @Override
    public void delete(int id) {
        dbClient.run(String.format(DELETE, id));
    }

    public int getMaxID() {
        return dbClient.get(MAX_ID);
    }

}
