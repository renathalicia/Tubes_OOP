package com.Spakborhills.item;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class RecipeRepository {
    public static Map<String, Recipe> allRecipes = new HashMap<>();

    public static void initializeRecipes() {
        allRecipes.clear();

        // recipe_1: Fish n' Chips
        List<Recipe.Ingredient> fishNChipsIngredients = new ArrayList<>();
        fishNChipsIngredients.add(new Recipe.Ingredient("Any Fish", 2, true));
        fishNChipsIngredients.add(new Recipe.Ingredient("Wheat", 1, false));
        fishNChipsIngredients.add(new Recipe.Ingredient("Potato", 1, false));
        allRecipes.put("recipe_1", new Recipe("recipe_1", "Fish n' Chips", 1, fishNChipsIngredients, "Fish n' Chips", false));

        // recipe_2: Baguette
        List<Recipe.Ingredient> baguetteIngredients = new ArrayList<>();
        baguetteIngredients.add(new Recipe.Ingredient("Wheat", 3, false));
        allRecipes.put("recipe_2", new Recipe("recipe_2", "Baguette", 1, baguetteIngredients, "Baguette", true));

        // recipe_3: Sashimi
        List<Recipe.Ingredient> sashimiIngredients = new ArrayList<>();
        sashimiIngredients.add(new Recipe.Ingredient("Salmon", 3, false));
        allRecipes.put("recipe_3", new Recipe("recipe_3", "Sashimi", 1, sashimiIngredients, "Sashimi", false));

        // recipe_4: Fugu
        List<Recipe.Ingredient> fuguIngredients = new ArrayList<>();
        fuguIngredients.add(new Recipe.Ingredient("Pufferfish", 1, false));
        allRecipes.put("recipe_4", new Recipe("recipe_4", "Fugu", 1, fuguIngredients, "Fugu", false));

        // recipe_5: Wine
        List<Recipe.Ingredient> wineIngredients = new ArrayList<>();
        wineIngredients.add(new Recipe.Ingredient("Grape", 2, false));
        allRecipes.put("recipe_5", new Recipe("recipe_5", "Wine", 1, wineIngredients, "Wine", true));

        // recipe_6: Pumpkin Pie
        List<Recipe.Ingredient> pumpkinPieIngredients = new ArrayList<>();
        pumpkinPieIngredients.add(new Recipe.Ingredient("Egg", 1, false));
        pumpkinPieIngredients.add(new Recipe.Ingredient("Wheat", 1, false));
        pumpkinPieIngredients.add(new Recipe.Ingredient("Pumpkin", 1, false));
        allRecipes.put("recipe_6", new Recipe("recipe_6", "Pumpkin Pie", 1, pumpkinPieIngredients, "Pumpkin Pie", true));

        // recipe_7: Veggie Soup
        List<Recipe.Ingredient> veggieSoupIngredients = new ArrayList<>();
        veggieSoupIngredients.add(new Recipe.Ingredient("Cauliflower", 1, false));
        veggieSoupIngredients.add(new Recipe.Ingredient("Parsnip", 1, false));
        veggieSoupIngredients.add(new Recipe.Ingredient("Potato", 1, false));
        veggieSoupIngredients.add(new Recipe.Ingredient("Tomato", 1, false));
        allRecipes.put("recipe_7", new Recipe("recipe_7", "Veggie Soup", 1, veggieSoupIngredients, "Veggie Soup", false));

        // recipe_8: Fish Stew
        List<Recipe.Ingredient> fishStewIngredients = new ArrayList<>();
        fishStewIngredients.add(new Recipe.Ingredient("Any Fish", 2, true));
        fishStewIngredients.add(new Recipe.Ingredient("Hot Pepper", 1, false));
        fishStewIngredients.add(new Recipe.Ingredient("Cauliflower", 2, false));
        allRecipes.put("recipe_8", new Recipe("recipe_8", "Fish Stew", 1, fishStewIngredients, "Fish Stew", false));

        // recipe_9: Spakbor Salad
        List<Recipe.Ingredient> spakborSaladIngredients = new ArrayList<>();
        spakborSaladIngredients.add(new Recipe.Ingredient("Melon", 1, false));
        spakborSaladIngredients.add(new Recipe.Ingredient("Cranberry", 1, false));
        spakborSaladIngredients.add(new Recipe.Ingredient("Blueberry", 1, false));
        spakborSaladIngredients.add(new Recipe.Ingredient("Tomato", 1, false));
        allRecipes.put("recipe_9", new Recipe("recipe_9", "Spakbor Salad", 1, spakborSaladIngredients, "Spakbor Salad", true));

        // recipe_10: Fish Sandwich
        List<Recipe.Ingredient> fishSandwichIngredients = new ArrayList<>();
        fishSandwichIngredients.add(new Recipe.Ingredient("Any Fish", 1, true));
        fishSandwichIngredients.add(new Recipe.Ingredient("Wheat", 2, false));
        fishSandwichIngredients.add(new Recipe.Ingredient("Tomato", 1, false));
        fishSandwichIngredients.add(new Recipe.Ingredient("Hot Pepper", 1, false));
        allRecipes.put("recipe_10", new Recipe("recipe_10", "Fish Sandwich", 1, fishSandwichIngredients, "Fish Sandwich", false));

        // recipe_11: The Legends of Spakbor
        List<Recipe.Ingredient> legendsIngredients = new ArrayList<>();
        legendsIngredients.add(new Recipe.Ingredient("Legend Fish", 1, false));
        legendsIngredients.add(new Recipe.Ingredient("Potato", 2, false));
        legendsIngredients.add(new Recipe.Ingredient("Parsnip", 1, false));
        legendsIngredients.add(new Recipe.Ingredient("Tomato", 1, false));
        legendsIngredients.add(new Recipe.Ingredient("Eggplant", 1, false));
        allRecipes.put("recipe_11", new Recipe("recipe_11", "The Legends of Spakbor", 1, legendsIngredients, "The Legends of Spakbor", false));
    }

    public static Recipe getRecipeById(String id) {
        return allRecipes.get(id);
    }
}