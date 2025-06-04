package com.Spakborhills.item; // Atau package yang sesuai, misal 'cooking' atau 'mechanics'

import java.util.List;
import java.util.ArrayList;

public class Recipe {
    private String recipeId;           
    private String outputFoodName;     
    private int outputFoodQuantity;   
    private List<Ingredient> ingredients;
    private String unlockMethodDescription; 
    private boolean isDefaultRecipe;    

    public static class Ingredient {
        public String itemNameOrCategory;
        public int quantityRequired;
        public boolean isCategory;   

        public Ingredient(String itemNameOrCategory, int quantityRequired, boolean isCategory) {
            this.itemNameOrCategory = itemNameOrCategory;
            this.quantityRequired = quantityRequired;
            this.isCategory = isCategory;
        }

        @Override
        public String toString() {
            return itemNameOrCategory + " x" + quantityRequired + (isCategory ? " (Kategori)" : "");
        }
    }

    public Recipe(String recipeId, String outputFoodName, int outputFoodQuantity,
                  List<Ingredient> ingredients, String unlockMethodDescription, boolean isDefaultRecipe) {
        this.recipeId = recipeId;
        this.outputFoodName = outputFoodName;
        this.outputFoodQuantity = outputFoodQuantity;
        this.ingredients = ingredients != null ? new ArrayList<>(ingredients) : new ArrayList<>();
        this.unlockMethodDescription = unlockMethodDescription;
        this.isDefaultRecipe = isDefaultRecipe;
    }
    public String getRecipeId() {
        return recipeId;
    }

    public String getOutputFoodName() {
        return outputFoodName;
    }

    public int getOutputFoodQuantity() {
        return outputFoodQuantity;
    }

    public List<Ingredient> getIngredients() {
        return new ArrayList<>(ingredients); 
    }

    public String getUnlockMethodDescription() {
        return unlockMethodDescription;
    }

    public boolean isDefaultRecipe() {
        return isDefaultRecipe;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("Resep: ").append(outputFoodName).append(" (ID: ").append(recipeId).append(")\n");
        sb.append("  Menghasilkan: ").append(outputFoodName).append(" x").append(outputFoodQuantity).append("\n");
        sb.append("  Bahan:\n");
        for (Ingredient ingredient : ingredients) {
            sb.append("    - ").append(ingredient.toString()).append("\n");
        }
        sb.append("  Cara Unlock: ").append(unlockMethodDescription).append(isDefaultRecipe ? " (Default)" : "").append("\n");
        return sb.toString();
    }
}