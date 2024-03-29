package com.example.trueproject.custom_classes; //

import android.graphics.drawable.Drawable;
import android.telephony.ClosedSubscriberGroupInfo;
import android.util.Log;

import com.example.trueproject.R;

import org.w3c.dom.ls.LSOutput;

import java.util.*;

public class SharedData {
    private final static String TAG = "SharedData";
    public static Set<Allergies> allergySet = new HashSet<>();
    public static Set<RecipeType> recipeTypeSet = new HashSet<>();
    public static Set<Recipe> recipeSet = new TreeSet<>(
            (Recipe r1, Recipe r2) -> r1.getName().compareTo(r2.getName())
    );

    public static Set<Difficulty> difficultySet = new HashSet<>();
    static { Collections.addAll(difficultySet, Difficulty.values()); }

    public static Set<IngredientQuantity> ingQtySet = new TreeSet<>(
            (IngredientQuantity i1, IngredientQuantity i2) ->
                    i1.getIngredient().getName().compareTo(i2.getIngredient().getName())
    );

    public static Set<IngredientQuantity> ingQtySetFiltered = new TreeSet<>(
            (IngredientQuantity i1, IngredientQuantity i2) ->
                    i1.getIngredient().getName().compareTo(i2.getIngredient().getName())
    );

    public static Recipe chosenRecipe = null;
    public static int nMeals = 1; // number of meals to cook
    public static boolean ingredientsLoaded = false;
    public static boolean showUncookables = true;
    public static boolean vegetarian = false;
    public static boolean reverseSort = false;
    public static SortType sortType = SortType.NAME;
    public static String searchQuery = "";
    public static String ingSearchQuery = "";

    public static int[] recipeImgs = new int[]{
            R.drawable.recipe1,
            R.drawable.recipe2,
            R.drawable.recipe3,
            R.drawable.recipe4,
            R.drawable.recipe5,
            R.drawable.recipe6,
            R.drawable.recipe7,
            R.drawable.recipe8,
            R.drawable.recipe9,
            R.drawable.recipe10,
            R.drawable.recipe11,
            R.drawable.recipe12,
            R.drawable.recipe13,
            R.drawable.recipe14,
            R.drawable.recipe15,
            R.drawable.recipe16,
    };

    // TODO
    static {
        Collections.addAll(recipeTypeSet, RecipeType.values());
        addEveryIngQty();
        loadJoaquina();
        Log.i(TAG, "update recipes");
        updateRecipes();
    }

    public static double getQuantityOf(Ingredient ingredient) {
        for (IngredientQuantity ingQty : ingQtySet)
            if (ingQty.getIngredient().getId() == ingredient.getId())
                return ingQty.getQuantity();
        return 0;
    }

    public static void addEveryIngQty() {
        for (Ingredient ing : IngredientBank.getAllIngredients())
            ingQtySet.add(new IngredientQuantity(ing, 0));
    }

    public static void addQtyToIng(Ingredient ing, double quantity) {
        for (IngredientQuantity ingQty : ingQtySet)
            if (ing.getId() == ingQty.getIngredient().getId())
                ingQty.addQuantity(quantity);
    }

    public static void addQtyToIng(int id, double quantity) {
        for (IngredientQuantity ingQty : ingQtySet)
            if (id == ingQty.getIngredient().getId())
                ingQty.addQuantity(quantity);
    }

    // simulate Joaquina initial data like in low fidelity prototype
    public static void loadJoaquina() {
        // necessary ingredients for 4 meals of "Carne de porco à alentejana"
        addQtyToIng(1, 800);
        addQtyToIng(2, 3);
        addQtyToIng(3, 1);
        addQtyToIng(4, 1);
        addQtyToIng(5, 2.5);
        addQtyToIng(6, 2.5);
        addQtyToIng(7, 100);
        addQtyToIng(8, 500);
        addQtyToIng(9, 2);
        addQtyToIng(84,900);
        addQtyToIng(62,6);
        addQtyToIng(85,240);


    }

    public static void selectAllRecipeTypes() {
        Collections.addAll(recipeTypeSet, RecipeType.values());
    }

