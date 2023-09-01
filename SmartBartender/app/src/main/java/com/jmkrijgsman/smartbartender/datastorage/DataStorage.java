package com.jmkrijgsman.smartbartender.datastorage;

import com.jmkrijgsman.smartbartender.datastorage.room.Recipe;

import java.util.List;

public interface DataStorage {
    List<Recipe> getRecipes();
    void insertRecipe(Recipe recipe);
}
