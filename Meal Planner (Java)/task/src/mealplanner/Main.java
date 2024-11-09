package mealplanner;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.sql.*;
import java.util.*;

public class Main {
    public static void main(String[] args) throws SQLException {

        DbMealDao mealDao = new DbMealDao();
        DbIngredientDao ingredientDao = new DbIngredientDao();
        DbPlanDao planDao = new DbPlanDao();

        try (Scanner scanner = new Scanner(System.in)) {
            while (true) {
                System.out.println("What would you like to do (add, show, plan, list plan, save, exit)?");
                String command = scanner.nextLine();
                switch (command) {
                    case "add": {

                        String mealType = null;
                        String mealName = null;
                        String[] ingredients = null;

                        boolean proceed = false;
                        while (!proceed) {
                            System.out.println("Which meal do you want to add (breakfast, lunch, dinner)?");
                            mealType = switch (scanner.nextLine()) {
                                case "breakfast" -> {proceed = true; yield "breakfast";}
                                case "lunch" -> {proceed = true; yield "lunch";}
                                case "dinner" -> {proceed = true; yield "dinner";}
                                default -> {
                                    System.out.println("Wrong meal category! Choose from: breakfast, lunch, dinner.");
                                    yield null;
                                }
                            };
                        }

                        boolean proceed2 = false;
                        System.out.println("Input the meal's name:");
                        while (!proceed2) {
                            mealName = scanner.nextLine();
                            proceed2 = mealName.matches("[A-Za-z][A-Za-z\\s]+") ? true : false;
                            if (!proceed2) {
                                System.out.println("Wrong format. Use letters only!");
                            }
                        }

                        boolean proceed3 = false;
                        System.out.println("Input the ingredients:");
                        while (!proceed3) {
                            String ingredientsLine = scanner.nextLine();
                            proceed3 = ingredientsLine.matches("([A-Za-z][A-Za-z\\s]*,\\s)*([A-Za-z][A-Za-z\\s]*)");
                            if (!proceed3) {
                                System.out.println("Wrong format. Use letters only!");
                            } else {
                                ingredients = ingredientsLine.split(", ");
                            }
                        }
                        int mealID = mealDao.getMaxId() + 1;
                        int ingredientID = ingredientDao.getMaxID() + 1;
                        List<Ingredient> ingredientsList = new ArrayList<>();
                        for (String ingredient : ingredients) {
                            Ingredient ingredientObj = (new Ingredient(ingredient, ingredientID, mealID));
                            ingredientDao.add(ingredientObj);
                            ingredientsList.add(ingredientObj);
                            ingredientID++;
                        }

                        Ingredient[] ingredientObjects = new Ingredient[ingredientsList.size()];
                        ingredientObjects = ingredientsList.toArray(ingredientObjects);
                        mealDao.add(new Meal(mealType, mealName, ingredientObjects));
                        break;
                    }
                    case "show": {
                        String categorySelection = "";
                        boolean proceed = false;

                        System.out.println("Which category do you want to print (breakfast, lunch, dinner)?");
                        while (!proceed) {
                            categorySelection = scanner.nextLine();
                            switch (categorySelection) {
                                case "breakfast" -> {proceed = true; break;}
                                case "lunch" -> {proceed = true; break;}
                                case "dinner" -> {proceed = true; break;}
                                default -> {System.out.println("Wrong meal category! Choose from: breakfast, lunch, dinner.");}
                            }
                        }
                        List<Meal> meals = mealDao.findBy(categorySelection);
                        meals.forEach(meal -> {
                            System.out.println(meal.getName());
                            for (Ingredient ingredient : meal.getIngredients()) {
                                System.out.println(ingredient.getName());
                            }
                            System.out.println();
                        });
                    }
                    case "plan": {
                        int planID = planDao.getMaxID() + 1;
                        String requestForm = "Choose the %s for %s from the list above:";

                        String[] days = {"Monday", "Tuesday", "Wednesday", "Thursday", "Friday", "Saturday", "Sunday"};
                        String[] categories = {"breakfast", "lunch", "dinner"};
                        for (String day : days) {
                            System.out.println(day);
                            for (String category : categories) {
                                List<Meal> mealList = mealDao.findBy(category);
                                mealList.forEach(meal -> System.out.println(meal.getName()));
                                System.out.println(String.format(requestForm, category, day));
                                boolean proceed = false;
                                while (!proceed) {
                                    String mealName = scanner.nextLine();
                                    Meal meal = mealDao.findBy(category, mealName);
                                    if (meal != null) {
                                        planDao.add(new Plan(day, category, meal.mealID, planID));
                                    }
                                    planID++;
                                    proceed = true;
                                }
                            }
                            System.out.printf("Yeah! we planned the meals for %s.\n\n", day);
                        }
                        listPlans(mealDao, planDao);
                    }
                    case "list plan": {listPlans(mealDao, planDao);break;}
                    case "save" : {
                        boolean proceed = false;
                        if (planDao.findAll().isEmpty()) {
                            System.out.println("Unable to save. Plan your meals first.");
                        } else {
                            System.out.println("Input a filename:");
                            String filename = scanner.nextLine();
                            try (FileWriter fw = new FileWriter(filename)) {
                                for (String ingredient : createShoppingList(planDao, mealDao)) {
                                    fw.write(ingredient);
                                }
                            }
                        }
                        break;
                    }
                    case "exit": {System.exit(0);}
                    default: {System.out.println("Invalid command");break;}
                }
            }
        } catch (InputMismatchException | SQLException | IOException e) {
            System.out.println(e.getMessage());
        }
    }

    public static String[] createShoppingList(PlanDao planDao, MealDao mealDao) {
        List<Plan> plans = planDao.findAll();
        List<Ingredient[]> ingredientsList = new ArrayList<>();
        plans.forEach(plan -> {
            ingredientsList.add(mealDao.findById(plan.getMealID()).getIngredients());
        });
        Map<String,Integer> ingredientCountMap = new HashMap<>();
        ingredientsList.forEach(ingredientArray -> {
            for (Ingredient ingredient : ingredientArray) {
                if (ingredientCountMap.containsKey(ingredient.getName())) {
                    ingredientCountMap.put(ingredient.getName(), ingredientCountMap.get(ingredient.getName()) + 1);
                } else {
                    ingredientCountMap.put(ingredient.getName(), 1);
                }
            }
        });
        List<String> shoppingList = new ArrayList<>();
        ingredientCountMap.forEach((k,v) -> {shoppingList.add(String.format("%s x%d%n", k, v));});
        return shoppingList.toArray(new String[shoppingList.size()]);
    }


    private static void listPlans(DbMealDao mealDao, DbPlanDao planDao) {
        String[] days = {"Monday", "Tuesday", "Wednesday", "Thursday", "Friday", "Saturday", "Sunday"};
        String[] categories = {"breakfast", "lunch", "dinner"};
        for (String day : days) {
            System.out.println(day + "\n");
            for (String category : categories) {
                Plan plan = planDao.findByDayAndCategory(day, category);
                System.out.printf("""
                   %s: %s%n
                   """, category, mealDao.findById(plan.getMealID()).getName());
            }
            System.out.println();
        }
        return;
    }
}