    public static void updateRecipes() {
        Log.i(TAG, "updateRecipes called");
        Log.i(TAG, "updateRecipes reverseSort: " + reverseSort);
        Log.i(TAG, "updateRecipes sortType: " + sortType);
        switch (sortType) {
            case NAME:
                Log.i(TAG, "updateRecipes: case NAME");
                recipeSet = new TreeSet<>((Recipe r1, Recipe r2) ->
                        (reverseSort ? -1 : 1) * r1.getName().compareTo(r2.getName()));
                break;
            case TIME:
                Log.i(TAG, "updateRecipes: case TIME");
                recipeSet = new TreeSet<>((Recipe r1, Recipe r2) ->
                        r1.getCookingTime().compareTo(r2.getCookingTime()) == 0 ?
                                (reverseSort ? -1 : 1) * r1.getName().compareTo(r2.getName())
                                : (reverseSort ? -1 : 1) * r1.getCookingTime().compareTo(r2.getCookingTime()));
                break;
            case DIFFICULTY:
                Log.i(TAG, "updateRecipes: case DIFFICULTY");
                recipeSet = new TreeSet<>((Recipe r1, Recipe r2) ->
                        r1.getDifficulty().getVal() - r2.getDifficulty().getVal() == 0 ?
                                (reverseSort ? -1 : 1) * r1.getName().compareTo(r2.getName())
                                : (reverseSort ? -1 : 1) * r1.getDifficulty().getVal() - r2.getDifficulty().getVal());
        }

        for (Recipe r : RecipeBank.getAllRecipes()) {
            Log.i(TAG, "recipe: " + r);
            Log.i(TAG, "getAllergies: " + r.getAllergies());
            Log.i(TAG, "allergieSet: " + allergySet);
            Log.i(TAG, "contains: " + !containsAllergy(r.getAllergies(), allergySet));
            if (!containsAllergy(r.getAllergies(), allergySet)
                    && recipeTypeSet.contains(r.getType())
                    && difficultySet.contains(r.getDifficulty())
                    && (!vegetarian || r.isVegetarian())
                    && (searchQuery.equals("") || r.getName().toLowerCase().contains(searchQuery))
                    && (showUncookables || r.canBeCookedWith(ingQtySet, nMeals))) {
                recipeSet.add(r);
                Log.i(TAG, "recipe added: " + r);
            }
        }
    }

    public static void sortByTime() {
        TreeSet<Recipe> tree = new TreeSet<>((Recipe r1, Recipe r2) ->
                (reverseSort ? -1 : 1) * r1.getCookingTime().compareTo(r2.getCookingTime()));

        tree.addAll(recipeSet);
        recipeSet = tree;
    }

    public static void sortByName() {
        TreeSet<Recipe> tree = new TreeSet<>((Recipe r1, Recipe r2) ->
                (reverseSort ? -1 : 1) * r1.getName().compareTo(r2.getName()));

        tree.addAll(recipeSet);
        recipeSet = tree;
    }

    public static void sortByDifficulty() {
        TreeSet<Recipe> tree = new TreeSet<>((Recipe r1, Recipe r2) ->
                (reverseSort ? -1 : 1) * r2.getDifficulty().getVal() - r1.getDifficulty().getVal());

        tree.addAll(recipeSet);
        recipeSet = tree;
    }

    private static boolean containsAllergy(Set<Allergies> set, Set<Allergies> userAllergies) {
        set.retainAll(userAllergies); // intersection
        Log.i("containsAllergy", "" + set);
        return (set.size() > 0);
    }

    // debug functions

    public static void debugAllergies() {
        for (Allergies a : allergySet)
            Log.i(TAG, a.getName());
    }

    public static void updateIngQtySetFiltered() {
        Log.i(TAG, "updateIngQtySetFiltered");
        for (IngredientQuantity ingQty : ingQtySet) {
            if (ingQty.getIngredient().getName().toLowerCase().contains(ingSearchQuery)) {
                ingQtySetFiltered.add(ingQty);
                Log.i(TAG, "ingQtySetFiltered: added " + ingQty);
            }
            else {
                ingQtySetFiltered.remove(ingQty);
                Log.i(TAG, "ingQtySetFiltered: removed " + ingQty);
            }
        }
    }
}
