package mealplanner;

import org.postgresql.ds.PGSimpleDataSource;

import java.util.List;

public class DbPlanDao implements PlanDao {

    private static final String CONNECTION_URL = "jdbc:postgresql:meals_db";
    private static final String USER = "postgres";
    private static final String PASS = "1111";

    private static final String CREATE_TABLE = "CREATE TABLE IF NOT EXISTS plan (" +
            "day VARCHAR(9) NOT NULL," +
            "meal_category VARCHAR(9) NOT NULL," +
            "meal_id INTEGER NOT NULL," +
            "plan_id INTEGER" +
            ")";


    private static final String MAX_ID = "SELECT MAX(plan_id) FROM plan";
    private static final String SELECT_ALL = "SELECT * FROM plan";
    private static final String SELECT_BY_DAY = "SELECT * FROM plan WHERE day = '%s'";
    private static final String SELECT_BY_CATEGORY = "SELECT * FROM plan WHERE meal_category = '%s'";
    private static final String SELECT_BY_DAY_AND_CATEGORY = "SELECT * FROM plan WHERE day = %s AND meal_category = '%s'";
    private static final String SELECT_BY_MEAL_ID = "SELECT * FROM plan WHERE meal_id = %d";
    private static final String SELECT_BY_PLAN_ID = "SELECT * FROM plan WHERE plan_id = %d";
    private static final String INSERT = "INSERT INTO plan (day, meal_category, meal_id) VALUES ('%s', '%s', %d)";
    private static final String UPDATE = "UPDATE plan SET day = '%s', meal_category = '%s', meal_id = %d WHERE plan_id = %d";
    private static final String DELETE = "DELETE FROM plan WHERE plan_id = %d";
    private static final String DROP = "DROP TABLE IF EXISTS plan";

    private final DbClient dbClient;

    public DbPlanDao() {
        PGSimpleDataSource dataSource = new PGSimpleDataSource();
        dataSource.setUrl(CONNECTION_URL);
        dataSource.setUser(USER);
        dataSource.setPassword(PASS);

        dbClient = new DbClient(dataSource);
        dbClient.run(DROP);
        dbClient.run(CREATE_TABLE);
    }

    @Override
    public void add(Plan plan) {
        dbClient.run(String.format(INSERT, plan.getDay(), plan.getMealCategory(), plan.getMealID()));
    }

    @Override
    public List<Plan> findAll() {
        return dbClient.selectPlanForList(SELECT_ALL);
    }

    @Override
    public List<Plan> findByDay(String day) {
        return dbClient.selectPlanForList(String.format(SELECT_BY_DAY, day));
    }

    @Override
    public List<Plan> findByCategory(String category) {
        return dbClient.selectPlanForList(String.format(SELECT_BY_CATEGORY, category));
    }

    public Plan findByDayAndCategory(String day, String category) {
        return dbClient.selectPlan(String.format(SELECT_BY_DAY_AND_CATEGORY, day, category));
    }

    @Override
    public List<Plan> findByMealID(int mealID) {
        return dbClient.selectPlanForList(String.format(SELECT_BY_MEAL_ID, mealID));
    }

    @Override
    public Plan findById(int id) {
        return dbClient.selectPlan(String.format(SELECT_BY_PLAN_ID, id));
    }

    @Override
    public void update(Plan plan) {
        dbClient.run(String.format(UPDATE, plan.getDay(), plan.getMealCategory(), plan.getMealID(), plan.getPlanID()));
    }

    @Override
    public void delete(int id) {
        dbClient.run(String.format(DELETE, id));
    }

    public int getMaxID() {
        return dbClient.get(MAX_ID);
    }
}
